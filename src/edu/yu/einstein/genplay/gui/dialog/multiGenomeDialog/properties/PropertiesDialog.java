/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesGlobalPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.FiltersData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.FiltersGlobalPanel;
import edu.yu.einstein.genplay.util.Images;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PropertiesDialog extends JDialog implements TreeSelectionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3713110227164397033L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;
	/** Height of the dialog */
	public static final	int					DIALOG_HEIGHT 		= 400;
	/** Text for GENERAL tree node */
	public static final		String			GENERAL 		= "General";
	/** Text for SETTINGS tree node */
	public static final		String			SETTINGS 		= "Settings";
	/** Text for FILES tree node */
	public static final		String			FILES 			= "Files";
	/** Text for INFORMATION tree node */
	public static final		String			INFORMATION 	= "Information";
	/** Text for STATISTICS tree node */
	public static final		String			STATISTICS 		= "Statistics";
	/** Text for FILTERS tree node */
	public static final		String			FILTERS 		= "Filters";
	/** Text for STRIPES tree node */
	public static final		String			STRIPES 		= "Stripes";


	/** Insets for the first title in top of the content panel */
	public static final 	Insets			FIRST_TITLE_INSET 	= new Insets(10, 10, 5, 0);
	/** Insets for titles of the content panel */
	public static final 	Insets			TITLE_INSET 		= new Insets(20, 10, 5, 0);
	/** Insets for panel of the content panel */
	public static final 	Insets			PANEL_INSET 		= new Insets(0, 20, 0, 0);


	private int				approved 			= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	private final Dimension regularContentDimension = new Dimension(600, DIALOG_HEIGHT);
	private final Dimension extraContentDimension = new Dimension(800, DIALOG_HEIGHT);

	private TreeContent 		treeContent;							// the tree manager
	private JTree 				tree;									// the tree of the dialog
	private JPanel 				contentPane;							// right part of the dialog
	private JPanel		 		validationPanel;						// the validation panel (ok/cancel buttons)
	private GeneralPanel 		generalPanel;							// the general information panel
	private SettingsPanel 		settingsPanel;							// the settings panel
	private StripesGlobalPanel 	stripesPanel;							// the stripes panel
	private FiltersGlobalPanel 	filtersPanel;							// the stripes panel


	/**
	 * Constructor of {@link PropertiesDialog}
	 */
	public PropertiesDialog () {
		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Tree (left part of the dialog)
		treeContent = new TreeContent();
		tree = treeContent.getTree();
		tree.addTreeSelectionListener(this);
		JScrollPane treeScroll = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		Dimension scrollDimension = new Dimension(200, DIALOG_HEIGHT);
		treeScroll.setPreferredSize(scrollDimension);
		treeScroll.setMinimumSize(scrollDimension);

		// Content panel (right part of the dialog)
		contentPane = new JPanel();
		contentPane.setPreferredSize(regularContentDimension);

		// Validation panel
		validationPanel = getValidationPanel();

		// Adds panels
		add(treeScroll, BorderLayout.WEST);
		add(contentPane, BorderLayout.CENTER);
		add(validationPanel, BorderLayout.SOUTH);


		// Creates the general panel
		generalPanel = new GeneralPanel();

		// Creates the settings panel
		settingsPanel = new SettingsPanel();

		// Creates the stripes panel
		stripesPanel = new StripesGlobalPanel();

		// Creates the filters panel
		filtersPanel = new FiltersGlobalPanel();

		// Dialog settings
		setTitle("Multi-Genome Project Properties");
		setIconImage(Images.getApplicationImage());
		setAlwaysOnTop(true);
		setResizable(false);
		setVisible(false);
		pack();
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		String accessor = MGDisplaySettings.getInstance().getVariousSettings().getDefaultDialogItem();
		return showDialog(parent, accessor);
	}


	/**
	 * Shows the component.
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details 
	 * @param accessor 	get into a specific node of the properties dialog
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent, String accessor) {
		// Sets the content panel
		if (accessor.equals(GENERAL)) {
			setScrollableCenterPanel(generalPanel);
		} else if (accessor.equals(SETTINGS)) {
			setScrollableCenterPanel(settingsPanel);
		} else if (accessor.equals(FILTERS)) {
			setCenterPanel(filtersPanel);
			//setCenterPanel(getEmptyPanel());
		} else if (accessor.equals(STRIPES)) {
			setCenterPanel(stripesPanel);
		}

		// Gets the tree path if exists and select it
		TreePath treePath = treeContent.getTreePath(accessor);
		if (treePath != null) {
			tree.setSelectionPath(treePath);
			tree.scrollPathToVisible(treePath);
		}

		// Sets dialog display options
		setLocationRelativeTo(parent);
		setModal(true);
		setVisible(true);

		return approved;
	}


	/**
	 * Sets the panel at the center of the dialog with the one given as parameter
	 * @param panel the panel to show at the center of the dialog
	 */
	protected void setCenterPanel (JPanel panel) {
		// Removes all content of the contentPane
		contentPane.removeAll();

		// Set the extra dimension for the content panel
		contentPane.setPreferredSize(extraContentDimension);

		// Set the panel gaps to zero
		((FlowLayout)(contentPane.getLayout())).setHgap(0);
		((FlowLayout)(contentPane.getLayout())).setVgap(0);

		// Add the panel to the content panel
		contentPane.add(panel);
		contentPane.repaint();
		validate();

		pack();

	}


	/**
	 * Sets the panel at the center of the dialog with the one given as parameter
	 * It first includes the panel in a scroll panel.
	 * @param panel the panel to show at the center of the dialog
	 */
	protected void setScrollableCenterPanel (JPanel panel) {
		// Set the panel to the right dimension
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(regularContentDimension);

		// Removes all content of the contentPane
		contentPane.removeAll();

		// Set the redular dimension for the content panel
		contentPane.setPreferredSize(regularContentDimension);

		// Set the panel gaps to zero
		((FlowLayout)(contentPane.getLayout())).setHgap(0);
		((FlowLayout)(contentPane.getLayout())).setVgap(0);

		// Add the panel to the content panel
		contentPane.add(scrollPane);
		contentPane.repaint();
		validate();

		pack();

	}


	/**
	 * TEST
	 * @return
	 */
	private JPanel getEmptyPanel () {
		JPanel emptyPane = new JPanel();
		emptyPane.add(new JLabel("Coming soon..."));
		return emptyPane;
	}


	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		Object nodeInfo = node.getUserObject();
		if (nodeInfo.equals(GENERAL)) {
			setScrollableCenterPanel(generalPanel);
		} else if (nodeInfo.equals(SETTINGS)) {
			setScrollableCenterPanel(settingsPanel);
		} else if (nodeInfo.equals(INFORMATION)) {
			VCFReader reader = retrieveReader(node.getParent().toString());
			setScrollableCenterPanel(new FileInformationPanel(reader));
		} else if (nodeInfo.equals(STATISTICS)) {
			//VCFReader reader = retrieveReader(node.getParent().toString());
			setScrollableCenterPanel(getEmptyPanel());
		} else if (nodeInfo.equals(FILTERS)) {
			setCenterPanel(filtersPanel);
			//setCenterPanel(getEmptyPanel());
		} else if (nodeInfo.equals(STRIPES)) {
			setCenterPanel(stripesPanel);
		}
	}


	/**
	 * Get a vcf reader object with a vcf file name.
	 * @param fileName 	the name of the vcf file
	 * @return			the reader
	 */
	private VCFReader retrieveReader (String fileName) {
		return ProjectManager.getInstance().getMultiGenomeProject().getReadersFromName(fileName);
	}


	/**
	 * Creates the panel that contains OK and CANCEL buttons
	 * @return the panel
	 */
	private JPanel getValidationPanel () {
		// Creates the ok button
		JButton jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		// Creates the cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = CANCEL_OPTION;
				setVisible(false);				
			}
		});

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}


	/**
	 * Set the settings panel with specific values
	 * @param settings
	 */
	public void setSettings (MGDisplaySettings settings) {
		// Settings panel
		settingsPanel.setSettings(settings.getVariousSettings().getDefaultDialogItem(),
				settings.getVariousSettings().getDefaultGroupText(),
				settings.getVariousSettings().getTransparencyPercentage(),
				settings.getVariousSettings().isShowLegend());

		// Filter settings panel
		filtersPanel.setSettings(settings.getFilterSettings().getFiltersList());
		filtersPanel.refresh();

		// Stripes settings panel
		stripesPanel.setSettings(settings.getStripeSettings().getStripesList());
		stripesPanel.refresh();
	}


	/**
	 * @return the default item dialog to show
	 */
	public String getDefaultItemDialog () {
		return settingsPanel.getDefaultItemDialog();
	}


	/**
	 * @return the default group text
	 */
	public String getDefaultGroupText () {
		return settingsPanel.getDefaultGroupText();
	}


	/**
	 * @return the transparency value
	 */
	public int getTransparency() {
		return settingsPanel.getTransparency();
	}


	/**
	 * @return the showLegend
	 */
	public boolean isShowLegend() {
		return settingsPanel.isShowLegend();
	}


	/**
	 * @return the optionValueList
	 */
	public List<Integer> getOptionValueList() {
		return settingsPanel.getOptionValueList();
	}


	/**
	 * @return the filters list
	 */
	public List<FiltersData> getFiltersData () {
		return filtersPanel.getFiltersData();
	}


	/**
	 * @return the filters list
	 */
	public List<StripesData> getStripesData () {
		return stripesPanel.getStripesData();
	}
	
	
	/**
	 * @return an array of Strings containing the 4 main items (GENERAL, SETTINGS, FILTERS, STRIPES)
	 */
	public static String[] getPropertiesDialogMainItems () {
		String[] items = {GENERAL, SETTINGS, FILTERS, STRIPES};
		return items;
	}
}
