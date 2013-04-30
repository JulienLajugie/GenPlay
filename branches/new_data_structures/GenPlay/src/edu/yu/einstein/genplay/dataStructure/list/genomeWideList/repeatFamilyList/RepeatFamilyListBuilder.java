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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.SimpleListView.SimpleListViewBuilder;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Class to build list of {@link ListView} of {@link RepeatFamilyListView} objects needed to create instances of {@link RepeatFamilyList}.
 * @author Julien Lajugie
 */
public class RepeatFamilyListBuilder {

	/** List organised by chromosome of map of family entry associated to {@link RepeatFamilyListViewBuilder} objects */
	private List<Map<String, RepeatFamilyListViewBuilder>> builders;

	/** We store the {@link ProjectChromosome} to avoid wasting time retrieving it */
	private final ProjectChromosome projectChromosome;


	/**
	 * Creates an instance of {@link RepeatFamilyListBuilder}
	 */
	public RepeatFamilyListBuilder() {
		projectChromosome =  ProjectManager.getInstance().getProjectChromosome();
		int chromosomeCount = projectChromosome.size();
		builders = new ArrayList<Map<String,RepeatFamilyListViewBuilder>>(chromosomeCount);
		for (int i = 1; i < chromosomeCount; i++) {
			builders.set(i, new HashMap<String, RepeatFamilyListViewBuilder>());
		}
	}


	/**
	 * Adds a repeat to the list to be built
	 * @param chromosome a chromosome of the repeat
	 * @param repeatFamilyName name of the family of the repeat
	 * @param repeat {@link ChromosomeWindow} repeat with the start and the stop position of the repeat
	 * @throws InvalidChromosomeException
	 * @throws ObjectAlreadyBuiltException
	 */
	public void addElementToBuild(Chromosome chromosome, String repeatFamilyName, ChromosomeWindow repeat) throws InvalidChromosomeException, ObjectAlreadyBuiltException {
		if (builders == null) {
			throw new ObjectAlreadyBuiltException();
		}
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		if (!builders.get(chromosomeIndex).containsKey(repeatFamilyName)) {
			builders.get(chromosomeIndex).put(repeatFamilyName, new RepeatFamilyListViewBuilder(repeatFamilyName));
		}
		builders.get(chromosomeIndex).get(repeatFamilyName).addElementToBuild(repeat.getStart(), repeat.getStop());
	}


	/**
	 * @return the list of {@link ListView} of RepeatFamilyListView
	 */
	public List<ListView<RepeatFamilyListView>> getGenomicList() {
		List<ListView<RepeatFamilyListView>> genomicRepeatList = new ArrayList<ListView<RepeatFamilyListView>>();
		for (Map<String, RepeatFamilyListViewBuilder> chromosomeFamilies: builders) {
			ListViewBuilder<RepeatFamilyListView> chrFamiliesLV = new SimpleListViewBuilder<RepeatFamilyListView>();
			for (RepeatFamilyListViewBuilder familyBuilder: chromosomeFamilies.values()) {
				chrFamiliesLV.addElementToBuild((RepeatFamilyListView) familyBuilder.getListView());
			}
			genomicRepeatList.add(chrFamiliesLV.getListView());

		}
		builders = null;
		return genomicRepeatList;
	}
}
