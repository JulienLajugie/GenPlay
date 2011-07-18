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
package edu.yu.einstein.genplay.core.list.nucleotideList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.list.DisplayableListOfLists;
import edu.yu.einstein.genplay.core.manager.ChromosomeManager;
import edu.yu.einstein.genplay.exception.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;



/**
 * Reads a 2Bit files and extracts the data from this kind of files.
 * 2bit files are used to store genome sequences in a random access file
 * @author Julien Lajugie
 * @version 0.1
 */
public class TwoBitSequenceList extends DisplayableListOfLists<Nucleotide, Nucleotide[]> implements Serializable, Stoppable {

	private static final long serialVersionUID = -2253030492143151302L;	// generated ID
	private final static String 		TWOBIT_SIGNATURE = "1A412743";	// signature of a 2bit file
	private boolean 					reverseBytes = false;			// true if the bytes of a multi-byte entity need to be reversed when read
	private  int 						version;						// version of the 2bit file
	private  String						filePath;						// path of the 2bit file  (used for the serialization)
	private transient RandomAccessFile	twoBitFile;						// 2bit file
	private TwoBitSequence 				sequence = null;				// sequence being extracted
	private boolean						needToBeStopped = false;		// true if the execution need to be stopped
	protected String					genomeName = null;				// genome name for a multi genome project
	
	/**
	 * Creates an instance of {@link TwoBitSequenceList}
	 */
	public TwoBitSequenceList(String genomeName) {
		super();
		this.genomeName = genomeName;
		ChromosomeManager chromosomeManager = ChromosomeManager.getInstance();
		// initializes the lists
		for (int i = 0; i < chromosomeManager.size(); i++) {
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
			ChromosomeManager chromosomeManager = ChromosomeManager.getInstance();
			while ((k < chromosomeManager.size()) && (!found)) {
				if (chromosomeManager.get(k).getName().equalsIgnoreCase(sequenceNames[i])) {
					// if the execution need to be stopped we generate an InterruptedException
					if (needToBeStopped) {
						throw new InterruptedException();
					}
					long currentPosition = twoBitFile.getFilePointer();
					sequence = new TwoBitSequence(genomeName, chromosomeManager.get(k));
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
		Nucleotide[] result = new Nucleotide[stop - start + 1];
		List<Nucleotide> currentList;
		try {
			currentList = get(fittedChromosome);
		} catch (InvalidChromosomeException e) {
			e.printStackTrace();
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
	 * Methods used for the unserialization of the object.
	 * Since the random access file can't be serialized we try to recreate it if the file path is still the same
	 * See javadocs for more information
	 * @return the unserialized object
	 * @throws ObjectStreamException
	 */
	private Object readResolve() throws ObjectStreamException {
		try {
			twoBitFile = new RandomAccessFile(new File(filePath), "r");
			return this;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
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
