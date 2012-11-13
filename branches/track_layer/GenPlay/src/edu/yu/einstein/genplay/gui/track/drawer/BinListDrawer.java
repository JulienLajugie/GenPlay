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

import edu.yu.einstein.genplay.core.enums.GraphType;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.GenPlayColor;



/**
 * Draws the data of a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListDrawer extends CurveDrawer {

	private final BinList binList; // data to draw


	/**
	 * Creates an instance of {@link BinListDrawer}
	 * @param graphics {@link Graphics} of a track
	 * @param trackWidth width of a track
	 * @param trackHeight height of a track
	 * @param scoreMin score minimum to display
	 * @param scoreMax score maximum to display
	 * @param trackColor color of the curve
	 * @param typeOfGraph type of graph
	 * @param binList data to draw
	 */
	public BinListDrawer(Graphics graphics, int trackWidth, int trackHeight, double scoreMin, double scoreMax, Color trackColor, GraphType typeOfGraph, BinList binList) {
		super(graphics, trackWidth, trackHeight, scoreMin, scoreMax, trackColor, typeOfGraph);
		this.binList = binList;
	}


	@Override
	protected void drawBarGraphics() {
		double[] data = binList.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			// Compute the reverse color
			Color reverseCurveColor = Colors.GREY;
			if (!trackColor.equals(Color.black)) {
				reverseCurveColor = new Color(trackColor.getRGB() ^ 0xffffff);
			}
			int currentMinX = projectWindow.getGenomeWindow().getStart();
			int currentMaxX = projectWindow.getGenomeWindow().getStop();
			// Compute the Y = 0 position
			int screenY0 = scoreToScreenPos(0);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;
			int i = 0;
			int screenWindowWidth = (int)Math.ceil(windowData * projectWindow.getXFactor());
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenXPosition = projectWindow.genomePosToScreenXPos(currentGenomePosition);
					int screenYPosition = scoreToScreenPos(currentIntensity);
					int rectHeight = screenYPosition - screenY0;

					if (currentIntensity > 0) {
						graphics.setColor(trackColor);
						rectHeight *= -1;
					} else {
						graphics.setColor(reverseCurveColor);
						screenYPosition = screenY0;
					}

					if (currentGenomePosition <= currentMinX) {
						int screenWindowWidthTmp = projectWindow.twoGenomePosToScreenWidth(currentGenomePosition, currentGenomePosition + windowData);
						graphics.fillRect(screenXPosition, screenYPosition, screenWindowWidthTmp, rectHeight);
					} else {
						graphics.fillRect(screenXPosition, screenYPosition, screenWindowWidth, rectHeight);
					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + (i * windowData);
			}
		}
	}


	@Override
	protected void drawCurveGraphics() {
		double[] data = binList.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = projectWindow.getGenomeWindow().getStart();
			int currentMaxX = projectWindow.getGenomeWindow().getStop();
			graphics.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;
			int i = 0;
			int screenWindowWidth = (int)Math.round(windowData * projectWindow.getXFactor());
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				int nextIndex = (currentGenomePosition + windowData) / windowData;
				if ((currentGenomePosition >= 0) && (nextIndex < data.length)){
					double currentIntensity = data[currentIndex];
					double nextIntensity = data[nextIndex];
					//int screenX1Position = genomePosToScreenPos(currentGenomePosition);
					int screenX1Position = (int)Math.round((currentGenomePosition - projectWindow.getGenomeWindow().getStart()) * projectWindow.getXFactor());
					int screenX2Position = screenX1Position + screenWindowWidth;
					int screenY1Position = scoreToScreenPos(currentIntensity);
					int screenY2Position = scoreToScreenPos(nextIntensity);
					if ((currentIntensity == 0) && (nextIntensity != 0)) {
						graphics.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);
					} else if ((currentIntensity != 0) && (nextIntensity == 0)) {
						graphics.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY1Position);
						graphics.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);
					} else if ((currentIntensity != 0) && (nextIntensity != 0)) {
						graphics.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY2Position);
					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + (i * windowData);
			}
		}
	}


	@Override
	protected void drawDenseGraphics() {
		double[] data = binList.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = projectWindow.getGenomeWindow().getStart();
			int currentMaxX = projectWindow.getGenomeWindow().getStop();
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;
			int i = 0;
			int screenWindowWidth = (int)Math.ceil(windowData * projectWindow.getXFactor());
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenXPosition = projectWindow.genomePosToScreenXPos(currentGenomePosition);
					graphics.setColor(GenPlayColor.scoreToColor(currentIntensity, scoreMin, scoreMax));
					graphics.fillRect(screenXPosition, 0, screenWindowWidth, trackHeight);
				}
				i++;
				currentGenomePosition = firstGenomePosition + (i * windowData);
			}
		}
	}


	@Override
	protected void drawPointGraphics() {
		double[] data = binList.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = projectWindow.getGenomeWindow().getStart();
			int currentMaxX = projectWindow.getGenomeWindow().getStop();
			graphics.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;
			int i = 0;
			int screenWindowWidth = (int)Math.round(windowData * projectWindow.getXFactor());
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenX1Position = projectWindow.genomePosToScreenXPos(currentGenomePosition);
					//int screenX2Position = screenX1Position + screenWindowWidth;
					int screenYPosition = scoreToScreenPos(currentIntensity);
					int screenX2Position;
					if (currentGenomePosition <= currentMinX) {
						screenX2Position = projectWindow.twoGenomePosToScreenWidth(currentGenomePosition, currentGenomePosition + windowData);
					} else {
						screenX2Position = screenX1Position + screenWindowWidth;
					}

					graphics.drawLine(screenX1Position, screenYPosition, screenX2Position, screenYPosition);
				}
				i++;
				currentGenomePosition = firstGenomePosition + (i * windowData);
			}
		}
	}
}
