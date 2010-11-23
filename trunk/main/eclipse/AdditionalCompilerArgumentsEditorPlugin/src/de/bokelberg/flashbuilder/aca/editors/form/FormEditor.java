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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
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

	private static final int FORM_WIDTH = 600;

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
		return new FormElement(type, id, label, tooltip);
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
		Composite form = createForm(scrollableContainer);
		scrollableContainer.setContent(form);
		int asMuchHeightAsItNeeds = SWT.DEFAULT;
		form.setSize(form.computeSize(FORM_WIDTH, asMuchHeightAsItNeeds));
		return scrollableContainer;
	}

	private ScrolledComposite createScrollableContainer(Composite container) {
		ScrolledComposite result = new ScrolledComposite(container,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		return result;
	}

	private Composite createForm(Composite container) {
		Composite form = createFormContainer(container);

		for (FormElement element : elements) {

			if (element.type.equals("checkbox")) {
				addOnOffInput(form, element);
			} else if (element.type.equals("string")) {
				addSingleStringInput(form, element);
			} else if (element.type.equals("strings")) {
				addMultipleStringsInput(form, element);
			} else {
				log().error(
						"Unexpected type of form element <" + element.type
								+ ">");
			}
		}
		return form;
	}

	private Composite createFormContainer(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		return composite;
	}

	private void addOnOffInput(Composite composite, FormElement element) {
		addLabel(composite, element);
		addCheckbox(composite, element);
	}

	private void addSingleStringInput(Composite composite, FormElement element) {
		addLabel(composite, element);
		addText(composite, element);
	}

	private void addMultipleStringsInput(Composite composite,
			FormElement element) {
		Composite sub = new Composite(composite, SWT.NONE);
		sub.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		FormLayout layout = new FormLayout();
		sub.setLayout(layout);

		addLabel(sub, element);
		addAppendCheckbox(sub, element);

		addText(composite, element);
	}

	private void addLabel(Composite composite, FormElement element) {
		Label label = new Label(composite, SWT.NULL);
		label.setText(element.label);
		label.setToolTipText(element.tooltip);
	}

	private void addCheckbox(Composite composite, FormElement element) {
		Button checkboxWidget = new Button(composite, SWT.CHECK);
		checkboxWidget.setData(element);
		checkboxWidget.setSelection(defaultValues.hasDefaultValue(element.id)
				&& defaultValues.getDefaultValue(element.id).equals("true"));
		idToFormElement.put(element.id, checkboxWidget);
		checkboxWidget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					Button checkboxWidget = (Button) event.widget;
					FormElement element = (FormElement) checkboxWidget
							.getData();
					updateModel(checkboxWidget, element);
				} catch (Exception e) {
					log().error(e.getMessage(), e);
					// don't rethrow as it gets sucked up
				}
			}

			private void updateModel(Button checkboxWidget, FormElement element) {
				model.updateBoolean(element.id, checkboxWidget.getSelection());
				notifyUpdate();
			}

		});
	}

	private void addAppendCheckbox(Composite composite, FormElement element) {
		Button checkboxWidget = new Button(composite, SWT.CHECK);
		checkboxWidget.setText("append");
		checkboxWidget.setData(element);
		checkboxWidget
				.setToolTipText("values are appended to existing values which might have ben loaded earlier.");
		FormData formData = new FormData();
		formData.right = new FormAttachment(100, 5);
		checkboxWidget.setLayoutData(formData);
		idToFormElement.put(getAppendId(element.id), checkboxWidget);
		checkboxWidget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					Button checkbox = (Button) event.widget;
					FormElement element = (FormElement) checkbox.getData();
					updateModel(checkbox, element);
				} catch (Exception e) {
					log().error(e.getMessage(), e);
				}
			}

			private void updateModel(Button checkbox, FormElement element) {
				model.updateAssignmentOperator(element.id, checkbox
						.getSelection());
				notifyUpdate();
			}
		});
	}

	private void addText(Composite composite, FormElement element) {
		Text textWidget = new Text(composite, SWT.BORDER);
		textWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textWidget.setData(element);
		if (defaultValues.hasDefaultValue(element.id)) {
			textWidget.setText(defaultValues.getDefaultValue(element.id) + "");
		}
		idToFormElement.put(element.id, textWidget);
		textWidget.addFocusListener(new FocusListener() {

			private String oldValue;

			public void focusLost(FocusEvent event) {
				try {
					Text textWidget = (Text) event.widget;
					FormElement element = (FormElement) textWidget.getData();
					updateModel(textWidget, element);
				} catch (Exception e) {
					log().error(e.getMessage(), e);
				}
			}

			private void updateModel(Text textWidget, FormElement element) {
				String newValue = textWidget.getText();
				if (!oldValue.equals(newValue)) {

					model.updateString(element.id, newValue);
					notifyUpdate();
				}
			}

			public void focusGained(FocusEvent event) {
				try {
					Text textWidget = (Text) event.widget;
					oldValue = textWidget.getText();
					if (oldValue == null) {
						oldValue = "";
					}
				} catch (Exception e) {
					log().error(e.getMessage(), e);
				}
			}
		});
	}

	private void updateFormElements() {
		for (Argument arg : model.getArguments()) {
			String id = arg.getName();
			log().debug("updateFormElements <" + id + ">");
			FormElement element = findElement(id);
			if (element == null) {
				// ignore
				log().warn("element not found, id <" + id + ">");
			} else {
				if (element.type.equals("checkbox")) {
					updateOnOffInput(arg, id);
				} else if (element.type.equals("string")) {
					updateSingleStringInput(arg, id);
				} else if (element.type.equals("strings")) {
					updateMultipleStringsInput(arg, id);
				} else {
					throw new RuntimeException(
							"updateFormElements: Unexpected element type <"
									+ element.type + "> id <" + id + ">");
				}
			}
		}
	}

	private void updateSingleStringInput(Argument arg, String id) {
		Text textWidget = (Text) idToFormElement.get(id);
		Object value = arg.getValue();
		if (value != null) {
			textWidget.setText(value + "");
		}
	}

	private void updateMultipleStringsInput(Argument arg, String id) {
		updateSingleStringInput(arg, id);
		Button checkboxWidget = (Button) idToFormElement.get(getAppendId(id));
		String assignmentOperator = arg.getAssignmentOperator();
		boolean selected = assignmentOperator != null
				&& assignmentOperator.equals("+=");
		checkboxWidget.setSelection(selected);
	}

	private void updateOnOffInput(Argument arg, String id) {
		Button checkboxWidget = (Button) idToFormElement.get(id);
		Object value = arg.getValue();
		boolean selected = value != null && ((String) value).equals("true");
		checkboxWidget.setSelection(selected);
	}

	private String getAppendId(String id) {
		return id + "_append";
	}

	private FormElement findElement(String id) {
		for (FormElement element : elements) {
			if (element != null && element.id != null && element.id.equals(id)) {
				return element;
			}
		}
		// ignore elements that we can not find
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
		Predicate<Argument> predicate = new ValueIsDifferentFromDefaultValuePredicate(
				defaultValues);
		String result = new FilteringArgumentsModelStringRenderer(model,
				predicate).render();
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

		public FormElement(String type, String id, String label, String tooltip) {
			this.type = type;
			this.id = id;
			this.label = label;
			this.tooltip = tooltip;
		}
	}

}
