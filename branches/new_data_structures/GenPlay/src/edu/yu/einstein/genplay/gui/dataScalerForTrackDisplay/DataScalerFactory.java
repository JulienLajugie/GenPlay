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
package edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay;

import java.awt.FontMetrics;

import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.NucleotideList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * Factory class vending DataScalerForTrackDisplay objects.
 * @author Julien Lajugie
 */
class DataScalerFactory {

	/**
	 * Creates a {@link DataScalerForTrackDisplay} for scaling the data of the specified {@link Layer};
	 * @param layer a layer
	 * @return a {@link DataScalerForTrackDisplay}
	 */
	static DataScalerForTrackDisplay<?, ?> createDataScaler(Layer<?> layer) {
		switch (layer.getType()) {
		case BIN_LAYER:
			return new BinListScaler((BinList) layer.getData());
		case GENE_LAYER:
			FontMetrics fontMetrics = layer.getTrack().getGraphicsPanel().getGraphics().getFontMetrics();
			return new GeneListScaler((GeneList) layer.getData(), fontMetrics);
		case MASK_LAYER:
			return new MaskSCWLScaler((SCWList) layer.getData());
		case NUCLEOTIDE_LAYER:
			return new NucleotideListScaler((NucleotideList) layer.getData());
		case REPEAT_FAMILY_LAYER:
			return new RepeatListScaler((RepeatFamilyList) layer.getData());
		case SIMPLE_SCW_LAYER:
			return new SimpleSCWLScaler((SCWList) layer.getData());
		default:
			return null;
		}
	}
}
