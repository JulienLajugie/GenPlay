/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.track.layer.foreground;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackConstants;
import edu.yu.einstein.genplay.gui.track.TrackScore;
import edu.yu.einstein.genplay.gui.track.layer.AbstractLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.NumberFormats;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * Foreground layer of a track.
 * This layer displays the name of the track, the score at the center of the track if needed and the multi-genome legend if needed.
 * @author Julien Lajugie
 */
public class ForegroundLayer extends AbstractLayer<ForegroundData> implements Layer<ForegroundData> {

	private static final long serialVersionUID = -6813481315069255351L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private LegendDrawer legendDrawer;


	/**
	 * Creates an instance of {@link ForegroundLayer}
	 * @param track track in which the foreground layer is displayed
	 */
	public ForegroundLayer(Track track) {
		super(track, new ForegroundData());
		legendDrawer = new LegendDrawer(this);
	}


	@Override
	public ForegroundLayer clone() {
		return new ForegroundLayer(getTrack());
	}


	/**
	 * @return true if the cursor is over the legend
	 */
	public boolean isCursorOverLegend() {
		return legendDrawer.isCursorOverLegend();
	}
	
	
	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			drawMiddleVerticalLine(g, width, height);
			drawScore(g, width, height);
			if (legendDrawer == null) {
				legendDrawer = new LegendDrawer(this);
			}
			legendDrawer.draw(g, width, height);
		}
	}


	/**
	 * Draws the main line in the middle of the track
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawMiddleVerticalLine(Graphics g, int width, int height) {
		int y1 = 0;
		int y2 = height;
		int x = (int)Math.round(width / (double)2);
		g.setColor(Colors.TRACK_MIDDLE_LINE);
		g.drawLine(x, y1, x, y2);
	}


	/**
	 * Draws the score of the track
	 * @param g {@link Graphics} on which the layer will be drawn
	 * @param width width of the graphics to draw
	 * @param height height of the graphics to draw
	 */
	private void drawScore(Graphics g, int width, int height) {
		ForegroundData data = getData();
		TrackScore trackScore = getTrack().getScore();
		if ((data != null) && (trackScore != null) && (trackScore.getCurrentScore() != null)) {
			float currentScore = trackScore.getCurrentScore();
			if (!Float.isNaN(currentScore)) {
				int scoreYPosition = 0;
				if (data.getScorePosition() == TrackConstants.BOTTOM_SCORE_POSITION) {
					scoreYPosition =  getTrack().getHeight() - 2;
				} else if (data.getScorePosition() == TrackConstants.TOP_SCORE_POSITION) {
					scoreYPosition = g.getFontMetrics().getHeight();
				}
				g.setColor(data.getScoreColor());
				g.drawString(NumberFormats.getScoreFormat().format(currentScore), (width / 2) + 3, scoreYPosition);
			}
		}
	}


	@Override
	public LayerType getType() {
		return LayerType.FOREGROUND_LAYER;
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}
}
