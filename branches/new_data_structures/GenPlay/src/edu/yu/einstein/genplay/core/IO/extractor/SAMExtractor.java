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
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.sf.samtools.AlignmentBlock;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
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
import edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter.UnpairedSAMRecordFilter;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.Strand;


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
	private SAMRecordFilter[]				recordFilters;					// SAM record filters (we don't consider records that don't pass these filters)
	private boolean							isPairedMode;					// true if the extractor is in pair end mode
	private final UnpairedSAMRecordFilter	unpairedFilter;					// filter that filters out unpaired reads
	private Chromosome 						chromosome;						// chromosome of the last record read (a record has exactly one chromosome)
	private final Queue<Integer>			startQueue;						// queue containing the start positions of the last record read (a record can have more than one start if split)
	private final Queue<Integer>			stopQueue;						// queue containing the stop position of the last record read (a record can have more than one stop if split)
	private Strand 							strand;							// strand of the last record read (a record has exactly one strand)


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
		unpairedFilter = new UnpairedSAMRecordFilter();
		startQueue = new ConcurrentLinkedQueue<Integer>();
		stopQueue = new ConcurrentLinkedQueue<Integer>();
	}


	/**
	 * Applies the SAM record filters to a specified samRecord
	 * @param samRecord a {@link SAMRecord}
	 * @return the record itself if it passes the filter tests, null otherwise.
	 */
	private SAMRecord applyFilters(SAMRecord samRecord) {
		if (recordFilters == null) {
			return samRecord;
		}
		for (SAMRecordFilter currentFilter: recordFilters) {
			if (currentFilter != null) {
				samRecord = currentFilter.applyFilter(samRecord);
				if (samRecord == null) {
					return null;
				}
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
		// check if the read is properly paired
		samRecord = unpairedFilter.applyFilter(samRecord);
		if (samRecord != null) {
			// check if the read is the leftmost of the pair
			if (isLeftMostOfPair(samRecord)) {
				int start = samRecord.getAlignmentStart();
				int stop = start + samRecord.getInferredInsertSize() + 1;
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
		strand = samRecord.getFirstOfPairFlag() ? Strand.FIVE : Strand.THREE;
		if (isPairedMode) {
			return processPairedSamRecord(samRecord);
		}
		List<AlignmentBlock> alignmentBlocks = samRecord.getAlignmentBlocks();
		if (alignmentBlocks != null) {
			for (AlignmentBlock currentBlock: alignmentBlocks) {
				int start = currentBlock.getReferenceStart();
				int stop = start + currentBlock.getLength() + 1;
				startQueue.add(start);
				stopQueue.add(stop);
			}
			return true;
		}
		return false;
	}



	@Override
	public boolean readItem() throws IOException {
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
			samRecord = iterator.next();
			samRecord = applyFilters(samRecord);
			if (processSamRecord(samRecord)) {
				return true;
			}
		}
		return false;
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


	/**
	 * Sets the {@link SAMRecordFilter} to apply on the reads.  Only the reads that pass all the filters will be considered.
	 * @param recordFilter
	 */
	public void setFilters(SAMRecordFilter... recordFilter) {
		recordFilters = recordFilter;
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
