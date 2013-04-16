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
package edu.yu.einstein.genplay.gui.event.genomeWindowLoader;

import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenomeWindowLoaderSettings {

	/** Option to disable limits */
	public static final int NO_LIMIT = -1;

	private double leftMargin;			// Value to load on left side of the window (%).
	private double rightMargin;			// Value to load on right side of the window (%).
	private int leftMinimum;			// Minimum Value to load on left side of the window (bp).
	private int leftMaximum;			// Maximum Value to load on left side of the window (bp).
	private int rightMinimum;			// Minimum Value to load on right side of the window (bp).
	private int rightMaximum;			// Maximum Value to load on right side of the window (bp).


	/**
	 * Constructor of {@link GenomeWindowLoaderSettings}
	 */
	public GenomeWindowLoaderSettings () {
		leftMargin = 0.05;
		rightMargin = 0.05;
		leftMinimum = NO_LIMIT;
		leftMaximum = NO_LIMIT;
		rightMinimum = NO_LIMIT;
		rightMaximum = NO_LIMIT;
	}


	/**
	 * Set the margin both side of the {@link GenomeWindow}
	 * @param margin the percentage of the current {@link GenomeWindow} to load
	 */
	public void set (double margin) {
		set(margin, margin);
	}


	/**
	 * Set the margins on the right and left side of the {@link GenomeWindow}
	 * @param leftMargin the percentage on the left side of the current {@link GenomeWindow} to load
	 * @param rightMargin the percentage on the right side of the current {@link GenomeWindow} to load
	 */
	public void set (double leftMargin, double rightMargin) {
		set(leftMargin, rightMargin, NO_LIMIT, NO_LIMIT, NO_LIMIT, NO_LIMIT);
	}


	/**
	 * Set the margins on the right and left side of the {@link GenomeWindow} as well as the min & max
	 * @param leftMargin the percentage on the left side of the current {@link GenomeWindow} to load
	 * @param rightMargin the percentage on the right side of the current {@link GenomeWindow} to load
	 * @param leftMinimum minimum bp to load on the left side
	 * @param leftMaximum maximum bp to load on the left side
	 * @param rightMinimum minimum bp to load on the right side
	 * @param rightMaximum maximum bp to load on the right side
	 */
	public void set (double leftMargin, double rightMargin, int leftMinimum, int leftMaximum, int rightMinimum, int rightMaximum) {
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.leftMinimum = leftMinimum;
		this.leftMaximum = leftMaximum;
		this.rightMinimum = rightMinimum;
		this.rightMaximum = rightMaximum;
	}


	/**
	 * @return the leftMargin
	 */
	public double getLeftMargin() {
		return leftMargin;
	}


	/**
	 * @return the rightMargin
	 */
	public double getRightMargin() {
		return rightMargin;
	}


	/**
	 * @return the leftMinimum
	 */
	public int getLeftMinimum() {
		return leftMinimum;
	}


	/**
	 * @return the leftMaximum
	 */
	public int getLeftMaximum() {
		return leftMaximum;
	}


	/**
	 * @return the rightMinimum
	 */
	public int getRightMinimum() {
		return rightMinimum;
	}


	/**
	 * @return the rightMaximum
	 */
	public int getRightMaximum() {
		return rightMaximum;
	}

}
