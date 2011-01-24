package de.tud.stg.tigerseye.core.preferences;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tud.stg.popart.eclipse.editor.PopartEditorUtils;
import de.tud.stg.tigerseye.core.DSLDefinition;
import de.tud.stg.tigerseye.core.DSLKey;
import de.tud.stg.tigerseye.core.NoLegalPropertyFound;
import de.tud.stg.tigerseye.core.PreferenceDSL;
import de.tud.stg.tigerseye.core.TigerseyeCore;

/**
 * TigerseyeDSLsPreferencesPage is the DSL languages configuration page. It provides
 * the configuration of registered DSLs. Such as defining which file extension
 * has to be interpreted as what DSL. <br>
 * Additionally, if supported by the DSL, its keywords are listed.
 * 
 * @author Yevgen Fanshil
 * @author Leonid Melnyk
 * @author Leo Roos
 */
public class TigerseyeDSLsPreferencesPage extends PreferencePage implements
	IWorkbenchPreferencePage {
    private static final Logger logger = LoggerFactory
	    .getLogger(TigerseyeDSLsPreferencesPage.class);

    private Table languagesTable = null;

    private Button editButton;

    private Table declaredKeywordsTable;

    private static final int swtStyle = SWT.LEAD | SWT.WRAP;

    /**
     * The "data model" of this preference page. Every property change is made
     * to the objects of this list.
     */
    final private List<PrefDSL> dslList = new ArrayList<PrefDSL>();

    /**
     * Instance cache of the check box buttons of each DSL
     */
    private final HashMap<PrefDSL, Button> checkBoxMap = new HashMap<PrefDSL, Button>();

    private final HashMap<PrefDSL, Text> extensionTextMap = new HashMap<PrefDSL, Text>();

    private PrefDSL currentlyEditedDSL;

    @Override
    public void init(IWorkbench workbench) {
	this.setPreferenceStore(TigerseyeCore.getPreferences());
	setDescription("Languages Preference Page");
    }

    @Override
    protected Control createContents(Composite parent) {

	Composite preferencePageComposite = new Composite(parent, SWT.NONE);

	GridLayout innerLayout = new GridLayout();
	innerLayout.numColumns = 1;
	preferencePageComposite.setLayout(innerLayout);

	Group languageConfigurationGroup = new Group(preferencePageComposite,
		SWT.NONE);
	languageConfigurationGroup.setText("Language Configuration");
	languageConfigurationGroup.setLayoutData(new GridData(SWT.FILL,
		SWT.FILL, true, false));
	languageConfigurationGroup.setLayout(new GridLayout());

	Composite popartTableComposite = new Composite(
		languageConfigurationGroup, SWT.NONE);
	GridData popartTableData = new GridData(SWT.FILL, SWT.FILL, true, false);
	GridLayout popartTableLayout = new GridLayout(2, false);
	popartTableComposite.setLayout(popartTableLayout);
	popartTableComposite.setLayoutData(popartTableData);

	languagesTable = getConfiguredTable(popartTableComposite, 339,
		SWT.DEFAULT);

	languagesTable.addSelectionListener(new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (currentlyEditedDSL != null)
		    finishEditingExtension(currentlyEditedDSL);
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		editDSL();
	    }
	});

	Composite menuComposite = new Composite(popartTableComposite, SWT.NONE);
	menuComposite.setLayout(new GridLayout(1, false));

	editButton = getPushButton(menuComposite, "Set Extension");
	editButton.setAlignment(SWT.UP);
	Button setAllActive = getPushButton(menuComposite, "All Active");
	Button deselectAll = getPushButton(menuComposite, "All Inactive");

	// @formatter:off
	editButton.
		addSelectionListener(new SelectionAdapter() {@Override public void widgetSelected(SelectionEvent e)
		{editDSL();}});
	setAllActive.
		addSelectionListener(new SelectionAdapter() {@Override public void widgetSelected(SelectionEvent e)
		{setAllCheckBoxState(true);}});
	deselectAll.
		addSelectionListener(new SelectionAdapter() {@Override public void widgetSelected(SelectionEvent e)
		{setAllCheckBoxState(false);}});
	//@formatter:on

	declaredKeywordsTable = getConfiguredTable(languageConfigurationGroup,
		SWT.DEFAULT, 70);
	initColumns();
	initializeTableContent();
	initOnTableRowSelect();
	enableDependingSwtElements(false);
	updateValidState();
	return preferencePageComposite;
    }

    private Table getConfiguredTable(Composite popartTableComposite,
	    int widthHint, int heightHint) {
	Table table = new Table(popartTableComposite, SWT.BORDER | SWT.H_SCROLL
		| SWT.V_SCROLL);
	table.setLinesVisible(true);
	table.setHeaderVisible(true);
	GridData tableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
	tableLayoutData.minimumHeight = 10;
	tableLayoutData.widthHint = widthHint;
	tableLayoutData.heightHint = heightHint;
	table.setLayoutData(tableLayoutData);
	return table;
    }

    private Button getPushButton(Composite menuComposite, String buttonText) {
	Button selectAllActive = new Button(menuComposite, SWT.PUSH);
	selectAllActive.setLayoutData(newButtonLayoutData());
	selectAllActive.setText(buttonText);
	return selectAllActive;
    }

    private GridData newButtonLayoutData() {
	final GridData gd_editButton = new GridData(SWT.LEFT, SWT.TOP, false,
		false);
	gd_editButton.widthHint = 106;
	return gd_editButton;
    }

    private void initializeTableContent() {
	List<DSLDefinition> dslDefinitions = TigerseyeCore
		.getLanguageProvider().getDSLDefinitions();
        languagesTable.removeAll();
        dslList.clear();
        for (DSLDefinition dslDefinition : dslDefinitions) {
            PrefDSL propDSL = new PrefDSL(dslDefinition, getPreferenceStore());
            dslList.add(propDSL);
        }
        rebuildTableItems();
    }

    private void initColumns() {

	final TableColumn nameCol = new TableColumn(languagesTable, swtStyle);
	nameCol.setWidth(100);
	nameCol.setText("Name");

	final TableColumn isActiveCol = new TableColumn(languagesTable,
		swtStyle);
	isActiveCol.setWidth(50);
	isActiveCol.setText("Active");

	final TableColumn dslExtensionNameCol = new TableColumn(languagesTable,
		swtStyle);
	dslExtensionNameCol.setWidth(100);
	dslExtensionNameCol.setText(".*.dsl");

	final TableColumn interpreterClassCol = new TableColumn(languagesTable,
		swtStyle);
	interpreterClassCol.setWidth(100);
	interpreterClassCol.setText("Interpreter Class");

	final TableColumn newKeywordTableColumn = new TableColumn(
		declaredKeywordsTable, swtStyle);
	newKeywordTableColumn.setWidth(100);
	newKeywordTableColumn.setText("Name");

	final TableColumn newKeqwordTableColumn_1 = new TableColumn(
		declaredKeywordsTable, swtStyle);
	newKeqwordTableColumn_1.setWidth(65);
	newKeqwordTableColumn_1.setText("Return");

	final TableColumn newKeywordTableColumn_2 = new TableColumn(
		declaredKeywordsTable, swtStyle);
	newKeywordTableColumn_2.setWidth(221);
	newKeywordTableColumn_2.setText("Parameters");

    }

    private void initializeExtensionTextItem(int column, final PrefDSL propDSL,
            TableItem item) {
        TableEditor tableEditor;
        tableEditor = new TableEditor(languagesTable);
        final Text extension = new Text(languagesTable, SWT.LEAD);
        extension.setText(propDSL.getExtension());
        extension.addFocusListener(new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            finishEditingExtension(propDSL);
        }
        });
        extension.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent e) {
            /*
             * won't work in FocusListener#focusGained(), since a
             * mouseDown is Directly followed, which implicitly performs
             * a Text#clearSelection() before #mouseDown(..) is called.
             */
            extension.selectAll();
        }
        });
        tableEditor.setEditor(extension, item, column);
        tableEditor.grabHorizontal = true;
        extensionTextMap.put(propDSL, extension);
        extension.setVisible(false);
    }

    private void initializeCheckBoxControl(int column, final PrefDSL propDSL,
            TableItem item) {
        TableEditor tableEditor = new TableEditor(languagesTable);
        final Button button = new Button(languagesTable, SWT.CHECK);
        button.setSelection(propDSL.isActive());
        button.pack();
        button.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            propDSL.setIsActive(button.getSelection());
            updateValidState();
        }
        });
        tableEditor.setEditor(button, item, column);
        tableEditor.minimumWidth = 20;
        checkBoxMap.put(propDSL, button);
    }

    /**
     * Defines the actions taken when on the language table a row is selected.
     */
    private void initOnTableRowSelect() {
        languagesTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
        	enableDependingSwtElements(true);
        	declaredKeywordsTable.removeAll();
        	if (languagesTable.getSelectionIndex() >= 0) {
        	    TableItem selectedItem = languagesTable.getSelection()[0];
        	    PrefDSL popartLang = (PrefDSL) selectedItem.getData();
        	    setDeclaredMethodsList(popartLang.getDsl());
        	}
            }
        });
    }

    private void setAllCheckBoxState(boolean isActive) {
        Set<Entry<PrefDSL, Button>> values = checkBoxMap.entrySet();
        for (Entry<PrefDSL, Button> entry : values) {
            entry.getKey().setIsActive(isActive);
            entry.getValue().setSelection(isActive);
        }
    }

    private void editDSL() {
        TableItem[] selection = languagesTable.getSelection();
        if ((selection == null) || (selection.length != 1)) {
            this.enableDependingSwtElements(false);
            return;
        }
        TableItem tableItem = selection[0];
        PrefDSL dsl = (PrefDSL) tableItem.getData();
        Text text = this.extensionTextMap.get(dsl);
        text.setVisible(true);
        this.currentlyEditedDSL = dsl;
    }

    private void refreshTableItem(PrefDSL dsl) {
	int rowIndex = dsl.getRowIndex();
	TableItem item = languagesTable.getItem(rowIndex);
	setTableItemContent(dsl, item);
	languagesTable.redraw();
    }

    private void rebuildTableItems() {
	List<PrefDSL> list = dslList;
	languagesTable.removeAll();
	for (int i = 0; i < list.size(); i++) {
	    PrefDSL coreDSL = list.get(i);
	    coreDSL.setRowIndex(i);
	    setNewItemTo(coreDSL, i);
	}
    }

    private void setNewItemTo(PrefDSL coreDSL, int index) {
	TableItem newItem = new TableItem(languagesTable, SWT.BORDER, index);
	setTableItemContent(coreDSL, newItem);
    }

    private String getExtensionColumnValue(PrefDSL dsl) {
	String extension;
	extension = dsl.getExtension();

	return extension;
    }

    private void setTableItemContent(final PrefDSL propDSL, TableItem item) {
	item.setText(0, propDSL.getDsl().getDslName());
	addCheckBoxControl(1, propDSL, item);
	item.setText(2, getExtensionColumnValue(propDSL));
	addExtensionTextFieldControl(2, propDSL, item);
	item.setText(3, propDSL.getDsl().getClassPath());
	item.setData(propDSL);
    }

    /*
     * Need to cache the TableEditor instance since the creation of a new
     * instance is expensive and causes a noticeable delay until the updated new
     * graphical representation is visible.
     */
    private void addCheckBoxControl(int column, final PrefDSL propDSL,
	    TableItem item) {
	Button oldButton = checkBoxMap.get(propDSL);
	if (oldButton == null) {
	    initializeCheckBoxControl(column, propDSL, item);
	} else {
	    oldButton.setSelection(propDSL.isActive());
	}
    }

    private void addExtensionTextFieldControl(int column,
	    final PrefDSL propDSL, TableItem item) {
	Text text = extensionTextMap.get(propDSL);
	if (text == null) {
	    initializeExtensionTextItem(column, propDSL, item);
	} else {
	    text.setText(propDSL.getExtension());
	}
    }

    private void finishEditingExtension(final PrefDSL propDSL) {
	Text extension = extensionTextMap.get(propDSL);
	propDSL.setExtension(extension.getText());
	extension.setVisible(false);
	refreshTableItem(propDSL);
	currentlyEditedDSL = null;
	updateValidState();
    }

    /**
     * This method sets temporal enabled elements.
     * 
     * @param enable
     *            shows whether elements are enabled or not.
     */
    private void enableDependingSwtElements(boolean enable) {
	editButton.setEnabled(enable);
	declaredKeywordsTable.setEnabled(enable);
    }

    @Override
    public boolean performOk() {
	for (PrefDSL dsl : this.dslList) {
	    this.storeDSL(dsl);
	}
	if (logger.isDebugEnabled()) {
	    outputAllPropertiesInStoreToLog();
	}
	TigerseyeCore.updateTigerseyeClassPaths();
	return super.performOk();
    }

    @Override
    protected void performApply() {
	this.performOk();
	this.rebuildTableItems();
	super.performApply();
    }

    private void outputAllPropertiesInStoreToLog() {
	try {
	    StringBuilder formatted = new StringBuilder();
	    ArrayList<String> listedProperties = getListedProperties();
	    for (String line : listedProperties) {
		formatted.append(line).append("\n");
	    }
	    logger.trace("Found {} properties: \n{}", listedProperties.size(),
		    formatted);
	} catch (BackingStoreException e) {
	    logger.error("Unexpected exception", e);
	}
    }

    private ArrayList<String> getListedProperties()
            throws BackingStoreException {
        ArrayList<String> all = new ArrayList<String>();
        IEclipsePreferences[] preferenceNodes = ((ScopedPreferenceStore) getPreferenceStore())
        	.getPreferenceNodes(true);
        for (IEclipsePreferences iEclipsePreferences : preferenceNodes) {
            all.add("Next types: " + iEclipsePreferences);
            String[] keys = iEclipsePreferences.keys();
            ArrayList<String> lines = new ArrayList<String>();
            for (String string : keys) {
        	StringBuilder line = new StringBuilder();
        	line.append("key ")
        		.append(string)
        		.append("\tvalue ")
        		.append(iEclipsePreferences.get(string, "NoValueFound"));
        	lines.add(line.toString());
            }
            Collections.sort(lines);
            all.addAll(lines);
        }
        return all;
    }

    private void storeDSL(PrefDSL dsl) {
	if (!dsl.needsStoring()) {
	    logger.trace(
		    "dsl >>{}<< not changed aborting modification of property store",
		    dsl);
	} else {
	    dsl.store();
	    logger.trace("updated store with dsl {}", dsl);
	}
    }

    @Override
    protected void performDefaults() {
	List<PrefDSL> dsls = this.dslList;
	for (PrefDSL dsl : dsls) {
	    DSLDefinition dslDefinition = dsl.getDsl();
	    String defExt = DSLKey.EXTENSION.getDefault(dslDefinition,
		    getPreferenceStore());
	    dsl.setExtension(defExt);

	    boolean activeKey = DSLKey.LANGUAGE_ACTIVE.getDefault(
		    dslDefinition, getPreferenceStore());
	    dsl.setIsActive(activeKey);
	}
	rebuildTableItems();
	super.performDefaults();
    }

    private void updateValidState() {
	PrefDSL[] prefDSLs = this.dslList.toArray(new PrefDSL[0]);
	for (int i = 0; i < prefDSLs.length; i++) {
	    PrefDSL prefDSL = prefDSLs[i];
	    if (prefDSL.isActive()) {
		if (existsSecondActiveDSLOfSameExtension(
			prefDSL.getExtension(), prefDSLs, i + 1)) {
		    setInvalidState("At most one DSL of the same extension may be active at the same time.");
		    return;
		}
	    }
	}
	setValidState();
    }

    private boolean existsSecondActiveDSLOfSameExtension(String setExtension,
            PrefDSL[] prefDSLs, int beginningIndex) {
        for (int j = beginningIndex; j < prefDSLs.length; j++) {
            PrefDSL dsl = prefDSLs[j];
            if (dsl.isActive()) {
        	boolean equals = setExtension.equals(dsl.getExtension());
        	if (equals)
        	    return true;
            }
        }
        return false;
    }

    private void setInvalidState(String msg) {
	setValid(false);
	setErrorMessage(msg);
    }

    private void setValidState() {
        setValid(true);
        setErrorMessage(null);
    }

    static class PrefDSL extends PreferenceDSL {
    
        public int rowIndex;
        private String extension;
        private Boolean isActive;
    
        public PrefDSL(DSLDefinition dsl, IPreferenceStore store) {
            super(dsl, store);
            this.rowIndex = Integer.MIN_VALUE;
        }
    
        public void setRowIndex(int i) {
            this.rowIndex = i;
        }
    
        public int getRowIndex() {
            return rowIndex;
        }
    
        @Override
        public void store() {
            getDsl().setValue(DSLKey.LANGUAGE_ACTIVE, isActive);
            getDsl().setValue(DSLKey.EXTENSION, extension);
            super.store();
        }
    
        public String getExtension() {
            if (extension == null) {
        	try {
        	    extension = getExtensionFromStore();
        	} catch (NoLegalPropertyFound e) {
        	    extension = "";
        	}
            }
            return extension;
        }
    
        public String getExtensionFromStore() throws NoLegalPropertyFound {
            return getDsl().getValue(DSLKey.EXTENSION);
        }
    
        public void setExtension(String extension) {
            setNeedsStoring();
            this.extension = extension;
        }
    
        public Boolean getIsActiveFromStore() throws NoLegalPropertyFound {
            return getDsl().getValue(DSLKey.LANGUAGE_ACTIVE);
        }
    
        public Boolean isActive() {
            if (isActive == null) {
        	try {
        	    isActive = getIsActiveFromStore();
        	} catch (NoLegalPropertyFound e) {
        	    isActive = false;
        	}
            }
            return isActive;
        }
    
        public void setIsActive(boolean isActive) {
            setNeedsStoring();
            this.isActive = isActive;
        }
    
        @Override
        public String toString() {
            return super.toString() + " ;ext: " + extension;
        }
    }

    /**
     * TODO move functionalities of this method solely responsible for
     * extracting keywords to separate class. It is probable that this might be
     * useful for another client.
     * 
     * This method gets a list of methods from an external class from
     * class-path.
     * 
     * @param language
     *            fill the table with keywords of this language
     */
    private void setDeclaredMethodsList(DSLDefinition language) {
    
        String externalClassPath = language.getClassPath();
        String contributorSymbolicName = language.getContributorSymbolicName();
    
        declaredKeywordsTable.removeAll();
    
        // Read all public declared Fields from external class
        Field[] publicDeclaredFields = PopartEditorUtils
        	.getDeclaredLiteralKeywords(contributorSymbolicName,
        		externalClassPath);
        if (publicDeclaredFields != null) {
            for (Field declaredField : publicDeclaredFields) {
        	TableItem tableItem = null;
    
        	// Add new keyword to table
        	tableItem = new TableItem(declaredKeywordsTable, SWT.BORDER);
    
        	tableItem.setText(0, declaredField.getName());
        	tableItem.setText(1, declaredField.getType().getSimpleName());
        	tableItem.setText(2, "(native literal)");
    
            }
        }
    
        Method[] publicDeclaredMethods = PopartEditorUtils
        	.printDeclaredMethodKeywords(contributorSymbolicName,
        		externalClassPath);
    
        // Read all getter setter from external class
        Set<String> fieldAccessors = new HashSet<String>();
        Set<String> fieldAccessorTypes = new HashSet<String>();
        if (publicDeclaredMethods != null) {
    
            for (Method declaredMethod : publicDeclaredMethods) {
    
        	// replace the Java coding conventions prefix from the keyword
        	// name
        	if (declaredMethod.getName().startsWith("get")) {
        	    fieldAccessors.add(declaredMethod.getName().substring(3));
        	    fieldAccessorTypes.add(declaredMethod.getReturnType()
        		    .getName());
        	}
        	if (declaredMethod.getName().startsWith("set")) {
        	    fieldAccessors.add(declaredMethod.getName().substring(3));
        	    fieldAccessorTypes.add(declaredMethod.getReturnType()
        		    .getName());
        	}
        	if (declaredMethod.getName().startsWith("is")) {
        	    if ((declaredMethod.getReturnType().isPrimitive() && (declaredMethod
        		    .getReturnType() == boolean.class))
        		    || (declaredMethod.getReturnType() == Boolean.class)) {
        		fieldAccessors.add(declaredMethod.getName()
        			.substring(2));
        		fieldAccessorTypes.add(declaredMethod.getReturnType()
        			.getName());
        	    }
        	}
            }
    
            Iterator<String> it = fieldAccessors.iterator();
            Iterator<String> itTypes = fieldAccessorTypes.iterator();
            while (it.hasNext() && itTypes.hasNext()) {
        	String item = it.next();
        	String itemType = itTypes.next();
    
        	TableItem tableItem = null;
    
        	// Add new keyword to table
        	tableItem = new TableItem(declaredKeywordsTable, SWT.BORDER);
    
        	tableItem.setText(0, item);
        	tableItem.setText(1, itemType);
        	tableItem.setText(2, "(literal defined by accessor)");
            }
        }
    
        // Read all public declared methods from external class
        if (publicDeclaredMethods != null) {
            for (Method declaredMethod : publicDeclaredMethods) {
        	TableItem tableItem = null;
    
        	// Add new keyword to table
        	tableItem = new TableItem(declaredKeywordsTable, SWT.BORDER);
    
        	tableItem.setText(0, declaredMethod.getName());
        	tableItem.setText(1, declaredMethod.getReturnType()
        		.getSimpleName());
    
        	StringBuilder parameters = new StringBuilder();
        	for (int i = 0; i < declaredMethod.getParameterTypes().length; i++) {
        	    if (i > 0) {
        		parameters = parameters.append(", ");
        	    }
        	    parameters = parameters.append(declaredMethod
        		    .getParameterTypes()[i].getSimpleName().toString());
        	}
    
        	tableItem.setText(2, parameters.toString());
            }
        }
    
    }

}