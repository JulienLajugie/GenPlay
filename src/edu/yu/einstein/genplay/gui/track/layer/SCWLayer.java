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
import java.util.List;

import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.GraphType;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOMaxScoreToDisplay;
import edu.yu.einstein.genplay.core.list.SCWList.operation.SCWLOMinScoreToDisplay;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.LayerColors;


/**
 * Layer displaying a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 */
public class SCWLayer extends AbstractVersionedLayer<ScoredChromosomeWindowList> implements Layer<ScoredChromosomeWindowList>, VersionedLayer<ScoredChromosomeWindowList>, GraphLayer, ColoredLayer {

	private static final long serialVersionUID = 3779631846077486596L; // generated ID
	private GraphType 	graphType;	// type of graph display in the layer
	private Color		color;		// color of the layer


	/**
	 * Creates an instance of a {@link SCWLayer}
	 * @param track track containing the layer
	 * @param data data of the layer
	 * @param name name of the layer
	 */
	public SCWLayer(Track track, ScoredChromosomeWindowList data, String name) {
		super(track, data, name);
		setGraphType(TrackConstants.DEFAULT_GRAPH_TYPE);
		color = LayerColors.getLayerColor();
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			Graphics2D g2D = (Graphics2D)g;
			switch(getGraphType()) {
			case BAR:
				g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				drawBarGraph(g, width, height);
				break;
			case CURVE:
				g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				drawCurveGraph(g, width, height);
				break;
			case POINTS:
				g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				drawPointGraph(g, width, height);
				break;
			case DENSE:
				g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				drawDenseGraph(g, width, height);
				break;
			}
		}
	}


	/**
	 * Draws the layer as a bar graph
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawBarGraph(Graphics g, int width, int height) {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			int screenY0 = getTrack().getScore().scoreToScreenPosition(0);
			Color reverseCurveColor;
			if (!getColor().equals(Color.BLACK)) {
				reverseCurveColor = new Color(getColor().getRGB() ^ 0xffffff);
			} else {
				reverseCurveColor = Colors.GREY;
			}
			reverseCurveColor = new Color(reverseCurveColor.getRed(), reverseCurveColor.getGreen(), reverseCurveColor.getBlue(), getColor().getAlpha());
			List<ScoredChromosomeWindow> listToPrint = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXRatio());
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x = projectWindow.genomeToScreenPosition(currentWindow.getStart());
					int widthWindow = projectWindow.genomeToScreenPosition(currentWindow.getStop()) - x;

					if (widthWindow < 1) {
						widthWindow = 1;
					}

					int y = getTrack().getScore().scoreToScreenPosition(currentWindow.getScore());
					int rectHeight = y - screenY0;
					if (currentWindow.getScore() > 0) {
						g.setColor(getColor());
						g.fillRect(x, y, widthWindow, -rectHeight);
					} else {
						g.setColor(reverseCurveColor);
						g.fillRect(x, screenY0, widthWindow, rectHeight);
					}
				}
			}
		}
	}


	/**
	 * Draws the layer as a curve graph
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawCurveGraph(Graphics g, int width, int height) {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			g.setColor(getColor());
			List<ScoredChromosomeWindow> listToPrint = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXRatio());
			if ((listToPrint != null) && (listToPrint.size() > 0)) {
				int x1 = -1;
				int x2 = -1;
				double score1 = -1;
				int y1 = -1;
				double score2 = -1;
				int y2 = -1;
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					x2 = projectWindow.genomeToScreenPosition(currentWindow.getStart());
					score2 = currentWindow.getScore();
					y2 = getTrack().getScore().scoreToScreenPosition(score2);
					if (x1 != -1) {
						if ((score1 == 0) && (score2 != 0)) {
							g.drawLine(x2, y1, x2, y2);
						} else if ((score1 != 0) && (score2 == 0)) {
							g.drawLine(x1, y1, x2, y1);
							g.drawLine(x2, y1, x2, y2);
						} else if ((score1 != 0) && (score2 != 0)) {
							g.drawLine(x1, y1, x2, y2);
						}
					}
					x1 = x2;
					score1 = score2;
					y1 = y2;
				}
				if (x1 != -1) {
					if ((score1 == 0) && (score2 != 0)) {
						g.drawLine(x2, y1, x2, y2);
					} else if ((score1 != 0) && (score2 == 0)) {
						g.drawLine(x1, y1, x2, y1);
						g.drawLine(x2, y1, x2, y2);
					} else if ((score1 != 0) && (score2 != 0)) {
						g.drawLine(x1, y1, x2, y2);
					}
				}
			}
		}
	}


	/**
	 * Draws the layer as a dense graph
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawDenseGraph(Graphics g, int width, int height) {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			List<ScoredChromosomeWindow> listToPrint = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXRatio());
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x = projectWindow.genomeToScreenPosition(currentWindow.getStart());
					int widthWindow = projectWindow.genomeToScreenPosition(currentWindow.getStop()) - x;
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					double scoreMin = getTrack().getScore().getMinimumScore();
					double scoreMax = getTrack().getScore().getMaximumScore();
					g.setColor(Colors.scoreToColor(currentWindow.getScore(), scoreMin, scoreMax));
					g.fillRect(x, 0, widthWindow, height);
				}
			}
		}
	}


	/**
	 * Draws the layer as a point graph
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawPointGraph(Graphics g, int width, int height) {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			g.setColor(getColor());
			List<ScoredChromosomeWindow> listToPrint = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXRatio());
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x1 = projectWindow.genomeToScreenPosition(currentWindow.getStart());
					int x2 = projectWindow.genomeToScreenPosition(currentWindow.getStop());
					if ((x2 - x1) < 1) {
						x2 = x1 + 1;
					}
					int y = getTrack().getScore().scoreToScreenPosition(currentWindow.getScore());
					g.drawLine(x1, y, x2, y);
				}
			}
		}
	}


	@Override
	public Color getColor() {
		return color;
	}


	@Override
	public Double getCurrentScoreToDisplay() {
		if (getData() != null) {
			double middlePosition = ProjectManager.getInstance().getProjectWindow().getGenomeWindow().getMiddlePosition();
			return getData().getScore((int) middlePosition);
		} else {
			return 0d;
		}
	}


	@Override
	public GraphType getGraphType() {
		return graphType;
	}


	@Override
	public double getMaximumScoreToDisplay() {
		return new SCWLOMaxScoreToDisplay(getData()).compute();
	}


	@Override
	public double getMinimumScoreToDisplay() {
		return new SCWLOMinScoreToDisplay(getData()).compute();
	}


	@Override
	public LayerType getType() {
		return LayerType.SCW_LAYER;
	}


	@Override
	public void setColor(Color color) {
		this.color = color;
	}


	@Override
	public void setData(ScoredChromosomeWindowList data) {
		super.setData(data);
		// tells the track score object to auto-rescale the score axis
		if ((getTrack() != null) && (getTrack().getScore() != null)) {
			getTrack().getScore().autorescaleScoreAxis();
		}
	}


	@Override
	public void setData(ScoredChromosomeWindowList data, String description) {
		super.setData(data, description);
		// tells the track score object to auto-rescale the score axis
		if ((getTrack() != null) && (getTrack().getScore() != null)) {
			getTrack().getScore().autorescaleScoreAxis();
		}
	}


	@Override
	public void setGraphType(GraphType graphType) {
		this.graphType = graphType;
	}
}
