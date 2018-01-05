/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.services;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.dfki.resc28.flapjack.resources.IContainer;
import de.dfki.resc28.flapjack.resources.IResource;
import de.dfki.resc28.flapjack.resources.IResourceManager;
import de.dfki.resc28.flapjack.services.BaseService;
import static de.dfki.resc28.flapjack.services.BaseService.RDF_MEDIA_TYPES;
import de.dfki.resc28.flapjack.services.PATCH;
import de.dfki.resc28.igraphstore.Constants;
import de.dfki.resc28.igraphstore.IGraphStore;
import de.dfki.resc28.ole.Util;
import de.dfki.resc28.ole.resources.Asset;
import de.dfki.resc28.ole.resources.Repository;
import de.dfki.resc28.ole.resources.ResourceManager;
import de.dfki.resc28.serendipity.client.GenerateAffordances;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

//@Path("")
@Path("/repo/{paths: .*}")
public class OLEService extends BaseService {

    private final IGraphStore fGraphStore;
    private final String fBaseUri;

    public OLEService(IGraphStore graphStore, String baseUri) {
        super();

        this.fGraphStore = graphStore;
        this.fBaseUri = baseUri;
    }

    @POST
    @Produces({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    public Response post(@HeaderParam(HttpHeaders.ACCEPT) @DefaultValue(Constants.CT_TEXT_TURTLE) final String acceptType,
            @QueryParam("uri") String fileUri) {

        final MediaType responseMediaType = negotiateRDFMediaType();
        if (responseMediaType == null) {
            return Response.notAcceptable(RDF_MEDIA_TYPES).build();
        }

        try {
            String requestURL = fRequestUrl.getRequestUri().toString();
            IResource r = getResourceManager().get(getCanonicalURL(new URI(requestURL.substring(0, requestURL.indexOf("?")))));

            if (r == null) {
                return Response.status(Status.NOT_FOUND).build();
            } else if (!(r instanceof Repository)) {
                return Response.status(Status.BAD_REQUEST).build();
            } else if (!(r.getAllowedMethods().contains("POST"))) {
                return Response.status(Status.METHOD_NOT_ALLOWED).build();
            }

            return ((Repository) r).createAsset(responseMediaType.getType() + "/" + responseMediaType.getSubtype(), fileUri);

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new WebApplicationException();
        }

    }

    @PATCH
    @Consumes({Constants.CT_APPLICATION_JSON_LD, Constants.CT_APPLICATION_NQUADS, Constants.CT_APPLICATION_NTRIPLES, Constants.CT_APPLICATION_RDF_JSON, Constants.CT_APPLICATION_RDFXML, Constants.CT_APPLICATION_TRIX, Constants.CT_APPLICATION_XTURTLE, Constants.CT_TEXT_N3, Constants.CT_TEXT_TRIG, Constants.CT_TEXT_TURTLE})
    public Response patch(InputStream content, @HeaderParam(HttpHeaders.CONTENT_TYPE) @DefaultValue(Constants.CT_TEXT_TURTLE) final String contentType) {
        IResource r = getResourceManager().get(getCanonicalURL(fRequestUrl.getRequestUri()));

        if (r == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else if (!(r instanceof Asset)) {
            return Response.status(Status.BAD_REQUEST).build();
        } else if (!(r.getAllowedMethods().contains("PATCH"))) {
            return Response.status(Status.METHOD_NOT_ALLOWED).build();
        }

        return ((Asset) r).patchAsset(content, contentType);
    }

    @Override
    @GenerateAffordances
    public Response get(String acceptType) {
        return super.get(acceptType);
    }

    @Override
    protected IResourceManager getResourceManager() {
        return new ResourceManager(fGraphStore);
    }

    @Override
    protected IContainer getRootContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getResourceURI(UriInfo uriInfo) {
        if (fBaseUri != null && !fBaseUri.isEmpty()) {
            return getCanonicalURL(Util.joinPath(fBaseUri, uriInfo.getPath()));
        } else {
            return getCanonicalURL(uriInfo.getRequestUri());
        }
    }

}
