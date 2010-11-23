package de.bokelberg.flashbuilder.aca.editors.form;

import java.net.URL;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.bokelberg.flashbuilder.aca.utils.XMLUtil;

/**
 * Could we split the two things? elements and defaultValues? Maybe if we rename
 * this class to ElementConfig with two members elements and defaultValues
 * 
 * @author rbokel
 * 
 */
public class FormElementConfig {

	private FormElementList elements = new FormElementList();
	private DefaultValues defaultValues = new DefaultValues();

	public FormElement findElement(String id) {
		return elements.findElement(id);
	}

	public void loadElementsConfiguration(URL elementsConfiguration) {
		Document doc = loadXMLDocument(elementsConfiguration);
		NodeList elementNodes = getElementNodes(doc);
		initFormElements(elementNodes);
		initDefaultValues(elementNodes);
	}

	public Iterable<FormElement> elementsIterator() {
		return elements.iterator();
	}

	public void addDefaultValue(String id, Object value) {
		defaultValues.addDefaultValue(id, value);
	}

	public boolean hasDefaultValue(String id) {
		return defaultValues.hasDefaultValue(id);
	}

	public Object getDefaultValue(String id) {
		return defaultValues.getDefaultValue(id);
	}
	
	public Predicate<Argument> getFilterPredicate()
	{
		return new ValueIsDifferentFromDefaultPredicate(defaultValues);
	}

	private Document loadXMLDocument(URL url) {
		try {
			return XMLUtil.toXML(url.openStream());
		} catch (Exception e) {
			throw new RuntimeException("Error loading xml from <" + url + ">",
					e);
		}
	}

	private NodeList getElementNodes(Document doc) {
		return XMLUtil.findNodes(doc, "/elements/form-element");
	}

	private void initFormElements(NodeList nodes) {
		final int size = nodes.getLength();
		for (int i = 0; i < size; i++) {
			try {
				addFormElement(nodes.item(i));
			} catch (Exception e) {
				log().error("Error creating FormElement from node <" + i + ">",
						e);
			}
		}
	}

	private void addFormElement(Node node) {
		String type = getAttribute(node, "type");
		String id = getAttribute(node, "id");
		String label = getAttribute(node, "label");
		String tooltip = getAttribute(node, "tooltip");
		elements.addFormElement(type, id, label, tooltip);
	}

	private String getAttribute(Node node, String id) {
		try {
			Node attribute = node.getAttributes().getNamedItem(id);
			return attribute.getNodeValue();
		} catch (Exception e) {
			throw new RuntimeException("Error getting attribute <" + id
					+ "> in node <" + XMLUtil.prettyPrint(node) + ">", e);
		}
	}

	private void initDefaultValues(NodeList elementNodes) {
		for (int i = 0; i < elementNodes.getLength(); i++) {
			Node node = elementNodes.item(i);
			String id = getAttribute(node, "id");
			Object defaultValue = getOptionalAttribute(node, "defaultValue",
					null);
			if (defaultValue != null) {
				defaultValues.addDefaultValue(id, defaultValue);
			}
		}
	}

	private String getOptionalAttribute(Node node, String id,
			String defaultValue) {
		Node attribute = node.getAttributes().getNamedItem(id);
		return attribute != null ? attribute.getNodeValue() : defaultValue;
	}

	private Logger _log = null;

	private Logger log() {
		if (_log == null) {
			_log = Logger.getLogger(this.getClass());
		}
		return _log;
	}
}
