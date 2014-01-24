/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.track;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * The handle of a track
 * <br/><b>Warning:</b> Serialized objects of this class will not be compatible between different implementation of the JVM
 * @author Julien Lajugie
 */
final class HandlePanel extends JPanel implements MouseListener, MouseMotionListener, TrackEventsGenerator {

	private static final long serialVersionUID = -1942543548154386009L;		// generated serial ID
	private static final int 				HANDLE_WIDTH = 50;				// width of the handle
	private static final int 				MOVE_RESIZE_ZONE_HEIGHT = 10;	// height of the resize zone
	private static final String 			FONT_NAME = "ARIAL";			// name of the font
	private static final int 				FONT_SIZE = 12;					// size of the font
	private final JLabel					jlNumber;						// label with the number of the track
	private final List<TrackListener> 		trackListeners;					// list of track listeners
	private int 							startDragY = 0; 				// height of the mouse when start dragging
	private int								newHeight = 0;					// seize of the track after resizing by dragging
	private boolean 						isTrackDragged = false;			// true if the user is dragging the track
	private boolean							isSelected = false;				// true if the track is selected
	private int								number;							// number of the track


	/**
	 * Creates an instance of {@link HandlePanel}
	 * @param number number of the track
	 */
	HandlePanel(int number) {
		this.number = number;
		setBackground(Colors.TRACK_HANDLE_BACKGROUND);
		setPreferredSize(new Dimension(HANDLE_WIDTH - 1, 0));

		trackListeners = new ArrayList<TrackListener>();

		addMouseListener(this);
		addMouseMotionListener(this);

		// Creates and add the number of the track
		jlNumber = new JLabel(Integer.toString(number));
		jlNumber.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		add(jlNumber, gbc);
	}


	@Override
	public void addTrackListener(TrackListener trackListener) {
		if (!trackListeners.contains(trackListener)) {
			trackListeners.add(trackListener);
		}
	}


	/**
	 * @return the height of the resizing when the handle is dragged
	 */
	int getNewHeight() {
		return newHeight;
	}


	/**
	 * @return the number of the track
	 */
	int getNumber() {
		return number;
	}


	@Override
	public TrackListener[] getTrackListeners() {
		TrackListener[] listeners = new TrackListener[trackListeners.size()];
		return trackListeners.toArray(listeners);
	}


	/**
	 * @return true if the track is selected
	 */
	boolean isSelected() {
		return isSelected;
	}


	/**
	 * Locks the handle
	 */
	void lock() {
		setEnabled(false);
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (isEnabled()) {
			if (arg0.getButton() == MouseEvent.BUTTON1) {
				boolean onEdge = (getHeight() - arg0.getY()) <= MOVE_RESIZE_ZONE_HEIGHT;
				if (arg0.getClickCount() == 2) {
					if (onEdge) {
						notifyTrackListeners(TrackEventType.SIZE_SET_TO_DEFAULT);
					} else {
						setSelected(true);
						notifyTrackListeners(TrackEventType.DOUBLE_CLICKED);
					}
				} else {
					if (!onEdge) {
						setSelected(!isSelected());
					}
				}
			} else if (arg0.getButton() == MouseEvent.BUTTON3) {
				setSelected(true);
				notifyTrackListeners(TrackEventType.RIGHT_CLICKED);
			}
		}
	}


	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (startDragY != 0) {
			// we compute the new size of the track
			newHeight = (getHeight() + arg0.getY()) - startDragY;
			// we make sure that the new size is not smaller than the minimum height
			newHeight = Math.max(TrackConstants.MINIMUM_HEIGHT, newHeight);
			// we notify the listeners that the size has been changed
			notifyTrackListeners(TrackEventType.RESIZED);
			// we reset the position where the dragging starts
			startDragY = arg0.getY();
		}
		if (isTrackDragged) {
			notifyTrackListeners(TrackEventType.DRAGGED);
		}
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {}


	/**
	 * Changes the background color
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		if (isEnabled()) {
			if (isSelected) {
				setBackground(Colors.TRACK_HANDLE_SELECTED);
			} else {
				setBackground(Colors.TRACK_HANDLE_BACKGROUND);
			}
		}
	}


	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (isEnabled()) {
			if ((getHeight() - arg0.getY()) <= MOVE_RESIZE_ZONE_HEIGHT) {
				if (arg0.getModifiers() == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) {
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
				}
			} else {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			if ((!(arg0.getModifiers() == MouseEvent.MOUSE_DRAGGED))) {
				setBackground(Colors.TRACK_HANDLE_ROLLOVER);
			}
		}
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		if (isEnabled()) {
			if (arg0.getButton() == MouseEvent.BUTTON1) {
				if ((getHeight() - arg0.getY()) <= MOVE_RESIZE_ZONE_HEIGHT) {
					startDragY = arg0.getY();
				} else {
					isTrackDragged = true;
				}
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		startDragY = 0;
		if (isTrackDragged) {
			isTrackDragged = false;
			notifyTrackListeners(TrackEventType.RELEASED);
		}
	}


	/**
	 * Notify all the track listener that the track changed
	 * @param trackEventType track event type
	 */
	public void notifyTrackListeners(TrackEventType trackEventType) {
		if (trackListeners != null) {
			TrackEvent trackEvent = new TrackEvent(this, trackEventType);
			for (TrackListener listener: trackListeners) {
				listener.trackChanged(trackEvent);
			}
		}
	}


	@Override
	public void removeTrackListener(TrackListener trackListener) {
		trackListeners.remove(trackListener);
	}


	/**
	 * @param number the number of the track to set
	 */
	void setNumber(int number) {
		this.number = number;
		jlNumber.setText(Integer.toString(number));
	}


	/**
	 * @param selected the value to set
	 */
	void setSelected(boolean selected) {
		if (selected != isSelected()) {
			isSelected = selected;
			if (selected) {
				setBackground(Colors.TRACK_HANDLE_ROLLOVER);
				notifyTrackListeners(TrackEventType.SELECTED);
			} else {
				setBackground(Colors.TRACK_HANDLE_BACKGROUND);
				notifyTrackListeners(TrackEventType.UNSELECTED);
			}
		}
	}


	/**
	 * Unlocks the handle
	 */
	void unlock() {
		setEnabled(true);
		if (isSelected) {
			setBackground(Colors.TRACK_HANDLE_ROLLOVER);
		} else {
			setBackground(Colors.TRACK_HANDLE_BACKGROUND);
		}
	}
}
