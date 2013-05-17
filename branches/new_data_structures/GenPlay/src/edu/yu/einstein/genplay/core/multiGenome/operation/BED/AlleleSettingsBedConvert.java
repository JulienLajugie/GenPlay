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

import java.util.List;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * This class help for the convertion of VCF track to a {@link SCWList} for a specific allele.
 * 
 * @author Nicolas Fourel
 */
public class AlleleSettingsBedConvert extends AlleleSettingsBed {

	private final SCWListBuilder listBuilder;


	/**
	 * Constructor of {@link AlleleSettingsBedConvert}
	 * @param path
	 * @param allele
	 * @throws CloneNotSupportedException
	 */
	protected AlleleSettingsBedConvert (AlleleType allele, CoordinateSystemType coordinateSystem) throws CloneNotSupportedException {
		super(allele, coordinateSystem);
		SCWListViewBuilder lvBuilderPrototype = new GenericSCWListViewBuilder();
		listBuilder = new SCWListBuilder(lvBuilderPrototype);
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
		Float dbScore = Float.parseFloat(score.toString());
		if (dbScore == null) {
			valid = false;
		}
		/*if (valid && (currentAltIndex == -1) && (!includeReferences)) {
			valid = false;
		}*/
		if (valid) {
			//int start = getCurrentStart();
			//int stop = getDisplayableCurrentStop();
			listBuilder.addElementToBuild(chromosome, currentStart, currentStop, dbScore);
		} else {
			System.err.println("AlleleSettingsBedConvert.addCurrentInformation() Could not convert '" + score + "' into a double.");
		}
	}


	/**
	 * @return the startList
	 */
	public List<ListView<ScoredChromosomeWindow>> getListOfListViews() {
		return listBuilder.getListOfListViews();
	}
}
