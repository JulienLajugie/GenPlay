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
import java.util.AbstractList;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.Nucleotide;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;


/**
 * This class provides the representation of a sequence from a .2bit file as described 
 * in the help file of the UCSC Genome Browser: http://genome.ucsc.edu/FAQ/FAQformat.html#format7
 * @author Julien Lajugie
 * @version 0.1
 */
public class TwoBitSequence extends AbstractList<Nucleotide> implements Serializable, List<Nucleotide>, Stoppable {

	private static final long serialVersionUID = 4155123051619828951L;	// generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private transient RandomAccessFile 	raf;			// 2bit random access file  
	private String	filePath;					// path of the 2bit file (used for the serialization)
	private int 	headerSize;					// the size in byte of the header of the sequence
	private String 	name;						// the sequence name  
	private int 	offset;						// the offset of the sequence data relative to the start of the file
	private int 	dnaSize;					// number of bases of DNA in the sequence
	private int[] 	nBlockStarts;				// the starting position for each block of Ns
	private int[] 	nBlockSizes;				// the length of each block of Ns
	private int[] 	maskBlockStarts;			// the starting position for each masked block
	private int[] 	maskBlockSizes;				// the length of each masked block
	private boolean	needToBeStopped = false; 	// true if the execution need to be stopped
	protected String	genomeName = null;		// genome name for a multi genome project
	private Chromosome chromosome;				// chromosome of the current list
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(filePath);
		out.writeInt(headerSize);
		out.writeObject(name);
		out.writeInt(offset);
		out.writeInt(dnaSize);
		out.writeObject(nBlockStarts);
		out.writeObject(nBlockSizes);
		out.writeObject(maskBlockStarts);
		out.writeObject(maskBlockSizes);
		out.writeObject(genomeName);
		out.writeObject(chromosome);
	}
	
	
	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		filePath = (String) in.readObject();	
		headerSize = in.readInt();
		name = (String) in.readObject();
		offset = in.readInt();
		dnaSize = in.readInt();
		nBlockStarts = (int[]) in.readObject();
		nBlockSizes = (int[]) in.readObject();
		maskBlockStarts = (int[]) in.readObject();
		maskBlockSizes = (int[]) in.readObject();
		genomeName = (String) in.readObject();
		chromosome = (Chromosome) in.readObject();
		needToBeStopped = false;
	}
	
	
	/**
	 * Default constructor. Creates an instance of {@link TwoBitSequence}
	 * @param chromosome	chromosome of the current list
	 * @param genomeName	genome name for a multi genome project
	 */
	public TwoBitSequence(String genomeName, Chromosome chromosome) {
		super();
		this.genomeName = genomeName;
		this.chromosome = chromosome;
	}
	
	
	/**
	 * Extract the information about a sequence from a {@link TwoBitSequence}
	 * @param filePath path to the file containing the sequence
	 * @param raf {@link RandomAccessFile}
	 * @param offset offset of the sequence in the file
	 * @param name name of the sequence
	 * @param reverseBytes true if the byte order in the input file need to be reversed
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void extract(String filePath, RandomAccessFile raf, int offset, String name, boolean reverseBytes) throws IOException, InterruptedException {
		this.filePath = filePath;
		this.raf = raf;
		this.name = name;
		this.offset = offset;
		raf.seek(offset);
		if (reverseBytes) {
			dnaSize = Integer.reverseBytes(raf.readInt());
		} else {
			dnaSize = raf.readInt();
		}
		int nBlockCount = 0;
		if (reverseBytes) {
			nBlockCount = Integer.reverseBytes(raf.readInt());
		} else {
			nBlockCount = raf.readInt();
		}
		nBlockStarts = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				nBlockStarts[i] = Integer.reverseBytes(raf.readInt());
			} else {
				nBlockStarts[i] = raf.readInt();
			}
		}
		nBlockSizes = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				nBlockSizes[i] = Integer.reverseBytes(raf.readInt());
			} else {
				nBlockSizes[i] = raf.readInt();
			}			
		}
		
		int maskBlockCount = 0;
		if (reverseBytes) {
			maskBlockCount = Integer.reverseBytes(raf.readInt());
		} else {
			maskBlockCount = raf.readInt();
		}
		maskBlockStarts = new int[maskBlockCount];
		for (int i = 0; i < maskBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				maskBlockStarts[i] = Integer.reverseBytes(raf.readInt());
			} else {
				maskBlockStarts[i] = raf.readInt();
			}
		}
		maskBlockSizes = new int[maskBlockCount];
		for (int i = 0; i < maskBlockCount; i++) {
			// if the execution need to be stopped we generate an InterruptedException
			if (needToBeStopped) {
				throw new InterruptedException();
			}
			if (reverseBytes) {
				maskBlockSizes[i] = Integer.reverseBytes(raf.readInt());
			} else {
				maskBlockSizes[i] = raf.readInt();
			}
		}
		headerSize = 8 * (nBlockCount + maskBlockCount + 2);
	}


	/**
	 * @return the headerSize of the sequence
	 */
	public final int getHeaderSize() {
		return headerSize;
	}


	/**
	 * @return the name of the sequence
	 */
	public final String getName() {
		return name;
	}


	/**
	 * @return the offset of the sequence
	 */
	public final int getOffset() {
		return offset;
	}


	/**
	 * @return the dnaSize of the sequence
	 */
	public final int getDnaSize() {
		return dnaSize;
	}


	/**
	 * @return the nBlockStarts of the sequence
	 */
	public final int[] getnBlockStarts() {
		return nBlockStarts;
	}


	/**
	 * @return the nBlockSizes of the sequence
	 */
	public final int[] getnBlockSizes() {
		return nBlockSizes;
	}


	/**
	 * @return the maskBlockStarts of the sequence
	 */
	public final int[] getMaskBlockStarts() {
		return maskBlockStarts;
	}


	/**
	 * @return the maskBlockSizes of the sequence
	 */
	public final int[] getMaskBlockSizes() {
		return maskBlockSizes;
	}
	
	
	/**
	 * Returns the {@link Nucleotide} at the specified position
	 */
	@Override
	public Nucleotide get(int position) {
		if ((position < 0) || (position > dnaSize)) { 
			return null;
		}
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			position = ShiftCompute.computeReversedShift(genomeName, chromosome, position);
			if (position < 0) {
				return Nucleotide.ANY;
			}
		}
		
		int i = 0;
		while ((i < nBlockStarts.length) && (nBlockStarts[i] <= position)) {
			if (position < nBlockStarts[i] + nBlockSizes[i]) {
				return Nucleotide.ANY;	
			}
			i++;
		}
		// integer in the file containing the position we look for
		int offsetPosition = (int)(position / 4);
		// position of the nucleotide inside the integer
		int offsetInsideByte = 3 - (position % 4);
		try {
			raf.seek(offsetPosition + offset + headerSize);
			// rotate the result until the two bits we want are on the far right 
			// and then apply a 0x0003 filter
			int result2Bit= Integer.rotateRight(raf.readByte(), offsetInsideByte * 2) & 0x3;
			return Nucleotide.get((byte)result2Bit);
		} catch (IOException e) {
			return null;
		}
	}


	/**
	 * Returns the number of nucleotides
	 */
	@Override
	public int size() {
		return dnaSize;
	}
	
	
	/**
	 * Stops the extraction of the data
	 */
	@Override
	public void stop() {
		needToBeStopped = true;
	}


	/**
	 * Re-initializes the connection to the random access file containing the sequences  
	 * @throws FileNotFoundException
	 */
	private void reinitDataFile() throws FileNotFoundException {
		raf = new RandomAccessFile(new File(filePath), "r");
	}
	
	
	/**
	 * Sets the file path to the random access file containing the sequences
	 * @param filePath path to the 
	 * @throws FileNotFoundException
	 */
	public void setSequenceFilePath(String filePath) throws FileNotFoundException {
		this.filePath = filePath;
		reinitDataFile();
	}
}
