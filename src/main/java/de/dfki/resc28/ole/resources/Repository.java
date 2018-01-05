/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.resources;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import de.dfki.resc28.LDrawParser.LDrawLexer;
import de.dfki.resc28.LDrawParser.LDrawParser;
import de.dfki.resc28.flapjack.resources.Container;
import de.dfki.resc28.flapjack.resources.IResource;
import de.dfki.resc28.flapjack.vocabularies.ADMS;
import de.dfki.resc28.flapjack.vocabularies.DCAT;
import de.dfki.resc28.igraphstore.Constants;
import de.dfki.resc28.igraphstore.IGraphStore;
import de.dfki.resc28.igraphstore.util.ProxyConfigurator;
import de.dfki.resc28.ole.Server;
import de.dfki.resc28.ole.Util;
import de.dfki.resc28.ole.listeners.AssetListener;
import de.dfki.resc28.ole.listeners.DistributionListener;
import org.apache.http.impl.client.CloseableHttpClient;


public class Repository extends Container implements IResource
{

	public Repository(String resourceURI, IGraphStore graphStore)
	{
		super(resourceURI, graphStore);
		this.fRDFType = ADMS.AssetRepository;
	}
	
	
	
	@Override
	public Set<String> getAllowedMethods() 
	{		
		HashSet<String> allowedMethods = new HashSet<String>();
		allowedMethods.add(HttpMethod.GET);
		allowedMethods.add(HttpMethod.HEAD);
		allowedMethods.add(HttpMethod.OPTIONS);
		allowedMethods.add(HttpMethod.POST);

	    return allowedMethods;
	}

	public Response createAsset( @HeaderParam(HttpHeaders.ACCEPT) @DefaultValue(Constants.CT_TEXT_TURTLE) final String acceptType,
								 String fileUri ) 
	{
		try
		{
			@SuppressWarnings("resource")
			CloseableHttpClient http = ProxyConfigurator.createHttpClient();
			HttpGet request = new HttpGet(fileUri);

			request.setHeader("Accept", "text/plain");			// TODO: set accept-header from inputFormat!
			HttpResponse response = (HttpResponse) http.execute(request);

			if (response.getStatusLine().getStatusCode() == 200)
			{
				DataInputStream input = new DataInputStream(new BufferedInputStream(response.getEntity().getContent()));
		        String ID = UUID.randomUUID().toString();
		        AssetListener assetListener = new AssetListener(ID, fileUri);
		        DistributionListener distributionListener = new DistributionListener(ID, fileUri);
		        
				LDrawLexer lexer = new LDrawLexer(new ANTLRInputStream(input));
		        LDrawParser parser = new LDrawParser(new CommonTokenStream(lexer));
		        ParseTreeWalker walker = new ParseTreeWalker();
		        ParseTree tree = parser.file();
		        walker.walk(assetListener, tree);
		        walker.walk(distributionListener, tree);
		        
		        Model assetModel = assetListener.getModel();
		        fGraphStore.addToNamedGraph(Util.joinPath(Server.fAssetBaseUri, ID), assetModel);

		        Model distributionModel = distributionListener.getModel();
		        fGraphStore.addToNamedGraph(Util.joinPath(Server.fDistributionBaseUri, ID), distributionModel);
		        
		        final Model repoModel = ModelFactory.createDefaultModel();//fGraphStore.getNamedGraph(Util.joinPath(Server.fBaseURI, "repo"));
		        Resource repo = repoModel.getResource(Util.joinPath(Server.fBaseURI, "repo"));
		        Resource asset = repoModel.createResource(Util.joinPath(Server.fBaseURI, "repo/assets/", ID));
		        repoModel.add(repo, DCAT.dataset, asset);
		        fGraphStore.addToNamedGraph(Util.joinPath(Server.fBaseURI, "repo"), repoModel);
				
				return Response.ok().status(Status.CREATED).contentLocation(new URI(asset.getURI())).build();

			}
			else
			{
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		catch (Exception e)
		{
			throw new WebApplicationException();
		}
	}
}
