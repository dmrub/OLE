/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.services;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import de.dfki.resc28.flapjack.resources.IContainer;
import de.dfki.resc28.flapjack.resources.IResourceManager;
import de.dfki.resc28.flapjack.services.BaseService;
import de.dfki.resc28.ole.resources.ResourceManager;
import de.dfki.resc28.serendipity.client.GenerateAffordances;
import de.dfki.resc28.igraphstore.IGraphStore;


//@Path("")
@Path("{paths: .*}")
public class OLEService extends BaseService
{
	private IGraphStore fGraphStore;
	
	public OLEService(IGraphStore graphStore)
	{
		super();
		
		this.fGraphStore = graphStore;
	}
	
	@Override
	@GenerateAffordances
	public Response get(String acceptType) 
	{
		return super.get(acceptType);
	}
	
	@Override
	protected IResourceManager getResourceManager() 
	{
		return new ResourceManager(fGraphStore);
	}

	@Override
	protected IContainer getRootContainer() 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
