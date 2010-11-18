package de.bokelberg.flashbuilder.aca.utils;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

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

}
