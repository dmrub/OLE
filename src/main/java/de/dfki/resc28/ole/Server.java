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

@ApplicationPath("/")
public class Server extends Application
{
    public static String fBaseURI = null;
    public static String fAssetBaseUri = null;
    public static String fDistributionBaseUri = null;
    public static String fUserBaseUri = null;


    public static IGraphStore fGraphStore = null;
    
    public static String fSerendipityURI = null;

	@Override
    public Set<Object> getSingletons() 
    {
		configure();
	
		OLEService officialPartsRepo = new OLEService(fGraphStore);
		RepresentationEnricher enricher = new RepresentationEnricher(fSerendipityURI);
		
		return new HashSet<Object>(Arrays.asList(officialPartsRepo, enricher));
    }


    public static synchronized void configure() 
    {
        try 
        {
            ProxyConfigurator.initHttpClient();
            String configFile = System.getProperty("ole.configuration");
            java.io.InputStream is;

            if (configFile != null) 
            {
                is = new java.io.FileInputStream(configFile);
                System.out.format("Loading OLE Repo configuration from %s ...%n", configFile);
            } 
            else 
            {
                is = Server.class.getClassLoader().getResourceAsStream("ole.properties");
                System.out.println("Loading OLE Repo configuration from internal resource file ...");
            }

            java.util.Properties p = new Properties();
            p.load(is);

            fBaseURI = getProperty(p, "baseURI", "ole.baseURI");
            fAssetBaseUri = Util.joinPath(fBaseURI, "repo/assets/") ;
            fDistributionBaseUri = Util.joinPath(fBaseURI, "repo/distributions/") ;
            fUserBaseUri = Util.joinPath(fBaseURI, "repo/users/") ;

            fSerendipityURI = getProperty(p, "serendipityURI", "ole.serendipityURI");

            String storage = getProperty(p, "graphStore", "ole.graphStore");
            if (storage.equals("fuseki")) 
            {
                String dataEndpoint = getProperty(p, "dataEndpoint", "ole.dataEndpoint");
                String queryEndpoint = getProperty(p, "queryEndpoint", "ole.queryEndpoint");
                System.out.format("Use Fuseki backend:%n  dataEndpoint=%s%n  queryEndpoint=%s ...%n", dataEndpoint, queryEndpoint);

                fGraphStore = new FusekiGraphStore(dataEndpoint, queryEndpoint);
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }

    public static String getProperty(java.util.Properties p, String key, String sysKey) 
    {
        String value = System.getProperty(sysKey);
        if (value != null) 
        {
            return value;
        }
        return p.getProperty(key);
    }
}