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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOMaxScoreToDisplay;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOMinScoreToDisplay;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GraphType;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay.DataScalerForTrackDisplay;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.LayerColors;

/**
 * Abstract class that defining the common methods of {@link BinLayer} and {@link SimpleSCWLayer} objects.
 * @author Julien Lajugie
 * @param <T> type of the data showed in the layer. The type T must implement {@link SCWList}.
 */
public abstract class AbstractSCWLayer<T extends SCWList> extends AbstractVersionedLayer<T> implements Cloneable, Layer<T>, VersionedLayer<T>, GraphLayer, ColoredLayer {

	/** Generated serial ID */
	private static final long serialVersionUID = 1612912685829758955L;

	/**  Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Object that scales the list of SCW for display */
	protected transient DataScalerForTrackDisplay<T, ListView<ScoredChromosomeWindow>>	dataScaler;	// object that scales the list of SCW for display

	/** Type of graph display in the layer */
	private GraphType graphType;

	/** Color of the layer */
	private Color color;


	/**
	 * Creates an instance of {@link AbstractSCWLayer} with the same properties as the specified {@link AbstractSCWLayer}
	 * @param abstractLayer
	 */
	protected AbstractSCWLayer(AbstractSCWLayer<T> abstractSCWLayer) {
		super(abstractSCWLayer);
		this.dataScaler = abstractSCWLayer.dataScaler;
		this.graphType = abstractSCWLayer.graphType;
		this.color = abstractSCWLayer.color;
	}


	/**
	 * Creates an instance of a {@link SimpleSCWLayer}
	 * @param track track containing the layer
	 * @param data data of the layer
	 * @param name name of the layer
	 */
	public AbstractSCWLayer(Track track, T data, String name) {
		super(track, data, name);
		setGraphType(TrackConstants.DEFAULT_GRAPH_TYPE);
		color = LayerColors.getLayerColor();
	}


	@Override
	public abstract AbstractSCWLayer<T> clone();


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
			// check that the data scaler is valid
			validateDataScaler();
			// Retrieve the scw to print
			ListView<ScoredChromosomeWindow> listToPrint = dataScaler.getDataScaledForTrackDisplay();
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					// we want to make sure that x is > 0
					int x = Math.max(0, projectWindow.genomeToScreenPosition(currentWindow.getStart()));
					// we want to make sure that window width is not larger than the screen width
					int widthWindow = Math.min(width, projectWindow.genomeToScreenPosition(currentWindow.getStop()) - x);
					if (widthWindow > 0) {
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
			// check that the data scaler is valid
			validateDataScaler();
			// Retrieve the genes to print
			ListView<ScoredChromosomeWindow> listToPrint = dataScaler.getDataScaledForTrackDisplay();
			if ((listToPrint != null) && (listToPrint.size() > 0)) {
				int x1 = -1;
				int x2 = -1;
				float score1 = Float.NaN;
				int y1 = -1;
				float score2 = Float.NaN;
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
			// check that the data scaler is valid
			validateDataScaler();
			// Retrieve the genes to print
			ListView<ScoredChromosomeWindow> listToPrint = dataScaler.getDataScaledForTrackDisplay();
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					int x = projectWindow.genomeToScreenPosition(currentWindow.getStart());
					int widthWindow = projectWindow.genomeToScreenPosition(currentWindow.getStop()) - x;
					// we want to make sure that the window width is > 0
					widthWindow = Math.max(1, widthWindow);
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
			// check that the data scaler is valid
			validateDataScaler();
			// Retrieve the genes to print
			ListView<ScoredChromosomeWindow> listToPrint = dataScaler.getDataScaledForTrackDisplay();
			if (listToPrint != null) {
				for (ScoredChromosomeWindow currentWindow: listToPrint) {
					// we want to make sure that x is > 0
					int x1 = Math.max(0, projectWindow.genomeToScreenPosition(currentWindow.getStart()));
					// we want to make sure that window width is not larger than the screen width
					int x2 = Math.min(width, projectWindow.genomeToScreenPosition(currentWindow.getStop()));
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
	public float getCurrentScoreToDisplay() {
		if (getData() != null) {
			GenomeWindow displayedWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
			Chromosome chromo = displayedWindow.getChromosome();
			int middlePosition = (int) displayedWindow.getMiddlePosition();
			return getData().getScore(chromo, middlePosition);
		} else {
			return 0f;
		}
	}


	@Override
	public GraphType getGraphType() {
		return graphType;
	}


	@Override
	public float getMaximumScoreToDisplay() {
		return new SCWLOMaxScoreToDisplay(getData()).compute();
	}


	@Override
	public float getMinimumScoreToDisplay() {
		return new SCWLOMinScoreToDisplay(getData()).compute();
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		color = (Color) in.readObject();
		graphType = (GraphType) in.readObject();
	}


	@Override
	public void setColor(Color color) {
		this.color = color;
	}


	@Override
	public <U extends T> void setData(U data) {
		super.setData(data);
		// tells the track score object to auto-rescale the score axis
		if ((getTrack() != null) && (getTrack().getScore() != null)) {
			getTrack().getScore().autorescaleScoreAxis();
		}
	}



	@Override
	public <U extends T> void setData(U data, String description) {
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


	/**
	 * Checks that the data scaler is valid. Regenerates the data scaler if it's not valid
	 */
	protected abstract void validateDataScaler();


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CLASS_VERSION_NUMBER);
		out.writeObject(color);
		out.writeObject(graphType);
	}
}
