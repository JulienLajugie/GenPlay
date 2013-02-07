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
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAAddConstant;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAAverage;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAChangeBinSize;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAChangeDataPrecision;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLACompress;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAConcatenate;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLACorrelate;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLACountNonNullBins;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLADensity;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLADivideConstant;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAFilter;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAFindPeaks;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAGauss;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAIndex;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAIndexByChromosome;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAIntervalsScoring;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAInvertConstant;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLALoessRegression;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLALog;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAMax;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAMin;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAMovingAverage;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAMultiplyConstant;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLANormalize;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLANormalizeStandardScore;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLARepartition;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAStandardDeviation;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLASubtractConstant;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLASumScore;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLATransfrag;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLATwoLayersOperation;
import edu.yu.einstein.genplay.gui.action.layer.binlayer.BLAUniqueScore;
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
				new BLAAddConstant(),
				new BLASubtractConstant(),
				new BLAMultiplyConstant(),
				new BLADivideConstant(),
				new BLAInvertConstant(),
				new BLAUniqueScore(),
				null,
				new BLATwoLayersOperation(),
				null,
				new BLAMovingAverage(),
				new BLAGauss(),
				new BLALoessRegression(),
				null,
				new BLAIndex(),
				new BLAIndexByChromosome(),
				new BLALog(),
				new BLANormalize(),
				new BLANormalizeStandardScore(),
				null,
				new BLAMin(),
				new BLAMax(),
				new BLACountNonNullBins(),
				new BLASumScore(),
				new BLAAverage(),
				new BLAStandardDeviation(),
				new BLACorrelate(),
				null,
				new BLAFilter(),
				new BLAFindPeaks(),
				new BLATransfrag(),
				null,
				new BLAChangeBinSize(),
				new BLAChangeDataPrecision(),
				null,
				new BLADensity(),
				new BLAIntervalsScoring(),
				new BLARepartition(),
				new BLAConcatenate(),
				null,
				new LAConvert(),
				new LASave()
		};
		return actions;
	}
}
