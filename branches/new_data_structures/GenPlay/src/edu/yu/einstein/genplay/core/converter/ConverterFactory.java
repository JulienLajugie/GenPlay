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
package edu.yu.einstein.genplay.core.converter;

import edu.yu.einstein.genplay.core.converter.SCWListConverter.SCWListToBinList;
import edu.yu.einstein.genplay.core.converter.SCWListConverter.SCWListToGeneList;
import edu.yu.einstein.genplay.core.converter.SCWListConverter.SCWListToMaskList;
import edu.yu.einstein.genplay.core.converter.binListConverter.BinListToGeneList;
import edu.yu.einstein.genplay.core.converter.binListConverter.BinListToMaskList;
import edu.yu.einstein.genplay.core.converter.binListConverter.BinListToSCWList;
import edu.yu.einstein.genplay.core.converter.geneListConverter.GeneListToBinList;
import edu.yu.einstein.genplay.core.converter.geneListConverter.GeneListToMaskList;
import edu.yu.einstein.genplay.core.converter.geneListConverter.GeneListToSCWList;
import edu.yu.einstein.genplay.core.converter.maskListConverter.MaskListToBinList;
import edu.yu.einstein.genplay.core.converter.maskListConverter.MaskListToGeneList;
import edu.yu.einstein.genplay.core.converter.maskListConverter.MaskListToSCWList;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.gui.track.layer.MaskLayer;
import edu.yu.einstein.genplay.gui.track.layer.GenericSCWLayer;


/**
 * @author Nicolas Fourel
 */
public class ConverterFactory {


	/**
	 * @return the array of {@link LayerType} a {@link BinLayer} can be converted in.
	 */
	public static LayerType[] getBinLayerType() {
		LayerType[] array = {LayerType.GENE_LAYER, LayerType.SCW_LAYER, LayerType.MASK_LAYER};
		return array;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of layer.
	 * @param binList		the bin list
	 * @param layerType		the new type of layer to convert the data
	 * @return				the appropriate converter
	 */
	private static Converter getBinListConverter(BinList binList, LayerType layerType) {
		Converter converter = null;

		if (layerType == LayerType.SCW_LAYER) {
			converter = new BinListToSCWList(binList);
		} else if (layerType == LayerType.GENE_LAYER) {
			converter = new BinListToGeneList(binList);
		} else if (layerType == LayerType.MASK_LAYER) {
			converter = new BinListToMaskList(binList);
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
	public static Converter getConverter (GenomicListView<?> data, LayerType layerType, int binSize, ScoreOperation method) {
		Converter converter = null;
		if (data != null) {
			if (data instanceof BinList) {
				BinList binList = (BinList) data;
				converter = getBinListConverter(binList, layerType);
			} else if (data instanceof GeneList) {
				GeneList geneList = (GeneList) data;
				converter = getGeneListConverter(geneList, layerType, binSize, method);
			} else if (data instanceof SimpleSCWList) {
				SCWList scwList = (SCWList) data;
				converter = getSCWListConverter(scwList, layerType, binSize, method);
			} else if ((data instanceof SCWList) && (((SCWList) data).getSCWListType() == SCWListType.MASK)) {
				SCWList scwList = (SCWList) data;
				converter = getMaskListConverter(scwList, layerType, binSize, method);
			}
		}
		return converter;
	}


	/**
	 * @return the array of {@link LayerType} a {@link GeneLayer} can be converted in.
	 */
	public static LayerType[] getGeneLayerType() {
		LayerType[] array = {LayerType.SCW_LAYER, LayerType.MASK_LAYER, LayerType.BIN_LAYER};
		return array;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of layer.
	 * @param binList		the gene list
	 * @param layerType		the new type of layer to convert the data
	 * @param binSize 		size of the bins	(can be null if the layer type is not a {@link BinList})
	 * @param method method to generate the BinList (can be null if the layer type is not a {@link BinList})
	 * @return				the appropriate converter
	 */
	private static Converter getGeneListConverter (GeneList geneList, LayerType layerType, int binSize, ScoreOperation method) {
		Converter converter = null;
		if (layerType == LayerType.BIN_LAYER) {
			converter = new GeneListToBinList(geneList, binSize, method);
		} else if (layerType == LayerType.SCW_LAYER) {
			converter = new GeneListToSCWList(geneList, method);
		} else if (layerType == LayerType.MASK_LAYER) {
			converter = new GeneListToMaskList(geneList);
		}
		return converter;
	}


	/**
	 * @param data	the data to convert
	 * @return		the {@link LayerType} available for the given data, null otherwise
	 */
	public static LayerType[] getLayerTypes (GenomicListView<?> data) {
		LayerType[] layerTypes = null;
		if (data instanceof BinList) {
			layerTypes = getBinLayerType();
		} else if (data instanceof GeneList) {
			layerTypes = getGeneLayerType();
		} else if (data instanceof SCWList) {
			layerTypes = getSCWLayerType();
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
	 * Creates a converter in order to convert the data to the new kind of layer.
	 * @param binList		the gene list
	 * @param layerType		the new type of layer to convert the data
	 * @param binSize 		size of the bins	(can be null if the layer type is not a {@link BinList})
	 * @param method method to generate the BinList (can be null if the layer type is not a {@link BinList})
	 * @return				the appropriate converter
	 */
	private static Converter getMaskListConverter (SCWList maskList, LayerType layerType, int binSize, ScoreOperation method) {
		Converter converter = null;
		if (layerType == LayerType.BIN_LAYER) {
			converter = new MaskListToBinList(maskList, binSize, method);
		} else if (layerType == LayerType.SCW_LAYER) {
			converter = new MaskListToSCWList(maskList);
		} else if (layerType == LayerType.GENE_LAYER) {
			converter = new MaskListToGeneList(maskList);
		}
		return converter;
	}


	/**
	 * @return the array of {@link LayerType} a {@link GenericSCWLayer} can be converted in.
	 */
	public static LayerType[] getSCWLayerType() {
		LayerType[] array = {LayerType.GENE_LAYER, LayerType.MASK_LAYER, LayerType.BIN_LAYER};
		return array;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of layer.
	 * @param binList		the SCW list
	 * @param layerType		the new type of layer to convert the data
	 * @param binSize 		size of the bins	(can be null if the layer type is not a {@link BinList})
	 * @param method method to generate the BinList (can be null if the layer type is not a {@link BinList})
	 * @return				the appropriate converter
	 */
	private static Converter getSCWListConverter (SCWList scwList, LayerType layerType, int binSize, ScoreOperation method) {
		Converter converter = null;
		if (layerType == LayerType.BIN_LAYER) {
			converter = new SCWListToBinList(scwList, binSize, method);
		} else if (layerType == LayerType.GENE_LAYER) {
			converter = new SCWListToGeneList(scwList);
		} else if (layerType == LayerType.MASK_LAYER) {
			converter = new SCWListToMaskList(scwList);
		}
		return converter;
	}
}
