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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.nucleotideListView.TwoBitListView.TwoBitListView;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;

public class TwoBitSequenceListFactory {
	/** True if the bytes of multi-byte entities need to be reversed when read */
	private final boolean reverseBytes;

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
					sequence = new TwoBitListView(genomeName, projectChromosome.get(k), alleleType);
					sequence.extract(filePath, twoBitFile, offsets[i], sequenceNames[i], reverseBytes);
					set(k, sequence);
					twoBitFile.seek(currentPosition);
					found = true;
				}
				k++;
			}
		}
	}
}
