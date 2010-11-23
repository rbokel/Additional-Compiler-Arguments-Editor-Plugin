package de.bokelberg.flashbuilder.aca.editors;

import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.bokelberg.flashbuilder.aca.Activator;
import de.bokelberg.flashbuilder.aca.Configuration;
import de.bokelberg.flashbuilder.aca.editors.form.FormEditor;
import de.bokelberg.flashbuilder.aca.editors.form.FormEditorNew;
import de.bokelberg.flashbuilder.aca.editors.form.FormView;
import de.bokelberg.flashbuilder.aca.editors.xml.XMLEditor;
import de.bokelberg.flashbuilder.aca.signals.Slot;
import de.bokelberg.flashbuilder.aca.utils.XMLUtil;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MultiPageEditor extends MultiPageEditorPart implements
		IResourceChangeListener {

	/** The text editor used in page 0. */
	private XMLEditor xmlEditor;

	/** The form editor used in page 1. */
	private FormEditorNew formEditor;

	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a xml editor.
	 */
	private void createPage0() {
		try {
			xmlEditor = new XMLEditor();
			int index = addPage(xmlEditor, getEditorInput());
			setPageText(index, xmlEditor.getTitle());
			setPartName(xmlEditor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(),
					"Error creating nested xml editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 1 of the multi-page editor, which contains a form editor.
	 */
	private void createPage1() {
		URL elementsConfiguration = getElementsConfiguration();
		formEditor = new FormEditorNew( elementsConfiguration);
		formEditor.createPartControl(getContainer());
		formEditor.formChangeSignal.addSlot(new Slot<String>() {
			
			public void onSignal(String newContent) {
				debug("handleFormChange");

				try {
					Document doc = getXMLDocument();
					Node node = getAdditionalCompilerArgumentsNode(doc);
					node.setTextContent(newContent);
					setXMLEditorContent(doc);
				} catch (Exception e) {
					
					RuntimeException re = new RuntimeException(
							"Error while updating xmlEditor", e);
					log().error("Error while updating xmlEditor", re);
					throw re;
				}
			}
		});
		int index = addPage(formEditor.getView());
		setPageText(index, formEditor.getTitle());
	}

	private URL getElementsConfiguration() {
		String path = Configuration.RESOURCE_FORMELEMENTS_XML;
		URL result = Activator.getDefault().getBundle().getResource(path);
		if (result == null) {
			throw new RuntimeException("Can't find configuration file <" + path
					+ ">");
		}
		return result;
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		IEditorPart editor = getEditor(0);
		editor.doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Updates the contents of the page when it is activated. TODO To keep the
	 * two editors in sync - What is the best way to do this? What happens if we
	 * move away from both editors and then press save all? What happens if we
	 * reload the content?
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 0) {
			updateXMLEditor();
		} else if (newPageIndex == 1) {
			updateFormEditor();
		} else {
			throw new RuntimeException("Unexpected pageIndex <" + newPageIndex
					+ ">");
		}
	}

	private void updateXMLEditor() {
		// TODO do we need it?
		// when we change the form, the xml is immediately updated
		// when we change the xml, ths xml is updated as well and as 
		// soon as we switch to the form view, the form is updated from 
		// the editor content
		// are there any other usecases that would make it necessary to 
		// update the xml editor?
	}

	private void updateFormEditor() {
		Document doc = getXMLDocument();
		Node node = getAdditionalCompilerArgumentsNode(doc);
		formEditor.updateFromString(node.getTextContent());
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) xmlEditor.getEditorInput())
								.getFile().getProject().equals(
										event.getResource())) {
							IEditorPart editorPart = pages[i]
									.findEditor(xmlEditor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	private Document getXMLDocument() {
		try {
			return XMLUtil.toXML(getEditorContentAsString());
		} catch (Exception e) {
			throw new RuntimeException("Error converting editor content to xml", e);
		}
	}

	private String getEditorContentAsString() {
		return xmlEditor.getDocumentProvider().getDocument(
				xmlEditor.getEditorInput()).get();
	}

	private Node getAdditionalCompilerArgumentsNode(Document doc) {
		return XMLUtil
				.findNode(doc,
						"/actionScriptProperties/compiler/@additionalCompilerArguments");
	}

	private void setXMLEditorContent(Document doc) {
		debug("setXMLEditorContent");
		String xmlString = XMLUtil.prettyPrint(doc);
		xmlEditor.getDocumentProvider().getDocument(xmlEditor.getEditorInput()).set(xmlString);
	}

	private Logger _log = null;

	private void debug(String msg) {
		log().debug(msg);
	}
	
	
	private Logger log()
	{
		if (_log == null) {
			_log = Logger.getLogger(this.getClass());
		}
		return _log;
	}

}
