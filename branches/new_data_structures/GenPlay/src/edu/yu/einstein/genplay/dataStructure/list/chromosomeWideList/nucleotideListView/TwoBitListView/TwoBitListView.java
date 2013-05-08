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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.nucleotideListView.TwoBitListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.list.listView.AbstractListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;


/**
 * This class provides the representation of a sequence from a .2bit file as described
 * in the help file of the UCSC Genome Browser: http://genome.ucsc.edu/FAQ/FAQformat.html#format7
 * @author Julien Lajugie
 */
public final class TwoBitListView extends AbstractListView<Nucleotide> implements ListView<Nucleotide>, Serializable {

	/** Generated Serial ID */
	private static final long serialVersionUID = -4820838292720902481L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** Code for a missing genome position (a billion) in multi genome project. */
	public static final int MISSING_POSITION = -1000000000;

	/** 2bit random access file */
	private transient RandomAccessFile raf;

	/** Path to the 2bit file (used for the serialization) */
	private final String filePath;

	/** The size in byte of the header of the sequence */
	private final int headerSize;

	/** The offset of the sequence data relative to the start of the file */
	private final int offset;

	/** Number of bases of DNA in the sequence */
	private final int dnaSize;

	/** The starting position for each block of Ns */
	private final int[] nBlockStarts;

	/** The length for each block of Ns */
	private final int[] nBlockSizes;

	/** Genome name for a multi genome project */
	private final String genomeName;

	/** Allele type for a multi genome project */
	private final AlleleType alleleType;

	/** Chromosome of the current list */
	private final Chromosome chromosome;


	/**
	 * Creates an instance of {@link TwoBitListView}
	 * @param filePath path to the 2bit file (used for the serialization)
	 * @param headerSize the size in byte of the header of the sequence
	 * @param offset the offset of the sequence data relative to the start of the file
	 * @param dnaSize the number of bases of DNA in the sequence
	 * @param nBlockStarts the starting position for each block of Ns
	 * @param nBlockSizes the length for each block of Ns
	 * @param genomeName The genome name for a multi genome project
	 * @param alleleType the allele type for a multi genome project
	 * @param chromosome the chromosome of the current list
	 * @param raf {@link RandomAccessFile}
	 */
	public TwoBitListView(
			String filePath,
			int headerSize,
			int offset,
			int dnaSize,
			int[] nBlockStarts,
			int[] nBlockSizes,
			String genomeName,
			AlleleType alleleType,
			Chromosome chromosome,
			RandomAccessFile raf
			) {
		super();
		this.filePath = filePath;
		this.headerSize = headerSize;
		this.offset = offset;
		this.dnaSize = dnaSize;
		this.nBlockStarts = nBlockStarts;
		this.nBlockSizes = nBlockSizes;
		this.genomeName = genomeName;
		this.alleleType = alleleType;
		this.chromosome = chromosome;
		this.raf = raf;
	}


	/**
	 * Creates a new instance of {@link TwoBitListView} similar to the
	 * one in parameter but reading the specified file instead. This constructor
	 * is handy when the file path has been modified.
	 * @param listView
	 * @param file 2bit file containing the sequences
	 * @throws FileNotFoundException
	 */
	public TwoBitListView(TwoBitListView listView, File file) throws FileNotFoundException {
		// note that we can't just modify the file path of a TwoBitListView objects because they are immutable
		filePath = file.getAbsolutePath();
		headerSize = listView.headerSize;
		offset = listView.offset;
		dnaSize = listView.dnaSize;
		nBlockStarts = listView.nBlockStarts;
		nBlockSizes = listView.nBlockSizes;
		genomeName = listView.genomeName;
		alleleType = listView.alleleType;
		chromosome = listView.chromosome;
		reinitDataFile();
	}


	/**
	 * Returns the {@link Nucleotide} at the specified position
	 */
	@Override
	public Nucleotide get(int position) {
		if ((position <= 0) || (position > dnaSize)) {
			return null;
		}
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			position = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, alleleType, position, chromosome, genomeName);
			//position = ShiftCompute.computeReversedShift(genomeName, chromosome, alleleType, position);
			if (position == MISSING_POSITION) {
				return Nucleotide.BLANK;
			}
			if (position < 0) {
				return Nucleotide.ANY;
			}
		}

		position--;
		int i = 0;
		while ((i < nBlockStarts.length) && (nBlockStarts[i] <= position)) {
			if (position < (nBlockStarts[i] + nBlockSizes[i])) {
				return Nucleotide.ANY;
			}
			i++;
		}
		// integer in the file containing the position we look for
		int offsetPosition = position / 4;
		// position of the nucleotide inside the integer
		int offsetInsideByte = 3 - (position % 4);
		try {
			raf.seek(offsetPosition + offset + headerSize);
			// rotate the result until the two bits we want are on the far right
			// and then apply a 0x0003 filter
			int result2Bit = Integer.rotateRight(raf.readByte(), offsetInsideByte * 2) & 0x3;
			return Nucleotide.get((byte)result2Bit);
		} catch (IOException e) {
			return null;
		}
	}


	/**
	 * @return the dnaSize of the sequence
	 */
	public final int getDnaSize() {
		return dnaSize;
	}


	/**
	 * @return the headerSize of the sequence
	 */
	public final int getHeaderSize() {
		return headerSize;
	}


	/**
	 * @return the nBlockSizes of the sequence
	 */
	public final int[] getnBlockSizes() {
		return nBlockSizes;
	}


	/**
	 * @return the nBlockStarts of the sequence
	 */
	public final int[] getnBlockStarts() {
		return nBlockStarts;
	}


	/**
	 * @return the offset of the sequence
	 */
	public final int getOffset() {
		return offset;
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


	/**
	 * Method used for unserialization
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
	 * Reinitialize the reader
	 * @throws FileNotFoundException
	 */
	public void reinitDataFile() throws FileNotFoundException {
		raf = new RandomAccessFile(new File(filePath), "r");
	}


	/**
	 * Returns the number of nucleotides
	 */
	@Override
	public int size() {
		return dnaSize;
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
