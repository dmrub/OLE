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

import de.dfki.resc28.ole.services.OLEService;
import de.dfki.resc28.ole.services.RepresentationEnricher;
import de.dfki.resc28.igraphstore.IGraphStore;
import de.dfki.resc28.igraphstore.jena.FusekiGraphStore;
import de.dfki.resc28.igraphstore.jena.TDBGraphStore;

@ApplicationPath("/")
public class Server extends Application
{
    public static IGraphStore fGraphStore = null;
    public static String fBaseURI = null;

	@Override
    public Set<Object> getSingletons() 
    {
		configure();
	
		OLEService officialPartsRepo = new OLEService(fGraphStore);
		RepresentationEnricher enricher = new RepresentationEnricher();
		
		return new HashSet<Object>(Arrays.asList(officialPartsRepo, enricher));
    }


    public static synchronized void configure() 
    {
        if (fGraphStore != null) 
        {
            return;
        }

        try 
        {
            String oleConfigFile = System.getProperty("ole.configuration");
            java.io.InputStream is;
            if (oleConfigFile != null) 
            {
                is = new java.io.FileInputStream(oleConfigFile);
                System.out.format("Loading OLE configuration from %s ...%n", oleConfigFile);
            } 
            else 
            {
                is = Server.class.getClassLoader().getResourceAsStream("ole.properties");
                System.out.println("Loading OLE configuration from internal resource file ...");
            }
            java.util.Properties p = new Properties();
            p.load(is);

            String storage = p.getProperty("graphStore");
            String baseURI = p.getProperty("baseURI");
            
            if (baseURI == null) 
            {
                System.out.println("OLE: baseURI property is null, use hostName property");
                String hostName = p.getProperty("hostName", "localhost");
                baseURI = "http://" + hostName;
            }
            System.out.format("OLE: baseURI = %s%n", baseURI);
            fBaseURI = baseURI;

            if (storage.equals("fuseki")) 
            {
                String dataEndpoint = p.getProperty("dataEndpoint");
                String queryEndpoint = p.getProperty("queryEndpoint");
                System.out.format("Use Fuseki backend: dataEndpoint=%s queryEndpoint=%s ...%n", dataEndpoint, queryEndpoint);
                Server.fGraphStore = new FusekiGraphStore(dataEndpoint, queryEndpoint);
            } 
            else if (storage.equals("tdb")) 
            {
                System.out.format("Use TDB backend: datasetDir=%s ...%n", p.getProperty("datasetDir"));

                if (p.containsKey("datasetDir")) 
                {
                    Server.fGraphStore = new TDBGraphStore(p.getProperty("datasetDir"));
                } 
                else 
                {
                    Server.fGraphStore = new TDBGraphStore();
                }
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}