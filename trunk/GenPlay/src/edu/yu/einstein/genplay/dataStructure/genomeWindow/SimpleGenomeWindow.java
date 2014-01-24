/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.dataStructure.genomeWindow;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ChromosomeWindowException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Simple implementation of the {@link GenomeWindow} interface.
 * {@link SimpleChromosomeWindow} objects are immutable.
 * @author Julien Lajugie
 */
public final class SimpleGenomeWindow extends AbstractGenomeWindow implements GenomeWindow {

	/** Generated serial ID */
	private static final long serialVersionUID = 8873056842762282328L;

	/**  Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Chromosome of the window */
	private final Chromosome chromosome;

	/**  Start and stop positions of the window on the chromosome */
	private final ChromosomeWindow chromosomeWindow;


	/**
	 * Creates an instance of {@link GenomeWindow}.
	 * @param chromosome a chromosome
	 * @param start a window start
	 * @param stop a window stop
	 */
	public SimpleGenomeWindow(Chromosome chromosome, int start, int stop) {
		this.chromosome = chromosome;
		chromosomeWindow = new SimpleChromosomeWindow(start, stop);
	}


	/**
	 * Creates an instance of {@link GenomeWindow} from a String.
	 * @param genomeWindowStr String following the format "chr:start-stop" (ex: "chr1:100-120")
	 * @throws ChromosomeWindowException
	 * @throws InvalidChromosomeException
	 */
	public SimpleGenomeWindow(String genomeWindowStr) throws ChromosomeWindowException, InvalidChromosomeException {
		chromosome = ProjectManager.getInstance().getProjectChromosomes().get(Utils.split(genomeWindowStr, ':')[0].trim());
		chromosomeWindow = new SimpleChromosomeWindow(Utils.split(genomeWindowStr, ':')[1].trim());
	}


	/**
	 * Creates an instance of {@link GenomeWindow} from 3 strings corresponding to the chromosome, start and stop of the genome window
	 * @param chromosome string representing the chromosome
	 * @param start string representing the start position
	 * @param stop string representing the stop position
	 * @throws ChromosomeWindowException
	 * @throws InvalidChromosomeException
	 */
	public SimpleGenomeWindow(String chromosome, String start, String stop) throws ChromosomeWindowException {
		this.chromosome = ProjectManager.getInstance().getProjectChromosomes().get(chromosome);
		chromosomeWindow = new SimpleChromosomeWindow(start, stop);
	}


	/**
	 * @return the chromosome
	 */
	@Override
	public final Chromosome getChromosome() {
		return chromosome;
	}


	@Override
	public int getStart() {
		return chromosomeWindow.getStart();
	}


	@Override
	public int getStop() {
		return chromosomeWindow.getStop();
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the class version number
		in.readInt();
		// read the final fields
		in.defaultReadObject();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		// write the class version number
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
	}
}
