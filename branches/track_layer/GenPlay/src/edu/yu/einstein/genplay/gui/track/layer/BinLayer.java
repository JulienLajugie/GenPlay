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
package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import edu.yu.einstein.genplay.core.enums.GraphType;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOMaxScoreToDisplay;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOMinScoreToDisplay;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.GenPlayColor;
import edu.yu.einstein.genplay.util.colors.TrackColor;


/**
 * Layer displaying a {@link BinList}
 * @author Julien Lajugie
 */
public class BinLayer extends AbstractLayer<BinList> implements Layer<BinList>, GraphLayer, ColoredLayer {

	private static final long serialVersionUID = 3779631846077486596L; // generated ID
	private GraphType 	graphType;	// type of graph display in the layer
	private Color		color;		// color of the layer


	/**
	 * Creates an instance of a {@link BinLayer}
	 */
	public BinLayer() {
		this.setGraphType(TrackConstants.DEFAULT_GRAPH_TYPE);
		this.color = TrackColor.getTrackColor();
	}


	/**
	 * Draws the layer as a bar graph
	 * @param g
	 */
	private void drawBarGraph(Graphics g) {
		if (this.getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			double[] binListData = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			if (binListData != null) {
				int windowData = getData().getFittedBinSize();
				// Compute the reverse color
				Color reverseCurveColor = Colors.GREY;
				if (!getColor().equals(Color.black)) {
					reverseCurveColor = new Color(getColor().getRGB() ^ 0xffffff);
				}
				int currentMinX = projectWindow.getGenomeWindow().getStart();
				int currentMaxX = projectWindow.getGenomeWindow().getStop();
				// Compute the Y = 0 position
				int screenY0 = getTrack().getScore().scoreToScreenPos(0);
				// First position
				int firstGenomePosition = (currentMinX / windowData) * windowData;
				int currentGenomePosition = firstGenomePosition;
				int i = 0;
				int screenWindowWidth = (int)Math.ceil(windowData * projectWindow.getXFactor());
				while (currentGenomePosition < currentMaxX) {
					int currentIndex = currentGenomePosition / windowData;
					if ((currentGenomePosition >= 0) && (currentIndex < binListData.length)){
						double currentIntensity = binListData[currentIndex];
						int screenXPosition = projectWindow.genomePosToScreenXPos(currentGenomePosition);
						int screenYPosition = getTrack().getScore().scoreToScreenPos(currentIntensity);
						int rectHeight = screenYPosition - screenY0;

						if (currentIntensity > 0) {
							g.setColor(getColor());
							rectHeight *= -1;
						} else {
							g.setColor(reverseCurveColor);
							screenYPosition = screenY0;
						}

						if (currentGenomePosition <= currentMinX) {
							int screenWindowWidthTmp = projectWindow.twoGenomePosToScreenWidth(currentGenomePosition, currentGenomePosition + windowData);
							g.fillRect(screenXPosition, screenYPosition, screenWindowWidthTmp, rectHeight);
						} else {
							g.fillRect(screenXPosition, screenYPosition, screenWindowWidth, rectHeight);
						}
					}
					i++;
					currentGenomePosition = firstGenomePosition + (i * windowData);
				}
			}
		}
	}


	/**
	 * Draws the layer as a curve graph
	 * @param g
	 */
	private void drawCurveGraph(Graphics g) {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			double[] binListData = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			if (binListData != null) {
				int windowData = getData().getFittedBinSize();
				int currentMinX = projectWindow.getGenomeWindow().getStart();
				int currentMaxX = projectWindow.getGenomeWindow().getStop();
				g.setColor(getColor());
				// First position
				int firstGenomePosition = (currentMinX / windowData) * windowData;
				int currentGenomePosition = firstGenomePosition;
				int i = 0;
				int screenWindowWidth = (int)Math.round(windowData * projectWindow.getXFactor());
				while (currentGenomePosition < currentMaxX) {
					int currentIndex = currentGenomePosition / windowData;
					int nextIndex = (currentGenomePosition + windowData) / windowData;
					if ((currentGenomePosition >= 0) && (nextIndex < binListData.length)){
						double currentIntensity = binListData[currentIndex];
						double nextIntensity = binListData[nextIndex];
						//int screenX1Position = genomePosToScreenPos(currentGenomePosition);
						int screenX1Position = (int)Math.round((currentGenomePosition - projectWindow.getGenomeWindow().getStart()) * projectWindow.getXFactor());
						int screenX2Position = screenX1Position + screenWindowWidth;
						int screenY1Position = getTrack().getScore().scoreToScreenPos(currentIntensity);
						int screenY2Position = getTrack().getScore().scoreToScreenPos(nextIntensity);
						if ((currentIntensity == 0) && (nextIntensity != 0)) {
							g.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);
						} else if ((currentIntensity != 0) && (nextIntensity == 0)) {
							g.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY1Position);
							g.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);
						} else if ((currentIntensity != 0) && (nextIntensity != 0)) {
							g.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY2Position);
						}
					}
					i++;
					currentGenomePosition = firstGenomePosition + (i * windowData);
				}
			}
		}
	}


	/**
	 * Draws the layer as a dense graph
	 * @param g
	 */
	private void drawDenseGraph(Graphics g) {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			double[] binListData = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			int windowData = getData().getFittedBinSize();
			if (binListData != null) {
				int currentMinX = projectWindow.getGenomeWindow().getStart();
				int currentMaxX = projectWindow.getGenomeWindow().getStop();
				// First position
				int firstGenomePosition = (currentMinX / windowData) * windowData;
				int currentGenomePosition = firstGenomePosition;
				int i = 0;
				int screenWindowWidth = (int)Math.ceil(windowData * projectWindow.getXFactor());
				while (currentGenomePosition < currentMaxX) {
					int currentIndex = currentGenomePosition / windowData;
					if ((currentGenomePosition >= 0) && (currentIndex < binListData.length)){
						double currentIntensity = binListData[currentIndex];
						int screenXPosition = projectWindow.genomePosToScreenXPos(currentGenomePosition);
						g.setColor(GenPlayColor.scoreToColor(currentIntensity, getTrack().getScore().getMinimumScore(), getTrack().getScore().getMaximumScore()));
						g.fillRect(screenXPosition, 0, screenWindowWidth, getTrack().getHeight());
					}
					i++;
					currentGenomePosition = firstGenomePosition + (i * windowData);
				}
			}
		}
	}


	/**
	 * Draws the layer as a point graph
	 * @param g
	 */
	private void drawPointGraph(Graphics g) {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			double[] data = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());
			int windowData = getData().getFittedBinSize();
			if (data != null) {
				int currentMinX = projectWindow.getGenomeWindow().getStart();
				int currentMaxX = projectWindow.getGenomeWindow().getStop();
				g.setColor(getColor());
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
						int screenYPosition = getTrack().getScore().scoreToScreenPos(currentIntensity);
						int screenX2Position;
						if (currentGenomePosition <= currentMinX) {
							screenX2Position = projectWindow.twoGenomePosToScreenWidth(currentGenomePosition, currentGenomePosition + windowData);
						} else {
							screenX2Position = screenX1Position + screenWindowWidth;
						}

						g.drawLine(screenX1Position, screenYPosition, screenX2Position, screenYPosition);
					}
					i++;
					currentGenomePosition = firstGenomePosition + (i * windowData);
				}
			}
		}
	}


	@Override
	public void drawLayer(Graphics g) {
		Graphics2D g2D = (Graphics2D)g;
		switch(getGraphType()) {
		case BAR:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawBarGraph(g);
			break;
		case CURVE:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			drawCurveGraph(g);
			break;
		case POINTS:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawPointGraph(g);
			break;
		case DENSE:
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			drawDenseGraph(g);
			break;
		}
	}


	@Override
	public Color getColor() {
		return color;
	}


	@Override
	public double getCurrentScoreToDisplay() {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			short currentChromosome = ProjectManager.getInstance().getProjectChromosome().getIndex(projectWindow.getGenomeWindow().getChromosome());
			int xMid = (int) projectWindow.getGenomeWindow().getMiddlePosition();
			if ((getData().get(currentChromosome) != null) && ((xMid / getData().getBinSize()) < getData().size(currentChromosome))) {
				return getData().getScore(xMid);
			}
		}
		return 0;
	}


	@Override
	public GraphType getGraphType() {
		return graphType;
	}


	@Override
	public double getMaximumScoreToDisplay() {
		return new BLOMaxScoreToDisplay(getData()).compute();
	}


	@Override
	public double getMinimumScoreToDisplay() {
		return new BLOMinScoreToDisplay(getData()).compute();
	}


	@Override
	public void setColor(Color color) {
		this.color = color;
	}


	@Override
	public void setGraphType(GraphType graphType) {
		this.graphType = graphType;
	}
}
