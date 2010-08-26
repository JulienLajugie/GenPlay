/**
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.overLap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.ScoreCalculationTwoTrackMethod;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.binList.BinList;
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
public class SCWLTwoTracksEngine implements Serializable, Stoppable {
	
	private static final long serialVersionUID = 2965349494486829320L;
	private final 	List<ChromosomeListOfLists<?>> 		list;				//list containing originals lists
	private 		List<ScoredChromosomeWindow> 		newScwList;			//new list
	private final	ScoreCalculationTwoTrackMethod		scm;				//operation to apply
	private 		Chromosome 							chromosome;
	private			boolean[]							isSCWList;			//stores the instance class of the lists
	private			boolean[]							onStart;			//stores the position on the current window
	private			boolean[]							validPosition;		//allow to know if the current index exists
	private			Double[]							currentScore;		//stores the current scores
	private			Integer[]							currentPosition;	//stores the current positions
	private			Integer[]							currentIndex;		//stores the current index
	private 		boolean								stopped = false;	// true if the operation must be stopped
	
	
	/**
	 * SCWLTwoTracks constructor
	 * 
	 * @param scwList1	first track
	 * @param scwList2	second track
	 * @param scm		operation
	 */
	public SCWLTwoTracksEngine (ScoreCalculationTwoTrackMethod scm) {
		this.list = new ArrayList<ChromosomeListOfLists<?>>();
		this.newScwList = new ArrayList<ScoredChromosomeWindow>();
		this.scm = scm;
	}
	
	
	/**
	 * init method
	 * Initializes attributes to run the process.
	 * @param list1 
	 * @param list2 
	 */
	public void init (ChromosomeListOfLists<?> list1, ChromosomeListOfLists<?> list2, Chromosome chromosome) {
		//index 0 refers to the first track
		//index 1 refers to the second track
		this.list.add(list1);
		this.list.add(list2);
		this.isSCWList = new boolean[2];
		if (list1 instanceof ScoredChromosomeWindowList) {
			this.isSCWList[0] = true;
		} else {
			this.isSCWList[0] = false;
		}
		if (list2 instanceof ScoredChromosomeWindowList) {
			this.isSCWList[1] = true;
		} else {
			this.isSCWList[1] = false;
		}
		this.chromosome = chromosome;
		this.onStart = new boolean[2];
		this.onStart[0] = true;
		this.onStart[1] = true;
		this.validPosition = new boolean[2];
		if (getTrackSize(0) > 0) {
			this.validPosition[0] = true;
		} else {
			this.validPosition[0] = false;
		}
		if (getTrackSize(1) > 0) {
			this.validPosition[1] = true;
		} else {
			this.validPosition[1] = false;
		}
		this.currentScore = new Double[2];
		this.currentScore[0] = 0.0;
		this.currentScore[1] = 0.0;
		this.currentPosition = new Integer[2];
		this.currentPosition[0] = 0;
		this.currentPosition[1] = 0;
		this.currentIndex = new Integer[2];
		this.currentIndex[0] = 0;
		this.currentIndex[1] = 0;
		run();
	}
	
	
	/**
	 * run method
	 * This method runs the algorithm to determine the new list.
	 */
	private void run () {
		int min;
		while (isValid() && !stopped) {
			min = min ();	//get the relative position of the current position on the track 1 and the current position on the track 2
			switch (min) {
			case 0:		//current positions are at the same place
				if (onStart(0)) {	//the current position of the first track is on the start
					if (onStart(1)) {	//the current position of the second track is on the start
						manageStart(0);
						manageStart(1);
					} else {	//the current position of the second track is on the stop
						manageStop(1);
						manageStart(0);
					}
				} else {	//the current position of the first track is on the stop
					if (onStart(1)) {	//the current position of the second track is on the start
						manageStop(0);
						manageStart(1);
					} else {	//the current position of the second track is on the stop
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
			case -1:	//the actual minimum is on the track 1
				if (onStart(0)) {	//the current position of the first track is on the start
					if (!onStart(1)) {	//the current position of the second track is on the stop
						currentPosition[1] = getStart(0);
						addPosition();
					}
					manageStart(0);
				} else {	//the current position of the first track is on the stop
					if (onStart(1)) {	//the current position of the second track is on the start
						manageStop(0);
					} else {	//the current position of the second track is on the stop
						int stop = getStop(0);
						manageStop(0);
						currentPosition[0] = stop;
						if (!validPosition[0]) {
							manageStop(1);
						}
					}
					
				}
				break;
			case 1:		//the actual minimum is on the track 2
				if (onStart(0)) {	//the current position of the first track is on the start
					if (onStart(1)) {	//the current position of the second track is on the start
						manageStart(1);
					} else {	//the current position of the second track is on the stop
						manageStop(1);
					}
				} else {	//the current position of the first track is on the stop
					if (onStart(1)) {	//the current position of the second track is on the start
						currentPosition[1] = getStart(1);
						addPosition();
						manageStart(1);
					} else {	//the current position of the second track is on the stop
						int stop = getStop(1);
						manageStop(1);
						currentPosition[0] = stop;
						if (!validPosition[1]) {
							manageStop(1);
						}
					}
				}
				break;
			}
		}
		//If the first track is still valid, these positions must be added
		finishTrack(0);
		//If the second track is still valid, these positions must be added
		finishTrack(1);
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
	 * This method says if the current position on a track is on the start or on the stop of the current position.
	 * 
	 * @param 	track	the concerned track
	 * @return			true if the current position is on the start
	 */
	private boolean onStart (int track) {
		return onStart[track];
	}
	
	/**
	 * min method
	 * This method returns the relative position of the current position on the first track with the second track. 
	 * 
	 * @return	0 	if it is equal,
	 * 			-1 	if the current position on the first track is lower than the current position on the second track
	 * 			1 	if the current position on the first track is higher than the current position on the second track 
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
		if (getScore() != 0.0) {
			newScwList.add(new ScoredChromosomeWindow(	currentPosition[0],
														currentPosition[1],
														getScore()));
		}
	}
	
	/**
	 * nextPosition method
	 * This method increments the current position for the track and manage his validity.
	 * 
	 * @param track	associated track
	 */
	private void nextPosition (int track) {
		int index = currentIndex[track] + 1;
		boolean valid = true;
		while (valid && !stopped) {
			if (index >= getTrackSize(track)) {
				valid = false;
				this.validPosition[track] = false;
			} else {
				if (getScore(track, index) == 0.0) {
					index++;
				} else {
					valid = false;
				}
			}
		}
		currentIndex[track] = index;
	}
	
	/**
	 * manageStart method
	 * This method manages required operations for a start position on a track.
	 * 
	 * @param track associated track
	 */
	private void manageStart (int track) {
		currentPosition[0] = getStart(track);
		currentScore[track] = getScore(track);
		onStart[track] = false;
	}
	
	/**
	 * manageStop method
	 * This method manages required operations for a start position on a track.
	 * 
	 * @param track associated track
	 */
	private void manageStop (int track) {
		currentPosition[1] = getStop(track);
		addPosition();
		currentScore[track] = 0.0;
		nextPosition(track);
		onStart[track] = true;
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
	 * finishTrack method
	 * This method allows to finish the recording of the unfinished track.
	 * 
	 * @param track
	 */
	private void finishTrack (int track) {
		if (validPosition[track]) {
			for (int i = currentIndex[track]; i < getTrackSize(track) && !stopped; i++) {
				newScwList.add(new ScoredChromosomeWindow(	getStart(track),
															getStop(track),
															getScore(track)));
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
		if (currentScore[0] != 0.0 && currentScore[1] != 0.0) {
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
	
	public List<ScoredChromosomeWindow> getList() {
		return newScwList;
	}
	
	private int getStart (int track) {
		if (this.isSCWList[track]) {
			return ((ScoredChromosomeWindow) this.list.get(track).get(chromosome).get(this.currentIndex[track])).getStart();
		} else {
			return this.currentIndex[track] * ((BinList)this.list.get(track)).getBinSize();
		}
	}
	
	private int getStop (int track) {
		if (this.isSCWList[track]) {
			return ((ScoredChromosomeWindow) this.list.get(track).get(chromosome).get(this.currentIndex[track])).getStop();
		} else {
			return (this.currentIndex[track] + 1) * ((BinList)this.list.get(track)).getBinSize();
		}
	}
	
	private Double getScore (int track) {
		if (this.isSCWList[track]) {
			return ((ScoredChromosomeWindow) this.list.get(track).get(chromosome).get(this.currentIndex[track])).getScore();
		} else {
			return (Double) this.list.get(track).get(chromosome).get(this.currentIndex[track]);
		}
	}
	
	private Double getScore (int track, int index) {
		if (this.isSCWList[track]) {
			return ((ScoredChromosomeWindow) this.list.get(track).get(chromosome).get(index)).getScore();
		} else {
			return (Double) this.list.get(track).get(chromosome).get(index);
		}
	}
	
	private int getTrackSize (int track) {
		if (this.isSCWList[track]) {
			return ((ScoredChromosomeWindowList) this.list.get(track)).get(chromosome).size();
		} else {
			return ((BinList) this.list.get(track)).get(chromosome).size();
		}
	}
	
	
	///////////////////////////	MISC
	
	public String showList() {
		String s = "";
		for (int i=0; i < newScwList.size(); i++) {
			s += "[" + newScwList.get(i).getStart() + "; " + newScwList.get(i).getStop() + "; " + newScwList.get(i).getScore() + "]";
		}
		return s;
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}