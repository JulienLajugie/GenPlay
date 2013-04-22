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
package edu.yu.einstein.genplay.core.operation.repeatFamilyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList.RepeatFamilyList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.MaskChromosomeWindow;
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
	public void stop() {
		this.stopped = true;		
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
	public SCWList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<ScoredChromosomeWindow>>> threadList = new ArrayList<Callable<List<ScoredChromosomeWindow>>>();

		for (short i = 0; i < repeatFamilyList.size(); i++) {
			final List<RepeatFamilyListView> currentList = repeatFamilyList.get(i);

			Callable<List<ScoredChromosomeWindow>> currentThread = new Callable<List<ScoredChromosomeWindow>>() {
				@Override
				public List<ScoredChromosomeWindow> call() throws Exception {
					List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
					if ((currentList != null) && (currentList.size() != 0)) {
						for (RepeatFamilyListView currentFamily: currentList) {
							// if the operation is being stopped we directly return null
							if (stopped) {
								op.notifyDone();
								return null;
							}
							String familyName = currentFamily.getName();
							if (isFamilySelected(familyName)) {
								List<ScoredChromosomeWindow> maskList = copyAsSCWList(currentFamily.getRepeatList());
								resultList.addAll(maskList);
							}
						}
						Collections.sort(resultList);


					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultList;
				}


			};

			threadList.add(currentThread);
		}
		List<List<ScoredChromosomeWindow>> result = op.startPool(threadList);
		if (result != null) {
			SCWList resultList = new SimpleSCWList(result);
			return resultList;
		} else {
			return null;
		}
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


	/**
	 * @param repeatList A list of repeats
	 * @return the list of repeats as a chromosome window lists
	 */
	private List<ScoredChromosomeWindow> copyAsSCWList(List<ChromosomeWindow> repeatList) {
		if (repeatList == null) {
			return null;
		}
		List<ScoredChromosomeWindow> resultList = new ArrayList<ScoredChromosomeWindow>();
		for (ChromosomeWindow currentRepeat: repeatList) {
			// if the operation is being stopped we directly return null
			if (stopped) {
				return null;
			}
			resultList.add(new MaskChromosomeWindow(currentRepeat));
		}
		return resultList;
	}


	@Override
	public int getStepCount() {
		return 1;
	}
}
