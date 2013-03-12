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
package edu.yu.einstein.genplay.core.operation.SCWList.overlap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreCalculationTwoLayersMethod;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.list.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomicDataList.ImmutableGenomicDataList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * This class manages theses operations on two layers:
 * 	- addition
 * 	- subtraction
 * 	- multiplication
 * 	- division
 * 
 * @author Nicolas
 * @version 0.1
 */
public class SCWLTwoLayersEngine implements Serializable, Stoppable {

	private static final long serialVersionUID = 2965349494486829320L;
	private final 	List<ImmutableGenomicDataList<?>> 	list;				//list containing originals lists
	private final 	List<ScoredChromosomeWindow> 		newScwList;			//new list
	private final	ScoreCalculationTwoLayersMethod		scm;				//operation to apply
	private 		Chromosome 							chromosome;
	private			boolean[]							isSCWList;			//stores the instance class of the lists
	private			boolean[]							onStart;			//stores the position on the current window
	private			boolean[]							validPosition;		//allow to know if the current index exists
	private			Double[]							currentScore;		//stores the current scores
	private			Integer[]							currentPosition;	//stores the current positions
	private			Integer[]							currentIndex;		//stores the current index
	private 		boolean								stopped = false;	// true if the operation must be stopped


	/**
	 * SCWLTwoLayersEngine constructor
	 * 
	 * @param scm		operation
	 */
	public SCWLTwoLayersEngine (ScoreCalculationTwoLayersMethod scm) {
		list = new ArrayList<ImmutableGenomicDataList<?>>();
		newScwList = new ArrayList<ScoredChromosomeWindow>();
		this.scm = scm;
	}


	/**
	 * init method
	 * Initializes attributes to run the process.
	 * @param list1 		first list
	 * @param list2 		second list
	 * @param chromosome 	chromosome
	 */
	public void init (ImmutableGenomicDataList<?> list1, ImmutableGenomicDataList<?> list2, Chromosome chromosome) {
		//index 0 refers to the first layer
		//index 1 refers to the second layer
		list.add(list1);
		list.add(list2);
		isSCWList = new boolean[2];
		isSCWList[0] = list1 instanceof ScoredChromosomeWindowList;
		isSCWList[1] = list2 instanceof ScoredChromosomeWindowList;
		this.chromosome = chromosome;
		onStart = new boolean[2];
		onStart[0] = true;
		onStart[1] = true;
		validPosition = new boolean[2];
		validPosition[0] = getLayerSize(0) > 0;
		validPosition[1] = getLayerSize(1) > 0;
		currentScore = new Double[2];
		currentScore[0] = 0.0;
		currentScore[1] = 0.0;
		currentPosition = new Integer[2];
		currentPosition[0] = 0;
		currentPosition[1] = 0;
		currentIndex = new Integer[2];
		currentIndex[0] = 0;
		currentIndex[1] = 0;
		run();
	}


	/**
	 * run method
	 * This method runs the algorithm to determine the new list.
	 */
	private void run () {
		int min;
		while (isValid() && !stopped) {
			min = min ();	//get the relative position of the current position on the layer 1 and the current position on the layer 2
			switch (min) {
			case 0:		//current positions are at the same place
				if (onStart(0)) {	//the current position of the first layer is on the start
					if (onStart(1)) {	//the current position of the second layer is on the start
						manageStart(0);
						manageStart(1);
					} else {	//the current position of the second layer is on the stop
						manageStop(1);
						manageStart(0);
					}
				} else {	//the current position of the first layer is on the stop
					if (onStart(1)) {	//the current position of the second layer is on the start
						manageStop(0);
						manageStart(1);
					} else {	//the current position of the second layer is on the stop
						currentPosition[1] = getStop(0);
						addPosition();
						nextPosition(0);
						nextPosition(1);
						onStart[0] = true;
						onStart[1] = true;
						currentScore[0] = 0.0;
						currentScore[1] = 0.0;
					}
				}
				break;
			case -1:	//the actual minimum is on the layer 1
				if (onStart(0)) {	//the current position of the first layer is on the start
					if (!onStart(1)) {	//the current position of the second layer is on the stop
						currentPosition[1] = getStart(0);
						addPosition();
					}
					manageStart(0);
				} else {	//the current position of the first layer is on the stop
					if (onStart(1)) {	//the current position of the second layer is on the start
						manageStop(0);
					} else {	//the current position of the second layer is on the stop
						int stop = getStop(0);
						manageStop(0);
						currentPosition[0] = stop;
						if (!validPosition[0]) {
							manageStop(1);
						}
					}

				}
				break;
			case 1:		//the actual minimum is on the layer 2
				if (onStart(0)) {	//the current position of the first layer is on the start
					if (onStart(1)) {	//the current position of the second layer is on the start
						manageStart(1);
					} else {	//the current position of the second layer is on the stop
						manageStop(1);
					}
				} else {	//the current position of the first layer is on the stop
					if (onStart(1)) {	//the current position of the second layer is on the start
						currentPosition[1] = getStart(1);
						addPosition();
						manageStart(1);
					} else {	//the current position of the second layer is on the stop
						int stop = getStop(1);
						manageStop(1);
						currentPosition[0] = stop;
						if (!validPosition[1]) {
							manageStop(0);
						}
					}
				}
				break;
			}
		}
		//If the first layer is still valid, these positions must be added
		finishLayer(0);
		//If the second layer is still valid, these positions must be added
		finishLayer(1);
	}

	/**
	 * isValid method
	 * This method check if the current position on each list is valid.
	 * 
	 * @return	true if both position are correct
	 */
	private boolean isValid () {
		if (validPosition[0] & validPosition[1]) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * onStart method
	 * This method says if the current position on a layer is on the start or on the stop of the current position.
	 * 
	 * @param 	layer	the concerned layer
	 * @return			true if the current position is on the start
	 */
	private boolean onStart (int layer) {
		return onStart[layer];
	}

	/**
	 * min method
	 * This method returns the relative position of the current position on the first layer with the second layer.
	 * 
	 * @return	0 	if it is equal,
	 * 			-1 	if the current position on the first layer is lower than the current position on the second layer
	 * 			1 	if the current position on the first layer is higher than the current position on the second layer
	 */
	private int min () {
		int currentMin0;
		int currentMin1;
		if (onStart(0)) {
			currentMin0 = getStart(0);
		} else {
			currentMin0 = getStop(0);
		}
		if (onStart(1)) {
			currentMin1 = getStart(1);
		} else {
			currentMin1 = getStop(1);
		}
		if (currentMin0 < currentMin1) {
			return -1;
		} else if (currentMin0 == currentMin1) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * addPosition method
	 * This method adds the current position and score in the new list
	 */
	private void addPosition () {
		double score = getScore();
		if (score != 0.0) {
			if (currentPosition[1] > currentPosition[0]) {
				newScwList.add(new SimpleScoredChromosomeWindow(	currentPosition[0],
						currentPosition[1],
						score));
			}
		}
	}

	/**
	 * nextPosition method
	 * This method increments the current position for the layer and manage his validity.
	 * 
	 * @param layer	associated layer
	 */
	private void nextPosition (int layer) {
		int index = currentIndex[layer] + 1;
		boolean valid = true;
		while (valid && !stopped) {
			if (index >= getLayerSize(layer)) {
				valid = false;
				validPosition[layer] = false;
			} else {
				if (getScore(layer, index) == 0.0) {
					index++;
				} else {
					valid = false;
				}
			}
		}
		currentIndex[layer] = index;
	}

	/**
	 * manageStart method
	 * This method manages required operations for a start position on a layer.
	 * 
	 * @param layer associated layer
	 */
	private void manageStart (int layer) {
		currentPosition[0] = getStart(layer);
		currentScore[layer] = getScore(layer);
		onStart[layer] = false;
	}

	/**
	 * manageStop method
	 * This method manages required operations for a start position on a layer.
	 * 
	 * @param layer associated layer
	 */
	private void manageStop (int layer) {
		currentPosition[1] = getStop(layer);
		addPosition();
		currentScore[layer] = 0.0;
		nextPosition(layer);
		onStart[layer] = true;
	}

	/**
	 * getScore method
	 * This method manages the calculation of the score according to the score calculation method.
	 * 
	 * @return	the score
	 */
	private double getScore () {
		switch (scm) {
		case ADDITION:
			return sum();
		case SUBTRACTION:
			return subtraction();
		case MULTIPLICATION:
			return multiplication();
		case DIVISION:
			return division();
		case AVERAGE:
			return average();
		case MAXIMUM:
			return maximum();
		case MINIMUM:
			return minimum();
		default:
			return -1.0;
		}
	}

	/**
	 * finishlayer method
	 * This method allows to finish the recording of the unfinished layer.
	 * 
	 * @param layer
	 */
	private void finishLayer (int layer) {
		if (validPosition[layer]) {
			if (layer == 0) {
				currentScore[1] = 0.0;
			} else {
				currentScore[0] = 0.0;
			}
			for (int i = currentIndex[layer]; (i < getLayerSize(layer)) && !stopped; i++) {
				currentScore[layer] = getScore(layer);
				double score = getScore();
				if (score != 0) {
					newScwList.add(new SimpleScoredChromosomeWindow(getStart(layer), getStop(layer), score));
					currentIndex[layer]++;
				}
			}
		}
	}


	///////////////////////////	Calculation methods

	private double sum() {
		return (currentScore[0] + currentScore[1]);
	}

	private double subtraction() {
		return (currentScore[0] - currentScore[1]);
	}

	private double multiplication() {
		return (currentScore[0] * currentScore[1]);
	}

	private double division() {
		if ((currentScore[0] != 0.0) && (currentScore[1] != 0.0)) {
			return currentScore[0] / currentScore[1];
		} else {
			return 0.0;
		}
	}

	private double average() {
		return sum() / 2;
	}

	private double maximum() {
		return Math.max(currentScore[0], currentScore[1]);
	}

	private double minimum() {
		return Math.min(currentScore[0], currentScore[1]);
	}

	///////////////////////////	GETTERS

	/**
	 * @return the new score chromosome window list
	 */
	public List<ScoredChromosomeWindow> getList() {
		return newScwList;
	}

	private int getStart (int layer) {
		if (isSCWList[layer]) {
			return ((ScoredChromosomeWindow) list.get(layer).getView(chromosome).get(currentIndex[layer])).getStart();
		} else {
			return currentIndex[layer] * ((BinList)list.get(layer)).getBinSize();
		}
	}

	private int getStop (int layer) {
		if (isSCWList[layer]) {
			return ((ScoredChromosomeWindow) list.get(layer).getView(chromosome).get(currentIndex[layer])).getStop();
		} else {
			return (currentIndex[layer] + 1) * ((BinList)list.get(layer)).getBinSize();
		}
	}

	private Double getScore (int layer) {
		/*if (this.isSCWList[layer]) {
			return ((ScoredChromosomeWindow) this.list.get(layer).get(chromosome).get(this.currentIndex[layer])).getScore();
		} else {
			return (Double) this.list.get(layer).get(chromosome).get(this.currentIndex[layer]);
		}*/
		return getScore(layer, currentIndex[layer]);
	}

	private Double getScore (int layer, int index) {
		if (isSCWList[layer]) {
			return ((ScoredChromosomeWindow) list.get(layer).getView(chromosome).get(index)).getScore();
		} else {
			return (Double) list.get(layer).getView(chromosome).get(index);
		}
	}

	private int getLayerSize (int layer) {
		if (isSCWList[layer]) {
			return ((ScoredChromosomeWindowList) list.get(layer)).getView(chromosome).size();
		} else {
			List<Double> data = ((BinList) list.get(layer)).get(chromosome);
			if (data != null) {
				return data.size();
			}
			return 0;
		}
	}


	///////////////////////////	MISC

	/**
	 * @return the string to display information
	 */
	public String showList() {
		String s = "";
		for (int i=0; i < newScwList.size(); i++) {
			s += "[" + newScwList.get(i).getStart() + "; " + newScwList.get(i).getStop() + "; " + newScwList.get(i).getScore() + "]";
		}
		return s;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
