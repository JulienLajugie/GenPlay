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
package edu.yu.einstein.genplay.core.multiGenome.operation.BED;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.dataStructure.list.ChromosomeArrayListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.DoubleArrayAsDoubleList;
import edu.yu.einstein.genplay.dataStructure.list.arrayList.IntArrayAsIntegerList;


/**
 * This class help for the convertion of VCF track to a {@link ScoredChromosomeWindowList} for a specific allele.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class AlleleSettingsBedConvert extends AlleleSettingsBed {

	private final ChromosomeListOfLists<Integer> 	startList;	// List of start position.
	private final ChromosomeListOfLists<Integer> 	stopList;	// List of stop position.
	private final ChromosomeListOfLists<Double> 	scoreList;	// List of scores.


	/**
	 * Constructor of {@link AlleleSettingsBedConvert}
	 * @param path
	 * @param allele
	 */
	protected AlleleSettingsBedConvert (AlleleType allele, CoordinateSystemType coordinateSystem) {
		super(allele, coordinateSystem);
		startList = new ChromosomeArrayListOfLists<Integer>();
		stopList = new ChromosomeArrayListOfLists<Integer>();
		scoreList = new ChromosomeArrayListOfLists<Double>();

		// initialize the sublists
		int chromosomeListSize = ProjectManager.getInstance().getProjectChromosome().getChromosomeList().size();
		for (int i = 0; i < chromosomeListSize; i++) {
			startList.add(new IntArrayAsIntegerList());
			stopList.add(new IntArrayAsIntegerList());
			scoreList.add(new DoubleArrayAsDoubleList());
		}
	}


	/**
	 * Add current information to the different list: start, stop and score
	 * @param chromosome	the current chromosome
	 * @param score			the score
	 * @param includeReferences include the references (0)
	 * @param includeNoCall 	include the no call (.)
	 */
	public void addCurrentInformation (Chromosome chromosome, Object score, boolean includeReferences, boolean includeNoCall) {
		boolean valid = true;
		Double dbScore = Double.parseDouble(score.toString());
		if (dbScore == null) {
			valid = false;
		}
		/*if (valid && (currentAltIndex == -1) && (!includeReferences)) {
			valid = false;
		}*/
		if (valid) {
			//int start = getCurrentStart();
			//int stop = getDisplayableCurrentStop();
			startList.add(chromosome, currentStart);
			stopList.add(chromosome, currentStop);
			scoreList.add(chromosome, dbScore);
		} else {
			System.err.println("AlleleSettingsBedConvert.addCurrentInformation() Could not convert '" + score + "' into a double.");
		}
	}


	/**
	 * @return the startList
	 */
	public ChromosomeListOfLists<Integer> getStartList() {
		return startList;
	}


	/**
	 * @return the stopList
	 */
	public ChromosomeListOfLists<Integer> getStopList() {
		return stopList;
	}


	/**
	 * @return the scoreList
	 */
	public ChromosomeListOfLists<Double> getScoreList() {
		return scoreList;
	}

}
