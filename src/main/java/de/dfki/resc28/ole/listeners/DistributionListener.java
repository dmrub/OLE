/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */

package de.dfki.resc28.ole.listeners;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.XSD;

import de.dfki.resc28.LDrawParser.LDrawParserBaseListener;
import de.dfki.resc28.LDrawParser.LDrawParser.Author_rowContext;
import de.dfki.resc28.ole.Server;
import de.dfki.resc28.ole.Util;
import de.dfki.resc28.ole.vocabularies.ADMS;
import de.dfki.resc28.ole.vocabularies.DCAT;
import de.dfki.resc28.ole.vocabularies.FOAF;


public class DistributionListener extends LDrawParserBaseListener
{
	private Model distributionModel;
	private Resource asset;
	private Resource distribution;
	
	public DistributionListener(String ID, String fileUri) 
	{
		super();
		
		this.distributionModel = ModelFactory.createDefaultModel();
		distributionModel.setNsPrefixes(FOAF.NAMESPACE);
		distributionModel.setNsPrefixes(ADMS.NAMESPACE);
		distributionModel.setNsPrefixes(DCAT.NAMESPACE);
		distributionModel.setNsPrefix("dcterms", DCTerms.NS);
		distributionModel.setNsPrefix("rdf", RDF.getURI());
		distributionModel.setNsPrefix("rdfs", RDFS.getURI());
		distributionModel.setNsPrefix("skos", SKOS.getURI());
		distributionModel.setNsPrefix("xsd", XSD.NS);
		distributionModel.setNsPrefix("ldraw", "http://www.ldraw.org/ns/ldraw#");
		distributionModel.setNsPrefix("users", Server.fUserBaseUri);
		distributionModel.setNsPrefix("assets", Server.fAssetBaseUri);
		distributionModel.setNsPrefix("distributions", Server.fDistributionBaseUri);

		this.asset = this.distributionModel.createResource(Util.joinPath(Server.fAssetBaseUri, ID));
		this.distribution = this.distributionModel.createResource(Util.joinPath(Server.fDistributionBaseUri, ID));
		Literal downloadURL = distributionModel.createTypedLiteral( fileUri, XSDDatatype.XSDanyURI );

		distributionModel.add( asset, RDF.type, ADMS.Asset);
		distributionModel.add( distribution, RDF.type, ADMS.AssetDistribution );
		distributionModel.add( distribution, DCTerms.format, "application/x-ldraw" );
		distributionModel.add( distribution, DCAT.mediaType, "application/x-ldraw" );
		distributionModel.add( distribution, DCTerms.isReferencedBy, asset );	
		distributionModel.add( distribution, DCAT.downloadURL, downloadURL);
	}
	
	public Model getModel()
	{
		return this.distributionModel;
	}
	
	@Override
	public void exitAuthor_row(Author_rowContext ctx)
	{
		if (ctx != null)
		{
			if (ctx.realname() != null)
			{
				Resource creator = distributionModel.createResource(Util.joinPath(Server.fUserBaseUri, Util.toURLEncodedStringLiteral(ctx.realname().STRING(), "_").toString()));
				distributionModel.add( distribution, FOAF.maker, creator );
				distributionModel.add( distribution,  DCTerms.creator, creator );
			}
		}
	}
}
