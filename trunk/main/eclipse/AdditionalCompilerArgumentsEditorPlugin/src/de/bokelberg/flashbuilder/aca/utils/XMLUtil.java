package de.bokelberg.flashbuilder.aca.utils;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLUtil {

	public static String prettyPrint(Document doc) {
		return prettyPrint(new DOMSource(doc));
	}

	public static String prettyPrint(Node node) {
		return prettyPrint(new DOMSource(node));
	}

	public static String prettyPrint(DOMSource domSource) {
		try {
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult streamResult = new StreamResult(new StringWriter());
			transformer.transform(domSource, streamResult);

			return streamResult.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException("Error while pretty printing xml", e);
		}
	}

	public static Node findNode(Document doc, String xpath) {
		return (Node) xpathFind(doc, xpath, XPathConstants.NODE);
	}

	public static NodeList findNodes(Document doc, String xpath) {
		return (NodeList) xpathFind(doc, xpath, XPathConstants.NODESET);
	}

	public static Object xpathFind(Document doc, String xpath, QName returnType) {
		try {
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpathToolkit = xpathFactory.newXPath();
			XPathExpression xpathExpression = xpathToolkit.compile(xpath);
			return xpathExpression.evaluate(doc, returnType);
		} catch (Exception e) {
			throw new RuntimeException("Error finding nodes for <" + xpath
					+ ">", e);
		}
	}

	public static Document toXML( InputSource input ) {
		try {
			return getDocBuilder().parse( input );
		} catch (Exception e) {
			throw new RuntimeException("Error while creating xml doc", e);
		} 
	}
	
	public static Document toXML( InputStream input ) {
		return toXML( new InputSource(input));
	}
	
	public static Document toXML( String input ) {
		return toXML( new InputSource( new StringReader( input )));
	}
	
	private static DocumentBuilder getDocBuilder()
			throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		docFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder;
	}
}
