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
package edu.yu.einstein.genplay.core.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;



/**
 * A list of {@link Chromosome} with a list of data for each {@link Chromosome}.
 * This abstract class is the template for the different kind of data that extends this class.
 * The methods to adapt the data to the screen resolution must be defined in the subclasses
 * @author Julien Lajugie
 * @version 0.1
 * @param <T> type of the data
 * @param <U> type returned by the method that fits the data for the screen resolution
 */
public abstract class DisplayableListOfLists<T, U> extends ChromosomeArrayListOfLists<T> implements Serializable, Cloneable, ChromosomeListOfLists<T>, DisplayableDataList<U> {

	private static final long serialVersionUID = -2238871286451859789L;	// generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	protected U		 					fittedDataList;					// List of data of the current chromosome adapted to the screen resolution
	protected Chromosome				fittedChromosome = null;		// Chromosome with the adapted data
	protected Double					fittedXRatio = null;			// xRatio of the adapted data (ie ratio between the number of pixel and the number of base to display )


	/**
	 * Saves the format version number during serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}


	/**
	 * Unserializes the save format version number
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		fittedChromosome = null;
		fittedXRatio = null;
		fittedDataList = null;
	}


	/**
	 * Adapts the data to the screen resolution.
	 * This method is called when the adapted chromosome or xFactor change
	 */
	abstract protected void fitToScreen();


	/**
	 * @param start a start position
	 * @param stop a stop position
	 * @return the fitted data between position start and position stop
	 */
	abstract protected U getFittedData(int start, int stop);


	/**
	 * Constructor
	 */
	public DisplayableListOfLists() {
		super();
	}


	@Override
	public final U getFittedData(GenomeWindow window, double xRatio) {
		if ((fittedChromosome == null) || (!fittedChromosome.equals(window.getChromosome()))) {
			fittedChromosome = window.getChromosome();
			if ((fittedXRatio == null) || (fittedXRatio != xRatio)) {
				fittedXRatio = xRatio;
			}
			fitToScreen();
		} else if ((fittedXRatio == null) || (fittedXRatio != xRatio)) {
			fittedXRatio = xRatio;
			fitToScreen();
		}
		return getFittedData(window.getStart(), window.getStop());
	}
}
