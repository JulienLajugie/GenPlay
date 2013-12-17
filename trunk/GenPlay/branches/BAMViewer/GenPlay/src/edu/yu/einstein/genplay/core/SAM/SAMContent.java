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
package edu.yu.einstein.genplay.core.SAM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SAMContent implements Iterable<SAMRead>{




	private final SAMFile file;
	private SAMReadArray readArray1;
	private SAMReadArray readArray2;

	private Map<Integer, Integer> pairs;
	private int index;

	/**
	 * Constructor of {@link SAMContent}
	 * @param file a {@link SAMFile}
	 */
	public SAMContent (SAMFile file) {
		this.file = file;
		initialize();
	}


	/**
	 * Initialize the {@link SAMContent}
	 */
	private void initialize () {
		readArray1 = new SAMReadArray();
		readArray2 = new SAMReadArray();
	}


	/**
	 * Load SAM content for the defined region
	 * @param chromosome	a {@link Chromosome}
	 * @param start	a start position
	 * @param stop a stop position
	 */
	public void load (Chromosome chromosome, int start, int stop) {
		SAMRecordIterator iterator = file.getReader().query(chromosome.getName(), start, stop, true);
		pairs = new HashMap<Integer, Integer>();
		index = 0;
		while(iterator.hasNext()){
			add(iterator.next());
			index++;
		}
		iterator.close();

		readArray1.resize(index);
		readArray2.resize(index);
	}


	/**
	 * @param chromosome a chromosome
	 * @param start a start position
	 * @param stop a stop position
	 * @return the {@link SAMRecord} at the given positions, null otherwise
	 */
	public SAMRecord getRecord (Chromosome chromosome, int start, int stop) {
		SAMRecordIterator iterator = file.getReader().query(chromosome.getName(), start, stop, true);
		SAMRecord record = null;
		if (iterator.hasNext()) {
			record = iterator.next();
		}
		iterator.close();
		return record;
	}


	/**
	 * Add a record into the list
	 * @param index the index where to add the {@link SAMRecord}
	 * @param record the {@link SAMRecord} to add
	 */
	private void add (SAMRecord record) {
		int currentIndex = index;
		SAMReadArray currentAlignement = readArray1;

		boolean isPaired = record.getReadPairedFlag();
		if (isPaired) {
			int pairedReferenceIndex = record.getMateReferenceIndex();
			if (pairs.get(pairedReferenceIndex) == null) {
				pairs.put(pairedReferenceIndex, index);
			} else {
				currentAlignement = readArray2;
				currentIndex = pairs.get(pairedReferenceIndex);
				pairs.remove(pairedReferenceIndex);
				index--;
			}
		}

		currentAlignement.add(currentIndex, record);
	}


	/**
	 * @return the number of elements
	 */
	public int getSize () {
		return readArray1.getSize();
	}


	/**
	 * @param index an index
	 * @return the read at the specified index, null otherwise
	 */
	public SAMRead getRead (int index) {
		SAMRead read = null;
		if (index < getSize()) {
			SAMRead pair = null;
			int start02 = readArray2.getStart(index);
			if (start02 >= 0) {
				pair = new SAMRead(start02, readArray2.getStop(index));
			}
			read = new SAMRead(readArray1.getStart(index), readArray1.getStop(index), pair);
		}
		return read;
	}


	/**
	 * Get the reads contained in between the given start and stop
	 * @param start a start position
	 * @param stop a stop position
	 * @return the reads contained in between the given start and stop
	 */
	public List<SAMRead> getReads (int start, int stop) {
		List<SAMRead> result = new ArrayList<SAMRead>();

		int stopIndex = readArray1.getStartIndex(stop);
		int startIndex = readArray1.getStartIndex(start);

		int index = startIndex - 1;
		boolean valid = true;
		while (valid) {
			if (index >= 0) {
				int currentStop = readArray1.getStop(index);
				int currentPairStop = readArray2.getStop(index);
				if (currentPairStop > currentStop) {
					currentStop = currentPairStop;
				}
				if (currentStop >= start) {
					index--;
				} else {
					valid = false;
				}
			} else {
				valid = false;
			}
		}

		if (index < 0) {
			index = 0;
		}
		startIndex = index;

		for (int i = startIndex; i <= stopIndex; i++) {
			SAMRead read = getRead(i);
			if (read.isContained(start, stop)) {
				result.add(read);
			}
		}

		return result;
	}


	/**
	 * Print the {@link SAMContent}
	 */
	public void print () {
		int count = 0;
		for (SAMRead read: this) {
			if (read.isPaired()) {
				count++;
				if (count > 20) {
					break;
				}
				System.out.println(read);
			}
		}
	}


	@Override
	public Iterator<SAMRead> iterator() {
		return new SAMIterator(this);
	}

}
