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
package edu.yu.einstein.genplay.gui.trackList;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.popupMenu.TrackMenu;
import edu.yu.einstein.genplay.gui.track.ScrollingManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;


/**
 * Scroll panel showing a list of tracks
 * @author Julien Lajugie
 */
public class TrackListPanel extends JScrollPane implements Serializable, TrackListener, ListDataListener {

	private final static int 	SCROLL_BAR_BLOCK_INCREMENT = 40;			// block increment for the scroll bar
	private final static int 	SCROLL_BAR_UNIT_INCREMENT = 15;				// unit increment for the scroll bar
	private static final long 	serialVersionUID = -5070245121955382857L; 	// generated serial ID

	private transient Track			copiedTrack = null; 			// list of the tracks in the clipboard
	private transient Track			draggedOverTrack = null; 		// track rolled over by the dragged track, null if none
	private transient Track			draggedTrack = null;			// dragged track, null if none
	private transient Track			selectedTrack = null;			// track selected
	private final JPanel 			jpTrackList;					// panel with the tracks
	private final TrackListModel	model;							// model handling the tracks list showed in this panel
	private final TrackMenu			trackMenu;


	/**
	 * Creates an instance of {@link TrackListPanel}
	 * @param model {@link TrackListModel} handling the tracks showed in this panel
	 */
	public TrackListPanel(TrackListModel model) {
		super();
		this.model = model;
		this.model.addListDataListener(this);
		this.jpTrackList = new JPanel();
		this.jpTrackList.setLayout(new BoxLayout(jpTrackList, BoxLayout.PAGE_AXIS));
		setActionMap(TrackListActionMap.getActionMap());
		setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, TrackListActionMap.getInputMap(this));
		this.trackMenu = new TrackMenu(getActionMap());
		getVerticalScrollBar().setUnitIncrement(SCROLL_BAR_UNIT_INCREMENT);
		getVerticalScrollBar().setBlockIncrement(SCROLL_BAR_BLOCK_INCREMENT);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		rebuildPanel();
	}


	@Override
	public void contentsChanged(ListDataEvent e) {
		rebuildPanel();
	}


	/**
	 * Copies the selected track.
	 */
	public void copyTrack() {
		if (selectedTrack != null) {
			try {
				/* we need to clone the selected track because the user may copy the track
				then modify it and finally paste the track.  If we don't do the deep clone
				all the modification made after the cloning will be copied (and we don't want that) */
				copiedTrack = selectedTrack.deepClone();
			} catch (Exception e) {
				ExceptionManager.handleException(this, e, "Error while copying the track");
			}
		}
	}


	/**
	 * Cuts the selected track
	 */
	public void cutTrack() {
		if (selectedTrack != null) {
			try {
				copiedTrack = selectedTrack;
				getModel().deleteTrack(selectedTrack);
				selectedTrack = null;
			} catch (Exception e) {
				ExceptionManager.handleException(this, e, "Error while cutting the track");
			}
		}
	}


	/**
	 * @return the model containing the data displayed in this panel
	 */
	public TrackListModel getModel() {
		return model;
	}


	/**
	 * @return the selected track
	 */
	public Track getSelectedTrack() {
		return selectedTrack;
	}


	@Override
	public void intervalAdded(ListDataEvent e) {
		rebuildPanel();
	}


	@Override
	public void intervalRemoved(ListDataEvent e) {
		rebuildPanel();
	}


	/**
	 * @return true if there is a track to paste
	 */
	public boolean isPasteEnable() {
		return (copiedTrack != null);
	}


	/**
	 * Locks the handles of all the tracks
	 */
	public void lockTrackHandles() {
		for (Track currentTrack: getModel().getTracks()) {
			if (currentTrack != null) {
				currentTrack.lockHandle();
			}
		}
	}


	private void rebuildPanel() {
		jpTrackList.removeAll();
		Track[] trackList = getModel().getTracks();
		for(int i = 0; i < trackList.length; i++) {
			trackList[i].setNumber(i + 1);
			jpTrackList.add(trackList[i]);
			trackList[i].addTrackListener(this);
		}
		jpTrackList.revalidate();
		setViewportView(jpTrackList);
	}


	/**
	 * Called when a track is released after being dragged in the list
	 */
	public void releaseTrack() {
		if ((draggedTrack != null) && (draggedOverTrack != null)) {
			draggedOverTrack.setBorder(TrackConstants.REGULAR_BORDER);
			if (getMousePosition() != null) {
				int insertIndex = getModel().indexOf(draggedOverTrack);
				getModel().deleteTrack(draggedTrack);
				getModel().insertTrack(insertIndex, draggedTrack);
			}
		}
		draggedTrack = null;
		draggedOverTrack = null;
		unlockTrackHandles();
	}


	@Override
	public void trackChanged(TrackEvent evt) {
		if (evt.getEventType() == TrackEventType.RIGHT_CLICKED) {
			selectedTrack = (Track)evt.getSource();
			trackMenu.setTrack(selectedTrack);
			Point mousePoint = getMousePosition();
			if (mousePoint != null) {
				trackMenu.show(this, mousePoint.x, mousePoint.y);
			}
		} else if (evt.getEventType() == TrackEventType.DRAGGED) {
			// set the dragged track
			if (draggedTrack == null) {
				draggedTrack = ((Track)evt.getSource());
			}
			trackDragged();
		} else if (evt.getEventType() == TrackEventType.RELEASED) {
			releaseTrack();
		} else if (evt.getEventType() == TrackEventType.SELECTED) {
			// unselect the previously selected track (if any)
			if (selectedTrack != null) {
				selectedTrack.setSelected(false);
			}
			// set the new selected track
			selectedTrack = (Track)evt.getSource();
		} else if (evt.getEventType() == TrackEventType.UNSELECTED) {
			selectedTrack = null;
		}
	}


	/**
	 * Called when a track is being dragged
	 */
	public void trackDragged() {
		lockTrackHandles();
		if (getMousePosition() != null) {
			for (Track currentTrack: getModel().getTracks()) {
				int currentTrackTop = currentTrack.getY() - getVerticalScrollBar().getValue();
				int currentrackBottom = (currentTrack.getY() + currentTrack.getHeight()) - getVerticalScrollBar().getValue();
				if ((getMousePosition().y > currentTrackTop) && ( getMousePosition().y < currentrackBottom) && (draggedOverTrack != currentTrack)) {
					if (draggedOverTrack != null) {
						draggedOverTrack.setBorder(TrackConstants.REGULAR_BORDER);
					}
					draggedOverTrack= currentTrack;
					if (draggedOverTrack == draggedTrack) {
						draggedOverTrack.setBorder(TrackConstants.DRAG_START_BORDER);
					} else if (draggedOverTrack.getNumber() < draggedTrack.getNumber()) {
						draggedOverTrack.setBorder(TrackConstants.DRAG_UP_BORDER);
					} else {
						draggedOverTrack.setBorder(TrackConstants.DRAG_DOWN_BORDER);
					}
				}
			}
		}
	}


	/**
	 * Unlocks the handles of all the tracks
	 */
	public void unlockTrackHandles() {
		for (Track currentTrack: getModel().getTracks()) {
			if (currentTrack != null) {
				currentTrack.unlockHandle();
			}
		}
	}
}
