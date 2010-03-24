/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core;

import java.io.Serializable;


/**
 * The ScoredChromosomeWindow class represents a window on a chromosome with a score. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ScoredChromosomeWindow extends ChromosomeWindow implements Serializable, Cloneable, Comparable<ChromosomeWindow> {
	
	private static final long serialVersionUID = 8073707507054963197L; // generated ID
	private double score;	// score of the window
	
	
	/**
	 * Default constructor. Creates an instance of {@link ScoredChromosomeWindow} 
	 */
	public ScoredChromosomeWindow() {
		super();
	}
	
	
	/**
	 * Creates an instance of a {@link ScoredChromosomeWindow}
	 * @param start start position
	 * @param stop stop position
	 * @param score score of the window
	 */
	public ScoredChromosomeWindow(int start, int stop, double score) {
		super(start, stop);
		this.setScore(score);
	}
	

	/**
	 * Creates an instance of a {@link ScoredChromosomeWindow}
	 * @param scw a {@link ScoredChromosomeWindow}
	 */
	public ScoredChromosomeWindow(ScoredChromosomeWindow scw) {
		super(scw);
		this.setScore(scw.getScore());
	}	
	

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}
	
	
}
