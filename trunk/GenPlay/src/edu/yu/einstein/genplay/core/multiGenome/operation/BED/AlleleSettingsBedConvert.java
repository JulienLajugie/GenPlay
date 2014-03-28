/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.core.multiGenome.operation.BED;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.pileupFlattener.GenomeWideFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.PileupFlattener;
import edu.yu.einstein.genplay.core.pileupFlattener.SimpleSCWPileupFlattener;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.dataStructure.enums.SCWListType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * This class help for the convertion of VCF track to a {@link SCWList} for a specific allele.
 * 
 * @author Nicolas Fourel
 */
public class AlleleSettingsBedConvert extends AlleleSettingsBed {

	private final GenomeWideFlattener gwPileupFlattener;

	private Chromosome retainedChromo;
	private int retainedStart;
	private final List<Integer> retainedStops;
	private final List<Float>	retainedScores;


	/**
	 * Constructor of {@link AlleleSettingsBedConvert}
	 * @param path
	 * @param allele
	 * @throws CloneNotSupportedException
	 */
	protected AlleleSettingsBedConvert (AlleleType allele, CoordinateSystemType coordinateSystem) throws CloneNotSupportedException {
		super(allele, coordinateSystem);
		PileupFlattener flattenerPrototype = new SimpleSCWPileupFlattener(ScoreOperation.ADDITION, SCWListType.GENERIC);
		gwPileupFlattener = new GenomeWideFlattener(flattenerPrototype);
		retainedStops = new ArrayList<Integer>();
		retainedScores = new ArrayList<Float>();
	}


	/**
	 * Add current information to the different list: start, stop and score
	 * @param chromosome	the current chromosome
	 * @param score			the score
	 * @param includeReferences include the references (0)
	 * @param includeNoCall 	include the no call (.)
	 */
	public void addCurrentInformation (Chromosome chromosome, Object score, boolean includeReferences, boolean includeNoCall) {
		Float dbScore = Float.parseFloat(score.toString());
		if (dbScore != null) {
			if (retainedStops.isEmpty()) {
				retainedChromo = chromosome;
				retainedStart = currentStart;
			} else {
				if ((currentStart > retainedStart) || (chromosome != retainedChromo)) {
					for (int i = 0; i < retainedStops.size(); i++) {
						gwPileupFlattener.addWindow(retainedChromo, retainedStart, retainedStops.get(i), retainedScores.get(i));
					}
					retainedStops.clear();
					retainedScores.clear();
					retainedChromo = chromosome;
					retainedStart = currentStart;
				}
			}
			retainedStops.add(currentStop);
			retainedScores.add(dbScore);
		} else {
			System.err.println("AlleleSettingsBedConvert.addCurrentInformation() Could not convert '" + score + "' into a double.");
		}
	}


	/**
	 * @return the startList
	 */
	public List<ListView<ScoredChromosomeWindow>> getListOfListViews() {
		return gwPileupFlattener.getListOfListViews();
	}
}
