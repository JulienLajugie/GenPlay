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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.VCFFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.DisplayableDataList;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.Variant;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;


/**
 * This class manages the genome information.
 * Those information are the chromosome and its relative information.
 * @author Nicolas Fourel
 */
public class VCFGenomeInformation implements DisplayableDataList<List<Variant>> {

	private 	Map<Chromosome, VCFChromosomeInformation> 	genomeInformation;			// Chromosomes information
	//private		List<VCFChromosomeInformation>				genomeInformation;
	private 	List<Variant> 								fittedDataList;				// List of variation according to the current chromosome and the x-ratio
	protected 	Chromosome									fittedChromosome = null;	// Chromosome with the adapted data
	protected 	Double										fittedXRatio = null;		// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )


	/**
	 * Constructor of {@link VCFGenomeInformation}
	 */
	protected VCFGenomeInformation () {
		genomeInformation = new HashMap<Chromosome, VCFChromosomeInformation>();
		//genomeInformation = new ArrayList<VCFChromosomeInformation>();
		//int i = 1;
		for (Chromosome chromosome: ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList().values()) {
			//short index = ChromosomeManager.getInstance().getIndex(chromosome);
			//genomeInformation.add(index, new VCFChromosomeInformation(chromosome));
			//System.out.println("add: " + index + " - " + chromosome.getName());
			genomeInformation.put(chromosome, new VCFChromosomeInformation());
			//i++;
		}
	}
	
	
	/**
	 * Adds a position information according to a chromosome.
	 * @param chromosome	the related chromosome
	 * @param position		the position
	 * @param type			the information type
	 * @param info map containing genome variation information 
	 */
	protected void addInformation (Chromosome chromosome, Integer position, VariantType type, Integer length, Map<String, String> info) {
		getChromosomeInformation(chromosome).addInformation(position, type, length, info);
	}


	/**
	 * @param chromosome 	the related chromosome
	 * @return				valid chromosome containing position information
	 */
	protected VCFChromosomeInformation getChromosomeInformation (Chromosome chromosome) {
		//System.out.println(chromosome.getName() + " gets " + ChromosomeManager.getInstance().getIndex(chromosome) + " - " + genomeInformation.get(ChromosomeManager.getInstance().getIndex(chromosome)).getChromosome().getName());
		//return genomeInformation.get(ChromosomeManager.getInstance().getIndex(chromosome));
		return genomeInformation.get(chromosome);
	}


	/**
	 * @param chromosome	the chromosome
	 * @param position		the position
	 * @return				the type of a specified position according to the chromosome
	 */
	protected VariantType getType (Chromosome chromosome, Integer position) {
		return getChromosomeInformation(chromosome).getType(position);
	}


	/**
	 * @return the genomeInformation
	 */
	protected Map<Chromosome, VCFChromosomeInformation> getGenomeInformation() {
	//protected List<VCFChromosomeInformation> getGenomeInformation() {
		return genomeInformation;
	}


	/**
	 * Shows chromosomes information.
	 */
	protected void showData () {
		for (Chromosome chromosome: genomeInformation.keySet()) {
			System.out.println("= chromosome name: " + chromosome.getName());
			getChromosomeInformation(chromosome).showData();
		}
		
		/*for (VCFChromosomeInformation chromosomeInformation: genomeInformation) {
			System.out.println("= chromosome name: " + chromosomeInformation.getChromosome().getName());
			getChromosomeInformation(chromosomeInformation.getChromosome()).showData();
		}*/
	}



	////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Merges two windows together if the gap between this two windows is not visible 
	 */
	protected void fitToScreen() {
		VCFChromosomeInformation chromosomeInformation = getChromosomeInformation(fittedChromosome);
		Map<Integer, VCFPositionInformation> currentChromosomePositionList;
		//List<VCFPositionInformation> currentChromosomePositionList;
		try {
			currentChromosomePositionList = chromosomeInformation.getPositionInformationList();
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
			fittedDataList = null;
			return;
		}

		fittedDataList = new ArrayList<Variant>();

		if (fittedXRatio > 1) {
			for (VCFPositionInformation position: currentChromosomePositionList.values()) {
			//for (VCFPositionInformation position: currentChromosomePositionList) {
				addVariant(position);
			}
		} else {
			//int[] indexes = chromosomeInformation.getPositionIndex();	//test
			if (currentChromosomePositionList.size() > 1) {
				addVariant(chromosomeInformation.getPositionInformationFromIndex(0));
				//addVariant(chromosomeInformation.getPositionInformationFromIndex(indexes[0]));
				int i = 1;
				int j = 0;
				while (i < currentChromosomePositionList.size()) {
					double distance = (chromosomeInformation.getPositionInformationFromIndex(i).getMetaGenomePosition() - fittedDataList.get(j).getStop()) * fittedXRatio;
					//double distance = (chromosomeInformation.getPositionInformationFromIndex(indexes[i]).getMetaGenomePosition() - fittedDataList.get(j).getStop()) * fittedXRatio;
					// we merge two intervals together if there is a gap smaller than 1 pixel
					while ((distance < 1) && (i + 1 < currentChromosomePositionList.size())) {
						// the new stop position is the max of the current stop and the stop of the new merged interval
						int newStop = Math.max(fittedDataList.get(j).getStop(), chromosomeInformation.getPositionInformationFromIndex(i).getNextMetaGenomePosition());
						//int newStop = Math.max(fittedDataList.get(j).getStop(), chromosomeInformation.getPositionInformationFromIndex(indexes[i]).getNextMetaGenomePosition());
						fittedDataList.get(j).setStop(newStop);
						double width = (chromosomeInformation.getPositionInformationFromIndex(i).getNextMetaGenomePosition() - chromosomeInformation.getPositionInformationFromIndex(j).getMetaGenomePosition()) * fittedXRatio;
						//double width = (chromosomeInformation.getPositionInformationFromIndex(indexes[i]).getNextMetaGenomePosition() - chromosomeInformation.getPositionInformationFromIndex(indexes[j]).getMetaGenomePosition()) * fittedXRatio;
						if (width < 1) {
							fittedDataList.get(j).setType(VariantType.MIX);
						}
						i++;
						distance = (chromosomeInformation.getPositionInformationFromIndex(i).getMetaGenomePosition() - fittedDataList.get(j).getStop()) * fittedXRatio;
						//distance = (chromosomeInformation.getPositionInformationFromIndex(indexes[i]).getMetaGenomePosition() - fittedDataList.get(j).getStop()) * fittedXRatio;
					}
					addVariant(chromosomeInformation.getPositionInformationFromIndex(i));
					//addVariant(chromosomeInformation.getPositionInformationFromIndex(indexes[i]));
					i++;
					j++;
				}
			}
		}
	}


	/**
	 * Add a variant object from a position information to the fitted list
	 * @param positionInformation	the position information
	 */
	private void addVariant (VCFPositionInformation positionInformation) {
		ChromosomeWindow chromosome = new ChromosomeWindow(positionInformation.getMetaGenomePosition() + 1, positionInformation.getNextMetaGenomePosition());
		int[] gt = positionInformation.getGT();
		Variant variant = new Variant(positionInformation.getType(), chromosome);

		if (positionInformation.getExtraOffset() > 0) {
			ChromosomeWindow extraChromosome = new ChromosomeWindow(positionInformation.getNextMetaGenomePosition() - positionInformation.getExtraOffset(), positionInformation.getNextMetaGenomePosition());
			variant.setDeadZone(extraChromosome);
		}

		if (gt[0] == 0) {
			variant.setOnFirstAllele(false);
		} else {
			variant.setOnFirstAllele(true);
		}
		if (gt[1] == 0) {
			variant.setOnSecondAllele(false);
		} else {
			variant.setOnSecondAllele(true);
		}

		variant.setQualityScore(positionInformation.getQuality());

		fittedDataList.add(variant);
	}


	@Override
	public final List<Variant> getFittedData(GenomeWindow window, double xRatio) {
		if ((fittedChromosome == null) || (!fittedChromosome.equals(window.getChromosome()))) {
			fittedChromosome = window.getChromosome();
			if ((fittedXRatio == null) || (fittedXRatio != xRatio)) {
				fittedXRatio = xRatio;
			}
			fitToScreen();
		} else if ((fittedXRatio == null) || (fittedXRatio != xRatio)) {
			fittedXRatio = xRatio;
			fitToScreen();
		}
		return getFittedData(window.getStart(), window.getStop());
	}


	/**
	 * @param start	start position
	 * @param stop	stop position
	 * @return the lsit of fitted data
	 */
	protected List<Variant> getFittedData(int start, int stop) {
		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}

		ArrayList<Variant> resultList = new ArrayList<Variant>();

		int indexStart = findStart(fittedDataList, start, 0, fittedDataList.size() - 1);
		int indexStop = findStop(fittedDataList, stop, 0, fittedDataList.size() - 1);
		if (indexStart > 0) {
			if (fittedDataList.get(indexStart - 1).getStop() >= start) {
				Variant currentVariant = fittedDataList.get(indexStart - 1);
				ChromosomeWindow chromosome = new ChromosomeWindow(start, currentVariant.getStop());
				Variant newLastVariant = new Variant(currentVariant.getType(), chromosome);
				resultList.add(newLastVariant);
			}
		}
		for (int i = indexStart; i <= indexStop; i++) {
			resultList.add(fittedDataList.get(i));
		}
		if (indexStop + 1 < fittedDataList.size()) {
			if (fittedDataList.get(indexStop + 1).getStart() <= stop) {
				Variant currentVariant = fittedDataList.get(indexStop + 1); 
				ChromosomeWindow chromosome = new ChromosomeWindow(currentVariant.getStart(), stop);
				Variant newLastVariant = new Variant(currentVariant.getType(), chromosome);
				resultList.add(newLastVariant);
			}
		}
		return resultList;
	}


	/**
	 * Recursive function. Returns the index where the start value of the window is found
	 * or the index right after if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return the start position
	 */
	private int findStart(List<Variant> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStart()) {
			return findStart(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStart(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * Recursive function. Returns the index where the stop value of the window is found
	 * or the index right before if the exact value is not find.
	 * @param list
	 * @param value
	 * @param indexStart
	 * @param indexStop
	 * @return the stop position
	 */
	private int findStop(List<Variant> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStop()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStop()) {
			return findStop(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findStop(list, value, indexStart, indexStart + middle);
		}
	}

}