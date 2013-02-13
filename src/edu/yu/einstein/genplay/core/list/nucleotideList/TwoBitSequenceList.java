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
package edu.yu.einstein.genplay.core.list.nucleotideList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.list.DisplayableListOfLists;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;



/**
 * Reads a 2Bit files and extracts the data from this kind of files.
 * 2bit files are used to store genome sequences in a random access file
 * @author Julien Lajugie
 * @version 0.1
 */
public class TwoBitSequenceList extends DisplayableListOfLists<Nucleotide, Nucleotide[]> implements Serializable, Stoppable {

	private static final long serialVersionUID = -2253030492143151302L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private final static String 		TWOBIT_SIGNATURE = "1A412743";	// signature of a 2bit file
	private ProjectChromosome 			projectChromosome;	 			// Instance of the Chromosome Manager
	private boolean 					reverseBytes = false;			// true if the bytes of a multi-byte entity need to be reversed when read
	private int 						version;						// version of the 2bit file
	private String						filePath;						// path of the 2bit file  (used for the serialization)
	private transient RandomAccessFile	twoBitFile;						// 2bit file
	private TwoBitSequence 				sequence = null;				// sequence being extracted
	private boolean						needToBeStopped = false;		// true if the execution need to be stopped
	protected String					genomeName = null;				// genome name for a multi genome project
	protected AlleleType 				alleleType = null;				// allele type for a multi genome project



	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(projectChromosome);
		out.writeBoolean(reverseBytes);
		out.writeInt(version);
		out.writeObject(filePath);
		out.writeObject(sequence);
		out.writeObject(genomeName);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		projectChromosome = (ProjectChromosome) in.readObject();
		reverseBytes = in.readBoolean();
		version = in.readInt();
		filePath = (String) in.readObject();
		sequence = (TwoBitSequence) in.readObject();
		needToBeStopped = false;
		genomeName = (String) in.readObject();
	}


	/**
	 * Creates an instance of {@link TwoBitSequenceList}
	 * @param genomeName name of the genome the {@link TwoBitSequenceList} represents
	 * @param alleleType 	allele type for a multi genome project
	 */
	public TwoBitSequenceList(String genomeName, AlleleType alleleType) {
		super();
		this.genomeName = genomeName;
		this.alleleType = alleleType;
		projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		// initializes the lists
		for (int i = 0; i < projectChromosome.size(); i++) {
			add(null);
		}
	}


	/**
	 * Extracts the sequence list from a 2bit file
	 * @param file 2Bit file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidFileTypeException
	 * @throws InterruptedException
	 */
	public void extract(File file) throws FileNotFoundException, IOException, InvalidFileTypeException, InterruptedException  {
		filePath = file.getAbsolutePath();
		twoBitFile = new RandomAccessFile(file, "r");
		twoBitFile.seek(0);
		int signature = twoBitFile.readInt();
		// if the signature is not equal to the signature defined in the 2bit files
		// it might means that the byte order need to be reversed
		if (!Integer.toHexString(signature).equalsIgnoreCase(TWOBIT_SIGNATURE)) {
			signature = Integer.reverseBytes(signature);
			// check if it matches with the bytes reversed
			if (Integer.toHexString(signature).equalsIgnoreCase(TWOBIT_SIGNATURE)) {
				// if it matches, turns the reverse mode on
				reverseBytes = true;
			} else {
				// if it doesn't the file is not correct
				throw new InvalidFileTypeException();
			}
		}
		if (reverseBytes) {
			version = Integer.reverseBytes(twoBitFile.readInt());
		} else {
			version = twoBitFile.readInt();
		}
		int sequenceCount = 0;
		if (reverseBytes) {
			sequenceCount = Integer.reverseBytes(twoBitFile.readInt());
		} else {
			sequenceCount = twoBitFile.readInt();
		}
		// skip 4 reserved bytes
		twoBitFile.skipBytes(4);
		String[] sequenceNames = new String[sequenceCount];
		int[] offsets = new int[sequenceCount];
		for (int i = 0; i < sequenceCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			byte sequenceNameSize = twoBitFile.readByte();
			byte[] sequenceNameBytes = new byte[sequenceNameSize];
			twoBitFile.read(sequenceNameBytes);
			sequenceNames[i] = new String(sequenceNameBytes);
			if (reverseBytes) {
				offsets[i] = Integer.reverseBytes(twoBitFile.readInt());
			} else {
				offsets[i] = twoBitFile.readInt();
			}
		}
		// we add the sequence to the list if the chromosome is specified in the ChromosomeManager
		for (int i = 0; i < sequenceCount; i++) {
			short k = 0;
			boolean found = false;
			while ((k < projectChromosome.size()) && (!found)) {
				if (projectChromosome.get(k).getName().equalsIgnoreCase(sequenceNames[i])) {
					// if the execution need to be stopped we generate an InterruptedException
					if (needToBeStopped) {
						throw new InterruptedException();
					}
					long currentPosition = twoBitFile.getFilePointer();
					sequence = new TwoBitSequence(genomeName, projectChromosome.get(k), alleleType);
					sequence.extract(filePath, twoBitFile, offsets[i], sequenceNames[i], reverseBytes);
					set(k, sequence);
					twoBitFile.seek(currentPosition);
					found = true;
				}
				k++;
			}
		}
	}


	/**
	 * @return the version
	 */
	public final int getVersion() {
		return version;
	}


	/**
	 * @return the twoBitFile
	 */
	public final RandomAccessFile getTwoBitFile() {
		return twoBitFile;
	}


	/**
	 * Does nothing
	 */
	@Override
	protected void fitToScreen() {}


	@Override
	protected Nucleotide[] getFittedData(int start, int stop) {
		Nucleotide[] result = new Nucleotide[(stop - start) + 1];
		List<Nucleotide> currentList;
		try {
			currentList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			fittedDataList = null;
			return null;
		}

		int j = 0;
		for (int i = start; i <= stop; i++) {
			result[j] = currentList.get(i);
			j++;
		}

		return result;
	}


	/**
	 * Re-initializes the connection to the random access file containing the sequences
	 * @throws FileNotFoundException
	 */
	public void reinitDataFile() throws FileNotFoundException {
		twoBitFile = new RandomAccessFile(new File(filePath), "r");
		for (List<Nucleotide> currentSequence: this) {
			((TwoBitSequence) currentSequence).reinitDataFile();
		}
		sequence.reinitDataFile();
	}


	/**
	 * @return the path to the random access file containing the sequences
	 */
	public String getDataFilePath() {
		return filePath;
	}


	/**
	 * Sets the file path to the random access file containing the sequences
	 * @param filePath path to the
	 * @throws FileNotFoundException
	 */
	public void setSequenceFilePath(String filePath) throws FileNotFoundException {
		for (List<Nucleotide> currentSequence: this) {
			((TwoBitSequence) currentSequence).setSequenceFilePath(filePath);
		}
		sequence.setSequenceFilePath(filePath);
		this.filePath = filePath;
		reinitDataFile();
	}


	/**
	 * Stops the extraction of the 2bit file
	 */
	@Override
	public void stop() {
		if (sequence != null) {
			sequence.stop();
		}
		needToBeStopped = true;
	}
}
