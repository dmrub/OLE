/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;

import de.dfki.resc28.flapjack.resources.Container;
import de.dfki.resc28.flapjack.resources.IContainer;
import de.dfki.resc28.flapjack.vocabularies.ADMS;
import de.dfki.resc28.igraphstore.IGraphStore;
import javax.ws.rs.core.HttpHeaders;

public class Asset extends Container implements IContainer
{

	public Asset(String resourceURI, IGraphStore graphStore) 
	{
		super(resourceURI, graphStore);
		this.fRDFType =  ADMS.Asset;
	}
	
	@Override
	public Response read(final String contentType) 
	{
		final Model description = fGraphStore.getNamedGraph(fURI);	
		
		StreamingOutput out = new StreamingOutput() 
		{
			public void write(OutputStream output) throws IOException, WebApplicationException
			{
				RDFDataMgr.write(output, description, RDFDataMgr.determineLang(null, contentType, null)) ;
			}
		};
		
		return Response.ok(out)
					   .header(HttpHeaders.VARY, HttpHeaders.ACCEPT)
					   .type(contentType)
					   .build();
	}
	
	public Response patchAsset(InputStream input, final String contentType)
	{
		Model assetModel = fGraphStore.getNamedGraph(fURI);
		Resource asset = assetModel.getResource(fURI);
		
		final Model patchModel = ModelFactory.createDefaultModel();
		RDFDataMgr.read(patchModel, input, fURI,  RDFDataMgr.determineLang(null, contentType, null) );
		
		
		// patch ADMS:last triples
		NodeIterator patchIterator = patchModel.listObjectsOfProperty(asset, ADMS.last);
		while (patchIterator.hasNext())
		{
			Resource newVersion = patchIterator.next().asResource();
			
			// first remove any old triples
			NodeIterator deleteIterator = fGraphStore.getNamedGraph(fURI).listObjectsOfProperty(asset, ADMS.last);
			while (deleteIterator.hasNext())
			{
				Resource oldVersion = deleteIterator.next().asResource();
				assetModel.remove(asset, ADMS.last, oldVersion);
			}
			
			// now, add new version triples
			assetModel.add(asset, ADMS.last, newVersion);
		}
			
		// do more patching here...

		fGraphStore.replaceNamedGraph(fURI, assetModel);
		return null;
	}

	@Override
	public Set<String> getAllowedMethods() 
	{		
		HashSet<String> allowedMethods = new HashSet<String>();
		allowedMethods.add(HttpMethod.GET);
		allowedMethods.add(HttpMethod.HEAD);
		allowedMethods.add(HttpMethod.OPTIONS);
		allowedMethods.add("PATCH");

	    return allowedMethods;
	}
}
