/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.twoBitFormat;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * This class provides the representation of a sequence from a .2bit file as described 
 * in the help file of the UCSC Genome Browser: http://genome.ucsc.edu/FAQ/FAQformat.html#format7
 * @author Julien Lajugie
 * @version 0.1
 */
public class TwoBitSequence {
	private final int 		headerSize;		// the size in byte of the header of the sequence
	private final String 	name;			// the sequence name  
	private final int 		offset;			// the offset of the sequence data relative to the start of the file
	private final int 		dnaSize;		// number of bases of DNA in the sequence
	private final int[] 	nBlockStarts;	// the starting position for each block of Ns
	private final int[] 	nBlockEnds;		// the ending position for each block of Ns
	private final int[] 	maskBlockStarts;// the starting position for each masked block
	private final int[] 	maskBlockEnds;	// the ending position for each masked block
	
	
	/**
	 * Creates an instance of {@link TwoBitSequence}
	 * @param raf {@link RandomAccessFile}
	 * @param offset offset of the sequence in the file
	 * @param name name of the sequence
	 * @throws IOException
	 */
	public TwoBitSequence(RandomAccessFile raf, int offset, String name) throws IOException {
		raf.seek(offset);
		this.name = name;
		this.offset = offset;
		dnaSize = raf.readInt() | 0x80000000;
		System.out.println(dnaSize);
		//1 080 497 744
		int nBlockCount = raf.readInt() & 0x7fffffff;
		nBlockStarts = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			nBlockStarts[i] = raf.readInt() & 0x7fffffff;
		}
		nBlockEnds = new int[nBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			int nBlockSize = raf.readInt() & 0x7fffffff;
			nBlockEnds[i] = nBlockStarts[i] + nBlockSize;
		}
		int maskBlockCount = raf.readInt() & 0x7fffffff;
		maskBlockStarts = new int[maskBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			maskBlockStarts[i] = raf.readInt() & 0x7fffffff;
		}
		maskBlockEnds = new int[maskBlockCount];
		for (int i = 0; i < nBlockCount; i++) {
			int maskBlockSize = raf.readInt() & 0x7fffffff;
			maskBlockEnds[i] = maskBlockStarts[i] + maskBlockSize;
		}
		headerSize = 64 * (nBlockCount + maskBlockCount +2);
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
	 * @return the nBlockEnds of the sequence
	 */
	public final int[] getnBlockEnds() {
		return nBlockEnds;
	}


	/**
	 * @return the maskBlockStarts of the sequence
	 */
	public final int[] getMaskBlockStarts() {
		return maskBlockStarts;
	}


	/**
	 * @return the maskBlockEnds of the sequence
	 */
	public final int[] getMaskBlockEnds() {
		return maskBlockEnds;
	}	
}
