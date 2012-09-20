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
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.TrackType;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.MaskWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.SCWList.SimpleScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.gui.track.BinListTrack;
import edu.yu.einstein.genplay.gui.track.GeneListTrack;
import edu.yu.einstein.genplay.gui.track.SCWListTrack;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ConverterFactory {


	/**
	 * Creates a converter in order to convert the data to the new kind of track.
	 * @param data			the data to convert (can be null if mask is not)
	 * @param mask 			the mask to convert (can be null if data is not)
	 * @param trackType		the new type of track to convert the data
	 * @param binSize 		size of the bins	(can be null if the track type is not a {@link BinList}
	 * @param precision 	precision of the data (eg: 1/8/16/32/64-BIT)	(can be null if the track type is not a {@link BinList}
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)	(can be null if the track type is not a {@link BinList}
	 * @return				the appropriate converter
	 */
	public static Converter getConverter (ChromosomeListOfLists<?> data, ChromosomeListOfLists<?> mask, TrackType trackType, int binSize, DataPrecision precision, ScoreCalculationMethod method) {
		Converter converter = null;
		if (data != null) {
			if (data instanceof BinList) {
				BinList binList = (BinList) data;
				converter = getBinListConverter(binList, trackType);
			} else if (data instanceof GeneList) {
				GeneList geneList = (GeneList) data;
				converter = getGeneListConverter(geneList, trackType, binSize, precision, method);
			} else if (data instanceof SimpleScoredChromosomeWindowList) {
				ScoredChromosomeWindowList scwList = (ScoredChromosomeWindowList) data;
				converter = getSCWListConverter(scwList, trackType, binSize, precision, method);
			}
		} else if (mask != null) {
			if (mask instanceof MaskWindowList) {
				ScoredChromosomeWindowList scwList = (ScoredChromosomeWindowList) mask;
				converter = getMaskListConverter(scwList, trackType, binSize, precision, method);
			}
		}
		return converter;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of track.
	 * @param binList		the bin list
	 * @param trackType		the new type of track to convert the data
	 * @return				the appropriate converter
	 */
	private static Converter getBinListConverter (BinList binList, TrackType trackType) {
		Converter converter = null;

		if (trackType == TrackType.SCW) {
			converter = new BinListToSCWList(binList);
		} else if (trackType == TrackType.GENE) {
			converter = new BinListToGeneList(binList);
		} else if (trackType == TrackType.MASK) {
			converter = new BinListToMaskList(binList);
		}

		return converter;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of track.
	 * @param binList		the SCW list
	 * @param trackType		the new type of track to convert the data
	 * @param binSize 		size of the bins	(can be null if the track type is not a {@link BinList}
	 * @param precision 	precision of the data (eg: 1/8/16/32/64-BIT)	(can be null if the track type is not a {@link BinList}
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)	(can be null if the track type is not a {@link BinList}
	 * @return				the appropriate converter
	 */
	private static Converter getSCWListConverter (ScoredChromosomeWindowList scwList, TrackType trackType, int binSize, DataPrecision precision, ScoreCalculationMethod method) {
		Converter converter = null;

		if (trackType == TrackType.BIN) {
			converter = new SCWListToBinList(scwList, binSize, precision, method);
		} else if (trackType == TrackType.GENE) {
			converter = new SCWListToGeneList(scwList);
		} else if (trackType == TrackType.MASK) {
			converter = new SCWListToMaskList(scwList);
		}

		return converter;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of track.
	 * @param binList		the gene list
	 * @param trackType		the new type of track to convert the data
	 * @param binSize 		size of the bins	(can be null if the track type is not a {@link BinList}
	 * @param precision 	precision of the data (eg: 1/8/16/32/64-BIT)	(can be null if the track type is not a {@link BinList}
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)	(can be null if the track type is not a {@link BinList}
	 * @return				the appropriate converter
	 */
	private static Converter getGeneListConverter (GeneList geneList, TrackType trackType, int binSize, DataPrecision precision, ScoreCalculationMethod method) {
		Converter converter = null;

		if (trackType == TrackType.BIN) {
			converter = new GeneListToBinList(geneList, binSize, precision, method);
		} else if (trackType == TrackType.SCW) {
			converter = new GeneListToSCWList(geneList, method);
		} else if (trackType == TrackType.MASK) {
			converter = new GeneListToMaskList(geneList, method);
		}

		return converter;
	}


	/**
	 * Creates a converter in order to convert the data to the new kind of track.
	 * @param binList		the gene list
	 * @param trackType		the new type of track to convert the data
	 * @param binSize 		size of the bins	(can be null if the track type is not a {@link BinList}
	 * @param precision 	precision of the data (eg: 1/8/16/32/64-BIT)	(can be null if the track type is not a {@link BinList}
	 * @param method method to generate the BinList (eg: AVERAGE, SUM or MAXIMUM)	(can be null if the track type is not a {@link BinList}
	 * @return				the appropriate converter
	 */
	private static Converter getMaskListConverter (ScoredChromosomeWindowList maskList, TrackType trackType, int binSize, DataPrecision precision, ScoreCalculationMethod method) {
		Converter converter = null;

		if (trackType == TrackType.BIN) {
			converter = new MaskListToBinList(maskList, binSize, precision, method);
		} else if (trackType == TrackType.SCW) {
			converter = new MaskListToSCWList(maskList);
		} else if (trackType == TrackType.GENE) {
			converter = new MaskListToGeneList(maskList);
		}

		return converter;
	}


	/**
	 * @param data	the data to convert
	 * @return		the {@link TrackType} available for the given data, null otherwise
	 */
	//public static TrackType[] getTrackTypes (ChromosomeListOfLists<?> data) {
	public static TrackType[] getTrackTypes (ChromosomeListOfLists<?> data) {
		TrackType[] trackTypes = null;
		if (data instanceof BinList) {
			trackTypes = getBinTrackType();
		} else if (data instanceof GeneList) {
			trackTypes = getGeneTrackType();
		} else if (data instanceof ScoredChromosomeWindowList) {
			trackTypes = getSCWTrackType();
		}
		return trackTypes;
	}


	/**
	 * @return the array of {@link TrackType} a {@link BinListTrack} can be converted in.
	 */
	public static TrackType[] getBinTrackType() {
		TrackType[] array = new TrackType[3];
		array[0] = TrackType.GENE;
		array[1] = TrackType.SCW;
		array[2] = TrackType.MASK;
		return array;
	}


	/**
	 * @return the array of {@link TrackType} a {@link SCWListTrack} can be converted in.
	 */
	public static TrackType[] getSCWTrackType() {
		TrackType[] array = new TrackType[3];
		array[0] = TrackType.GENE;
		array[1] = TrackType.MASK;
		array[2] = TrackType.BIN;
		return array;
	}


	/**
	 * @return the array of {@link TrackType} a {@link GeneListTrack} can be converted in.
	 */
	public static TrackType[] getGeneTrackType() {
		TrackType[] array = new TrackType[3];
		array[0] = TrackType.SCW;
		array[1] = TrackType.MASK;
		array[2] = TrackType.BIN;
		return array;
	}


	/**
	 * @return the array of {@link TrackType} a {@link MaskWindowList} can be converted in.
	 */
	public static TrackType[] getMaskTrackType() {
		TrackType[] array = new TrackType[3];
		array[0] = TrackType.GENE;
		array[1] = TrackType.SCW;
		array[2] = TrackType.BIN;
		return array;
	}
}
