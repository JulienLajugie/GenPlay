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
package edu.yu.einstein.genplay.gui.popupMenu.layerMenu;

import javax.swing.Action;

import edu.yu.einstein.genplay.gui.action.layer.LAConvert;
import edu.yu.einstein.genplay.gui.action.layer.LASave;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAAverageScore;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLACountAllGenes;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLACountExons;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLACountNonNullGenes;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAExtractExons;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAExtractInterval;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAFilter;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAFilterStrand;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAGeneRenamer;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAScoreDistributionAroundStart;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAScoreExons;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLASearchGene;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLASumScore;
import edu.yu.einstein.genplay.gui.action.layer.geneLayer.GLAUniqueScore;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * Menu containing all the actions available for a {@link GeneLayer}
 * @author Julien Lajugie
 */
public class GeneLayerMenu extends AbstractLayerMenu {

	private static final long serialVersionUID = 788597708482717514L; // generated ID


	/**
	 * Creates an instance of {@link GeneLayerMenu}
	 * @param layer
	 */
	public GeneLayerMenu(Layer<?> layer) {
		super(layer);
	}


	@Override
	protected Action[] getLayerMenuActions() {
		Action[] actions = {
				new GLASumScore(),
				new GLAAverageScore(),
				new GLACountAllGenes(),
				new GLACountNonNullGenes(),
				new GLACountExons(),
				null,
				new GLASearchGene(),
				new GLAExtractInterval(),
				new GLAExtractExons(),
				new GLAUniqueScore(),
				new GLAScoreExons(),
				null,
				new GLAFilter(),
				new GLAFilterStrand(),
				null,
				new GLAGeneRenamer(),
				new GLAScoreDistributionAroundStart(),
				null,
				new LAConvert(),
				new LASave()
		};
		return actions;
	}
}
