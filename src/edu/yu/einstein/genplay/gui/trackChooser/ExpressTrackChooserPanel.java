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
package edu.yu.einstein.genplay.gui.trackChooser;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExpressTrackChooserPanel extends JPanel implements MouseListener {

	/** Generated default version ID */
	private static final long serialVersionUID = -1472615405340919386L;

	private Track<?>[] allTracks;
	private int[] indexes;
	private JList jList;
	private DefaultListModel model;


	/**
	 * Constructor of {@link ExpressTrackChooserPanel}
	 */
	public ExpressTrackChooserPanel () {
		// Sets the layout
		((FlowLayout)getLayout()).setHgap(0);
		((FlowLayout)getLayout()).setVgap(0);

		// Creates the model
		model = new DefaultListModel();
		allTracks = MainFrame.getInstance().getTrackList().getTrackList();
		for (Track<?> track: allTracks) {
			model.addElement(track);
		}

		// Creates the list
		jList = new JList();
		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jList.setModel(model);
		jList.addMouseListener(this);

		// Set the list of selected index
		indexes = new int[0];

		// Adds the list
		add(jList);
	}


	/**
	 * Reset the panel clearing the list selection.
	 */
	public void reset () {
		jList.clearSelection();
		indexes = new int[0];
	}


	/**
	 * Sets the tracks in the list
	 * @param tracks the tracks to select
	 */
	public void setSelectedTrack (Track<?>[] tracks) {
		for (Track<?> track: tracks) {
			int index = getTrackIndex(track);
			if (index >= 0) {
				jList.setSelectedIndex(index);
			}
		}
	}


	/**
	 * Retrieve the index of a track from the list of track
	 * @param track a track
	 * @return		its index on the list of track
	 */
	private int getTrackIndex (Track<?> track) {
		for (int i = 0; i < model.size(); i++) {
			if (model.get(i).equals(track)) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * @return an array of selected tracks
	 */
	public Track<?>[] getSelectedTrack () {
		if (indexes.length > 0) {
			Track<?>[] result = new Track<?>[indexes.length];
			int i = 0;
			for (int index: indexes) {
				result[i] = allTracks[index];
				i++;
			}
			return result;
		}
		return null;
	}


	/**
	 * Look after an index in the index array
	 * @param index the index do find
	 * @return	true if it has been found, false otherwise
	 */
	private boolean hasFound (int index) {
		boolean hasBeenFound = false;
		int i = 0;
		while (i < indexes.length && !hasBeenFound) {
			if (indexes[i] == index) {
				hasBeenFound = true;
			}
			i++;
		}
		return hasBeenFound;
	}
	
	
	/**
	 * Adds an index in the list of selected index
	 * @param index the index
	 */
	private void addIndex (int index) {
		int size = indexes.length + 1;
		int[] indexesTmp = new int[size];
		for (int i = 0; i < indexes.length; i++) {
			indexesTmp[i] = indexes[i];
		}
		indexesTmp[indexesTmp.length - 1] = index;
		indexes = indexesTmp;
	}
	

	/**
	 * Removes an index in the list of selected index
	 * @param index the index
	 */
	private void removeIndex (int index) {
		int size = indexes.length - 1;
		int[] indexesTmp = new int[size];
		int i = 0;
		for (int current: indexes) {
			if (index != current) {
				indexesTmp[i] = current;
				i++;
			}
		}
		indexes = indexesTmp;
	}
	
	
	/**
	 * Makes the calculation of the minimum dimension of the list.
	 * @return the dimension of the list
	 */
	public Dimension getDimension () {
		String longestString = "";
		for (Track<?> track: allTracks) {
			String currentString = track.getName();
			if (currentString.length() > longestString.length()) {
				longestString = currentString;
			}
		}
		FontMetrics fm = getFontMetrics(getFont());
		int width = fm.stringWidth(longestString);
		int height = (fm.getHeight() + 3) * allTracks.length;
		return new Dimension(width, height);
	}
	
	
	/**
	 * Set the size of the list.
	 * @param dimension
	 */
	public void setListSize (Dimension dimension) {
		jList.setPreferredSize(dimension);
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		int index = jList.getSelectedIndex();
		if (hasFound(index)) {
			removeIndex(index);
		} else {
			addIndex(index);
		}
		jList.setSelectedIndices(indexes);
		//jList.setSelectionInterval(index, index);
	}

}
