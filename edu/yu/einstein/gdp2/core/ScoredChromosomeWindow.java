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
