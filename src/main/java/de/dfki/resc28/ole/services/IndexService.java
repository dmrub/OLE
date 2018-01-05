/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.services;

import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.server.mvc.Template;

/**
 *
 * @author Dmitri Rubinstein
 */
@Path("/")
public class IndexService {

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Template(name = "/index")
    public Map<String, Object> getIndex(@Context UriInfo uriInfo) {
        Map<String, Object> model = new HashMap<String, Object>();
        Map<String, Object> uriInfoModel = new HashMap<String, Object>();
        uriInfoModel.put("absolutePath", uriInfo.getAbsolutePath());
        model.put("uriInfo", uriInfoModel);
        return model;
    }
}
