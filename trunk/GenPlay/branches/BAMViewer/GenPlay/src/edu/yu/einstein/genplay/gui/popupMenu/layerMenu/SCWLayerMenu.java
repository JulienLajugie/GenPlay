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
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAAddConstant;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAAverage;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLACountNonNullLength;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLADivideConstant;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAFilter;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAIndex;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAIndexByChromosome;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAInvertConstant;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLALog;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLALogOnAvgWithDamper;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAMax;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAMin;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAMultiplyConstant;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLANormalize;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLANormalizeStandardScore;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAScoreDistribution;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAStandardDeviation;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLASubtractConstant;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLASumScore;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLATransfrag;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLATwoLayersOperation;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAUniqueScore;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAWindowCount;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.SCWLayer;

/**
 * Menu containing all the actions available for a {@link SCWLayer}
 * @author Julien Lajugie
 */
public class SCWLayerMenu extends AbstractLayerMenu {

	private static final long serialVersionUID = 1160366801758520827L; // generated serial ID


	/**
	 * Creates an instance of {@link SCWLayerMenu}
	 * @param layer
	 */
	public SCWLayerMenu(Layer<?> layer) {
		super(layer);
	}


	@Override
	protected Action[] getLayerMenuActions() {
		Action[] actions = {
				new SCWLAAddConstant(),
				new SCWLASubtractConstant(),
				new SCWLAMultiplyConstant(),
				new SCWLADivideConstant(),
				new SCWLAInvertConstant(),
				new SCWLAUniqueScore(),
				null,
				new SCWLATwoLayersOperation(),
				null,
				new SCWLAIndex(),
				new SCWLAIndexByChromosome(),
				new SCWLALog(),
				new SCWLALogOnAvgWithDamper(),
				new SCWLANormalize(),
				new SCWLANormalizeStandardScore(),
				null,
				new SCWLAMin(),
				new SCWLAMax(),
				new SCWLACountNonNullLength(),
				new SCWLASumScore(),
				new SCWLAAverage(),
				new SCWLAStandardDeviation(),
				new SCWLAWindowCount(),
				null,
				new SCWLAFilter(),
				new SCWLATransfrag(),
				null,
				new SCWLAScoreDistribution(),
				null,
				new LAConvert(),
				new LASave()
		};
		return actions;
	}
}
