/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The handle of a track
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackHandle extends JPanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -1789820124134205454L;	// generated ID
	private static final int 	HANDLE_WIDTH = 50;							// width of the handle
	private static final int 	MOVE_RESIZE_ZONE_HEIGHT = 10;				// height of the resize zone
	private static final Color 	BACKGROUND_COLOR = new Color(228, 236, 247);// background color 
	private static final Color 	ROLLOVER_COLOR = new Color(187, 196, 209); 	// rollover color
	private static final Color 	SELECTED_COLOR = new Color(157, 193, 228); 	// selected color
	private static final String FONT_NAME = "ARIAL";						// name of the font
	private static final int 	FONT_SIZE = 12;								// size of the font

	private int					trackNumber;				// number of the track
	private JLabel 				jlNumber;					// label with the number of the track
	private int 				startDragY = 0; 			// height of the mouse when start draggin
	private boolean 			trackDragged = false;		// true if the user is dragging the track
	private boolean				selected = false;			// true if the track is selected
	
	
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
				if (getHeight() - arg0.getY() <= MOVE_RESIZE_ZONE_HEIGHT) {
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
		if ((isEnabled()) && (arg0.getButton() == MouseEvent.BUTTON1)) {
			if (getHeight() - arg0.getY() <= MOVE_RESIZE_ZONE_HEIGHT) {
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
			if (getHeight() - arg0.getY() <= MOVE_RESIZE_ZONE_HEIGHT) {
				if (arg0.getModifiers() == MouseEvent.CTRL_DOWN_MASK) {
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
