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
import java.util.List;

import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.colors.TrackColor;


/**
 * Layer displaying a mask
 * @author Julien Lajugie
 */
public class MaskLayer extends AbstractLayer<ScoredChromosomeWindowList> implements Layer<ScoredChromosomeWindowList>, ColoredLayer {

	private static final long serialVersionUID = 3779631846077486596L; // generated ID
	private Color color;		// color of the layer


	/**
	 * Creates an instance of a {@link MaskLayer}
	 * @param track track containing the layer
	 * @param data data of the layer
	 * @param name name of the layer
	 */
	public MaskLayer(Track track, ScoredChromosomeWindowList data, String name) {
		super(track, data, name);
		this.color = TrackColor.getTrackColor();
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		if (getData() != null) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			// create a transparent color for the stripes
			g.setColor(color);
			List<ScoredChromosomeWindow> chromoStripeList = getData().getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXRatio());//(start, stop);
			if (chromoStripeList != null) {
				for (ScoredChromosomeWindow currentStripe: chromoStripeList) {
					int x = projectWindow.genomeToScreenPosition(currentStripe.getStart());
					int widthWindow = projectWindow.genomeToScreenWidth(currentStripe.getStop() - currentStripe.getStart());
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					g.fillRect(x, 0, widthWindow, height);
				}
			}
		}
	}


	@Override
	public Color getColor() {
		return color;
	}


	@Override
	public LayerType getType() {
		return LayerType.MASK_LAYER;
	}


	@Override
	public void setColor(Color color) {
		this.color = color;
	}
}
