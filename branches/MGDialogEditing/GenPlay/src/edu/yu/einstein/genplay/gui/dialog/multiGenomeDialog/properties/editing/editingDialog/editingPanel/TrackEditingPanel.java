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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import edu.yu.einstein.genplay.gui.dialog.MultiTrackChooser;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TrackEditingPanel extends EditingPanel<Track<?>[]> {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;
	private JList jList;
	private DefaultListModel model;
	
	
	/**
	 * Constructor of {@link TrackEditingPanel}
	 */
	public TrackEditingPanel() {
		super("Track(s)");
	}
	

	@Override
	protected void initializeContentPanel() {
		// Creates the list
		model = new DefaultListModel();
		jList = new JList();
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setModel(model);
		
		// Create the button for selecting tracks
		JButton selectTrackButton = new JButton("Select tracks");
		selectTrackButton.setMargin(new Insets(0, 0, 0, 0));
		selectTrackButton.setToolTipText("Select tracks");
		selectTrackButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Track<?>[] tracks = MultiTrackChooser.getSelectedTracks(getCurrentInstance(), getAvailableTracks(), getSelectedTracks());
				if (tracks != null) {
					DefaultListModel listModel = new DefaultListModel();
					for (Track<?> track: tracks) {
						listModel.addElement(track);
					}
					jList.setModel(listModel);
				}
			}
		});
		
		// Sets the content panel
		contentPanel.setLayout(new BorderLayout(0, 0));
		contentPanel.add(new JScrollPane(jList), BorderLayout.CENTER);
		contentPanel.add(selectTrackButton, BorderLayout.SOUTH);
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
	public Track<?>[] getSelectedTracks () {
		Track<?>[] tracks = null;
		if (jList != null) {
			ListModel model = jList.getModel();
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
	 * @return the stripes editing panel instance
	 */
	protected Component getCurrentInstance() {
		return this;
	}
	
	
	@Override
	public void update(Object object) {}
	
	
	@Override
	public String getErrors() {
		String errors = "";
		if (getSelectedTracks() == null) {
			errors += "Track(s) selection\n";
		}
		return errors;
	}
	
	
	@Override
	public void reset() {
		jList.setModel(new DefaultListModel());
		jList.clearSelection();
	}


	@Override
	public void initialize(Track<?>[] element) {
		if (element != null) {
			DefaultListModel listModel = new DefaultListModel();
			for (Track<?> track: element) {
				listModel.addElement(track);
			}
			jList.setModel(listModel);
		}
	}
	
}
