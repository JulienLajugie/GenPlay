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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;

import edu.yu.einstein.genplay.gui.dialog.MultiTrackChooser;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K> class of the editing element
 */
public abstract class EditingPanel<K> extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 5845552615077060873L;

	/** Text for the button 'Add' **/
	public static final String ADD_BUTTON_TEXT = "Add";
	/** Text for the button 'Apply' **/
	public static final String APPLY_BUTTON_TEXT = "Apply";

	
	// Insets
	protected Insets firstInset = new Insets(10, 15, 0, 0);
	protected Insets titleInset = new Insets(15, 5, 5, 0);
	protected Insets panelInset = new Insets(0, 5, 0, 0);
	
	protected GridBagConstraints gbc;				// Layout constraints
	
	protected JList				selectedTracks;		// List of selected tracks
	private JButton 			addButton;			// Button to add a new stripe setting
	private JButton 			applyButton;		// Button to apply new settings to the current selected stripe


	/**
	 * Constructor of {@link EditingPanel}
	 */
	protected EditingPanel () {
		// Set the size of the panel
		Dimension dimension = new Dimension(200, PropertiesDialog.DIALOG_HEIGHT);
		setMinimumSize(dimension);
		setPreferredSize(dimension);
		
		// Border
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
	}
	
	
	/**
	 * Reset the panel to an "empty" state
	 */
	public abstract void clearSelection ();
	
	
	/**
	 * Set the editing panel content with an element
	 * @param element
	 */
	protected abstract void setEditingPanel (K element);


	/**
	 * Retrieves information from the panel to create and return the element
	 * @return the element
	 */
	protected abstract K getElement ();
	
	
	/**
	 * Creates the panel that contains the list of the selected tracks.
	 * It also contains a button to select tracks.
	 * @return the track selection panel
	 */
	protected JPanel getTrackSelectionPanel () {
		// Create the list to show selected tracks
		selectedTracks = new JList();
		selectedTracks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedTracks.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		selectedTracks.setVisibleRowCount(-1);
		selectedTracks.setToolTipText("Selected tracks.");
		JScrollPane listScroller = new JScrollPane(selectedTracks);
		Dimension dimension = new Dimension(180, 50);
		listScroller.setPreferredSize(dimension);
		listScroller.setMinimumSize(dimension);

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
	protected Track<?>[] getSelectedTracks () {
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
	protected JPanel getValidationPanel () {
		// Create the panel
		JPanel panel = new JPanel();

		// Set the size of the panel
		Dimension dimension = new Dimension(200, 40);
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);

		// Create buttons
		addButton = new JButton(ADD_BUTTON_TEXT);
		applyButton = new JButton(APPLY_BUTTON_TEXT);
		applyButton.setEnabled(false);

		// Add buttons
		panel.add(addButton);
		panel.add(applyButton);
		
		// Set the 'add' button as default
		//getRootPane().setDefaultButton(addButton);

		// Return the panel
		return panel;
	}
	
	
	/**
	 * @return the applyButton
	 */
	public JButton getApplyButton() {
		return applyButton;
	}
	
	
	/**
	 * Add an action listener to the 'Add' and 'Apply' buttons
	 * @param al the action listener
	 */
	public void addListener (ActionListener al) {
		addButton.addActionListener(al);
		applyButton.addActionListener(al);
	}
	
	
	/**
	 * @return the stripes editing panel instance
	 */
	protected Component getCurrentInstance() {
		return this;
	}
}
