package edu.yu.einstein.genplay.gui.trackList;

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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.track.TrackHandle;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * 
 * @author Julien Lajugie
 */
public class TrackHandleRenderer extends JLabel implements MouseListener, MouseMotionListener, TrackEventsGenerator {
	private static final long serialVersionUID = -1789820124134205454L;				// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;						// saved format version
	private static final int 	HANDLE_WIDTH = 50;									// width of the handle
	private static final int 	MOVE_RESIZE_ZONE_HEIGHT = 10;						// height of the resize zone
	private static final Color 	BACKGROUND_COLOR = Colors.TRACK_HANDLE_BACKGROUND;	// background color
	private static final Color 	ROLLOVER_COLOR = Colors.TRACK_HANDLE_ROLLOVER; 		// rollover color
	private static final Color 	SELECTED_COLOR = Colors.TRACK_HANDLE_SELECTED; 		// selected color
	private static final String FONT_NAME = "ARIAL";								// name of the font
	private static final int 	FONT_SIZE = 12;										// size of the font

	private int					trackNumber;				// number of the track
	private JLabel 				jlNumber;					// label with the number of the track
	private int 				startDragY = 0; 			// height of the mouse when start dragging
	private int					resizeHeight = 0;			// seize of the track after resizing by dragging
	private boolean 			trackDragged = false;		// true if the user is dragging the track
	private boolean				selected = false;			// true if the track is selected

	private List<TrackListener> trackListeners;				// list of track listeners


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
		trackListeners = new ArrayList<TrackListener>();
	}


	/**
	 * Creates an instance of {@link TrackHandle}
	 * @param number number of the track
	 */
	public TrackHandleRenderer(int number) {
		setBackground(BACKGROUND_COLOR);
		setPreferredSize(new Dimension(HANDLE_WIDTH - 1, 0));

		trackListeners = new ArrayList<TrackListener>();

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
					notifyTrackListeners(TrackEventType.SELECTED);
				} else {
					setBackground(BACKGROUND_COLOR);
					notifyTrackListeners(TrackEventType.UNSELECTED);
				}
			}
			if (arg0.getButton() == MouseEvent.BUTTON1) {
				if ((getHeight() - arg0.getY()) <= MOVE_RESIZE_ZONE_HEIGHT) {
					if (arg0.getClickCount() == 2) {
						notifyTrackListeners(TrackEventType.SIZE_SET_TO_DEFAULT);
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
					notifyTrackListeners(TrackEventType.SELECTED);
				}
				setBackground(ROLLOVER_COLOR);
				notifyTrackListeners(TrackEventType.RIGHT_CLICKED);
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		startDragY = 0;
		if (trackDragged) {
			trackDragged = false;
			notifyTrackListeners(TrackEventType.RELEASED);
		}
	}


	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (startDragY != 0) {
			resizeHeight = arg0.getY() - startDragY;
			notifyTrackListeners(TrackEventType.RESIZED);
			startDragY = arg0.getY();
		}
		if (trackDragged) {
			notifyTrackListeners(TrackEventType.DRAGGED);
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


	@Override
	public void addTrackListener(TrackListener trackListener) {
		if (!trackListeners.contains(trackListener)) {
			trackListeners.add(trackListener);
		}
	}


	@Override
	public TrackListener[] getTrackListeners() {
		TrackListener[] listeners = new TrackListener[trackListeners.size()];
		return trackListeners.toArray(listeners);
	}


	@Override
	public void removeTrackListener(TrackListener trackListener) {
		trackListeners.remove(trackListener);
	}
	
	
	/**
	 * Notify all the track listener that the track changed
	 * @param trackEventType track event type
	 */
	public void notifyTrackListeners(TrackEventType trackEventType) {
		TrackEvent trackEvent = new TrackEvent(this, trackEventType);
		for (TrackListener listener: trackListeners) {
			listener.trackChanged(trackEvent);
		}
	}


	/**
	 * @return the height of the resizing when the handle is dragged
	 */
	public int getResizeHeight() {
		return resizeHeight;
	}
	

}
