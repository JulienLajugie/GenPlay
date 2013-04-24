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
import javax.swing.JCheckBoxMenuItem;

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
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAMax;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAMin;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAMultiplyConstant;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLANormalize;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLANormalizeStandardScore;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAStandardDeviation;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLASubtractConstant;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLASumScore;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLATwoLayersOperation;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAUniqueScore;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAChangeBinSize;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAChangeDataPrecision;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLACompress;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAConcatenate;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLACorrelate;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLADensity;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAFindPeaks;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAGauss;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAIntervalsScoring;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLALoessRegression;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAMovingAverage;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAScoreDistribution;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLATransfrag;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * Menu containing all the actions available for a {@link BinLayer}
 * @author Julien Lajugie
 */
public class BinLayerMenu extends AbstractLayerMenu {

	private static final long serialVersionUID = -3827457549561342225L; // generated ID


	/**
	 * Creates an instance of {@link BinLayerMenu}
	 * @param layer the layer for the action
	 */
	public BinLayerMenu(Layer<?> layer) {
		super(layer);
		BinLayer binLayer = (BinLayer) layer;
		// check the compression checkbox if the selected list is checked
		Action action = new BLACompress();
		action.putValue("Layer", layer);
		JCheckBoxMenuItem jcbmiCompression = new JCheckBoxMenuItem(action);
		jcbmiCompression.setState(binLayer.getData().isCompressed());
		if (jcbmiCompression.getState()) {
			for (int i = 0; i < getItemCount(); i++) {
				if (getItem(i) != null) {
					getItem(i).setEnabled(false);
				}
			}
		}
		addSeparator();
		add(jcbmiCompression);
	}


	@Override
	protected Action[] getLayerMenuActions() {
		Action[] actions = {
				//TODO new LASave(),
				//TODO null,
				new SCWLAAddConstant(),
				new SCWLASubtractConstant(),
				new SCWLAMultiplyConstant(),
				new SCWLADivideConstant(),
				new SCWLAInvertConstant(),
				new SCWLAUniqueScore(),
				null,
				new SCWLATwoLayersOperation(),
				null,
				new BLAMovingAverage(),
				new BLAGauss(),
				new BLALoessRegression(),
				null,
				new SCWLAIndex(),
				new SCWLAIndexByChromosome(),
				new SCWLALog(),
				new SCWLANormalize(),
				new SCWLANormalizeStandardScore(),
				null,
				new SCWLAMin(),
				new SCWLAMax(),
				new SCWLACountNonNullLength(),
				new SCWLASumScore(),
				new SCWLAAverage(),
				new SCWLAStandardDeviation(),
				new BLACorrelate(),
				null,
				new SCWLAFilter(),
				new BLAFindPeaks(),
				new BLATransfrag(),
				null,
				new BLAChangeBinSize(),
				new BLAChangeDataPrecision(),
				null,
				new BLADensity(),
				new BLAIntervalsScoring(),
				new BLAScoreDistribution(),
				new BLAConcatenate(),
				null,
				new LAConvert(),
				new LASave()
		};
		return actions;
	}
}
