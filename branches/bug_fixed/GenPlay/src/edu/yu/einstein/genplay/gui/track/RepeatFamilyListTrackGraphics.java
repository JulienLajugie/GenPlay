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
import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.yu.einstein.genplay.core.RepeatFamily;
import edu.yu.einstein.genplay.core.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.core.list.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * A {@link TrackGraphics} part of a {@link RepeatFamilyListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamilyListTrackGraphics extends TrackGraphics<RepeatFamilyList> {

	private static final long 	serialVersionUID = 477730131587880969L; // generated ID
	private static final int  	SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private static final short 	REPEAT_HEIGHT = 6;						// height of a repeat in pixel
	private static final short 	SPACE_HEIGHT = 3;						// height of the space between two families of repeats
	private int 				firstLineToDisplay = 0;					// number of the first line to be displayed
	private int 				repeatLinesCount = 0;					// number of lines of repeats
	private int 				mouseStartDragY = -1;					// position of the mouse when start dragging
	private List<String> 		familyNames;							// list containing all the families of the repeat track
	private String 				highlightedFamilyName = null;			// name of the highlighted family (family with cursor over)
	private String				selectedFamilyName = null;					// name of the selected family (family is selected if right clicked)


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(repeatLinesCount);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		repeatLinesCount = in.readInt();
		firstLineToDisplay = 0;
		mouseStartDragY = -1;
		generateFamilyNameList();
	}



	/**
	 * Creates an instance of {@link RepeatFamilyListTrackGraphics}
	 * @param data list of repeats to display
	 */
	protected RepeatFamilyListTrackGraphics(RepeatFamilyList data) {
		super(data);
		generateFamilyNameList();
	}


	@Override
	protected void chromosomeChanged() {
		repaint();
		super.chromosomeChanged();
	}


	/**
	 * Generates the sorted list of the repeat families of the track
	 */
	private void generateFamilyNameList() {
		familyNames = new ArrayList<String>();
		for (List<RepeatFamily> currentChromoList: data) {
			for (RepeatFamily currentRepeatFamily: currentChromoList) {
				String currentRepeatFamilyName = currentRepeatFamily.getName();
				if (!familyNames.contains(currentRepeatFamilyName)) {
					familyNames.add(currentRepeatFamilyName);
				}
			}
		}
		Collections.sort(familyNames);
	}


	/**
	 * Draws the repeats
	 * @param g {@link Graphics}
	 */
	private void drawRepeat(Graphics g) {
		int currentHeight = SPACE_HEIGHT;
		int width = getWidth();
		int height = getHeight();
		List<RepeatFamily> repeatFamilyList = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		if ((repeatFamilyList != null) && (repeatFamilyList.size() > 0)) {
			// calculate how many lines are displayable
			int displayedLineCount = ((getHeight() - SPACE_HEIGHT) / (REPEAT_HEIGHT + (2 * SPACE_HEIGHT))) + 1;
			// calculate how many scroll on the Y axis are necessary to show all the repeats
			repeatLinesCount = (familyNames.size() - displayedLineCount) + 2;
			int currentColor = firstLineToDisplay;
			// loop for each line of the track
			for (int i = 0; i < displayedLineCount; i++) {
				if ((i + firstLineToDisplay) < familyNames.size()) {
					// retrieve the repeat associated to the current line to draw
					String familyToDraw = familyNames.get(i + firstLineToDisplay);
					RepeatFamily currentFamily = null;
					int indexFamilyToDisplay = 0;
					while ((currentFamily == null) && (indexFamilyToDisplay < repeatFamilyList.size())) {
						if (repeatFamilyList.get(indexFamilyToDisplay).getName().equals(familyToDraw)) {
							currentFamily = repeatFamilyList.get(indexFamilyToDisplay);
						}
						indexFamilyToDisplay++;
					}
					// calculate if the background is white or gray
					if ((currentColor % 2) == 1) {
						g.setColor(Colors.LIGHT_GREY);
						if (selectedFamilyName == null) {
							g.fillRect(0, currentHeight, width, REPEAT_HEIGHT + (2 * SPACE_HEIGHT));
						} else if ((selectedFamilyName != null) && (selectedFamilyName.equals(familyToDraw))) {
							g.fillRect(0, 0, width, height);
						}
					}
					if ((selectedFamilyName == null) && (highlightedFamilyName != null) && (familyToDraw.equals(highlightedFamilyName))) {
						g.setColor(Colors.BLACK);
						g.drawRect(0, currentHeight, width, REPEAT_HEIGHT + (2 * SPACE_HEIGHT) - 1);
					}
					currentHeight += SPACE_HEIGHT;
					// calculate the color of the line
					g.setColor(intToColor(currentColor));
					// loop for each repeat of the current family
					if (currentFamily != null) {
						for(ChromosomeWindow currentRepeat : currentFamily.getRepeatList()) {
							if (currentRepeat != null) {
								int x = projectWindow.genomePosToScreenXPos(currentRepeat.getStart());
								int repeatWidth = projectWindow.genomePosToScreenXPos(currentRepeat.getStop()) - x;
								if (repeatWidth < 1) {
									repeatWidth = 1;
								}
								if (selectedFamilyName == null) {
									g.fillRect(x, currentHeight, repeatWidth, REPEAT_HEIGHT);
								} else if ((selectedFamilyName != null) && (selectedFamilyName.equals(familyToDraw))) {
									g.fillRect(x, 0, repeatWidth, height);
								}
							}
						}
					}
					// calculate the witdh of the text of the repeat name
					int textWidth = fm.stringWidth(familyToDraw);
					// draw a rectangle under the text with the color of the background
					if ((currentColor % 2) == 1) {
						g.setColor(Colors.LIGHT_GREY);
					} else {
						g.setColor(Colors.WHITE);
					}
					if (selectedFamilyName == null) {
						g.fillRect(1, currentHeight, textWidth + 2, REPEAT_HEIGHT);
					} else if ((selectedFamilyName != null) && (selectedFamilyName.equals(familyToDraw))) {
						int yTextRect = (height / 2) - (fm.getHeight());
						int heightTextRect = fm.getHeight() + 5;
						g.fillRect(1, yTextRect, textWidth + 2, heightTextRect);
					}
					currentHeight += REPEAT_HEIGHT;
					// Write the repeat name
					g.setColor(intToColor(currentColor));
					if (selectedFamilyName == null) {
						g.drawString(familyToDraw, 2, currentHeight);
					} else if ((selectedFamilyName != null) && (selectedFamilyName.equals(familyToDraw))) {
						g.drawString(familyToDraw, 2, height / 2);
					}
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
		drawMask(g);
		drawMultiGenomeInformation(g);
		drawHeaderTrack(g);
		drawMiddleVerticalLine(g);
	}


	/**
	 * Associates a {@link Color} to an integer value
	 * @param i integer value
	 * @return a {@link Color}
	 */
	private Color intToColor(int i) {
		Color[] colorArray = {Colors.BLACK, Colors.GREEN, Colors.BLUE, Colors.DARK_YELLOW, Colors.RED, Color.CYAN, Color.MAGENTA, Colors.ORANGE};
		i = i % colorArray.length;
		return colorArray[i];
	}


	/**
	 * @param yPosition a y position on the track
	 * @return the repeat family at this y position. Null if none
	 */
	private String getFamilyRolledOver(int yPosition) {
		int repeatHeight = REPEAT_HEIGHT + (2 * SPACE_HEIGHT);
		int highlightedFamilyIndex = (int) (yPosition / repeatHeight) + firstLineToDisplay;
		if (highlightedFamilyIndex >= familyNames.size()) {
			return null;
		} else {
			return familyNames.get(highlightedFamilyIndex);
		}
	}


	/**
	 * Changes the scroll position of the panel when mouse dragged with the right button
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			int distance = (mouseStartDragY - e.getY()) / (REPEAT_HEIGHT + (2 * SPACE_HEIGHT));
			if (Math.abs(distance) > 0) {
				if (((distance < 0) && ((distance + firstLineToDisplay) >= 0))
						|| ((distance > 0) && ((distance + firstLineToDisplay) <= repeatLinesCount))) {
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
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			mouseStartDragY = e.getY();
		}
	}


	/**
	 * Changes the scroll position of the panel when the wheel of the mouse is used with the right button
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
			if (((e.getWheelRotation() < 0) && ((e.getWheelRotation() + firstLineToDisplay) >= 0))
					|| ((e.getWheelRotation() > 0) && ((e.getWheelRotation() + firstLineToDisplay) <= repeatLinesCount))) {
				firstLineToDisplay += e.getWheelRotation();
				repaint();
			}
		} else {
			super.mouseWheelMoved(e);
		}
	}


	/**
	 * Sets the family with the cursor over when the cursor moves
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		int mouseYPosition = e.getPoint().y;
		String newHighlightedFamilyName = getFamilyRolledOver(mouseYPosition);
		if (newHighlightedFamilyName != highlightedFamilyName) {
			highlightedFamilyName = newHighlightedFamilyName;
			repaint();
		}
		super.mouseMoved(e);
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// handle right clicks
		if ((e.getModifiers() == InputEvent.BUTTON3_MASK) && (e.getClickCount() == 2)){
			if (selectedFamilyName != null) {
				selectedFamilyName = null;
				repaint();
			} else {
				int mouseYPosition = e.getPoint().y;
				String newSelectedFamilyName = getFamilyRolledOver(mouseYPosition);
				if (newSelectedFamilyName != selectedFamilyName) {
					selectedFamilyName = newSelectedFamilyName;
					repaint();
				}
			}
		}
		super.mouseClicked(e);
	}


	@Override
	public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		highlightedFamilyName = null;
		repaint();
	}


	@Override
	protected void xFactorChanged() {
		firstLineToDisplay = 0;
		repaint();
		super.xFactorChanged();
	}
}
