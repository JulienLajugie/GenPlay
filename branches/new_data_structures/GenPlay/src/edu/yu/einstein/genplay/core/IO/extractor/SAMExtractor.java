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
package edu.yu.einstein.genplay.core.IO.extractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFormatException;
import net.sf.samtools.SAMProgramRecord;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import edu.yu.einstein.genplay.core.IO.dataReader.ChromosomeWindowReader;
import edu.yu.einstein.genplay.core.IO.dataReader.DataReader;
import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.dataReader.StrandReader;
import edu.yu.einstein.genplay.core.IO.utils.StrandedExtractorOptions;
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.SAMRecordFilter;
import edu.yu.einstein.genplay.core.comparator.ChromosomeWindowStartComparator;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.SimpleChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;
import edu.yu.einstein.genplay.exception.exceptions.DataLineException;


/**
 * Extractor that extract data from a SAM / BAM file
 * @author Julien Lajugie
 */
public class SAMExtractor extends Extractor implements DataReader, ChromosomeWindowReader, SCWReader, StrandReader, StrandedExtractor {

	/**
	 * Default first base position of bed files. SAM files are 1-based
	 * Even though BAM files are 0-based, {@link SAMFileReader} objects
	 * returns 1 base coordinates.
	 * */
	public static final int DEFAULT_FIRST_BASE_POSITION = 1;

	private int	firstBasePosition = DEFAULT_FIRST_BASE_POSITION; 			// position of the first base
	private final SAMReadGroupRecord[]		readGroups;						// read groups of the SAM file
	private final String[]					programNames;					// programs used to generate and process the SAM file
	private final SAMFileReader 			samReader;						// reader that read sam / bam files (from sam.jar)
	private final SAMRecordIterator 		iterator;						// iterator in the file
	private StrandedExtractorOptions		strandOptions;					// options on the strand and read length / shift
	private final List<SAMRecordFilter>		recordFilters;					// SAM record filters (we don't consider records that don't pass these filters)
	private boolean							isPairedMode;					// true if the extractor is in pair end mode
	private Chromosome 						chromosome;						// chromosome of the last record read (a record has exactly one chromosome)
	private final Queue<Integer>			startQueue;						// queue containing the start positions of the last record read (a record can have more than one start if split)
	private final Queue<Integer>			stopQueue;						// queue containing the stop position of the last record read (a record can have more than one stop if split)
	private Strand 							strand;							// strand of the last record read (a record has exactly one strand)
	private final NavigableSet<ChromosomeWindow>	waitingAlignmentBlocks;			// set containing the alignment blocks sorted by start position

	/**
	 * Creates an instance of {@link SAMExtractor}
	 * @param dataFile SAM / BAM file to extract
	 */
	public SAMExtractor(File dataFile) {
		super(dataFile);
		samReader = new SAMFileReader(dataFile);
		readGroups = retrieveReadGroups();
		programNames = retrieveProgramNames();
		iterator = samReader.iterator();
		startQueue = new ConcurrentLinkedQueue<Integer>();
		stopQueue = new ConcurrentLinkedQueue<Integer>();
		recordFilters = new ArrayList<SAMRecordFilter>();
		waitingAlignmentBlocks = new TreeSet<ChromosomeWindow>(new ChromosomeWindowStartComparator());
	}


	/**
	 * Add a filter to apply on the reads.  Only the reads that pass all the filters will be extracted.
	 * @param recordFilter
	 */
	public void addFilter(SAMRecordFilter recordFilter) {
		recordFilters.add(recordFilter);
	}


	/**
	 * Applies the SAM record filters to a specified samRecord
	 * @param samRecord a {@link SAMRecord}
	 * @return the record itself if it passes the filter tests, null otherwise.
	 */
	private SAMRecord applyFilters(SAMRecord samRecord) {
		for (SAMRecordFilter currentFilter: recordFilters) {
			samRecord = currentFilter.applyFilter(samRecord);
			if (samRecord == null) {
				return null;
			}
		}
		return samRecord;
	}


	@Override
	public Chromosome getChromosome() {
		return chromosome;
	}


	@Override
	public int getFirstBasePosition() {
		return firstBasePosition;
	}


	/**
	 * @return the header of the BAM file
	 */
	public String getHeaderString() {
		return samReader.getFileHeader().getTextHeader();
	}


	/**
	 * @return the list of the programs that were used to generate the SAM / BAM file
	 */
	public String[] getProgramNames() {
		return programNames;
	}


	/**
	 * @return the read groups of the SAM / BAM file
	 */
	public SAMReadGroupRecord[] getReadGroups() {
		return readGroups;
	}


	@Override
	public Float getScore() {
		return 1f;
	}


	@Override
	public Integer getStart() {
		if (startQueue.isEmpty()) {
			return null;
		} else {
			return startQueue.peek();
		}
	}


	@Override
	public Integer getStop() {
		if (stopQueue.isEmpty()) {
			return null;
		} else {
			return stopQueue.peek();
		}
	}


	@Override
	public Strand getStrand() {
		return strand;
	}


	@Override
	public StrandedExtractorOptions getStrandedExtractorOptions() {
		return strandOptions;
	}


	/**
	 * @param samRecord
	 * @return true if the read is the leftmost one of a pair
	 */
	private boolean isLeftMostOfPair(SAMRecord samRecord) {
		return !samRecord.getReadNegativeStrandFlag();
	}


	/**
	 * @return true if the extractor is in pair end mode
	 */
	public boolean isPairedMode() {
		return isPairedMode;
	}


	/**
	 * Process a paired SAM record and extract its starts and stops
	 * @param samRecord a {@link SAMRecord}
	 * @return true if a window was extracted from the record, false otherwise
	 */
	private boolean processPairedSamRecord(SAMRecord samRecord) {
		// check if the read is the leftmost of the pair
		if (isLeftMostOfPair(samRecord)) {
			int start = samRecord.getAlignmentStart();
			int stop = samRecord.getMateAlignmentStart() + 1;
			// compute the read position with specified strand shift and read length
			if (strandOptions != null) {
				SimpleChromosomeWindow resultStartStop = strandOptions.computeStartStop(chromosome, start, stop, strand);
				start = resultStartStop.getStart();
				stop = resultStartStop.getStop();
			}
			// if we are in a multi-genome project, we compute the position on the meta genome
			start = getRealGenomePosition(chromosome, start);
			stop = getRealGenomePosition(chromosome, stop);
			if ((stop - start) > 0) {
				strand = samRecord.getFirstOfPairFlag() ? Strand.FIVE : Strand.THREE;
				startQueue.add(start);
				stopQueue.add(stop);
				return true;
			}
		}
		return false;
	}


	/**
	 * Process the specified {@link SAMRecord} and extract its chromosome, starts, stops and strand values
	 * @param samRecord a {@link SAMRecord}
	 */
	private boolean processSamRecord(SAMRecord samRecord) {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		chromosome = projectChromosomes.get(samRecord.getReferenceName());
		if (isPairedMode) {
			return processPairedSamRecord(samRecord);
		}
		strand = samRecord.getReadNegativeStrandFlag() ? Strand.THREE : Strand.FIVE;
		List<AlignmentBlock> alignmentBlocks = samRecord.getAlignmentBlocks();
		if ((alignmentBlocks != null) && (!alignmentBlocks.isEmpty())) {
			AlignmentBlock firstBlock = alignmentBlocks.get(0);
			int start = firstBlock.getReferenceStart();
			int stop = start + firstBlock.getLength();
			// compute the read position with specified strand shift and read length
			if (strandOptions != null) {
				SimpleChromosomeWindow resultStartStop = strandOptions.computeStartStop(chromosome, start, stop, strand);
				start = resultStartStop.getStart();
				stop = resultStartStop.getStop();
			}
			// if we are in a multi-genome project, we compute the position on the meta genome
			start = getRealGenomePosition(chromosome, start);
			stop = getRealGenomePosition(chromosome, stop);
			ChromosomeWindow chromosomeWindow = new SimpleChromosomeWindow(start, stop);
			while (!waitingAlignmentBlocks.isEmpty() && (waitingAlignmentBlocks.first().compareTo(chromosomeWindow) <= 0)) {
				ChromosomeWindow removedWaitingBlock = waitingAlignmentBlocks.pollFirst();
				startQueue.add(removedWaitingBlock.getStart());
				stopQueue.add(removedWaitingBlock.getStop());
			}
			// add the first alignment block to the start and stop list ready to be retrieved
			startQueue.add(start);
			stopQueue.add(stop);

			// add the other blocks to the waiting list to make sure that they will be retrieved in sorted order
			for (int i = 1; i < alignmentBlocks.size(); i++) {
				AlignmentBlock currentBlock = alignmentBlocks.get(i);
				start = currentBlock.getReferenceStart();
				stop = start + currentBlock.getLength();
				// compute the read position with specified strand shift and read length
				if (strandOptions != null) {
					SimpleChromosomeWindow resultStartStop = strandOptions.computeStartStop(chromosome, start, stop, strand);
					start = resultStartStop.getStart();
					stop = resultStartStop.getStop();
				}
				// if we are in a multi-genome project, we compute the position on the meta genome
				start = getRealGenomePosition(chromosome, start);
				stop = getRealGenomePosition(chromosome, stop);
				waitingAlignmentBlocks.add(new SimpleChromosomeWindow(start, stop));
			}
			return true;
		}
		return false;
	}


	@Override
	public boolean readItem() throws IOException {
		if (isStopped()) {
			return false;
		}
		// remove the last item from the queues
		if (!startQueue.isEmpty()) {
			startQueue.poll();
			stopQueue.poll();
			// nothing to do if there still some items in the queues
			if (!startQueue.isEmpty()) {
				return true;
			}
		}
		SAMRecord samRecord = null;
		while (iterator.hasNext()) {
			try {
				samRecord = iterator.next();
				String chromosomeName = samRecord.getReferenceName();
				// case where last chromosome already extracted, no more data to extract
				if ((getChromosomeSelector() != null) && (getChromosomeSelector().isExtractionDone(chromosomeName))) {
					return false;
				}
				// chromosome was selected for extraction
				if ((getChromosomeSelector() == null) || getChromosomeSelector().isSelected(chromosomeName)) {
					samRecord = applyFilters(samRecord);
					if ((samRecord != null) && processSamRecord(samRecord)) {
						return true;
					}
				}
			} catch (SAMFormatException e) {
				DataLineException dataLineException = new DataLineException(e.getMessage(), DataLineException.SKIP_PROCESS);
				dataLineException.setFile(getDataFile());
				if ((samRecord != null) && (samRecord.getSAMString() != null)) {
					dataLineException.setLine(samRecord.getSAMString());
				}
				notifyDataEventListeners(dataLineException);

			}
		}
		return false;
	}


	/**
	 * Add a filter to apply on the reads.  Only the reads that pass all the filters will be extracted.
	 * @param recordFilter
	 */
	public void removeFilter(SAMRecordFilter recordFilter) {
		recordFilters.remove(recordFilter);
	}


	@Override
	protected String retrieveDataName(File dataFile) {
		return dataFile.getName();
	}


	/**
	 * @return the name of the programs that processed the SAM file
	 */
	private String[] retrieveProgramNames() {
		SAMFileHeader header = samReader.getFileHeader();
		List<SAMProgramRecord> programs = header.getProgramRecords();
		if (programs == null) {
			return null;
		}
		String[] programNames = new String[programs.size()];
		for (int i = 0; i < programs.size(); i++) {
			programNames[i] = programs.get(i).getProgramName();
		}
		return programNames;
	}


	/**
	 * @return the read groups of the SAM file extracted from the header
	 */
	private SAMReadGroupRecord[] retrieveReadGroups() {
		SAMFileHeader header = samReader.getFileHeader();
		List<SAMReadGroupRecord> readGroups = header.getReadGroups();
		if (readGroups == null) {
			return null;
		}
		SAMReadGroupRecord[] SAMReadGroupRecords = new SAMReadGroupRecord[readGroups.size()];
		for (int i = 0; i < readGroups.size(); i++) {
			SAMReadGroupRecords[i] = readGroups.get(i);
		}
		return SAMReadGroupRecords;
	}


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;

	}


	/**
	 * @param isPairedMode set to true to extract to set the extractor in pair end mode.
	 * In pair end mode the extractor returns windows having the size of the fragments delimited by the pair ends.
	 */
	public void setPairedMode(boolean isPairedMode) {
		this.isPairedMode = isPairedMode;
	}


	@Override
	public void setStrandedExtractorOptions(StrandedExtractorOptions options) {
		strandOptions = options;
	}
}
