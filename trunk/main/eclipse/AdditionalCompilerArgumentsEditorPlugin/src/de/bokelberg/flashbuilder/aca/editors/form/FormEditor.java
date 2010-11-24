package de.bokelberg.flashbuilder.aca.editors.form;

import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.DOMException;

import de.bokelberg.flashbuilder.aca.signals.Signal;

public class FormEditor extends ViewPart {

	final private ArgumentsModel model = new ArgumentsModel();

	final private FormElementConfig config = new FormElementConfig();

	final private WidgetLocator widgetLocator = new WidgetLocator();

	public Signal<String> formChangeSignal = new Signal<String>();

	private Composite mainView;
	private FormToolkit toolkit;

	public FormEditor(URL elementsConfiguration) {
		if (elementsConfiguration == null) {
			throw new RuntimeException(
					"ArgumentError: elementsConfiguration is null");
		}

		config.loadElementsConfiguration(elementsConfiguration);
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

	private Composite createView(Composite parent) {
		ScrolledForm scrolledForm = createScrollableContainer(parent);
		createForm(scrolledForm.getBody());
		return scrolledForm;
	}

	private ScrolledForm createScrollableContainer(Composite container) {
		ScrolledForm result = toolkit.createScrolledForm(container);
		return result;
	}

	private void createForm(Composite container) {
		container.setLayout(new GridLayout(3, false));
		for (FormElement element : config.elementsIterator()) {

			if (element.type.equals("checkbox")) {
				addOnOffInput(container, element);
			} else if (element.type.equals("string")) {
				addSingleStringInput(container, element);
			} else if (element.type.equals("strings")) {
				addMultipleStringsInput(container, element);
			} else {
				log().error(
						"Unexpected type of form element <" + element.type
								+ ">");
			}
		}
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
		// Composite sub = toolkit.createComposite(composite, SWT.BORDER);
		// sub.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// sub.setLayout(new GridLayout());

		addLabel(composite, element, new GridData());
		addAppendButton(composite, element);
		addText(composite, element);
	}

	private void addLabel(Composite composite, FormElement element) {
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		addLabel(composite, element, layoutData);
	}

	private void addLabel(Composite composite, FormElement element,
			GridData layoutData) {
		Label label = toolkit.createLabel(composite, element.label);
		label.setLayoutData(layoutData);
		label.setToolTipText(element.tooltip);
	}

	private void addCheckbox(Composite composite, FormElement element) {
		Button checkboxWidget = toolkit.createButton(composite, "", SWT.CHECK);
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

	private void addAppendButton(Composite composite, FormElement element) {
		Button toggleButtonWidget = toolkit.createButton(composite, "+=",
				SWT.TOGGLE | SWT.FLAT);
		toggleButtonWidget.setData(element);
		toggleButtonWidget
				.setToolTipText("values are appended to values which might have been loaded before.");
		// FormData formData = new FormData();
		// formData.right = new FormAttachment(100, 5);
		// checkboxWidget.setLayoutData(formData);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.END;
		toggleButtonWidget.setLayoutData(gridData);

		widgetLocator.addSubWidget(element.id, "append", toggleButtonWidget);
		toggleButtonWidget.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					Button toggleButton = (Button) event.widget;
					FormElement element = (FormElement) toggleButton.getData();
					toggleButton.setText(toggleButton.getSelection() ? " ="
							: "+=");
					updateModel(toggleButton, element);
				} catch (Exception e) {
					log().error(e.getMessage(), e);
				}
			}

			private void updateModel(Button toggleButton, FormElement element) {
				model.updateAssignmentOperator(element.id, toggleButton
						.getSelection());
				notifyUpdate();
			}
		});
	}

	private void addText(Composite composite, FormElement element) {
		String text = config.hasDefaultValue(element.id) ? config
				.getDefaultValue(element.id)
				+ "" : "";
		Text textWidget = toolkit.createText(composite, text, SWT.BORDER);
		textWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		textWidget.setData(element);
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

					if (element.type.equals("string")) {
						model.updateSingleString(element.id, newValue);
					} else {
						model.updateMultipleStrings(element.id, newValue);
					}
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
		Text textWidget = (Text) widgetLocator.getWidget(id);
		String value = getMultiStringValue(arg);
		if (value != null) {
			textWidget.setText(value);
		}
		Button toggleButtonWidget = (Button) widgetLocator.getSubWidget(id,
				"append");
		String assignmentOperator = arg.getAssignmentOperator();
		boolean selected = assignmentOperator != null
				&& assignmentOperator.equals("+=");
		toggleButtonWidget.setSelection(selected);
		toggleButtonWidget.setText(toggleButtonWidget.getSelection() ? "="
				: "+=");
	}

	private String getMultiStringValue(Argument arg) {
		if (arg.values == null || arg.values.size() == 0) {
			return null;
		}
		String separator = "";
		StringBuffer buf = new StringBuffer();
		for (String value : arg.values) {
			buf.append(separator);
			separator = ",";
			if (value.contains(" ")) {
				buf.append("\"");
				buf.append(value);
				buf.append("\"");
			} else {
				buf.append(value);
			}
		}
		return buf.toString();
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
		toolkit.dispose();
		super.dispose();
	}

	@Override
	public void createPartControl(Composite container) {
		if (container == null) {
			throw new RuntimeException("ArgumentError: container is null");
		}
		toolkit = new FormToolkit(container.getDisplay());
		mainView = createView(container);
	}

	@Override
	public void setFocus() {
		mainView.setFocus();
	}

}
