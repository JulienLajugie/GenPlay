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
package yu.einstein.gdp2.core.list.SCWList.overLap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.ScoreCalculationTwoTrackMethod;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.gui.statusBar.Stoppable;

/**
 * This class manages theses operations on two tracks:
 * 	- addition
 * 	- subtraction
 * 	- multiplication
 * 	- division 
 * 
 * @author Nicolas
 * @version 0.1
 */
public class SCWLTwoTracksManagement implements Serializable, Stoppable {
	
	private static final long serialVersionUID = -4066526880193456101L;
	protected 	final 	ChromosomeManager 					chromosomeManager;	//ChromosomeManager
	private 	final 	List<ChromosomeListOfLists<?>> 		scwList;			//list containing originals lists
	private 			List<SCWLTwoTracksEngine>			twoTracksEngineList;
	
	/**
	 * SCWLTwoTracks constructor
	 * 
	 * @param list1	first track
	 * @param list2	second track
	 * @param scm		operation
	 */
	public SCWLTwoTracksManagement (	ChromosomeListOfLists<?> list1,
										ChromosomeListOfLists<?> list2,
										ScoreCalculationTwoTrackMethod scm) {
		this.chromosomeManager = ChromosomeManager.getInstance();
		this.scwList = new ArrayList<ChromosomeListOfLists<?>>();
		this.scwList.add(list1);
		this.scwList.add(list2);
		this.twoTracksEngineList = new ArrayList<SCWLTwoTracksEngine>();
		for (int i = 0; i < chromosomeManager.size(); i++) {
			this.twoTracksEngineList.add(new SCWLTwoTracksEngine(scm));
		}
	}
	
	public void run(Chromosome chromosome) {
		this.twoTracksEngineList.get(chromosomeManager.getIndex(chromosome)).init(scwList.get(0), scwList.get(1), chromosome);
	}

	public List<ScoredChromosomeWindow> getList(Chromosome chromosome) {
		return this.twoTracksEngineList.get(chromosomeManager.getIndex(chromosome)).getList();
	}

	@Override
	public void stop() {
		if (twoTracksEngineList != null) {
			for (SCWLTwoTracksEngine currentEngine: twoTracksEngineList) {
				currentEngine.stop();
			}
		}
	}
	
}
