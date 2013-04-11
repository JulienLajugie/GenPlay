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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * Reads a 2Bit files and extracts the data from this kind of files.
 * 2bit files are used to store genome sequences in a random access file
 * @author Julien Lajugie
 */
public class TwoBitSequenceList implements Serializable, NucleotideList, Iterator<ListView<Nucleotide>> {

	/** Generated serial ID */
	private static final long serialVersionUID = -2253030492143151302L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Signature of a 2bit file */
	public final static String TWOBIT_SIGNATURE = "1A412743";

	/** Path of the 2bit file  (used for the serialization) */
	private final String filePath;

	/** Genome name for a multi genome project */
	private final String genomeName;

	/** Allele type for a multi genome project */
	private final AlleleType alleleType;

	/** Each element of this list read a chromosome in the file */
	private final List<ListView<Nucleotide>> data;

	/** Current index of the iterator */
	private transient int iteratorIndex = 0;


	/**
	 * Creates an instance of {@link TwoBitSequenceList}
	 * @param filePath path to the 2bit file
	 * @param reverseBytes true if the bytes of multi-byte entities need to be reversed when read
	 * @param genomeName name of the genome the {@link TwoBitSequenceList} represents
	 * @param alleleType 	allele type for a multi genome project
	 * @throws IOException
	 */
	protected TwoBitSequenceList(String filePath, boolean reverseBytes, String genomeName, AlleleType alleleType, List<ListView<Nucleotide>> data) throws IOException {
		super();
		this.filePath = filePath;
		this.genomeName = genomeName;
		this.alleleType = alleleType;
		this.data = data;
	}


	@Override
	public ListView<Nucleotide> get(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex);
	}


	@Override
	public Nucleotide get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex, index);
	}


	@Override
	public ListView<Nucleotide> get(int chromosomeIndex) {
		return data.get(chromosomeIndex);
	}


	@Override
	public Nucleotide get(int chromosomeIndex, int elementIndex) {
		return data.get(chromosomeIndex).get(elementIndex);
	}


	/**
	 * @return the allele type for a multi genome project
	 */
	public AlleleType getAlleleType() {
		return alleleType;
	}


	/**
	 * @return the path to the random access file containing the sequences
	 */
	public String getDataFilePath() {
		return filePath;
	}


	/**
	 * @return The genome name for a multi genome project
	 */
	public String getGenomeName() {
		return genomeName;
	}


	@Override
	public String getSequenceFile() {
		return filePath;
	}


	@Override
	public boolean hasNext() {
		return iteratorIndex < size();
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


	@Override
	public Iterator<ListView<Nucleotide>> iterator() {
		return this;
	}


	@Override
	public ListView<Nucleotide> next() {
		int currentIndex = iteratorIndex;
		iteratorIndex++;
		return get(currentIndex);
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


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}


	@Override
	public int size() {
		return data.size();
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return size(chromosomeIndex);
	}


	@Override
	public int size(int chromosomeIndex) {
		return data.get(chromosomeIndex).size();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the class version number
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
	}
}
