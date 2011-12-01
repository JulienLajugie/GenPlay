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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.stripesEditing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.dialog.MultiTrackChooser;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.Utils;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class StripesEditingPanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 1002708176297238005L;
	/** Text for the button 'Add' **/
	protected static final String ADD_BUTTON_TEXT = "Add";
	/** Text for the button 'Apply' **/
	protected static final String APPLY_BUTTON_TEXT = "Apply";
	
	private final Color[]		defaultVariationColor = {Color.green, Color.red, Color.cyan};	// Array of default variation colors (Insertion, Deletion, SNPs)
	private JComboBox 			jcbGenome;			// The combo box for the genome selection
	private List<VariantType> 	variationName;		// Variation names list
	private List<JCheckBox> 	selectedVariation;	// Variation check box selection list
	private List<JButton> 		variationColor;		// Variation color list
	private JList				selectedTracks;		// List of selected tracks
	private JButton 			addButton;			// Button to add a new stripe setting
	private JButton 			applyButton;		// Button to apply new settings to the current selected stripe

	

	/**
	 * Constructor of {@link StripesEditingPanel}
	 */
	protected StripesEditingPanel () {
		// Set the size of the panel
		Dimension dimension = new Dimension(200, PropertiesDialog.DIALOG_HEIGHT);
		setMinimumSize(dimension);
		setPreferredSize(dimension);

		// Insets
		Insets firstInset = new Insets(10, 15, 0, 0);
		Insets titleInset = new Insets(15, 5, 5, 0);
		Insets panelInset = new Insets(0, 5, 0, 0);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Panel title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = firstInset;
		add(Utils.getTitleLabel("Editing"), gbc);

		// Genome selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Genome"), gbc);

		// Genome selection box
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getGenomeBox(), gbc);

		// Variation selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Variations"), gbc);

		// Variation selection panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		add(getVariationSelectionPanel(), gbc);

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


	/**
	 * Creates a combo box containing all genomes
	 * @return the combo box
	 */
	private JComboBox getGenomeBox () {
		jcbGenome = new JComboBox(ProjectManager.getInstance().getGenomeSynchronizer().getFormattedGenomeArray());
		int height = jcbGenome.getFontMetrics(jcbGenome.getFont()).getHeight() + 5;
		Dimension dimension = new Dimension(180, height);
		jcbGenome.setPreferredSize(dimension);
		jcbGenome.setToolTipText("Select a genome to display its variation(s).");
		return jcbGenome;
	}


	/**
	 * Creates the variation selection panel.
	 * It contains all variation with a check box to select it for display and a button to choose its color.
	 * @return the variation selection panel
	 */
	private JPanel getVariationSelectionPanel () {
		// Initialize lists
		variationName = new ArrayList<VariantType>();
		selectedVariation = new ArrayList<JCheckBox>();
		variationColor = new ArrayList<JButton>();

		// Fill the list that contains the variation names
		variationName.add(VariantType.INSERTION);	// INSERTION will be on index 0
		variationName.add(VariantType.DELETION);	// DELETION will be on index 1
		variationName.add(VariantType.SNPS);		// SNPS will be on index 2

		// Fill the list that contains the checkbox for selecting variation
		for (int i = 0; i < 3; i++) {
			JCheckBox checkBox = new JCheckBox();
			checkBox.setBorder(null);
			checkBox.setMargin(new Insets(0, 0, 0, 0));
			checkBox.setToolTipText("Enable/Disable " + variationName.get(i).toString().toLowerCase() + ".");
			selectedVariation.add(checkBox);
		}

		// Fill the list that contains the button for selecting variation color
		for (int i = 0; i < 3; i++) {
			JButton colorButton = new JButton();
			Dimension colorDim = new Dimension(13, 13);
			colorButton.setPreferredSize(colorDim);
			colorButton.setBorder(null);
			colorButton.setToolTipText("Select color for " + variationName.get(i).toString().toLowerCase() + ".");
			colorButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JButton button = (JButton) arg0.getSource();
					Color newColor = JColorChooser.showDialog(getCurrentInstance(), "Choose color", button.getBackground());
					if (newColor != null) {
						button.setBackground(newColor);
					}
				}
			});
			// Initialize button color
			colorButton.setBackground(defaultVariationColor[i]);

			// Add the button to the list
			variationColor.add(colorButton);
		}


		// Create the panel
		JPanel panel = new JPanel();

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Insets
		Insets nameInset = new Insets(0, 0, 0, 0);
		Insets selectInset = new Insets(0, 15, 0, 0);
		Insets colorInset = new Insets(0, 10, 0, 0);

		// Add components to the panel
		for (int i = 0; i < 3; i++) {
			// Variation name
			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.insets = nameInset;
			panel.add(new JLabel(variationName.get(i).toString()), gbc);

			// Selection button
			gbc.gridx = 1;
			gbc.gridy = i;
			gbc.insets = selectInset;
			panel.add(selectedVariation.get(i), gbc);

			// Color button
			gbc.gridx = 2;
			gbc.gridy = i;
			gbc.insets = colorInset;
			panel.add(variationColor.get(i), gbc);
		}

		// Return the panel
		return panel;
	}


	/**
	 * Creates the panel that contains the list of the selected tracks.
	 * It also contains a button to select tracks.
	 * @return the track selection panel
	 */
	private JPanel getTrackSelectionPanel () {
		// Create the list to show selected tracks
		selectedTracks = new JList();
		selectedTracks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedTracks.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		selectedTracks.setVisibleRowCount(-1);
		selectedTracks.setToolTipText("Selected tracks.");
		JScrollPane listScroller = new JScrollPane(selectedTracks);
		Dimension dimension = new Dimension(180, 50);
		listScroller.setPreferredSize(dimension);

		// Create the button for selecting tracks
		JButton selectTrackButton = new JButton("Select tracks");
		selectTrackButton.setMargin(new Insets(0, 0, 0, 0));
		selectTrackButton.setToolTipText("Select tracks");
		selectTrackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Track<?>[] tracks = MultiTrackChooser.getSelectedTracks(getCurrentInstance(), getAvailableTracks(), getSelectedTracks());
				//if (tracks != null && tracks.length > 0) {
				if (tracks != null) {
					DefaultListModel listModel = new DefaultListModel();
					for (Track<?> track: tracks) {
						listModel.addElement(track);
					}
					selectedTracks.setModel(listModel);
				} else {
					System.out.println("null");
				}
			}
		});


		// Create the panel
		JPanel panel = new JPanel();

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Selected track list
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(listScroller, gbc);

		// Track selection button
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(selectTrackButton, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * @return an array of {@link Track} that are available
	 */
	private Track<?>[] getAvailableTracks () {
		Track<?>[] allTracks = MainFrame.getInstance().getTrackList().getTrackList();
		Track<?>[] selectedTracks = getSelectedTracks();
		Track<?>[] availableTracks = null;

		if (selectedTracks == null) {
			availableTracks = allTracks;
		} else {
			availableTracks = new Track<?>[allTracks.length - selectedTracks.length];
			int indexTrack = 0;
			for (Track<?> track: allTracks) {
				int index = 0;
				boolean found = false;
				while (!found && index < selectedTracks.length) {
					if (track.equals(selectedTracks[index])) {
						found = true;
					} else {
						index++;
					}
				}
				if (!found) {
					availableTracks[indexTrack] = track;
					indexTrack++;
				}
			}
		}

		return availableTracks;
	}


	/**
	 * @return an array of {@link Track} that are already selected
	 */
	private Track<?>[] getSelectedTracks () {
		Track<?>[] tracks = null;
		if (selectedTracks != null) {
			ListModel model = selectedTracks.getModel();
			int size = model.getSize(); 
			if (size > 0) {
				tracks = new Track<?>[size];
				for (int i = 0; i < size; i++) {
					tracks[i] = (Track<?>) model.getElementAt(i);
				}
			}
		}
		return tracks;
	}


	/**
	 * Creates the validation panel containing the "Add" and "Apply" buttons
	 * @return the validation panel
	 */
	private JPanel getValidationPanel () {
		// Create the panel
		JPanel panel = new JPanel();

		// Set the size of the panel
		Dimension dimension = new Dimension(200, 40);
		panel.setPreferredSize(dimension);

		// Create buttons
		addButton = new JButton(ADD_BUTTON_TEXT);
		applyButton = new JButton(APPLY_BUTTON_TEXT);
		applyButton.setEnabled(false);

		// Add buttons
		panel.add(addButton);
		panel.add(applyButton);

		// Return the panel
		return panel;
	}
	
	
	/**
	 * Set the editing panel content with a stripe data object
	 * @param data stripe data
	 */
	protected void setEditingPanel (StripeData data) {
		// Set the genome
		jcbGenome.setSelectedItem(data.getGenome());
		
		// Set selected variation and color
		for (int i = 0; i < variationName.size(); i++) {
			int variationIndex = data.getVariantList().indexOf(variationName.get(i));
			if (variationIndex == -1) {
				selectedVariation.get(i).setSelected(false);
				variationColor.get(i).setBackground(defaultVariationColor[i]);
			} else {
				selectedVariation.get(i).setSelected(true);
				variationColor.get(i).setBackground(data.getColorList().get(variationIndex));
			}
		}
		
		// Set the selected track list
		DefaultListModel listModel = new DefaultListModel();
		for (Track<?> track: data.getTrackList()) {
			listModel.addElement(track);
		}
		selectedTracks.setModel(listModel);
	}
	
	
	/**
	 * Retrieves information from the panel to create and return a stripe data object
	 * @return a stripe data object
	 */
	protected StripeData getStripeData () {
		// Retrieve the genome name
		String genome = jcbGenome.getSelectedItem().toString();
		
		// Retrieve the variant and color lists
		List<VariantType> variantList = new ArrayList<VariantType>();
		List<Color> colorList = new ArrayList<Color>();
		for (int i = 0; i < variationName.size(); i++) {
			if (selectedVariation.get(i).isSelected()) {
				variantList.add(variationName.get(i));
				colorList.add(variationColor.get(i).getBackground());
			}
		}
		
		// Retrieve selected tracks
		Track<?>[] trackList = getSelectedTracks();
		
		// Create the stripe data object
		StripeData data = new StripeData(genome, variantList, colorList, trackList);
		
		// Return the stripe data object
		return data;
	}
	

	/**
	 * @return the applyButton
	 */
	protected JButton getApplyButton() {
		return applyButton;
	}


	/**
	 * Add an action listener to the 'Add' and 'Apply' buttons
	 * @param al the action listener
	 */
	protected void addListener (ActionListener al) {
		addButton.addActionListener(al);
		applyButton.addActionListener(al);
	}


	/**
	 * @return the stripes editing panel instance
	 */
	private Component getCurrentInstance() {
		return this;
	}
}
