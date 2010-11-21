package de.bokelberg.flashbuilder.aca.editors.form;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.bokelberg.flashbuilder.aca.utils.XMLUtil;

public class FormEditor {

	private FormElement[] elements;

	final private Composite mainView;

	final private Map<String, Widget> idToFormElement = new HashMap<String, Widget>();

	final private ArgumentsModel model = new ArgumentsModel();

	private DefaultValues defaultValues = null;

	private FormChangeHandler formChangeHandler = null;

	public FormEditor(Composite container, URL elementsConfiguration) {
		if (container == null) {
			throw new RuntimeException("ArgumentError: container is null");
		}
		if (elementsConfiguration == null) {
			throw new RuntimeException("ArgumentError: url is null");
		}

		elements = loadElementsConfiguration(elementsConfiguration);
		mainView = createView(container);
	}

	public Composite getView() {
		return mainView;
	}

	public String getTitle() {
		return "FormEditor";
	}

	/**
	 * Poor man's event listening ;)
	 * 
	 * @param changeHandler
	 */
	public void setFormChangeHandler(FormChangeHandler value) {
		formChangeHandler = value;
	}

	private FormElement[] loadElementsConfiguration(URL elementsConfiguration) {
		Document doc = loadXMLDocument(elementsConfiguration);
		NodeList elementNodes = getElementNodes(doc);
		defaultValues = getDefaultValues(elementNodes);
		return createFormElements(elementNodes);
	}

	private DefaultValues getDefaultValues(NodeList elementNodes) {
		DefaultValues result = new DefaultValues();
		for (int i = 0; i < elementNodes.getLength(); i++) {
			Node node = elementNodes.item(i);
			String id = getAttribute(node, "id");
			Object defaultValue = getOptionalAttribute(node, "defaultValue",
					null);
			if (defaultValue != null) {
				result.addDefaultValue(id, defaultValue);
			}
		}
		return result;
	}

	private FormElement[] createFormElements(NodeList nodes) {
		final int size = nodes.getLength();
		FormElement[] result = new FormElement[size];
		for (int i = 0; i < size; i++) {
			try {
				FormElement element = createFormElement(nodes.item(i));
				result[i] = element;
			} catch (Exception e) {
				log().error("Error creating FormElement from node <" + i + ">",
						e);
			}
		}
		return result;
	}

	private FormElement createFormElement(Node node) {
		String type = getAttribute(node, "type");
		String id = getAttribute(node, "id");
		String label = getAttribute(node, "label");
		String tooltip = getAttribute(node, "tooltip");
		String defaultValue = getOptionalAttribute(node, "defaultValue", null);
		return new FormElement(type, id, label, tooltip, defaultValue);
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

	private String getOptionalAttribute(Node node, String id,
			String defaultValue) {
		Node attribute = node.getAttributes().getNamedItem(id);
		return attribute != null ? attribute.getNodeValue() : defaultValue;
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

	private Composite createView(Composite container) {
		ScrolledComposite scrollableContainer = createScrollableContainer(container);
		Composite form = addForm(scrollableContainer);
		scrollableContainer.setContent(form);
		int asMuchHeightAsItNeeds = SWT.DEFAULT;
		form.setSize(form.computeSize(500, asMuchHeightAsItNeeds));
		return scrollableContainer;
	}

	private ScrolledComposite createScrollableContainer(Composite container) {
		ScrolledComposite result = new ScrolledComposite(container,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		return result;
	}

	private Composite addForm(Composite container) {
		Composite form = createFormContainer(container);

		for (FormElement element : elements) {

			if (element.type.equals("checkbox")) {
				addCheckBox(form, element);

			} else if (element.type.equals("string")) {
				addTextInput(form, element);
			}
		}
		return form;
	}

	private Composite createFormContainer(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;
		return composite;
	}

	private void addTextInput(Composite composite, FormElement element) {
		Label label = new Label(composite, SWT.NULL);
		label.setText(element.label);
		label.setToolTipText(element.tooltip);
		Text text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setData(element);
		idToFormElement.put(element.id, text);
		text.addFocusListener(new FocusListener() {

			private String oldValue;

			public void focusLost(FocusEvent event) {
				try {
					Text widget = (Text) event.widget;
					String newValue = widget.getText();
					if (!oldValue.equals(newValue)) {
						FormElement element = (FormElement) widget.getData();
						model.updateString(element.id, newValue);
						notifyUpdate();
					}
				} catch (Exception e) {
					log().error(e.getMessage(), e);
				}

			}

			public void focusGained(FocusEvent event) {
				try {
					Text widget = (Text) event.widget;
					oldValue = widget.getText();
					if (oldValue == null) {
						oldValue = "";
					}
				} catch (Exception e) {
					log().error(e.getMessage(), e);
				}
			}
		});
	}

	private void addCheckBox(Composite composite, FormElement element) {
		Label label = new Label(composite, SWT.NULL);
		label.setText(element.label);
		label.setToolTipText(element.tooltip);
		Button checkbox = new Button(composite, SWT.CHECK);
		checkbox.setData(element);
		checkbox.setSelection(element.defaultValue != null
				&& element.defaultValue.equals("true"));
		idToFormElement.put(element.id, checkbox);
		checkbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					Button checkbox = (Button) event.widget;
					// log().debug("widgetSelected <" + checkbox + ">");
					FormElement element = (FormElement) checkbox.getData();
					// log().debug("widgetSelected <" + element.id + ">");
					model.updateBoolean(element.id, checkbox.getSelection());
					// log().debug("widgetSelected <" + model.getString() +
					// ">");
					notifyUpdate();
					// log().debug("widgetSelected end");
				} catch (Exception e) {
					log().error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * parse the additional compiler arguments string and initalise the form
	 * elements
	 */
	public void updateFromString(String source) {
		try {
			ArgumentsParser parser = new ArgumentsParser(model);
			parser.parse(source);
			updateFormElements();
		} catch (DOMException e) {
			throw new RuntimeException("Error while updating from string.", e);
		}
	}

	private void updateFormElements() {
		for (Argument arg : model.arguments) {
			String id = arg.name;
			log().debug("updateFormElements <" + id + ">");
			FormElement element = findElement(id);
			if (element == null) {
				// ignore
				log().debug("element not found, id <" + id + ">");
			} else {
				if (element.type.equals("checkbox")) {
					updateCheckBox(arg, id);
				} else if (element.type.equals("string")) {
					updateTextInput(arg, id);
				}
			}
		}
	}

	private void updateTextInput(Argument arg, String id) {
		Text widget = (Text) idToFormElement.get(id);
		if (arg.values != null && arg.values.size() > 0) {
			String value = arg.values.get(0);
			if (value != null) {
				widget.setText(value);
			}
		}
	}

	private void updateCheckBox(Argument arg, String id) {
		Button widget = (Button) idToFormElement.get(id);
		widget.setSelection(arg.values == null
				|| arg.values.get(0).equals("true"));
	}

	private FormElement findElement(String id) {
		for (FormElement element : elements) {
			if (element != null && element.id != null && element.id.equals(id)) {
				return element;
			}
		}
		return null;
	}

	private void notifyUpdate() {
		log().debug("notifyUpdate");
		formChangeHandler.handleFormChange(getFormSettings());
	}

	/**
	 * get all the values from the form and return a string which can be used as
	 * the value for the additionalCompilerArguments attribute
	 * 
	 * @return
	 */
	private String getFormSettings() {
		ValueIsDifferentFromDefaultValuePredicate predicate = new ValueIsDifferentFromDefaultValuePredicate(defaultValues);
		String result = new FilteringArgumentsModelStringRenderer(model, predicate).render();
		log().debug("getFormSettings <" + result + ">");
		return result;
	}

	private Logger _log = null;

	private Logger log() {
		if (_log == null) {
			_log = Logger.getLogger(this.getClass());
		}
		return _log;
	}

	public void dispose() {

	}

	private class FormElement {
		public String label;
		public String tooltip;
		public String id;
		public String type;
		public String defaultValue;

		public FormElement(String type, String id, String label,
				String tooltip, String defaultValue) {
			this.type = type;
			this.id = id;
			this.label = label;
			this.tooltip = tooltip;
			this.defaultValue = defaultValue;
		}
	}

}
