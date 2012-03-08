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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.colors.Colors;



/**
 * A dialog box used to choose tracks 
 * @author Julien Lajugie
 * @version 0.1
 */
public class MultiTrackChooser extends JDialog {

	private static final long serialVersionUID = -4678243279540123148L; // generated ID

	private static final Dimension WINDOW_SIZE = new Dimension(680, 400); // size of the dialog box
	private static final int LIST_WIDTH = 200; // preferred width of the 2 JList

	private static JLabel 			jlaAvailableTracks;	// label for the list of available tracks
	private static JLabel 			jlaSelectedTracks;	// label for the list of selected tracks
	private static JList 			jliAvailableTracks;	// list of available tracks
	private static DefaultListModel dlmAvailableTracks;	// model of the list of available tracks
	private static JList 			jliSelectedTracks;	// list of selected tracks
	private static DefaultListModel dlmSelectedTracks;	// model of the list of selected tracks
	private static JButton 			jbLeft;				// left button
	private static JButton 			jbRight;			// right button
	private static JButton 			jbUp;				// up button
	private static JButton 			jbDown;				// down button
	private static JButton			jbOk;				// OK button
	private static JButton 			jbCancel;			// cancel button
	private static boolean 			validated;			// true if OK has been pressed

	/**
	 * Private constructor. Used internally to create a {@link MultiTrackChooser} dialog. 
	 * @param parent The parent {@link Component} from which the dialog is displayed.
	 * @param availableTracks array of {@link Track}
	 */
	private MultiTrackChooser(Component parent, Track<?>[] availableTracks) {
		initComponents(availableTracks);
		addComponents();
		this.pack();
		setIconImage(Images.getApplicationImage());
		setModal(true);
		setAlwaysOnTop(true);
		validated = false;
		setTitle("Select Tracks");
		getRootPane().setDefaultButton(jbOk);
		setLocationRelativeTo(parent);
		setSize(WINDOW_SIZE);
	}


	/**
	 * Initializes the subcomponents of the dialog box
	 * @param availableTracks array of {@link Track}
	 */
	private void initComponents(Track<?>[] availableTracks) {
		dlmAvailableTracks = new DefaultListModel();
		for (Track<?> currentTrack: availableTracks) {
			dlmAvailableTracks.addElement(currentTrack);
		}

		jlaAvailableTracks = new JLabel("Available Tracks");
		jlaSelectedTracks = new JLabel("Selected Tracks");

		jliAvailableTracks = new JList(dlmAvailableTracks);
		jliAvailableTracks.setBorder(BorderFactory.createLineBorder(Colors.BLACK));
		jliAvailableTracks.setBackground(Colors.WHITE);
		jliAvailableTracks.addListSelectionListener(new ListSelectionListener() {			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (jliAvailableTracks.isSelectionEmpty()) {
					jbRight.setEnabled(false);
				} else {
					jbRight.setEnabled(true);
				}
			}
		});
		jliAvailableTracks.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				// if jbRight gain the focus we need to keep the selection in order to 
				// know which tracks need to be transfered
				if (e.getOppositeComponent() != jbRight) {
					jliAvailableTracks.clearSelection();
				}
			}
		});
		jliAvailableTracks.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() % 2 == 0) {
					selectTracks();
				}
			}
		});

		jliSelectedTracks = new JList(dlmSelectedTracks);
		//jliSelectedTracks.setPreferredSize(new Dimension(LIST_WIDTH, getPreferredSize().height));
		jliSelectedTracks.setBorder(BorderFactory.createLineBorder(Color.black));
		jliSelectedTracks.setBackground(Color.white);
		jliSelectedTracks.addListSelectionListener(new ListSelectionListener() {			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (jliSelectedTracks.isSelectionEmpty()) {
					jbLeft.setEnabled(false);
					jbUp.setEnabled(false);
					jbDown.setEnabled(false);
				} else {
					jbLeft.setEnabled(true);
					jbUp.setEnabled(true);
					jbDown.setEnabled(true);
					if (jliSelectedTracks.getSelectedIndices().length > 1) {
						jbUp.setEnabled(false);
						jbDown.setEnabled(false);
					} else {
						if (jliSelectedTracks.getSelectedIndex() == 0) {
							jbUp.setEnabled(false);
						}
						if (jliSelectedTracks.getSelectedIndex() == dlmSelectedTracks.getSize() - 1) {
							jbDown.setEnabled(false);
						}
					}
				}
			}
		});
		jliSelectedTracks.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				// if jbLeft, jbUp or jbDown gain the focus we need to keep the selection in order to 
				// know which tracks need to be transfered
				if ((e.getOppositeComponent() != jbLeft) &&
						(e.getOppositeComponent() != jbUp) &&
						(e.getOppositeComponent() != jbDown)) {
					jliSelectedTracks.clearSelection();
				}
			}
		});
		jliSelectedTracks.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() % 2 == 0) {
					unSelectTracks();
				}
			}
		});

		jbLeft = new JButton("<");
		jbLeft.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				unSelectTracks();				
			}
		});		
		jbLeft.setEnabled(false);

		jbRight = new JButton(">");
		jbRight.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectTracks();				
			}
		});		
		jbRight.setEnabled(false);

		jbUp = new JButton("Up");
		jbUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedTrackUp();
			}
		});
		jbUp.setEnabled(false);

		jbDown = new JButton("Down");
		jbDown.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedTrackDown();
			}
		});
		jbDown.setEnabled(false);

		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				jbOkClicked();				
			}
		});

		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelClicked();				
			}
		});
	}


	/**
	 * Adds the subcomponents
	 */
	private void addComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(10, 10, 0, 0);
		add(jlaAvailableTracks, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.PAGE_END;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(10, 0, 0, 0);
		add(jlaSelectedTracks, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 10, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scrollAvailableTracks = new JScrollPane(jliAvailableTracks);
		scrollAvailableTracks.setPreferredSize(new Dimension(LIST_WIDTH, getPreferredSize().height));
		add(scrollAvailableTracks, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 1;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 10, 0, 10);
		add(jbRight, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 1;
		c.anchor = GridBagConstraints.PAGE_START;
		c.insets = new Insets(0, 10, 0, 10);
		add(jbLeft, c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scrollSelectedTracks = new JScrollPane(jliSelectedTracks);
		scrollSelectedTracks.setPreferredSize(new Dimension(LIST_WIDTH, getPreferredSize().height));
		add(scrollSelectedTracks, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_END;
		c.insets = new Insets(0, 10, 0, 10);
		add(jbUp, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.insets = new Insets(0, 10, 0, 10);
		add(jbDown, c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 3;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 0, 10, 0);
		add(jbOk, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 3;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jbCancel, c);
	}


	/**
	 * Moves the selected {@link Track} down in the list of selected tracks
	 */
	protected void selectedTrackDown() {
		int selectedIndex = jliSelectedTracks.getSelectedIndex();
		Object tempTrack = dlmSelectedTracks.getElementAt(selectedIndex);
		dlmSelectedTracks.setElementAt(dlmSelectedTracks.getElementAt(selectedIndex + 1), selectedIndex);
		dlmSelectedTracks.setElementAt(tempTrack, selectedIndex + 1);
		jliSelectedTracks.setSelectedIndex(selectedIndex + 1);
	}


	/**
	 * Moves the selected {@link Track} up in the list of selected tracks
	 */
	protected void selectedTrackUp() {
		int selectedIndex = jliSelectedTracks.getSelectedIndex();
		Object tempTrack = dlmSelectedTracks.getElementAt(selectedIndex);
		dlmSelectedTracks.setElementAt(dlmSelectedTracks.getElementAt(selectedIndex - 1), selectedIndex);
		dlmSelectedTracks.setElementAt(tempTrack, selectedIndex - 1);
		jliSelectedTracks.setSelectedIndex(selectedIndex - 1);
	}


	/**
	 * Moves the selected tracks from the list of selected tracks to the list of available tracks
	 */
	protected void unSelectTracks() {
		if (!jliSelectedTracks.isSelectionEmpty()) {
			Track<?>[] selectedTracks = new Track[jliSelectedTracks.getSelectedValues().length];
			for (int i = 0; i < selectedTracks.length; i++) {
				selectedTracks[i] = (Track<?>) jliSelectedTracks.getSelectedValues()[i];
			}
			
			/*List<Track<?>> tracks = new ArrayList<Track<?>>();
			for (Object object: dlmAvailableTracks.toArray()) {
				tracks.add((Track<?>) object);
			}*/
			
			for (Track<?> currentTrack: selectedTracks) {
				dlmAvailableTracks.addElement(currentTrack);
				//tracks.add(currentTrack);
				dlmSelectedTracks.removeElement(currentTrack);
			}
			
			/*Collections.sort(tracks, new TrackComparator());
			dlmAvailableTracks.removeAllElements();
			for (Track<?> currentTrack: tracks) {
				dlmAvailableTracks.addElement(currentTrack);
			}*/
		}
	}


	/**
	 * Moves the selected tracks from the list of available tracks to the list of selected tracks
	 */
	protected void selectTracks() {
		if (!jliAvailableTracks.isSelectionEmpty()) {
			Track<?>[] selectedTracks = new Track[jliAvailableTracks.getSelectedValues().length];
			for (int i = 0; i < selectedTracks.length; i++) {
				selectedTracks[i] = (Track<?>) jliAvailableTracks.getSelectedValues()[i];
			}			
			for (Track<?> currentTrack: selectedTracks) {
				dlmSelectedTracks.addElement(currentTrack);
				dlmAvailableTracks.removeElement(currentTrack);
			}			
		}
	}


	/**
	 * Closes the dialog. No action are performed.
	 */
	private void jbCancelClicked() {
		this.dispose();

	}


	/**
	 * Closes the dialog. Sets validated to true so the main function can return the two selected curves.
	 */
	private void jbOkClicked() {
		validated = true;
		this.dispose();		
	}


	/**
	 * Displays a dialog box allowing the user to select tracks
	 * @param parent the parent {@link Component} from which the dialog is displayed
	 * @param availableTracks array of {@link Track}
	 * @return an array containing the track selected. Null if cancel was pressed
	 */
	public static Track<?>[] getSelectedTracks(Component parent, Track<?>[] availableTracks) {
		// the list model for selected tracks must be empty
		dlmSelectedTracks = new DefaultListModel();
		
		// show the dialog and return selected tracks
		return getTracks(parent, availableTracks);
	}


	/**
	 * Displays a dialog box allowing the user to select tracks.
	 * This method allows user to define a list of track that are already selected 
	 * @param parent the parent {@link Component} from which the dialog is displayed
	 * @param availableTracks 	array of {@link Track} to select
	 * @param selectedTracks 	array of {@link Track} already selected
	 * @return an array containing the track selected. Null if cancel was pressed
	 */
	public static Track<?>[] getSelectedTracks(Component parent, Track<?>[] availableTracks, Track<?>[] selectedTracks) {
		// the list model for selected tracks is set to empty
		dlmSelectedTracks = new DefaultListModel();
		
		// we will try to add tracks in the list model for selected tracks
		if (selectedTracks != null) {
			for (Track<?> currentTrack: selectedTracks) {
				dlmSelectedTracks.addElement(currentTrack);
			}
		}
		
		// show the dialog and return selected tracks
		return getTracks(parent, availableTracks);
	}
	
	
	/**
	 * Displays a dialog box allowing the user to select tracks.
	 * @param parent the parent {@link Component} from which the dialog is displayed
	 * @param availableTracks 	array of {@link Track} to select
	 * @return an array containing the track selected. Null if cancel was pressed
	 */
	private static Track<?>[] getTracks (Component parent, Track<?>[] availableTracks) {
		MultiTrackChooser mtc = new MultiTrackChooser(parent, availableTracks);
		mtc.setVisible(true);
		if(validated) {
			Track<?>[] result = new Track[dlmSelectedTracks.getSize()];
			for (int i = 0; i < dlmSelectedTracks.getSize(); i++) {
				result[i] = (Track<?>) dlmSelectedTracks.getElementAt(i);
			}
			return result;
		} else {
			return null;
		}
	}

}
