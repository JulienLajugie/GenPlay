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
package edu.yu.einstein.genplay.gui.track;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * The handle of a track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackHandle extends JPanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -1789820124134205454L;						// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;								// saved format version
	private static final int 	HANDLE_WIDTH = 50;											// width of the handle
	private static final int 	MOVE_RESIZE_ZONE_HEIGHT = 10;								// height of the resize zone
	private static final Color 	BACKGROUND_COLOR = Colors.TRACK_HANDLE_BACKGROUND_COLOR;	// background color
	private static final Color 	ROLLOVER_COLOR = Colors.TRACK_HANDLE_ROLLOVER_COLOR; 		// rollover color
	private static final Color 	SELECTED_COLOR = Colors.TRACK_HANDLE_SELECTED_COLOR; 		// selected color
	private static final String FONT_NAME = "ARIAL";										// name of the font
	private static final int 	FONT_SIZE = 12;												// size of the font

	private int					trackNumber;				// number of the track
	private JLabel 				jlNumber;					// label with the number of the track
	private int 				startDragY = 0; 			// height of the mouse when start draggin
	private boolean 			trackDragged = false;		// true if the user is dragging the track
	private boolean				selected = false;			// true if the track is selected


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(trackNumber);
		out.writeObject(jlNumber);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		trackNumber = in.readInt();
		jlNumber = (JLabel) in.readObject();
		startDragY = 0;
		trackDragged = false;
		selected = false;
	}


	/**
	 * Creates an instance of {@link TrackHandle}
	 * @param number number of the track
	 */
	public TrackHandle(int number) {
		setBackground(BACKGROUND_COLOR);
		setPreferredSize(new Dimension(HANDLE_WIDTH - 1, 0));
		addMouseListener(this);
		addMouseMotionListener(this);

		// Creates and add the number of the track
		trackNumber = number;
		jlNumber = new JLabel(Integer.toString(number));
		jlNumber.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		add(jlNumber, gbc);
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (isEnabled()) {
			if (arg0.getButton() == MouseEvent.BUTTON1) {
				selected = !selected;
				if (selected) {
					setBackground(SELECTED_COLOR);
				} else {
					setBackground(BACKGROUND_COLOR);
				}
				firePropertyChange("selected", !selected, selected);
			}
			if (arg0.getButton() == MouseEvent.BUTTON1) {
				if ((getHeight() - arg0.getY()) <= MOVE_RESIZE_ZONE_HEIGHT) {
					if (arg0.getClickCount() == 2) {
						firePropertyChange("defaultSize", false, true);
					}
				}
			}
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
			if (selected) {
				setBackground(SELECTED_COLOR);
			} else {
				setBackground(BACKGROUND_COLOR);
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
					trackDragged = true;
				}
			}
			if (arg0.getButton() == MouseEvent.BUTTON3) {
				if (!selected) {
					selected = true;
					firePropertyChange("selected", false, true);
				}
				setBackground(ROLLOVER_COLOR);
				firePropertyChange("trackRightClicked", false, true);
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		startDragY = 0;
		if (trackDragged) {
			trackDragged = false;
			firePropertyChange("trackDraggedReleased", false, true);
		}
	}


	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (startDragY != 0) {
			firePropertyChange("resize", 0, arg0.getY() - startDragY);
			startDragY = arg0.getY();
		}
		if (trackDragged) {
			firePropertyChange("trackDragged", false, true);
		}
	}


	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (isEnabled()) {
			if ((getHeight() - arg0.getY()) <= MOVE_RESIZE_ZONE_HEIGHT) {
				if (arg0.getModifiers() == InputEvent.CTRL_DOWN_MASK) {
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
				} else {
					setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
				}
			} else {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			if ((!(arg0.getModifiers() == MouseEvent.MOUSE_DRAGGED))) {
				setBackground(ROLLOVER_COLOR);
			}
		}
	}


	/**
	 * @param trackNumber the trackNumber to set
	 */
	public void setTrackNumber(int trackNumber) {
		this.trackNumber = trackNumber;
		jlNumber.setText(Integer.toString(trackNumber));
	}


	/**
	 * @return the trackNumber
	 */
	public int getTrackNumber() {
		return trackNumber;
	}


	/**
	 * Locks the handle
	 */
	public void lock() {
		setEnabled(false);
	}


	/**
	 * Unlocks the handle
	 */
	public void unlock() {
		setEnabled(true);
		if (selected) {
			setBackground(SELECTED_COLOR);
		} else {
			setBackground(BACKGROUND_COLOR);
		}
	}


	/**
	 * @param selected the value to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		if (selected) {
			setBackground(SELECTED_COLOR);
		} else {
			setBackground(BACKGROUND_COLOR);
		}
	}


	/**
	 * @return true if the track is selected
	 */
	public boolean isSelected() {
		return selected;
	}
}
