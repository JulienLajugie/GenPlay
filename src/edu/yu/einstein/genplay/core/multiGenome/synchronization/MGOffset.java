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
package edu.yu.einstein.genplay.core.multiGenome.synchronization;


/**
 * The {@link MGOffset} class represents a variation in a genome.
 * It is composed of two ints:
 * - a position: that says at which position of the actual genome the value must be taken into account
 * - a value: that says how many base pairs must be added/removed to this position in order to have it in the meta genome coordinate system
 * 
 * Every deletion for the actual genome (and insertion from the other genomes) will involve a gap in its coordinate system.
 * eg: A deletion of 2 nucleotides at the position 10 of the actual genome means that every position above 10 (therefore from 11)
 * of its genome must be increased of 2 in order to know the related position on the meta genome.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGOffset {

	private int position;	// position where the offset must be applied
	private int value;		// value of the offset


	/**
	 * Constructor of {@link MGOffset}
	 */
	public MGOffset () {
		this.position = 0;
		this.value = 0;
	}

	
	/**
	 * Constructor of {@link MGOffset}
	 * @param position 	position
	 * @param value		value for this position 
	 */
	public MGOffset (int position, int value) {
		this.position = position;
		this.value = value;
	}


	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}


	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}


	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	
	/**
	 * Show the information of the {@link MGOffset}
	 */
	public void show () {
		System.out.println("position: " + position + "; value: " + value);
	}


}
