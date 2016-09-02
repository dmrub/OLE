/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.resources;


import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import de.dfki.resc28.flapjack.resources.Container;
import de.dfki.resc28.flapjack.resources.IResource;
import de.dfki.resc28.flapjack.vocabularies.ADMS;
import de.dfki.resc28.igraphstore.IGraphStore;

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

	    return allowedMethods;
	}

}
