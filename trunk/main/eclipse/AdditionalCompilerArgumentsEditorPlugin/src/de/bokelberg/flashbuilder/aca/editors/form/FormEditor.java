package de.bokelberg.flashbuilder.aca.editors.form;

import java.net.URL;

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
import org.w3c.dom.DOMException;

import de.bokelberg.flashbuilder.aca.signals.Signal;

public class FormEditor {

	private static final int FORM_WIDTH = 600;

	final private Composite mainView;

	final private ArgumentsModel model = new ArgumentsModel();

	final private FormElementConfig config = new FormElementConfig();

	final private WidgetLocator widgetLocator = new WidgetLocator();

	public Signal<String> formChangeSignal = new Signal<String>();

	public FormEditor(Composite container, URL elementsConfiguration) {
		if (container == null) {
			throw new RuntimeException("ArgumentError: container is null");
		}
		if (elementsConfiguration == null) {
			throw new RuntimeException("ArgumentError: url is null");
		}

		config.loadElementsConfiguration(elementsConfiguration);
		mainView = createView(container);
	}

	public Composite getView() {
		return mainView;
	}

	public String getTitle() {
		return "FormEditor";
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

		for (FormElement element : config.elementsIterator()) {

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
		checkboxWidget.setSelection(config.hasDefaultValue(element.id)
				&& config.getDefaultValue(element.id).equals("true"));
		widgetLocator.addWidget(element.id, checkboxWidget);
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
		widgetLocator.addSubWidget(element.id, "append", checkboxWidget);
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
		if (config.hasDefaultValue(element.id)) {
			textWidget.setText(config.getDefaultValue(element.id) + "");
		}
		widgetLocator.addWidget(element.id, textWidget);
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
			FormElement element = config.findElement(id);
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
		Text textWidget = (Text) widgetLocator.getWidget(id);
		Object value = arg.getValue();
		if (value != null) {
			textWidget.setText(value + "");
		}
	}

	private void updateMultipleStringsInput(Argument arg, String id) {
		updateSingleStringInput(arg, id);
		Button checkboxWidget = (Button) widgetLocator.getSubWidget(id,
				"append");
		String assignmentOperator = arg.getAssignmentOperator();
		boolean selected = assignmentOperator != null
				&& assignmentOperator.equals("+=");
		checkboxWidget.setSelection(selected);
	}

	private void updateOnOffInput(Argument arg, String id) {
		Button checkboxWidget = (Button) widgetLocator.getWidget(id);
		Object value = arg.getValue();
		boolean selected = value != null && ((String) value).equals("true");
		checkboxWidget.setSelection(selected);
	}

	private void notifyUpdate() {
		log().debug("notifyUpdate");
		formChangeSignal.fire(getFormSettings());
	}

	/**
	 * get all the values from the form and return a string which can be used as
	 * the value for the additionalCompilerArguments attribute
	 * 
	 * @return
	 */
	private String getFormSettings() {
		Predicate<Argument> predicate = config.getFilterPredicate();
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

}
