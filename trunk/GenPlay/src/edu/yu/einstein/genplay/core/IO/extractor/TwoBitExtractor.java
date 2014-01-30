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
package edu.yu.einstein.genplay.core.IO.extractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.IO.dataReader.NucleotideReader;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.nucleotideListView.twoBitListView.TwoBitListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * Extract the data from a 2bit file.
 * This extractor is used to create {@link GenomicListView} nucleotide
 * @author Julien Lajugie
 */
public class TwoBitExtractor extends Extractor implements Stoppable, NucleotideReader {

	/** Default first base position of bed files. 2bit files are 0-based */
	public static final int DEFAULT_FIRST_BASE_POSITION = 0;

	/** Signature of a 2bit file */
	private final static String TWOBIT_SIGNATURE = "1A412743";

	/** Set to true if the execution of the extractor needs to be stopped */
	private boolean	isStopped = false;

	/** Position of the first base */
	private int	firstBasePosition = DEFAULT_FIRST_BASE_POSITION;

	/** Genome name for a multi genome project */
	private String genomeName;

	/** Allele type for a multi genome project */
	private AlleleType alleleType;

	/** Each element of this list read a chromosome in the file */
	private final List<ListView<Nucleotide>> data;

	/** 2bit random access file */
	private RandomAccessFile twoBitFile;

	/** Path to the 2bit file (used for the serialization) */
	private String filePath;

	/** True if the order of the bytes of multi-bytes entities need to be reversed  */
	private boolean reverseBytes;


	/**
	 * Creates an instance of {@link TwoBitExtractor}
	 * @param dataFile 2Bit file
	 */
	public TwoBitExtractor(File dataFile) {
		super(dataFile);
		data = new ArrayList<ListView<Nucleotide>>();
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		for (int i = 0; i < projectChromosomes.size(); i++) {
			data.add(null);
		}
	}


	/**
	 * Extracts the sequence list from a 2bit file
	 * @param genomeName the genome name for a multi genome project
	 * @param alleleType the allele type for a multi genome project
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidFileTypeException
	 * @throws InterruptedException
	 */
	public void extract(String genomeName, AlleleType alleleType) throws FileNotFoundException, IOException, InvalidFileTypeException, InterruptedException  {
		this.genomeName = genomeName;
		this.alleleType = alleleType;
		// true if the bytes of multi-byte entities need to be reversed when read
		reverseBytes = false;
		filePath = getDataFile().getAbsolutePath();
		twoBitFile = new RandomAccessFile(getDataFile(), "r");
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
			// read the version of the 2bit file
			Integer.reverseBytes(twoBitFile.readInt());
		} else {
			twoBitFile.readInt();
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
			if (isStopped) {
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
			ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
			while ((k < projectChromosomes.size()) && (!found)) {
				if (projectChromosomes.get(k).getName().equalsIgnoreCase(sequenceNames[i])) {
					// if the execution need to be stopped we generate an InterruptedException
					if (isStopped) {
						throw new InterruptedException();
					}
					long currentPosition = twoBitFile.getFilePointer();
					Chromosome chromosome = projectChromosomes.get(k);
					data.set(k, extractChromosome(chromosome, offsets[i]));
					twoBitFile.seek(currentPosition);
					found = true;
				}
				k++;
			}
		}
	}


	/**
	 * Extracts the information for a chromosome and create a {@link ListView} of {@link Nucleotide} objects
	 * from the data extracted.
	 * @param chromosome chromosome to extract
	 * @param offset offset of the beginning of the section of the chromosome to extract in the random file
	 * @return A {@link ListView} of {@link Nucleotide} objects from the data extracted
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private ListView<Nucleotide> extractChromosome(Chromosome chromosome, int offset) throws IOException, InterruptedException {
		int dnaSize;
		int[] nBlockStarts;
		int[] nBlockSizes;
		twoBitFile.seek(offset);
		if (reverseBytes) {
			dnaSize = Integer.reverseBytes(twoBitFile.readInt());
		} else {
			dnaSize = twoBitFile.readInt();
		}
		int nBlockCount = 0;
		if (reverseBytes) {
			nBlockCount = Integer.reverseBytes(twoBitFile.readInt());
		} else {
			nBlockCount = twoBitFile.readInt();
		}
		nBlockStarts = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (isStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				nBlockStarts[i] = Integer.reverseBytes(twoBitFile.readInt());
			} else {
				nBlockStarts[i] = twoBitFile.readInt();
			}
		}
		nBlockSizes = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (isStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				nBlockSizes[i] = Integer.reverseBytes(twoBitFile.readInt());
			} else {
				nBlockSizes[i] = twoBitFile.readInt();
			}
		}

		int maskBlockCount = 0;
		if (reverseBytes) {
			maskBlockCount = Integer.reverseBytes(twoBitFile.readInt());
		} else {
			maskBlockCount = twoBitFile.readInt();
		}
		int headerSize = 8 * (nBlockCount + maskBlockCount + 2);
		ListView<Nucleotide> lv = new TwoBitListView(filePath, headerSize, offset, dnaSize, nBlockStarts, nBlockSizes, genomeName, alleleType, chromosome, twoBitFile);
		return lv;
	}


	/**
	 * @return The data extracted from the 2bit file.
	 */
	public List<ListView<Nucleotide>> getExtractedData() {
		return data;
	}


	@Override
	public int getFirstBasePosition() {
		return firstBasePosition;
	}


	/**
	 * @return True if the order of the bytes of multi-bytes entities need to be reversed
	 */
	public boolean needToReverseBytes() {
		return reverseBytes;
	}


	@Override
	protected String retrieveDataName(File dataFile) {
		return dataFile.getName();
	}


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;
	}


	@Override
	public void stop() {
		isStopped = true;
	}
}
