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
package edu.yu.einstein.genplay.core.multiGenome.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.DisplayableDataList;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.DisplayableVariant;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;


/**
 * This class manages the genome information.
 * Those information are the chromosome and its relative information.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGGenomeInformation implements DisplayableDataList<List<DisplayableVariant>> {

	private		final String								genomeFullName;				// The full genome information
	private 	Map<Chromosome, MGChromosomeInformation> 	genomeInformation;			// Chromosomes information
	private 	List<DisplayableVariant> 					fittedDataList;				// List of variation according to the current chromosome and the x-ratio
	protected 	Chromosome									fittedChromosome = null;	// Chromosome with the adapted data
	protected 	Double										fittedXRatio = null;		// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )
	private		int											smallestFittedDataIndex;	// The smaller index of the returned fitted data list
	private		int											highestFittedDataIndex;		// The highest index of the returned fitted data list


	/**
	 * Constructor of {@link MGGenomeInformation}
	 */
	protected MGGenomeInformation (String genomeFullName) {
		this.genomeFullName = genomeFullName;
		genomeInformation = new HashMap<Chromosome, MGChromosomeInformation>();
		for (Chromosome chromosome: ChromosomeManager.getInstance().getCurrentMultiGenomeChromosomeList().values()) {
			genomeInformation.put(chromosome, new MGChromosomeInformation(chromosome, this));
		}
	}


	/**
	 * Adds a position information according to a chromosome.
	 * @param chromosome	the related chromosome
	 * @param position		the position
	 * @param positionInformation 
	 * @param vcfType 
	 * @param type			the information type
	 * @param info map containing genome variation information 
	 */
	protected void addInformation (Chromosome chromosome, Integer position, String fullGenomeName, Map<String, Object> VCFLine, MGPositionInformation positionInformation, VCFType vcfType) {
		getChromosomeInformation(chromosome).addVariant(position, fullGenomeName, VCFLine, positionInformation, vcfType);
	}


	/**
	 * @param chromosome 	the related chromosome
	 * @return				valid chromosome containing position information
	 */
	protected MGChromosomeInformation getChromosomeInformation (Chromosome chromosome) {
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
	protected Map<Chromosome, MGChromosomeInformation> getGenomeInformation() {
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
	}



	////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Merges two windows together if the gap between this two windows is not visible 
	 */
	protected void fitToScreen() {
		MGChromosomeInformation chromosomeInformation = getChromosomeInformation(fittedChromosome);
		Map<Integer, Variant> currentChromosomePositionList;
		try {
			currentChromosomePositionList = chromosomeInformation.getPositionInformationList();
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
			fittedDataList = null;
			return;
		}

		fittedDataList = new ArrayList<DisplayableVariant>();

		if (fittedXRatio > 1) {
			for (Variant position: currentChromosomePositionList.values()) {
				addVariant(position);
			}
		} else {

			if (currentChromosomePositionList.size() > 0) {
				addVariant(chromosomeInformation.getPositionInformationFromIndex(0));
				//addVariant(chromosomeInformation.getPositionInformationFromIndex(indexes[0]));
				int i = 1;
				int j = 0;
				while (i < currentChromosomePositionList.size()) {
					double distance = (chromosomeInformation.getPositionInformationFromIndex(i).getMetaGenomePosition() - fittedDataList.get(j).getStop()) * fittedXRatio;
					// we merge two intervals together if there is a gap smaller than 1 pixel
					int count = 1;
					while ((distance < 1) && (i + 1 < currentChromosomePositionList.size())) {
						// the new stop position is the max of the current stop and the stop of the new merged interval
						int newStop = Math.max(fittedDataList.get(j).getStop(), chromosomeInformation.getPositionInformationFromIndex(i).getNextMetaGenomePosition());
						fittedDataList.get(j).setStop(newStop);
						fittedDataList.get(j).setType(VariantType.MIX);
						count++;
						i++;
						distance = (chromosomeInformation.getPositionInformationFromIndex(i).getMetaGenomePosition() - fittedDataList.get(j).getStop()) * fittedXRatio;
					}
					/*if (fittedDataList.get(j).getType().equals(VariantType.MIX)) {
						double newQuality = fittedDataList.get(j).getQualityScore() / count;
						fittedDataList.get(j).setQualityScore(newQuality);
					}*/
					addVariant(chromosomeInformation.getPositionInformationFromIndex(i));
					i++;
					j++;
				}
			}
		}
		showFittedDataList();
	}
	
	/**
	 * Shows the fitted data list in the console
	 */
	private void showFittedDataList () {
		String info = "";
		for (int i = 0; i < fittedDataList.size(); i++) {
			info += i + ": " + fittedDataList.get(i).getStart() + " -> " + fittedDataList.get(i).getStop() + " (" + fittedDataList.get(i).getType() +  ")\n";
		}
		System.out.println(info);
	}


	/**
	 * Add a variant object from a position information to the fitted list
	 * @param positionInformation	the position information
	 */
	private void addVariant (Variant positionInformation) {
		ChromosomeWindow chromosome = new ChromosomeWindow(positionInformation.getMetaGenomePosition(), positionInformation.getNextMetaGenomePosition());
		DisplayableVariant displayableVariant = new DisplayableVariant(positionInformation.getType(), chromosome, positionInformation);

		if (positionInformation.getExtraOffset() > 0) {
			ChromosomeWindow extraChromosome = new ChromosomeWindow(positionInformation.getNextMetaGenomePosition() - positionInformation.getExtraOffset(), positionInformation.getNextMetaGenomePosition());
			displayableVariant.setDeadZone(extraChromosome);
		}

		fittedDataList.add(displayableVariant);
	}


	@Override
	public final List<DisplayableVariant> getFittedData(GenomeWindow window, double xRatio) {
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
	 * @return the list of fitted data
	 */
	protected List<DisplayableVariant> getFittedData(int start, int stop) {
		if ((fittedDataList == null) || (fittedDataList.size() == 0)) {
			return null;
		}

		ArrayList<DisplayableVariant> resultList = new ArrayList<DisplayableVariant>();

		int indexStart = findStart(fittedDataList, start, 0, fittedDataList.size() - 1);
		int indexStop = findStop(fittedDataList, stop, 0, fittedDataList.size() - 1);

		// Last index exclusion
		if (indexStop > 0) {
			indexStop--;
		}

		smallestFittedDataIndex = indexStart;
		highestFittedDataIndex = indexStop;

		if (indexStart > 0) {
			if (fittedDataList.get(indexStart - 1).getStop() >= start) {
				smallestFittedDataIndex = indexStart - 1;
				DisplayableVariant currentDisplayableVariant = fittedDataList.get(indexStart - 1);
				ChromosomeWindow chromosome = new ChromosomeWindow(start, currentDisplayableVariant.getStop());
				DisplayableVariant newLastDisplayableVariant = new DisplayableVariant(currentDisplayableVariant.getType(), chromosome, currentDisplayableVariant.getVariantPosition());
				resultList.add(newLastDisplayableVariant);
			}
		}

		for (int i = indexStart; i <= indexStop; i++) {
			resultList.add(fittedDataList.get(i));
		}

		if (indexStop + 1 < fittedDataList.size()) {
			if (fittedDataList.get(indexStop + 1).getStart() <= stop) {
				highestFittedDataIndex = indexStop + 1;
				DisplayableVariant currentDisplayableVariant = fittedDataList.get(indexStop + 1);
				ChromosomeWindow chromosome = new ChromosomeWindow(currentDisplayableVariant.getStart(), stop);
				DisplayableVariant newLastDisplayableVariant = new DisplayableVariant(currentDisplayableVariant.getType(), chromosome, currentDisplayableVariant.getVariantPosition());
				resultList.add(newLastDisplayableVariant);
			}
		}

		return resultList;
	}


	/**
	 * @param offset	value to add to the current highest fitted data index
	 * @return the next fitted variant according to the current highest fitted data index and the offset parameter
	 */
	public DisplayableVariant getNextFittedDisplayableVariant (int offset) {
		DisplayableVariant displayableVariant = null;
		if (highestFittedDataIndex + offset < fittedDataList.size()) {
			displayableVariant = fittedDataList.get(highestFittedDataIndex + offset);
		}
		return displayableVariant;
	}

	
	/**
	 * @param offset	value to add to the current smallest fitted data index
	 * @return the next fitted variant according to the current smallest fitted data index and the offset parameter
	 */
	public DisplayableVariant getPreviousFittedDisplayableVariant (int offset) {
		DisplayableVariant displayableVariant = null;
		if (smallestFittedDataIndex - offset >= 0) {
			displayableVariant = fittedDataList.get(smallestFittedDataIndex - offset);
		}
		return displayableVariant;
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
	private int findStart(List<DisplayableVariant> list, int value, int indexStart, int indexStop) {
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
	private int findStop(List<DisplayableVariant> list, int value, int indexStart, int indexStop) {
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


	/**
	 * @return the genomeFullName
	 */
	public String getGenomeFullName() {
		return genomeFullName;
	}


	/**
	 * @param chromosome the chromosome
	 * @param position position of the variant on the reference genome
	 * @return the associated position information
	 */
	public MGPositionInformation getPositionInformation (Chromosome chromosome, int position) {
		if (genomeInformation.get(chromosome) != null) {
			return genomeInformation.get(chromosome).getPositionInformation(position);
		}
		return null;
	}

}