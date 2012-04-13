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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.Utils;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.ListDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.IDEditors.IDEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.IDEditors.IDFlagEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.IDEditors.IDNumberEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.IDEditors.IDStringEditor;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class FiltersEditingPanel extends EditingPanel<FiltersData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = 1002708176297238005L;

	// File panel components
	private JLabel					jlFile;				// Label to display the file name
	private ListDialog<VCFReader> 	fileListDialog;		// The file list dialog
	private JButton 				showFileListButton;	// The button to show the file list dialog

	// ID panel components
	private JLabel					jlID;				// Label to display the ID
	private ListDialog<String> 		IDListDialog;		// The ID list dialog
	private JButton 				showIDListButton;	// The button to show the ID list dialog

	// Filter panel components
	private JPanel					filterPanel;		// Panel that contains all necessary elements for selecting a filter
	private IDEditor				filterEditor;		// The filter editor (creates panel for the filter and return the filter)

	// Others
	private VCFReader 					currentVCFReader;		// the current VCF reader
	private VCFHeaderType 				currentID;				// the current header ID
	private Map<VCFHeaderType, String> 	idMap;					// map between ID and their full description (as shown in the jlist)
	private Map<VCFColumnName, String>	nonIdMap;				// map between the non ID (some ALT/FILTER and QUAL) and their full description


	/**
	 * Constructor of {@link FiltersEditingPanel}
	 */
	protected FiltersEditingPanel () {
		super();

		nonIdMap = new HashMap<VCFColumnName, String>();
		nonIdMap.put(VCFColumnName.ALT, VCFColumnName.ALT + ": Alternative value");
		nonIdMap.put(VCFColumnName.QUAL, VCFColumnName.QUAL + ": Quality value");
		nonIdMap.put(VCFColumnName.FILTER, VCFColumnName.FILTER + ": Filter value");


		// Panel title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = firstInset;
		add(Utils.getTitleLabel("Editing"), gbc);

		// File selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		JLabel label = Utils.getTitleLabel("VCF File");
		Dimension dimension = new Dimension(100, label.getFontMetrics(label.getFont()).getHeight());
		label.setPreferredSize(dimension);
		label.setMinimumSize(dimension);
		add(label, gbc);

		// File selection box
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getFilePanel(), gbc);

		// ID selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("ID"), gbc);

		// ID selection list
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getIDPanel(), gbc);

		// Filter selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Filter"), gbc);

		// Filter selection panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(resetFilterPanel(), gbc);

		// Track selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Tracks"), gbc);

		// Track selection panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		gbc.weighty = 1;
		add(getTrackSelectionPanel(), gbc);

		// Validation panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.weighty = 0;
		add(getValidationPanel(), gbc);
	}


	@Override
	public void refresh () {
		currentVCFReader = null;
		currentID = null;
		idMap = null;

		jlFile.setText("<html><i>select -></i></html>");
		jlFile.setForeground(Colors.GREY);

		jlID.setText("<html><i>select -></i></html>");
		jlID.setForeground(Colors.GREY);
		showIDListButton.setEnabled(false);

		resetFilterPanel();
		filterPanel.repaint();
		validate();

		selectedTracks.setModel(new DefaultListModel());
		getApplyButton().setEnabled(false);
	}


	/**
	 * Creates a panel containing the vcf file name and a button to select one
	 * @return the file panel
	 */
	private JPanel getFilePanel () {
		// Creates the selection ID dialog
		fileListDialog = new ListDialog<VCFReader>("Select a file");

		// Instantiates the label
		jlFile = getDefaultLabel("select ->");

		// Instantiates the button
		showFileListButton = new JButton("+");

		// Gets the panel
		JPanel panel = createPanelSelection(jlFile, showFileListButton);

		// Initializes the button for the selection ID dialog
		showFileListButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<VCFReader> readerList = ProjectManager.getInstance().getMultiGenomeProject().getAllReaders();
				List<VCFReader> list = new ArrayList<VCFReader>();
				for (VCFReader reader: readerList) {
					list.add(reader);
				}
				if (fileListDialog.showDialog(getRootPane(), list) == ListDialog.APPROVE_OPTION){
					VCFReader newVCFReader = fileListDialog.getSelectedElement();
					if (currentVCFReader == null) {
						setCurrentReader(newVCFReader);
					}
					if (!currentVCFReader.getFile().equals(newVCFReader.getFile())) {
						setCurrentReader(newVCFReader);
					}
				}
			}
		});

		// Return the panel
		return panel;
	}


	/**
	 * Creates a panel containing the ID value and a button to select an ID
	 * @return the ID panel
	 */
	private JPanel getIDPanel () {
		// Creates the selection ID dialog
		IDListDialog = new ListDialog<String>("Select an ID");

		// Instantiates the label
		jlID = getDefaultLabel("select ->");

		// Instantiates the button
		showIDListButton = new JButton("+");

		// Gets the panel
		JPanel panel = createPanelSelection(jlID, showIDListButton);

		// Initializes the button for the selection ID dialog
		showIDListButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (idMap != null) {
					List<String> list = new ArrayList<String>();
					list.add(nonIdMap.get(VCFColumnName.ALT));
					list.add(nonIdMap.get(VCFColumnName.QUAL));
					list.add(nonIdMap.get(VCFColumnName.FILTER));
					for (String s: idMap.values()) {
						list.add(s);
					}
					Collections.sort(list);
					if (IDListDialog.showDialog(getRootPane(), list) == ListDialog.APPROVE_OPTION){
						String fullDetail = IDListDialog.getSelectedElement();		// get the value
						jlID.setText(fullDetail);
						jlID.setToolTipText(fullDetail);
						jlID.setForeground(getForeground());
						String text = fullDetail.substring(0, fullDetail.indexOf(":"));
						VCFColumnName category = VCFColumnName.getColumnNameFromString(text);
						VCFHeaderType newID = null;										// instanciate the ID
						for (VCFHeaderType id: idMap.keySet()) {						// scan all full detail ID to find the right ID object
							if (fullDetail.equals(idMap.get(id))) {
								newID = id;
							}
						}
						if (currentID == null) {										// if the current id does not exist yet
							setCurrentID(newID, category);								// we set it
						} else if (newID == null) {
							setCurrentID(null, category);								// we set it
						} else if (!currentID.getId().equals(newID.getId())) {			// if the new id is different than the current one
							setCurrentID(newID, category);								// we set it
						}
					}
				}
			}
		});

		// Return the panel
		return panel;
	}


	/**
	 * Creates a label/button panel (label on the left, button to set the label on the right)
	 * @param label		the label
	 * @param button	the button
	 * @return			the panel
	 */
	private JPanel createPanelSelection (JLabel label, JButton button) {
		// Creates the panel
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		int height = panel.getFontMetrics(panel.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(180, height);
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);

		// Set the button for the selection dialog
		button.setMargin(new Insets(0, 0, 0, 0));
		Dimension buttonDimension = new Dimension(height, height);
		button.setPreferredSize(buttonDimension);
		button.setMinimumSize(buttonDimension);

		// Add label and button to the panel
		panel.add(label, BorderLayout.CENTER);
		panel.add(button, BorderLayout.EAST);

		// Return the panel
		return panel;
	}


	/**
	 * Creates a label with a gray italic text
	 * @param text	text of the label
	 * @return		the label
	 */
	private JLabel getDefaultLabel (String text) {
		JLabel label = new JLabel("<html><i>" + text + "</i></html>");
		label.setForeground(Colors.GREY);
		return label;
	}


	/**
	 * Sets the current vcf file reader.
	 * Creates a map between ID and their description for the ID list.
	 * @param reader the new vcf file reader
	 */
	private void setCurrentReader (VCFReader reader) {
		currentVCFReader = reader;
		jlFile.setText(reader.toString());
		jlFile.setForeground(getForeground());
		jlFile.setToolTipText(reader.toString());
		
		idMap = new HashMap<VCFHeaderType, String>();

		// Store the ALT fields
		for (VCFHeaderType header: reader.getAltHeader()) {
			idMap.put(header, VCFColumnName.ALT + ": " + header.getId() + " (" + header.getDescription() + ")");
		}

		// Store the FILTER fields
		for (VCFHeaderType header: reader.getFilterHeader()) {
			idMap.put(header, VCFColumnName.FILTER + ": " + header.getId() + " (" + header.getDescription() + ")");
		}

		// Store the FORMAT fields
		/*for (VCFHeaderAdvancedType header: reader.getFormatHeader()) {
			idMap.put(header, VCFColumnName.FORMAT + ": " + header.getId() + " (" + header.getDescription() + ")");
		}*/

		// Store the INFO fields
		for (VCFHeaderAdvancedType header: reader.getInfoHeader()) {
			idMap.put(header, VCFColumnName.INFO + ": " + header.getId() + " (" + header.getDescription() + ")");
		}
		
		// Updates the ID area to empty
		showIDListButton.setEnabled(true);
		jlID.setText("<html><i>select -></i></html>");
		jlID.setForeground(Colors.GREY);
		
		// Updates the filter area to empty
		resetFilterPanel();
		filterPanel.repaint();
		validate();
		
	}


	/**
	 * Sets the new selected ID
	 * @param id the new ID
	 */
	private void setCurrentID (VCFHeaderType id, VCFColumnName category) {
		currentID = id;
		setFilterPanel(currentID, category);
	}


	/**
	 * Set the filter panel according to the ID.
	 * @param id the header ID
	 */
	private void setFilterPanel (VCFHeaderType id, VCFColumnName category) {
		filterEditor = null;

		if (category == VCFColumnName.ALT) {
			if (id != null) {
				filterEditor = new IDFlagEditor();
				filterEditor.setID(id);
			} else {
				filterEditor = new IDStringEditor();
				filterEditor.setID(null);
			}
			filterEditor.setCategory(category);
		} else if (category == VCFColumnName.QUAL) {
			filterEditor = new IDNumberEditor();
			filterEditor.setID(null);
			filterEditor.setCategory(category);
		} else if (category == VCFColumnName.FILTER) {
			filterEditor = new IDStringEditor();
			filterEditor.setID(null);
			filterEditor.setCategory(category);
		} else {
			if (currentID instanceof VCFHeaderAdvancedType) {
				VCFHeaderAdvancedType adCurrentID = (VCFHeaderAdvancedType) currentID;
				if (adCurrentID.getType().isInstance(new Boolean(true))) {
					filterEditor = new IDFlagEditor();
					filterEditor.setID(adCurrentID);
					filterEditor.setCategory(category);
				} else if (adCurrentID.getType().isInstance(new Integer(0)) || adCurrentID.getType().isInstance(new Float(0))) {
					filterEditor = new IDNumberEditor();
					filterEditor.setID(adCurrentID);
					filterEditor.setCategory(category);
				} else if (adCurrentID.getType().isInstance(new String())) {
					filterEditor = new IDStringEditor();
					filterEditor.setID(adCurrentID);
					filterEditor.setCategory(category);
					List<String> elements = new ArrayList<String>();
					Map<Object, Integer> elementsMap = adCurrentID.getElements();
					for (Object o: elementsMap.keySet()) {
						elements.add(o.toString());
					}
					((IDStringEditor)filterEditor).setDefaultElements(elements);
				} else {
					System.out.println("not supported: " + adCurrentID.getId());
				}
			}
		}

		if (filterEditor != null) {
			filterEditor.updatePanel(filterPanel);
			filterPanel.repaint();
			validate();
		} else {
			resetFilterPanel();
			filterPanel.repaint();
			validate();
		}
	}


	/**
	 * Creates a panel to select the filter
	 * @return the filter panel
	 */
	private JPanel resetFilterPanel () {
		if (filterPanel != null) {
			filterPanel.removeAll();
		} else {
			filterPanel = new JPanel();
		}
		
		FlowLayout layout = new FlowLayout();
		filterPanel.setLayout(layout);
		
		Dimension dimension = new Dimension(180, 50);
		filterPanel.setPreferredSize(dimension);
		filterPanel.setMinimumSize(dimension);
		
		filterPanel.add(getDefaultLabel("no filter available"));
		
		return filterPanel;
	}


	@Override
	protected void setEditingPanel (FiltersData data) {
		// Set the vcf reader
		setCurrentReader(data.getReader());

		// Set the ID
		String fullDetail;
		VCFColumnName category;
		if (data.getId() != null) {
			fullDetail = idMap.get(data.getId());
			category = VCFColumnName.getColumnNameFromString(fullDetail.substring(0, fullDetail.indexOf(":")));
		} else {
			//category = VCFColumnName.getColumnNameFromString(data.getNonIdName());
			category = data.getNonIdName();
			fullDetail = nonIdMap.get(category);
		}
		
		setCurrentID(data.getId(), category);
		jlID.setText(fullDetail);
		jlID.setForeground(getForeground());

		// Set the panel with the current filter
		filterEditor.initializesPanel(data.getFilter());

		// Set the selected track list
		DefaultListModel listModel = new DefaultListModel();
		for (Track<?> track: data.getTrackList()) {
			listModel.addElement(track);
		}
		selectedTracks.setModel(listModel);
	}


	@Override
	protected FiltersData getElement () {
		// Retrieve selected tracks
		Track<?>[] trackList = getSelectedTracks();

		// Create the stripe data object
		FiltersData data;
		if (filterEditor == null) {
			data = new FiltersData(currentVCFReader, currentID, null, trackList);
		} else if (filterEditor.getID() == null) { 
			data = new FiltersData(currentVCFReader, filterEditor.getCategory(), filterEditor.getFilter(), trackList);
		} else {
			data = new FiltersData(currentVCFReader, currentID, filterEditor.getFilter(), trackList);
		}

		// Return the stripe data object
		return data;
	}

}
