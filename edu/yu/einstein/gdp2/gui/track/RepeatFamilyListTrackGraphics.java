/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import yu.einstein.gdp2.core.ChromosomeWindow;
import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.RepeatFamily;
import yu.einstein.gdp2.core.list.repeatFamilyList.RepeatFamilyList;

/**
 * A {@link TrackGraphics} part of a {@link RepeatFamilyListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamilyListTrackGraphics extends TrackGraphics<RepeatFamilyList> {

	private static final long 	serialVersionUID = 477730131587880969L; // generated ID
	private static final short 	REPEAT_HEIGHT = 6;						// height of a repeat in pixel
	private static final short 	SPACE_HEIGHT = 3;						// height of the space between two families of repeats
	private int 				firstLineToDisplay = 0;					// number of the first line to be displayed
	private int 				repeatLinesCount = 0;					// number of lines of repeats
	private int 				mouseStartDragY = -1;					// position of the mouse when start dragging


	/**
	 * Creates an instance of {@link RepeatFamilyListTrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param data list of repeats to display
	 */
	protected RepeatFamilyListTrackGraphics(GenomeWindow displayedGenomeWindow, RepeatFamilyList data) {
		super(displayedGenomeWindow, data);
	}


	@Override
	protected void chromosomeChanged() {
		repaint();
		super.chromosomeChanged();
	}


	/**
	 * Draws the repeats
	 * @param g {@link Graphics}
	 */
	private void drawRepeat(Graphics g) {
		int currentHeight = SPACE_HEIGHT;
		int width = getWidth();
		List<RepeatFamily> repeatFamilyList = data.getFittedData(genomeWindow, xFactor);
		if ((repeatFamilyList != null) && (repeatFamilyList.size() > 0)) {
			// calculate how many lines are displayable
			int displayedLineCount = (getHeight() - SPACE_HEIGHT) / (REPEAT_HEIGHT + 2 * SPACE_HEIGHT) + 1;
			// calculate how many scroll on the Y axis are necessary to show all the repeats
			repeatLinesCount = repeatFamilyList.size() - displayedLineCount + 2;
			int currentColor = firstLineToDisplay;		
			// loop for each line of the track
			for (int i = 0; i < displayedLineCount; i++) { //(RepeatFamily currentFamily : repeatFamilyList) {
				if (i + firstLineToDisplay < repeatFamilyList.size()) {
					// retrieve the repeat associated to the current line to draw
					RepeatFamily currentFamily = repeatFamilyList.get(i + firstLineToDisplay);
					// calculate if the background is white or gray
					if (currentColor % 2 == 1) {
						g.setColor(Color.LIGHT_GRAY);
						g.fillRect(0, currentHeight, width, REPEAT_HEIGHT + 2 * SPACE_HEIGHT);
					}
					// calculate the color of the line
					g.setColor(intToColor(currentColor));
					currentHeight += SPACE_HEIGHT;
					// loop for each repeat of the current family
					for(ChromosomeWindow currentRepeat : currentFamily.getRepeatList()) {
						if (currentRepeat != null) {
							int x = genomePosToScreenPos(currentRepeat.getStart());
							int repeatWidth = genomePosToScreenPos(currentRepeat.getStop()) - x;
							if (repeatWidth < 1) {
								repeatWidth = 1;
							}
							g.fillRect(x, currentHeight, repeatWidth, REPEAT_HEIGHT);
						}
					}
					// calculate the witdh of the text of the repeat name
					int textWidth = fm.stringWidth(currentFamily.getName());
					// draw a rectangle under the text with the color of the background
					if (currentColor % 2 == 1) {
						g.setColor(Color.LIGHT_GRAY);
					} else {
						g.setColor(Color.WHITE);
					}
					g.fillRect(1, currentHeight, textWidth + 2, REPEAT_HEIGHT);
					currentHeight += REPEAT_HEIGHT;
					// Write the repeat name
					g.setColor(intToColor(currentColor));
					g.drawString(currentFamily.getName(), 2, currentHeight);
					currentHeight += SPACE_HEIGHT;				
					currentColor++;
				}
			}	
		}
	}


	@Override
	protected void drawTrack(Graphics g) {
		drawVerticalLines(g);
		drawRepeat(g);
		drawStripes(g);
		drawName(g);
		drawMiddleVerticalLine(g);
	}


	/**
	 * Associates a {@link Color} to an integer value 
	 * @param i integer value
	 * @return a {@link Color}
	 */
	private Color intToColor(int i) {
		Color[] colorArray = {Color.BLACK, Color.GREEN, Color.BLUE, Color.PINK, Color.RED, Color.CYAN, Color.MAGENTA, Color.ORANGE};
		i = i % colorArray.length;
		return colorArray[i];
	}	


	/**
	 * Changes the scroll position of the panel when mouse dragged with the right button
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			int distance = (mouseStartDragY - e.getY()) / (REPEAT_HEIGHT + 2 * SPACE_HEIGHT);
			if (Math.abs(distance) > 0) {
				if (((distance < 0) && (distance + firstLineToDisplay >= 0)) 
						|| ((distance > 0) && (distance + firstLineToDisplay <= repeatLinesCount))) {
					firstLineToDisplay += distance;
					mouseStartDragY = e.getY();
					repaint();
				}
			}
		}		
	}


	/**
	 * Sets the variable mouseStartDragY when the user press the right button of the mouse
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (e.getModifiers() == MouseEvent.BUTTON3_MASK) {
			mouseStartDragY = e.getY();
		}		
	}


	/**
	 * Changes the scroll position of the panel when the wheel of the mouse is used with the right button
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if (((e.getWheelRotation() < 0) && (e.getWheelRotation() + firstLineToDisplay >= 0)) 
					|| ((e.getWheelRotation() > 0) && (e.getWheelRotation() + firstLineToDisplay <= repeatLinesCount))) {
				firstLineToDisplay += e.getWheelRotation();
				repaint();
			}		
		} else {
			super.mouseWheelMoved(e);
		}
	}


	@Override
	protected void xFactorChanged() {
		firstLineToDisplay = 0;
		repaint();
		super.xFactorChanged();
	}
}
