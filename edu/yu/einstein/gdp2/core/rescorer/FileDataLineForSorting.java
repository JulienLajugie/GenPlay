/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.rescorer;

import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.exception.InvalidChromosomeException;


/**
 * Class to sort the file based on chromosome names and start values
 * @author Chirag Gorasia
 * @version 0.1
 */
public class FileDataLineForSorting implements Comparable<FileDataLineForSorting>{

	private String chromosomeName;														// chromosome name
	private int start;																	// start position
	private int stop;																	// stop position
	private double score;																// score
		
	
	/**
	 * Creates an instance of {@link FileDataLineForSorting}
	 * @param chromosomeName
	 * @param start
	 * @param stop
	 * @param score
	 */
	public FileDataLineForSorting(String chromosomeName, int start, int stop, double score) {
		this.chromosomeName = chromosomeName;
		this.start = start;
		this.stop = stop;
		this.score = score;		
	}
	
	
	/**
	 * Returns the chromosomeNumber 
	 * @return chromosomeNumber
	 */
	public int getChromosomeNumber() {
		try {
			return ChromosomeManager.getInstance().getIndex(getChromosomeName());
		} catch (InvalidChromosomeException e) {
			return 0;
		}
	}

	
	/**
	 * Returns the chromosmeName			
	 * @return chromosomeName
	 */
	public String getChromosomeName() {
		return chromosomeName;
	}
	
	
	/**
	 * Returns the start position
	 * @return start
	 */
	public int getStart() {
		return start;
	}
	
	
	/**
	 * Returns the stop position
	 * @return stop
	 */
	public int getStop() {
		return stop;
	}
	
	
	/**
	 * Returns the score
	 * @return score
	 */
	public double getScore() {
		return score;
	}
	
	
	@Override
	public int compareTo(FileDataLineForSorting o) {
		if (this.getChromosomeNumber() > o.getChromosomeNumber()) {
			return 1;
		} else if (this.getChromosomeNumber() < o.getChromosomeNumber()) {
			return -1;
		} else { 
			if (this.start > o.start) {
				return 1;
			} else if (this.start < o.start) {
				return -1;
			} else {
				if (this.stop > o.stop) {
					return 1;
				} else if (this.stop < o.stop) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
}
