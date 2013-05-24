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
package edu.yu.einstein.genplay.core.operation.converter;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOConvertIntoBinList;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOConvertIntoGeneList;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOConvertIntoSimpleSCWList;
import edu.yu.einstein.genplay.core.operation.geneList.GLOConvertIntoSimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.gui.track.layer.MaskLayer;
import edu.yu.einstein.genplay.gui.track.layer.SimpleSCWLayer;


/**
 * Factory vending {@link Operation} of GenomicListView objects.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class ConverterFactory {

	/**
	 * Creates a converter in order to convert the data to the new kind of layer.
	 * @param binList		the bin list
	 * @param layerType		the new type of layer to convert the data
	 * @return				the appropriate converter
	 * @param binSize 		size of the bins	(can be null if the layer type is not a {@link BinList})
	 * @param method method to generate the BinList (can be null if the layer type is not a {@link BinList})
	 */
	@SuppressWarnings("unchecked")
	private static <T extends GenomicListView<?>> Operation<T> createBinListConverter(BinList binList, LayerType layerType, int binSize, ScoreOperation method) {
		Operation<T> converter = null;
		if (layerType == LayerType.SCW_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoSimpleSCWList(binList, SCWListType.GENERIC);
		} else if (layerType == LayerType.GENE_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoGeneList(binList);
		} else if (layerType == LayerType.MASK_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoSimpleSCWList(binList, SCWListType.MASK);
		} else if (layerType == LayerType.BIN_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoBinList(binList, binSize, method);
		}
		return converter;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of Layer.
	 * @param data			the data to convert
	 * @param layerType		the new type of layer to convert the data
	 * @param binSize 		size of the bins	(can be null if the layer type is not a {@link BinList})
	 * @param method method to generate the BinList (can be null if the layer type is not a {@link BinList})
	 * @return				the appropriate converter
	 */
	public static <T extends GenomicListView<?>> Operation<T> createConverter(GenomicListView<?> data, LayerType layerType, int binSize, ScoreOperation method) {
		Operation<T> converter = null;
		if (data != null) {
			if (data instanceof GeneList) {
				GeneList geneList = (GeneList) data;
				converter = createGeneListConverter(geneList, layerType, binSize, method);
			} else if (data instanceof SCWList) {
				SCWListType scwListType = ((SCWList) data).getSCWListType();
				if (scwListType == SCWListType.BIN) {
					BinList binList = (BinList) data;
					converter = createBinListConverter(binList, layerType, binSize, method);
				} else if ((scwListType == SCWListType.GENERIC) || (scwListType == SCWListType.DENSE)) {
					SCWList scwList = (SCWList) data;
					converter = createSCWListConverter(scwList, layerType, binSize, method);
				} else if (scwListType == SCWListType.MASK) {
					SCWList scwList = (SCWList) data;
					converter = createMaskListConverter(scwList, layerType, binSize, method);
				}
			}
		}
		return converter;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of layer.
	 * @param binList		the gene list
	 * @param layerType		the new type of layer to convert the data
	 * @param binSize 		size of the bins	(can be null if the layer type is not a {@link BinList})
	 * @param method method to generate the BinList (can be null if the layer type is not a {@link BinList})
	 * @return				the appropriate converter
	 */
	@SuppressWarnings("unchecked")
	private static <T extends GenomicListView<?>> Operation<T> createGeneListConverter(GeneList geneList, LayerType layerType, int binSize, ScoreOperation method) {
		Operation<T> converter = null;
		if (layerType == LayerType.BIN_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoBinList(geneList, binSize, method);
		} else if (layerType == LayerType.SCW_LAYER) {
			converter = (Operation<T>) new GLOConvertIntoSimpleSCWList(geneList, method);
		} else if (layerType == LayerType.MASK_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoSimpleSCWList(geneList, SCWListType.MASK);
		}
		return converter;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of layer.
	 * @param binList		the gene list
	 * @param layerType		the new type of layer to convert the data
	 * @param binSize 		size of the bins	(can be null if the layer type is not a {@link BinList})
	 * @param method method to generate the BinList (can be null if the layer type is not a {@link BinList})
	 * @return				the appropriate converter
	 */
	@SuppressWarnings("unchecked")
	private static <T extends GenomicListView<?>> Operation<T> createMaskListConverter(SCWList maskList, LayerType layerType, int binSize, ScoreOperation method) {
		Operation<T> converter = null;
		if (layerType == LayerType.BIN_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoBinList(maskList, binSize, method);
		} else if (layerType == LayerType.SCW_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoSimpleSCWList(maskList, SCWListType.GENERIC);
		} else if (layerType == LayerType.GENE_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoGeneList(maskList);
		}
		return converter;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of layer.
	 * @param binList		the SCW list
	 * @param layerType		the new type of layer to convert the data
	 * @param binSize 		size of the bins	(can be null if the layer type is not a {@link BinList})
	 * @param method method to generate the BinList (can be null if the layer type is not a {@link BinList})
	 * @return				the appropriate converter
	 */
	@SuppressWarnings("unchecked")
	private static <T extends GenomicListView<?>> Operation<T> createSCWListConverter(SCWList scwList, LayerType layerType, int binSize, ScoreOperation method) {
		Operation<T> converter = null;
		if (layerType == LayerType.BIN_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoBinList(scwList, binSize, method);
		} else if (layerType == LayerType.GENE_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoGeneList(scwList);
		} else if (layerType == LayerType.MASK_LAYER) {
			converter = (Operation<T>) new SCWLOConvertIntoSimpleSCWList(scwList, SCWListType.MASK);
		}
		return converter;
	}


	/**
	 * @return the array of {@link LayerType} a {@link BinLayer} can be converted in.
	 */
	public static LayerType[] getBinLayerType() {
		LayerType[] array = {LayerType.GENE_LAYER, LayerType.SCW_LAYER, LayerType.MASK_LAYER, LayerType.BIN_LAYER};
		return array;
	}


	/**
	 * @return the array of {@link LayerType} a {@link GeneLayer} can be converted in.
	 */
	public static LayerType[] getGeneLayerType() {
		LayerType[] array = {LayerType.SCW_LAYER, LayerType.MASK_LAYER, LayerType.BIN_LAYER};
		return array;
	}


	/**
	 * @param data	the data to convert
	 * @return		the {@link LayerType} available for the given data, null otherwise
	 */
	public static LayerType[] getLayerTypes (GenomicListView<?> data) {
		LayerType[] layerTypes = null;
		if (data instanceof GeneList) {
			layerTypes = getGeneLayerType();
		} else if (data instanceof SCWList) {
			SCWListType scwListType = ((SCWList) data).getSCWListType();
			if (scwListType == SCWListType.BIN) {
				layerTypes = getBinLayerType();
			} else if ((scwListType == SCWListType.GENERIC) || (scwListType == SCWListType.DENSE)) {
				layerTypes = getSCWLayerType();
			} else if (scwListType == SCWListType.MASK) {
				layerTypes = getMaskLayerType();
			}
		}
		return layerTypes;
	}


	/**
	 * @return the array of {@link LayerType} a {@link MaskLayer} can be converted in.
	 */
	public static LayerType[] getMaskLayerType() {
		LayerType[] array = {LayerType.GENE_LAYER, LayerType.SCW_LAYER, LayerType.BIN_LAYER};
		return array;
	}


	/**
	 * @return the array of {@link LayerType} a {@link SimpleSCWLayer} can be converted in.
	 */
	public static LayerType[] getSCWLayerType() {
		LayerType[] array = {LayerType.GENE_LAYER, LayerType.MASK_LAYER, LayerType.BIN_LAYER};
		return array;
	}
}
