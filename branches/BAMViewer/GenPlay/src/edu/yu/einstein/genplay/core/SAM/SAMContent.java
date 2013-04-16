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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.comparator.SAMReadComparator;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SAMContent implements Iterable<SAMRead>{

	private final SAMFile 				file;			// The SAM/BAM file.
	private SAMReadArray 				readArray1;		// The first array of reads.
	private SAMReadArray 				readArray2;		// The second array of reads (for paired reads).
	private SAMRecordIterator 			iterator;

	private List<SAMProcessor> processors;

	private boolean						loading;
	private boolean						reading;
	private int 						index;			// Index for iteration.


	/**
	 * Constructor of {@link SAMContent}
	 * @param file a {@link SAMFile}
	 */
	public SAMContent (SAMFile file) {
		this.file = file;
		loading = false;
		reading = false;
		initialize();
	}


	/**
	 * Initialize the {@link SAMContent}
	 */
	private void initialize () {
		iterator = null;
		readArray1 = new SAMReadArray();
		readArray2 = new SAMReadArray();
		processors = new ArrayList<SAMProcessor>();
	}


	/**
	 * Load SAM content for the defined region
	 * @param chromosome	a {@link Chromosome}
	 * @param start	a start position
	 * @param stop a stop position
	 */
	public void load (Chromosome chromosome, int start, int stop) {
		/*if (loading) {													// The loading can be asked when it is already loading, the newer request has higher priority.
			loading = false;
			load(chromosome, start, stop);
		} else {*/
		if (!loading) {
			loading = true;
			SAMReadArray readArray1 = new SAMReadArray();
			SAMReadArray readArray2 = new SAMReadArray();
			setIterator(chromosome, start, stop);
			index = 0;

			while(iterator.hasNext()){
				add(iterator.next(), readArray1, readArray2);
				index++;
			}

			readArray1.resize(index);
			readArray2.resize(index);

			waitForReading();

			this.readArray1.removeAllData();
			this.readArray1 = readArray1;
			this.readArray2.removeAllData();
			this.readArray2 = readArray2;

			loading = false;
			Utils.garbageCollect();
		} else {
			System.out.println("SAMContent.load() already loading");
		}
	}


	/**
	 * @return true if the {@link SAMContent} contains data
	 */
	public boolean hasData () {
		return (readArray1.getSize() > 0) && (readArray1.getStart(0) > 0);
	}


	private void waitForReading () {
		while (reading) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * @param chromosome a chromosome
	 * @param start a start position
	 * @param stop a stop position
	 * @return the {@link SAMRecord} at the given positions, null otherwise
	 */
	public SAMRecord getRecord (Chromosome chromosome, int start, int stop) {
		setIterator(chromosome, start, stop);
		SAMRecord record = null;
		if (iterator.hasNext()) {
			record = iterator.next();
		}
		return record;
	}


	/**
	 * Add a record into the list
	 * @param record the {@link SAMRecord} to add
	 */
	private void add (SAMRecord record, SAMReadArray readArray1, SAMReadArray readArray2) {
		int currentIndex = index;
		SAMReadArray currentAlignement = readArray1;

		boolean isValid = false;
		boolean isMapped = !record.getReadUnmappedFlag();
		if (isMapped) {
			boolean isPaired = record.getReadPairedFlag();
			if (isPaired) {
				boolean isPairMapped = !record.getMateUnmappedFlag();
				if (isPairMapped) {
					int pairedReferenceIndex = record.getMateReferenceIndex();
					int referenceIndex = record.getReferenceIndex();
					if (referenceIndex == pairedReferenceIndex) {
						isValid = true;
					}
				}
			} else {
				isValid = true;
			}
		}
		if (isValid) {
			currentAlignement.add(currentIndex, record);
		}
	}


	private void setIterator (Chromosome chromosome, int start, int stop) {
		if (iterator != null) {
			iterator.close();
		}
		try {
			iterator = file.getReader().query(chromosome.getName(), start, stop, true);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("SAMContent.setIterator() " + chromosome + " " + start + " " + stop);
		}
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
			reading = true;
			SAMRead pair = null;
			int start02 = readArray2.getStart(index);
			if (start02 >= 0) {
				pair = new SAMRead(start02, readArray2.getStop(index));
			}
			read = new SAMRead(readArray1.getStart(index), readArray1.getStop(index), pair);
			reading = false;
		}
		return read;
	}


	/**
	 * Retrieve all the reads previously loaded
	 */
	public void retrieveAllReads () {
		reading = true;
		List<SAMRead> currentReads = new ArrayList<SAMRead>();
		if (hasData()) {
			int stopIndex = readArray1.getSize();
			for (int i = 0; i <= stopIndex; i++) {
				SAMRead read = getRead(i);
				if ((read != null)) {
					currentReads.add(read);
				}
			}
			Collections.sort(currentReads, new SAMReadComparator());
			alertProcessors(currentReads);
		}

		reading = false;
	}


	/**
	 * Get the reads contained in between the given start and stop
	 * @param start a start position
	 * @param stop a stop position
	 */
	public void retrieveReads (int start, int stop) {
		reading = true;
		List<SAMRead> currentReads = new ArrayList<SAMRead>();
		if (hasData()) {
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
				if ((read != null) && read.isContained(start, stop)) {
					currentReads.add(read);
				}
			}
			Collections.sort(currentReads, new SAMReadComparator());
			alertProcessors(currentReads);
		}
		reading = false;
	}


	/**
	 * @return the readArray1
	 */
	public SAMReadArray getReadArray1() {
		return readArray1;
	}


	/**
	 * @return the readArray2
	 */
	public SAMReadArray getReadArray2() {
		return readArray2;
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


	/**
	 * Add new data after existing data
	 * @param tmpContent the {@link SAMContent} data to fetch
	 */
	public void fetchData (SAMContent tmpContent) {
		readArray1.getStart().fetchArray(tmpContent.getReadArray1().getStart());
		readArray1.getStop().fetchArray(tmpContent.getReadArray1().getStop());
		readArray2.getStart().fetchArray(tmpContent.getReadArray1().getStart());
		readArray2.getStop().fetchArray(tmpContent.getReadArray1().getStop());
	}


	/**
	 * Add new data before existing data
	 * @param tmpContent the {@link SAMContent} data to push
	 */
	public void pushData (SAMContent tmpContent) {
		readArray1.getStart().pushArray(tmpContent.getReadArray1().getStart());
		readArray1.getStop().pushArray(tmpContent.getReadArray1().getStop());
		readArray2.getStart().pushArray(tmpContent.getReadArray1().getStart());
		readArray2.getStop().pushArray(tmpContent.getReadArray1().getStop());
	}


	/**
	 * @return the loading
	 */
	public boolean isLoading() {
		return loading;
	}


	/**
	 * Adds a {@link SAMProcessor} the list of {@link SAMProcessor} this {@link SAMContent} alerts.
	 * @param processor a {@link SAMProcessor}
	 */
	public void addProcessor (SAMProcessor processor) {
		processors.add(processor);
	}


	private void alertProcessors (List<SAMRead> currentReads) {
		if ((currentReads != null) && (currentReads.size() > 0)) {
			for (SAMProcessor processor: processors) {
				processor.handleReads(currentReads);
			}
		}
	}

}
