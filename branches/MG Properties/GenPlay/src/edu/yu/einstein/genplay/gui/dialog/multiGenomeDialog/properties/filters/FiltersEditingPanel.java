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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.Utils;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.EditingPanel;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class FiltersEditingPanel extends EditingPanel<FiltersData> {

	/** Generated serial version ID */
	private static final long serialVersionUID = 1002708176297238005L;
	
	private JComboBox 			jcbVCFReader;			// The combo box for the vcf file selection
	private JList				jlIDList;				// List of ID
	
	private VCFReader 			currentVCFReader;		// the current VCF reader
	private VCFHeaderType 		currentID;				// the current header ID
	private Map<VCFHeaderType, String> idMap;			// map between ID and their full description (as shown in the jlist)
	


	/**
	 * Constructor of {@link FiltersEditingPanel}
	 */
	protected FiltersEditingPanel () {
		super();

		// Panel title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = firstInset;
		add(Utils.getTitleLabel("Editing"), gbc);

		// File selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("VCF File"), gbc);

		// File selection box
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getFileBox(), gbc);

		// ID selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("ID"), gbc);

		// ID selection list
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getListID(), gbc);
		
		// Filter selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Filter"), gbc);

		// Filter selection panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getFilterPanel(), gbc);

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
	public void clearSelection () {
		jcbVCFReader.setSelectedIndex(0);
		DefaultListModel model = new DefaultListModel();
		jlIDList = new JList(model);
		//TODO
		selectedTracks.setModel(new DefaultListModel());
		getApplyButton().setEnabled(false);
	}
	
	
	/**
	 * Creates the vcf file box
	 * @return the vcf file box
	 */
	private JComboBox getFileBox () {
		// Creates the model for the combo box
		List<VCFReader> readerList = ProjectManager.getInstance().getGenomeSynchronizer().getReaderList();
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (VCFReader reader: readerList) {
			model.addElement(reader);
		}
		
		// Creates the combo box
		jcbVCFReader = new JComboBox(model);
		
		// Initializes combo box size
		int height = jcbVCFReader.getFontMetrics(jcbVCFReader.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(180, height);
		jcbVCFReader.setPreferredSize(dimension);
		jcbVCFReader.setMinimumSize(dimension);
		jcbVCFReader.setToolTipText("Select a file to display its IDs.");
		
		// Initializes the listener
		jcbVCFReader.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox) e.getSource();
				VCFReader newVCFReader = (VCFReader) box.getSelectedItem();
				if (currentVCFReader == null) {
					setCurrentReader(newVCFReader);
					updateListContent();
				}
				if (!currentVCFReader.getFile().equals(newVCFReader.getFile())) {
					setCurrentReader(newVCFReader);
					updateListContent();
				}
				
			}
		});
		
		// return the combo box
		return jcbVCFReader;
	}


	/**
	 * Creates the list of IDs
	 * @return a scrollable panel containing the list of IDs
	 */
	private JScrollPane getListID () {
		// Creates an empty list model
		DefaultListModel model = new DefaultListModel();
		
		// Creates the list
		jlIDList = new JList(model);
		
		// Creates the scrollable panel that contains the list
		JScrollPane panel = new JScrollPane(jlIDList);
		Dimension dimension = new Dimension(180, 40);
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);
		
		// Defines the listener for the list
		jlIDList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {								// if the selection is stable
					String fullDetail = jlIDList.getSelectedValue().toString();		// get the value
					VCFHeaderType newID = null;										// instanciate the ID
					for (VCFHeaderType id: idMap.keySet()) {						// scan all full detail ID to find the right ID object
						if (fullDetail.equals(idMap.get(id))) {
							newID = id;
						}
					}
					if (currentID == null) {										// if the current id does not exist yet
						setCurrentID(newID);										// we set it
					}
					if (!currentID.getId().equals(newID.getId())) {					// if the new id is different than the current one
						setCurrentID(newID);										// we set it
					}
				}
			}
		});
		
		// Return the panel
		return panel;
	}
	
	
	/**
	 * Sets the current vcf file reader.
	 * Creates a map between ID and their description for the ID list.
	 * @param reader the new vcf file reader
	 */
	private void setCurrentReader (VCFReader reader) {
		currentVCFReader = reader;
		idMap = new HashMap<VCFHeaderType, String>();
		
		// Store the ALT fields
		for (VCFHeaderType header: reader.getAltHeader()) {
			idMap.put(header, "ALT: " + header.getId() + " (" + header.getDescription() + ")");
		}
		
		// Store the FILTER fields
		for (VCFHeaderType header: reader.getFilterHeader()) {
			idMap.put(header, "FILTER: " + header.getId() + " (" + header.getDescription() + ")");
		}
		
		// Store the FORMAT fields
		for (VCFHeaderAdvancedType header: reader.getFormatHeader()) {
			idMap.put(header, "FORMAT: " + header.getId() + " (" + header.getDescription() + ")");
		}
		
		// Store the INFO fields
		for (VCFHeaderAdvancedType header: reader.getInfoHeader()) {
			idMap.put(header, "INFO: " + header.getId() + " (" + header.getDescription() + ")");
		}
	}
	
	
	/**
	 * Sets the new selected ID
	 * @param id the new ID
	 */
	private void setCurrentID (VCFHeaderType id) {
		currentID = id;
		if (currentID instanceof VCFHeaderAdvancedType) {
			VCFHeaderAdvancedType adCurrentID = (VCFHeaderAdvancedType) currentID;
			System.out.println(adCurrentID.getId() + ": " + adCurrentID.getDescription() + "(" + adCurrentID.getType() + ", " + adCurrentID.getNumber() + ")");
		} else {
			System.out.println(currentID.getId() + ": " + currentID.getDescription());
		}
	}
	
	
	/**
	 * Update the content to the list according to the map of IDs
	 */
	private void updateListContent () {
		DefaultListModel model = new DefaultListModel();
		Collection<String> collection = idMap.values();
		List<String> values = new ArrayList<String>();
		for (String s: collection) {
			values.add(s);
		}
		Collections.sort(values);
		for (String value: values) {
			model.addElement(value);
		}
		jlIDList.setModel(model);
	}
	
	
	/**
	 * Creates a panel to select the filter
	 * @return the filter panel
	 */
	private JPanel getFilterPanel () {
		JPanel panel = new JPanel();
		Dimension dimension = new Dimension(180, 40);
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);
		panel.setBackground(Color.lightGray);
		return panel;
	}
	
	
	@Override
	protected void setEditingPanel (FiltersData data) {
		// Set the vcf reader
		jcbVCFReader.setSelectedItem(data.getReader());
		
		// TODO
		
		// Set the selected track list
		DefaultListModel listModel = new DefaultListModel();
		for (Track<?> track: data.getTrackList()) {
			listModel.addElement(track);
		}
		selectedTracks.setModel(listModel);
	}
	
	
	@Override
	protected FiltersData getElement () {
		// TODO
		
		// Retrieve selected tracks
		Track<?>[] trackList = getSelectedTracks();
		
		// Create the stripe data object
		FiltersData data = new FiltersData(currentVCFReader, currentID, null, trackList);
		
		// Return the stripe data object
		return data;
	}

}
