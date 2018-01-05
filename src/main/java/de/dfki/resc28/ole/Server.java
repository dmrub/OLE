/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import de.dfki.resc28.igraphstore.IGraphStore;
import de.dfki.resc28.igraphstore.jena.FusekiGraphStore;
import de.dfki.resc28.ole.services.OLEService;
import de.dfki.resc28.serendipity.client.RepresentationEnricher;
import de.dfki.resc28.igraphstore.util.ProxyConfigurator;
import de.dfki.resc28.ole.services.IndexService;
import org.glassfish.jersey.server.mvc.mustache.MustacheMvcFeature;

@ApplicationPath("/")
public class Server extends Application {

    public static String fBaseURI = null;
    public static String fAssetBaseUri = null;
    public static String fDistributionBaseUri = null;
    public static String fUserBaseUri = null;

    public static IGraphStore fGraphStore = null;

    public static String fSerendipityURI = null;

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(MustacheMvcFeature.class);
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        configure();

        final Set<Object> singletons = new HashSet<Object>();
        singletons.add(new ForwardedHeaderFilter());
        singletons.add(new WebAppExceptionMapper());

        singletons.add(new IndexService());
        singletons.add(new OLEService(fGraphStore, fBaseURI));
        singletons.add(new RepresentationEnricher(fSerendipityURI));

        return singletons;
    }

    public static synchronized void configure() {
        try {
            ProxyConfigurator.initHttpClient();
            String configFile = System.getProperty("ole.configuration");
            java.io.InputStream is;

            if (configFile != null) {
                is = new java.io.FileInputStream(configFile);
                System.out.format("Loading OLE Repo configuration from %s ...%n", configFile);
            } else {
                is = Server.class.getClassLoader().getResourceAsStream("ole.properties");
                System.out.println("Loading OLE Repo configuration from internal resource file ...");
            }

            java.util.Properties p = new Properties();
            p.load(is);

            fBaseURI = getProperty(p, "baseURI", "ole.baseURI");
            fAssetBaseUri = Util.joinPath(fBaseURI, "repo/assets/");
            fDistributionBaseUri = Util.joinPath(fBaseURI, "repo/distributions/");
            fUserBaseUri = Util.joinPath(fBaseURI, "repo/users/");

            fSerendipityURI = getProperty(p, "serendipityURI", "ole.serendipityURI");

            System.out.format("OLE BaseUri: %s%nOLE AssetBaseUri: %s%nOLE DistributionBaseUri %s%nOLE UserBaseUri %s%nSerendipityUri %s%n",
                    fBaseURI, fAssetBaseUri, fDistributionBaseUri, fUserBaseUri, fSerendipityURI);

            String storage = getProperty(p, "graphStore", "ole.graphStore");
            if (storage.equals("fuseki")) {
                String dataEndpoint = getProperty(p, "dataEndpoint", "ole.dataEndpoint");
                String queryEndpoint = getProperty(p, "queryEndpoint", "ole.queryEndpoint");
                System.out.format("Use Fuseki backend:%n  dataEndpoint=%s%n  queryEndpoint=%s ...%n", dataEndpoint, queryEndpoint);

                fGraphStore = new FusekiGraphStore(dataEndpoint, queryEndpoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(java.util.Properties p, String key, String sysKey) {
        String value = System.getProperty(sysKey);
        if (value != null) {
            return value;
        }
        return p.getProperty(key);
    }
}
