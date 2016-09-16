/*
 * This file is part of OLE. It is subject to the license terms in
 * the LICENSE file found in the top-level directory of this distribution.
 * You may not use this file except in compliance with the License.
 */
package de.dfki.resc28.ole.listeners;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.io.FilenameUtils;
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
import de.dfki.resc28.LDrawParser.LDrawParser.Category_rowContext;
import de.dfki.resc28.LDrawParser.LDrawParser.Comment_rowContext;
import de.dfki.resc28.LDrawParser.LDrawParser.FileContext;
import de.dfki.resc28.LDrawParser.LDrawParser.Help_rowContext;
import de.dfki.resc28.LDrawParser.LDrawParser.History_rowContext;
import de.dfki.resc28.LDrawParser.LDrawParser.Keywords_rowContext;
import de.dfki.resc28.LDrawParser.LDrawParser.Ldraw_rowContext;
import de.dfki.resc28.LDrawParser.LDrawParser.License_rowContext;
import de.dfki.resc28.LDrawParser.LDrawParser.Reference_rowContext;
import de.dfki.resc28.LDrawParser.LDrawParser.TitleContext;
import de.dfki.resc28.ole.Server;
import de.dfki.resc28.ole.Util;
import de.dfki.resc28.ole.vocabularies.ADMS;
import de.dfki.resc28.ole.vocabularies.DCAT;
import de.dfki.resc28.ole.vocabularies.FOAF;


public class AssetListener extends LDrawParserBaseListener
{
	private Model assetModel;
	private Resource asset;
	private Resource distribution;
	
	public AssetListener(String ID, String fileUri) 
	{
		super();
		
		this.assetModel = ModelFactory.createDefaultModel();
		this.assetModel.setNsPrefixes(FOAF.NAMESPACE);
		this.assetModel.setNsPrefixes(ADMS.NAMESPACE);
		this.assetModel.setNsPrefixes(DCAT.NAMESPACE);
		this.assetModel.setNsPrefix("dcterms", DCTerms.NS);
		this.assetModel.setNsPrefix("rdf", RDF.getURI());
		this.assetModel.setNsPrefix("rdfs", RDFS.getURI());
		this.assetModel.setNsPrefix("skos", SKOS.getURI());
		this.assetModel.setNsPrefix("xsd", XSD.NS);
		this.assetModel.setNsPrefix("ldraw", "http://www.ldraw.org/ns/ldraw#");
		this.assetModel.setNsPrefix("users", Server.fUserBaseUri);
		this.assetModel.setNsPrefix("assets", Server.fAssetBaseUri);
		this.assetModel.setNsPrefix("distributions", Server.fDistributionBaseUri);
		
		Resource repo = assetModel.createResource(Util.joinPath(Server.fBaseURI, "repo"));
		this.asset = this.assetModel.createResource(Util.joinPath(Server.fAssetBaseUri, ID));
		this.distribution = this.assetModel.createResource(Util.joinPath(Server.fDistributionBaseUri, ID));
		Literal downloadURL = assetModel.createTypedLiteral( fileUri, XSDDatatype.XSDanyURI );

		this.assetModel.add( asset, RDF.type, ADMS.Asset );
		this.assetModel.add( distribution, RDF.type, ADMS.AssetDistribution );
		this.assetModel.add( distribution, DCTerms.format, "application/x-ldraw" );
		this.assetModel.add( distribution, DCAT.mediaType, "application/x-ldraw" );
		this.assetModel.add( distribution, DCAT.downloadURL, downloadURL);
		this.assetModel.add( asset, DCAT.distribution, distribution );
		this.assetModel.add( repo, RDF.type, ADMS.AssetRepository);
		this.assetModel.add( asset, DCTerms.isReferencedBy, repo );
	}
	
	public Model getModel()
	{
		return this.assetModel;
	}
	
	@Override
	public void exitTitle(TitleContext ctx)
	{
		if (ctx.free_text() != null )
			assetModel.add( asset, DCTerms.description, Util.toStringLiteral(ctx.free_text(), " ") );
	}

	@Override
	public void exitAuthor_row(Author_rowContext ctx)
	{
		if (ctx != null)
		{
			if (ctx.realname() != null)
			{
				Resource creator = assetModel.createResource(Util.joinPath(Server.fUserBaseUri, Util.toURLEncodedStringLiteral(ctx.realname().STRING(), "_").getString()));
				assetModel.add( asset, FOAF.maker, creator );
				assetModel.add( asset,  DCTerms.creator, creator );

			}
		}
	}
	
	@Override
	public void exitCategory_row(Category_rowContext ctx)
	{	
		if (ctx.category() != null)
			for (TerminalNode c : ctx.category().STRING())
				assetModel.add( asset, DCAT.theme, c.getText() );
	}

	@Override
	public void exitComment_row(Comment_rowContext ctx)
	{
		if (ctx.free_text() != null)
			assetModel.add( asset, RDFS.comment, Util.toStringLiteral(ctx.free_text(), " ") );
	}

	@Override
	public void exitHistory_row(History_rowContext ctx)
	{
		if (ctx != null)
		{
			Resource changeNote = assetModel.createResource();
			assetModel.add( changeNote, DCTerms.date, assetModel.createTypedLiteral(ctx.YYYY_MM_DD().getText(), XSDDatatype.XSDdate));
			assetModel.add( changeNote, RDF.value, Util.toStringLiteral(ctx.free_text(), " ") );
			assetModel.add( asset, SKOS.changeNote, changeNote );
			
			if (ctx.realname() != null)
			{
				Resource contributor = assetModel.createResource(Util.joinPath(Server.fUserBaseUri, Util.toURLEncodedStringLiteral(ctx.realname().STRING(), "_").toString()));
				assetModel.add( changeNote,  DCTerms.creator, contributor);
				assetModel.add( contributor, DCTerms.contributor, asset );
			}

			// TODO: ctx.username()
		}
	}
	
	@Override
	public void exitKeywords_row(Keywords_rowContext ctx)
	{
		if (ctx.free_text() != null)
			assetModel.add( asset, DCAT.keyword, Util.toStringLiteral(ctx.free_text(), " ") );
	}

	@Override
	public void exitLicense_row(License_rowContext ctx)
	{
		if (ctx.free_text() != null)
		{
			Resource rightsStatement = assetModel.createResource();
			assetModel.add( rightsStatement, RDF.type, DCTerms.RightsStatement );
			assetModel.add( rightsStatement, RDFS.label, Util.toStringLiteral(ctx.free_text(), " ") );
			assetModel.add( asset, DCTerms.rights, rightsStatement );
		}
	}

	@Override
	public void exitReference_row(Reference_rowContext ctx)
	{
		if (ctx.subPart() != null)
		{
			Resource subPart = assetModel.createResource(Util.joinPath(Server.fAssetBaseUri, Util.urlEncoded(FilenameUtils.getBaseName(ctx.subPart().FILENAME().getText())).toString()));
			assetModel.add( asset, ADMS.includedAsset, subPart );
		}
		if (ctx.subFile() != null)
		{
			Resource subFile = assetModel.createResource(Util.joinPath(Server.fAssetBaseUri, Util.urlEncoded(FilenameUtils.getBaseName(ctx.subFile().FILENAME().getText())).toString()));
			assetModel.add( asset, ADMS.includedAsset, subFile );
		}
		if (ctx.hiResPrimitive() != null)
		{
			Resource hisResPrimitive = assetModel.createResource(Util.joinPath(Server.fAssetBaseUri, Util.urlEncoded(FilenameUtils.getBaseName(ctx.hiResPrimitive().FILENAME().getText())).toString()));
			assetModel.add( asset, ADMS.includedAsset, hisResPrimitive );
		}
	}

	public void exitHelp_row(Help_rowContext ctx)
	{
		if (ctx.free_text() != null)
		{
			assetModel.add( asset, SKOS.note, Util.toStringLiteral(ctx.free_text(), " ") );
		}
	}

	public void exitLdraw_row(Ldraw_rowContext ctx)
	{
		if (ctx != null)
		{
			assetModel.add( asset, DCTerms.type, assetModel.createResource("http://www.ldraw.org/ns/ldraw#" + Util.urlEncoded(ctx.type().TYPE().getText())));
		}
	}
}
