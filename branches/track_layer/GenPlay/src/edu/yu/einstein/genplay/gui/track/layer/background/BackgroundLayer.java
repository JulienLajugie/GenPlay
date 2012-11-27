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
package edu.yu.einstein.genplay.gui.track.layer.background;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.AbstractLayer;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Background layer of a track.
 * This layer contains the horizontal and vertical lines displayed in the background of a track.
 * @author Julien Lajugie
 */
public class BackgroundLayer extends AbstractLayer<BackgroundData> implements Layer<BackgroundData> {

	private static final long serialVersionUID = -5149270915068813760L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	
	/**
	 * Creates an instance of {@link BackgroundLayer}
	 * @param track track in which the background layer is displayed
	 */
	public BackgroundLayer(Track track) {
		super();
		BackgroundData data = new BackgroundData();
		setData(data);
		setTrack(track);
	}

	
	@Override
	public void drawLayer(Graphics g) {
		if (!isHidden()) {
			drawVerticalLines(g);
			drawHorizontalLines(g);
		}
	}


	/**
	 * Draws horizontal lines on the track
	 * @param g {@link Graphics}
	 */
	private void drawHorizontalLines(Graphics g) {
		if (getData().isHorizontalGridVisible()) {
			double scoreMin = getTrack().getScore().getMinimumScore();
			double scoreMax = getTrack().getScore().getMaximumScore();
			int horizontalLineCount = getData().getHorizontalLineCount();
			int width = Track.getGraphicsWidth();
			double scoreGapBetweenLineY = (scoreMax - scoreMin) / (double)horizontalLineCount;
			double intensityFirstLineY = scoreMin - (scoreMin % scoreGapBetweenLineY);
			g.setColor(Colors.LIGHT_GREY);
			for(int i = 0; i <= horizontalLineCount; i++) {
				double intensityLineY = ((double) i) * scoreGapBetweenLineY + intensityFirstLineY;
				if (intensityLineY >= scoreMin) {
					int screenLineY = getTrack().getScore().scoreToScreenPos(intensityLineY);
					g.drawLine(0, screenLineY, width, screenLineY);
					DecimalFormat formatter = new DecimalFormat("#.#####");
					formatter.setRoundingMode(RoundingMode.DOWN);
					String positionStr = formatter.format(intensityLineY);
					g.drawString(positionStr, 2, screenLineY);
				}
			}
		}
	}


	/**
	 * Draws the vertical lines
	 * @param g {@link Graphics}
	 */
	private void drawVerticalLines(Graphics g) {
		g.setColor(Colors.TRACK_LINE);
		double gap = Track.getGraphicsWidth() / (double)getData().getVerticalLineCount();
		int y1 = 0;
		int y2 = getTrack().getHeight();
		for (int i = 0; i < getData().getVerticalLineCount(); i++) {
			int x = (int)Math.round(i * gap);
			g.drawLine(x, y1, x, y2);
		}
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
