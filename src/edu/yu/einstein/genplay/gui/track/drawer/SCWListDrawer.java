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
package edu.yu.einstein.genplay.gui.track.drawer;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import edu.yu.einstein.genplay.core.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.GraphicsType;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.gui.track.SCWListTrackGraphics;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.GenPlayColor;



/**
 * Draws the data of a {@link SCWListTrackGraphics}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWListDrawer extends CurveDrawer {

	private final ScoredChromosomeWindowList data; // data to draw

	
	/**
	 * Creates an instance of {@link SCWListDrawer}
	 * @param graphics {@link Graphics} of a track
	 * @param trackWidth width of a track 
	 * @param trackHeight height of a track
	 * @param scoreMin score minimum to display
	 * @param scoreMax score maximum to display
	 * @param trackColor color of the curve
	 * @param typeOfGraph type of graph
	 * @param data data to draw
	 */
	public SCWListDrawer(Graphics graphics, int trackWidth, int trackHeight, double scoreMin, double scoreMax, Color trackColor, GraphicsType typeOfGraph, ScoredChromosomeWindowList data) {
		super(graphics, trackWidth, trackHeight, scoreMin, scoreMax, trackColor, typeOfGraph);
		this.data = data;
	}

	
	@Override
	protected void drawBarGraphics() {
		if (data != null) {
			int screenY0 = scoreToScreenPos(0);
			Color reverseCurveColor = Colors.GREY;
			if (!trackColor.equals(Colors.BLACK)) {
				reverseCurveColor = new Color(trackColor.getRGB() ^ 0xffffff);
			}		
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					System.out.println(currentWindow.getStart() + " to " + currentWindow.getStop());
					int x = projectWindow.genomePosToScreenXPos(currentWindow.getStart()); 
					int widthWindow = projectWindow.genomePosToScreenXPos(currentWindow.getStop()) - x;
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					int y = scoreToScreenPos(currentWindow.getScore());
					int rectHeight = y - screenY0;
					if (currentWindow.getScore() > 0) {
						graphics.setColor(trackColor);
						graphics.fillRect(x, y, widthWindow, -rectHeight);
					} else {
						graphics.setColor(reverseCurveColor);
						graphics.fillRect(x, screenY0, widthWindow, rectHeight);
					}
				}
			}		
		}
	}

	
	@Override
	protected void drawCurveGraphics() {
		if (data != null) {
			graphics.setColor(trackColor);
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			if ((listToPrint != null) && (listToPrint.size() > 0)) {
				int x1 = -1; 
				int x2 = -1;
				double score1 = -1;
				int y1 = -1;
				double score2 = -1;
				int y2 = -1;
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					x2 = projectWindow.genomePosToScreenXPos(currentWindow.getStart());
					score2 = currentWindow.getScore();
					y2 = scoreToScreenPos(score2);
					if (x1 != -1) {
						if ((score1 == 0) && (score2 != 0)) {
							graphics.drawLine(x2, y1, x2, y2);
						} else if ((score1 != 0) && (score2 == 0)) {
							graphics.drawLine(x1, y1, x2, y1);
							graphics.drawLine(x2, y1, x2, y2);					
						} else if ((score1 != 0) && (score2 != 0)) {
							graphics.drawLine(x1, y1, x2, y2);
						}						
					}
					x1 = x2;
					score1 = score2;
					y1 = y2;					
				}
				if (x1 != -1) {
					if ((score1 == 0) && (score2 != 0)) {
						graphics.drawLine(x2, y1, x2, y2);
					} else if ((score1 != 0) && (score2 == 0)) {
						graphics.drawLine(x1, y1, x2, y1);
						graphics.drawLine(x2, y1, x2, y2);					
					} else if ((score1 != 0) && (score2 != 0)) {
						graphics.drawLine(x1, y1, x2, y2);
					}						
				}				
			}
		}			
	}
	

	@Override
	protected void drawDenseGraphics() {
		if (data != null) {
			//int height = getHeight();			
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x = projectWindow.genomePosToScreenXPos(currentWindow.getStart()); 
					int widthWindow = projectWindow.genomePosToScreenXPos(currentWindow.getStop()) - x;
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					graphics.setColor(GenPlayColor.scoreToColor(currentWindow.getScore(), scoreMin, scoreMax));
					graphics.fillRect(x, 0, widthWindow, trackHeight);
				}
			}		
		}
	}
	

	@Override
	protected void drawPointGraphics() {
		if (data != null) {
			graphics.setColor(trackColor);
			List<ScoredChromosomeWindow> listToPrint = data.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x1 = projectWindow.genomePosToScreenXPos(currentWindow.getStart()); 
					int x2 = projectWindow.genomePosToScreenXPos(currentWindow.getStop());
					if (x2 - x1 < 1) {
						x2 = x1 + 1;
					}
					int y = scoreToScreenPos(currentWindow.getScore());					
					graphics.drawLine(x1, y, x2, y);
				}
			}		
		}	
	}
}
