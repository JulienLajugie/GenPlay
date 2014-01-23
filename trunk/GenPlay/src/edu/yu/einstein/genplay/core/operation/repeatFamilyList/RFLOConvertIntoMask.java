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
package edu.yu.einstein.genplay.core.operation.repeatFamilyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListOfListViewsIterator;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Converts the selected families of a repeat track into a MaskList
 * @author Julien Lajugie
 */
public class RFLOConvertIntoMask implements Operation<SCWList> {

	private boolean	stopped = false;					// true if the operation must be stopped
	private final RepeatFamilyList 	repeatFamilyList;	//
	private final List<String>		selectedFamilies;	//


	/**
	 * Creates an instance of {@link RFLOConvertIntoMask}
	 * @param repeatFamilyList input repeat family list that needs to be converted into mask
	 * @param selectedFamilies family selected to be converted
	 */
	public RFLOConvertIntoMask(RepeatFamilyList repeatFamilyList, List<String> selectedFamilies) {
		this.repeatFamilyList = repeatFamilyList;
		this.selectedFamilies = selectedFamilies;
	}


	@Override
	public SCWList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		ListViewBuilder<ScoredChromosomeWindow> maskLVBuilderPrototype = new MaskListViewBuilder();
		final ListOfListViewBuilder<ScoredChromosomeWindow> maskListBuilder = new ListOfListViewBuilder<ScoredChromosomeWindow>(maskLVBuilderPrototype);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<RepeatFamilyListView> currentList = repeatFamilyList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						List<ListView<ScoredChromosomeWindow>> listOfLV = new ArrayList<ListView<ScoredChromosomeWindow>>();
						for (RepeatFamilyListView currentFamily: currentList) {
							String familyName = currentFamily.getName();
							if (isFamilySelected(familyName)) {
								listOfLV.add(currentFamily);
							}
						}
						Iterator<ScoredChromosomeWindow> listOfLVIterator = new ListOfListViewsIterator<ScoredChromosomeWindow>(listOfLV);
						while (listOfLVIterator.hasNext() && !stopped) {
							ScoredChromosomeWindow currentWindow = listOfLVIterator.next();
							maskListBuilder.addElementToBuild(chromosome, currentWindow);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		op.startPool(threadList);
		return new SimpleSCWList(maskListBuilder.getGenomicList());
	}


	@Override
	public String getDescription() {
		return "Operation: Convert into Mask";
	}


	@Override
	public String getProcessingDescription() {
		return "Converting into Mask";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	/**
	 * @param familyName a repeat family name
	 * @return true if the specified family is part of the selected families. False otherwise
	 */
	private boolean isFamilySelected(String familyName) {
		for (String currentFamilyName: selectedFamilies) {
			if (currentFamilyName.equals(familyName)) {
				return true;
			}
		}
		return false;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
