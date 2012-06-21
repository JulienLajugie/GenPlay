package edu.yu.einstein.genplay.core.multiGenome.tabixAPI;

/* Contact: Heng Li <hengli@broadinstitute.org> */


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import net.sf.samtools.util.BlockCompressedInputStream;


/**
 * This class handle the way to communicate between an indexed VCF file (using Tabix) and Java.
 * The author is mentioned on the license above, however, the layout has been reformatted. 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TabixReader {

	private String mFn;
	private BlockCompressedInputStream mFp;
	private int mPreset;
	private int mSc;
	private int mBc;
	private int mEc;
	private int mMeta;
	private int lineToSkip;	//exists only for the vcf reading but never used
	private String[] chromosomeNames;
	private HashMap<String, Integer> chromosomeIndexes;
	private static int MAX_BIN = 37450;
	//private static int TAD_MIN_CHUNK_GAP = 32768;
	private static int TAD_LIDX_SHIFT = 14;
	private TIndex[] mIndex;


	protected static boolean less64(final long u, final long v) { // unsigned 64-bit comparison
		return (u < v) ^ (u < 0) ^ (v < 0);
	}


	/**
	 * The constructor
	 * @param fn File name of the data file
	 * @throws IOException
	 */
	public TabixReader(final String fn) throws IOException {
		mFn = fn;
		mFp = new BlockCompressedInputStream(new File(fn));
		readIndex();
	}


	private static int reg2bins(final int beg, final int _end, final int[] list) {
		int i = 0, k, end = _end;
		if (beg >= end) return 0;
		if (end >= 1<<29) end = 1<<29;
		--end;
		list[i++] = 0;
		for (k =    1 + (beg>>26); k <=    1 + (end>>26); ++k) list[i++] = k;
		for (k =    9 + (beg>>23); k <=    9 + (end>>23); ++k) list[i++] = k;
		for (k =   73 + (beg>>20); k <=   73 + (end>>20); ++k) list[i++] = k;
		for (k =  585 + (beg>>17); k <=  585 + (end>>17); ++k) list[i++] = k;
		for (k = 4681 + (beg>>14); k <= 4681 + (end>>14); ++k) list[i++] = k;
		return i;
	}


	/**
	 * Reads an int primitive object
	 * @param is	the input stream
	 * @return		the int value
	 * @throws IOException
	 */
	public static int readInt(final InputStream is) throws IOException {
		byte[] buf = new byte[4];
		is.read(buf);
		return ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}


	/**
	 * Reads a long primitive object
	 * @param is	the input stream
	 * @return		the int value
	 * @throws IOException
	 */
	public static long readLong(final InputStream is) throws IOException {
		byte[] buf = new byte[8];
		is.read(buf);
		return ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}

	
	/**
	 * Reads a line
	 * @param br	the buffered input stream
	 * @return		a String value
	 * @throws IOException
	 */
	public String readLine(final BufferedReader br) throws IOException {
		return br.readLine();
	}
	

	/**
	 * Reads the Tabix index from a file
	 * @param fp File pointer
	 * @throws IOException 
	 */
	public void readIndex(final File fp) throws IOException {
		if (fp == null) return;
		BlockCompressedInputStream is = new BlockCompressedInputStream(fp);
		byte[] buf = new byte[4];

		is.read(buf, 0, 4); // read "TBI\1"
		chromosomeNames = new String[readInt(is)]; // # sequences
		chromosomeIndexes = new HashMap<String, Integer>();
		mPreset = readInt(is);
		mSc = readInt(is);
		mBc = readInt(is);
		mEc = readInt(is);
		mMeta = readInt(is);
		lineToSkip = readInt(is);
		// read sequence dictionary
		int i, j, k, l = readInt(is);
		buf = new byte[l];
		is.read(buf);
		for (i = j = k = 0; i < buf.length; ++i) {
			if (buf[i] == 0) {
				byte[] b = new byte[i - j];
				System.arraycopy(buf, j, b, 0, b.length);
				String s = new String(b);
				chromosomeIndexes.put(s, k);
				chromosomeNames[k++] = s;
				j = i + 1;
			}
		}
		// read the index
		mIndex = new TIndex[chromosomeNames.length];
		for (i = 0; i < chromosomeNames.length; ++i) {
			// the binning index
			int n_bin = readInt(is);
			mIndex[i] = new TIndex();
			mIndex[i].b = new HashMap<Integer, TPair64[]>();
			for (j = 0; j < n_bin; ++j) {
				int bin = readInt(is);
				TPair64[] chunks = new TPair64[readInt(is)];
				for (k = 0; k < chunks.length; ++k) {
					long u = readLong(is);
					long v = readLong(is);
					chunks[k] = new TPair64(u, v); // in C, this is inefficient
				}
				mIndex[i].b.put(bin, chunks);
			}
			// the linear index
			mIndex[i].l = new long[readInt(is)];
			for (k = 0; k < mIndex[i].l.length; ++k) {
				mIndex[i].l[k] = readLong(is);
			}
		}
		// close
		is.close();
	}


	/**
	 * Read the Tabix index from the default file.
	 * @throws IOException 
	 */
	public void readIndex() throws IOException {
		readIndex(new File(mFn + ".tbi"));
	}


	/**
	 * @param chr a chromosome
	 * @return the index of the chromosome
	 */
	protected int getChromosomeIndex (final String chr) {
		if (chromosomeIndexes.containsKey(chr)) {
			return chromosomeIndexes.get(chr);
		}
		return -1;
	}


	/**
	 * Parse a region in the format of "chr1", "chr1:100" or "chr1:100-1000"
	 *
	 * @param reg Region string
	 * @return An array where the three elements are sequence_id,
	 *         region_begin and region_end. On failure, sequence_id==-1.
	 */
	public int[] parseReg(final String reg) {
		int colon, hyphen;
		int[] ret = new int[3];
		colon = reg.indexOf(':');
		hyphen = reg.indexOf('-');
		ret[0] = getChromosomeIndex(reg.substring(0, colon));
		ret[1] = Integer.parseInt(reg.substring(colon+1, hyphen));
		ret[2] = Integer.parseInt(reg.substring(hyphen+1, reg.length()));
		return ret;
	}

	
	/**
	 * @param indexChr index of the chromosome (e.g.: 0)
	 * @return the results of the first chromosome presents in the VCF 
	 */
	public Iterator shortQuery (int indexChr) {
		int begin = 0;
		int end = 1<<29;
		return query(indexChr, begin, end);
	}
	

	/**
	 * Performs a query
	 * @param tid 	the chromosome
	 * @param beg	start position
	 * @param end	stop position
	 * @return		the iterator to scan the result
	 */
	public Iterator query(final int tid, final int beg, final int end) {
		TPair64[] off, chunks;
		long min_off;
		TIndex idx = mIndex[tid];
		int[] bins = new int[MAX_BIN];
		int i, l, n_off, n_bins = reg2bins(beg, end, bins);
		if (idx.l.length > 0)
			min_off = (beg>>TAD_LIDX_SHIFT >= idx.l.length)? idx.l[idx.l.length-1] : idx.l[beg>>TAD_LIDX_SHIFT];
			else min_off = 0;
		for (i = n_off = 0; i < n_bins; ++i) {
			if ((chunks = idx.b.get(bins[i])) != null)
				n_off += chunks.length;
		}
		if (n_off == 0) return null;
		off = new TPair64[n_off];
		for (i = n_off = 0; i < n_bins; ++i)
			if ((chunks = idx.b.get(bins[i])) != null)
				for (int j = 0; j < chunks.length; ++j)
					if (less64(min_off, chunks[j].v))
						off[n_off++] = new TPair64(chunks[j]);
		if (n_off == 0) return null;
		Arrays.sort(off, 0, n_off);
		// resolve completely contained adjacent blocks
		for (i = 1, l = 0; i < n_off; ++i) {
			if (less64(off[l].v, off[i].v)) {
				++l;
				off[l].u = off[i].u; off[l].v = off[i].v;
			}
		}
		n_off = l + 1;
		// resolve overlaps between adjacent blocks; this may happen due to the merge in indexing
		for (i = 1; i < n_off; ++i)
			if (!less64(off[i-1].v, off[i].u)) off[i-1].v = off[i].u;
		// merge adjacent blocks
		for (i = 1, l = 0; i < n_off; ++i) {
			if (off[l].v>>16 == off[i].u>>16) off[l].v = off[i].v;
			else {
				++l;
				off[l].u = off[i].u;
				off[l].v = off[i].v;
			}
		}
		n_off = l + 1;
		// return
		TPair64[] ret = new TPair64[n_off];
		for (i = 0; i < n_off; ++i) ret[i] = new TPair64(off[i].u, off[i].v); // in C, this is inefficient
		return new Iterator(this, tid, beg, end, ret);
	}


	/**
	 * Performs a query
	 * @param reg 	the string query
	 * @return 		the iterator to scan the result
	 */
	public Iterator query(final String reg) {
		int[] x = parseReg(reg);
		if (x[0] != -1) {	//The chromosome is not necessary presents in the file
			return query(x[0], x[1], x[2]);
		}
		return null;
	}


	/**
	 * @return the mFp
	 */
	public BlockCompressedInputStream getmFp() {
		return mFp;
	}


	/**
	 * @return the mPreset
	 */
	protected int getmPreset() {
		return mPreset;
	}


	/**
	 * @return the mSc
	 */
	protected int getmSc() {
		return mSc;
	}


	/**
	 * @return the mBc
	 */
	protected int getmBc() {
		return mBc;
	}


	/**
	 * @return the mEc
	 */
	protected int getmEc() {
		return mEc;
	}


	/**
	 * @return the mMeta
	 */
	protected int getmMeta() {
		return mMeta;
	}

	
	/**
	 * Show all class parameters.
	 */
	public void show () {
		String info = "===== show tabix reader\n";
		info += "mFn: " + mFn + "\n";
		info += "mPreset: " + mPreset + "\n";
		info += "mSc: " + mSc + "\n";
		info += "mBc: " + mBc + "\n";
		info += "mEc: " + mEc + "\n";
		info += "mMeta: " + mMeta + "\n";
		info += "mSkip: " + lineToSkip + "\n";
		
		for (int i = 0; i < chromosomeNames.length; i++) {
			info += "mSeq[" + i + "]: " + chromosomeNames[i] + "\n";
		}
		
		int cpt = 0;
		for (String key: chromosomeIndexes.keySet()) {
			info += "mChr2tid[" + cpt + "]: " + key + " -> " + chromosomeIndexes.get(key) + "\n";
			cpt++;
		}

		/*for (int i = 0; i < mIndex.length; i++) {
			info += "mIndex[" + i + "]:\n" + mIndex[i].getDescription();
		}*/
		
		info += "mIndex: " + mIndex.length + "\n";
		
		info += "=====";
		
		System.out.println(info);
	}
}
