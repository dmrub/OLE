/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.resources;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import de.dfki.resc28.flapjack.resources.IResource;
import de.dfki.resc28.flapjack.vocabularies.ADMS;
import de.dfki.resc28.igraphstore.IGraphStore;

public class ResourceManager extends de.dfki.resc28.flapjack.resources.ResourceManager
{

	public ResourceManager(IGraphStore graphStore) 
	{
		super(graphStore);
	}

	public IResource get(String resourceURI) 
	{
		Model resourceModel = fGraphStore.getNamedGraph(resourceURI);
		
		if (resourceModel == null)
			return null;
		
		Resource r = resourceModel.getResource(resourceURI);
		
		if (r.hasProperty(RDF.type, ADMS.Asset))
		{
			return new Asset(resourceURI, fGraphStore);
		}
		else if (r.hasProperty(RDF.type, ADMS.AssetDistribution))
		{
			return new Distribution(resourceURI, fGraphStore);
		}
		else if (r.hasProperty(RDF.type, ADMS.AssetRepository))
		{
			return new Repository(resourceURI, fGraphStore);
		}
		else
		{
			return null;
		}
	}

}
