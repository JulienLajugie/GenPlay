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
package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay.DataScalerManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.colors.LayerColors;


/**
 * Layer displaying a mask
 * @author Julien Lajugie
 */
public class MaskLayer extends AbstractVersionedLayer<SCWList> implements Serializable, Cloneable, Layer<SCWList>, VersionedLayer<SCWList>, ColoredLayer {

	private static final long serialVersionUID = 3779631846077486596L; // generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// Saved format version
	private Color 						color;		// color of the layer


	/**
	 * Creates an instance of {@link MaskLayer} with the same properties as the specified {@link MaskLayer}.
	 * The copy of the data is shallow.
	 * @param binLayer
	 */
	private MaskLayer(MaskLayer maskLayer) {
		super(maskLayer);
		color = maskLayer.color;
	}


	/**
	 * Creates an instance of a {@link MaskLayer}
	 * @param track track containing the layer
	 * @param data data of the layer
	 * @param name name of the layer
	 */
	public MaskLayer(Track track, SCWList data, String name) {
		super(track, data, name);
		color = LayerColors.getLayerColor();
	}


	@Override
	public MaskLayer clone() {
		return new MaskLayer(this);
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			if (getData() != null) {
				ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
				g.setColor(color);
				// Retrieve the genes to print
				ListView<ScoredChromosomeWindow> listToPrint = DataScalerManager.getInstance().getScaledData(this);
				if (listToPrint == null) {
					getTrack().drawLoadingAnimation(g);
				} else {
					for (ScoredChromosomeWindow currentStripe: listToPrint) {
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
	}


	@Override
	public Color getColor() {
		return color;
	}


	@Override
	public LayerType getType() {
		return LayerType.MASK_LAYER;
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
	}


	@Override
	public void setColor(Color color) {
		this.color = color;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(color);
	}
}
