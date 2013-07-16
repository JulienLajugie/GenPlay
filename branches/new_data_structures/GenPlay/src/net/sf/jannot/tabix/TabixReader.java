package net.sf.jannot.tabix;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import net.sf.samtools.util.BlockCompressedInputStream;
import edu.yu.einstein.genplay.util.Utils;


/**
 * This class is part of the Tabix API written by Heng Li.
 * hengli@broadinstitute.org
 * 
 * Here what has been modified:
 * - attributes names more explicit
 * - javadoc added
 * - punctuation added
 * - getters & setters added (no more direct access to attributes)
 * - few processing improvements
 * 		- reading much faster, do not use the read char method but read line
 * 		- can match chromosome names in request and the ones in the vcf (e.g. request "chr1:0-100" will work even if chromosome 1 is written "1" in the vcf)
 * 
 * 
 * This class handle the way to communicate between an indexed VCF file (using Tabix) and Java.
 * The author is mentioned on the license above, however, the layout has been reformatted.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TabixReader {

	protected String mFn;
	private final BlockCompressedInputStream mFp;
	protected int preset;
	protected int chromosomeColumn;
	protected int startColumn;
	protected int stopColumn;
	protected int mMeta;
	protected int lineToSkip;	//exists only for the vcf reading but never used
	private String[] chromosomeNames;
	protected HashMap<String, Integer> chromosomeIndexes;
	protected HashMap<String, String> chromosomeNamesMap;
	protected HashMap<String, String> chromosomeNamesHistoryMap;
	private static int MAX_BIN = 37450;
	//private static int TAD_MIN_CHUNK_GAP = 32768;
	/** ??? */
	public static int TAD_LIDX_SHIFT = 14;
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
		if (beg >= end) {
			return 0;
		}
		if (end >= (1<<29)) {
			end = 1<<29;
		}
		--end;
		list[i++] = 0;
		for (k =    1 + (beg>>26); k <=    (1 + (end>>26)); ++k) {
			list[i++] = k;
		}
		for (k =    9 + (beg>>23); k <=    (9 + (end>>23)); ++k) {
			list[i++] = k;
		}
		for (k =   73 + (beg>>20); k <=   (73 + (end>>20)); ++k) {
			list[i++] = k;
		}
		for (k =  585 + (beg>>17); k <=  (585 + (end>>17)); ++k) {
			list[i++] = k;
		}
		for (k = 4681 + (beg>>14); k <= (4681 + (end>>14)); ++k) {
			list[i++] = k;
		}
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
	 * Reads a line (unefficient method)
	 * @param is input stream
	 * @return	the line
	 * @throws IOException
	 */
	public static String readLine(final InputStream is) throws IOException {
		StringBuffer buf = new StringBuffer();
		int c;
		while (((c = is.read()) >= 0) && (c != '\n')) {
			buf.append((char) c);
		}
		if (c < 0) {
			return null;
		}
		return buf.toString();
	}


	/**
	 * Reads the Tabix index from a file
	 * @param fp File pointer
	 * @throws IOException
	 */
	private void readIndex(final File fp) throws IOException {
		if (fp == null) {
			return;
		}
		BlockCompressedInputStream is = new BlockCompressedInputStream(fp);
		byte[] buf = new byte[4];

		is.read(buf, 0, 4); // read "TBI\1"
		chromosomeNames = new String[readInt(is)]; // # sequences
		chromosomeIndexes = new HashMap<String, Integer>();
		preset = readInt(is);
		chromosomeColumn = readInt(is);
		startColumn = readInt(is);
		stopColumn = readInt(is);
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
			mIndex[i].setB(new HashMap<Integer, TPair64[]>());
			for (j = 0; j < n_bin; ++j) {
				int bin = readInt(is);
				TPair64[] chunks = new TPair64[readInt(is)];
				for (k = 0; k < chunks.length; ++k) {
					long u = readLong(is);
					long v = readLong(is);
					chunks[k] = new TPair64(u, v); // in C, this is inefficient
				}
				mIndex[i].getB().put(bin, chunks);
			}
			// the linear index
			mIndex[i].setL(new long[readInt(is)]);
			for (k = 0; k < mIndex[i].getL().length; ++k) {
				mIndex[i].getL()[k] = readLong(is);
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
		initializeChromosomeNameMap();
		initializeChromosomeNameHistoryMap();
	}


	/**
	 * The chromosome map is made for compatibility purpose.
	 * The way chromosome names are written in a vcf file and the way they are loaded in GenPlay can differ.
	 * This map will use part of both names in order to figure if names are equals or not.
	 * For instance:
	 * Chromosomes in GenPlay have the prefix "chr" (e.g. chr1, chr2...), chromosomes in a vcf do not have (e.g. 1, 2...).
	 * The principle is to get the string starting at the first integer of the name: chr1 -> 1; 1 -> 1.
	 * That way, we compare names and admit "chr1" is equal to "1".
	 * If no int is found, the whole is kept (chrfirst -> chrfirst). The user will have to change his files or to load the right assembly to match the names.
	 */
	private void initializeChromosomeNameMap () {
		chromosomeNamesMap = new HashMap<String, String>();
		for (String fileChromosomeName: chromosomeIndexes.keySet()) {
			String shortName;
			int intOffset = Utils.getFirstIntegerOffset(fileChromosomeName, 0);

			if (intOffset != -1) {
				shortName = fileChromosomeName.substring(intOffset);
			} else {
				char lastChar = fileChromosomeName.charAt(fileChromosomeName.length() - 1);
				if ((lastChar == 'X') || (lastChar == 'Y') || (lastChar == 'M')) {
					shortName = "" + lastChar;
				} else {
					shortName = fileChromosomeName;
				}
			}
			chromosomeNamesMap.put(shortName, fileChromosomeName);
		}
	}


	/**
	 * The chromosome name history map is made for efficiency purpose.
	 * When a request with "chr1" is made, it looks for "1".
	 * If the "chr1" request has already been made, it is a waste of time to find out "1" again.
	 * That map stores the association between the requested chromosome names and their short names.
	 */
	private void initializeChromosomeNameHistoryMap () {
		chromosomeNamesHistoryMap = new HashMap<String, String>();
	}


	/**
	 * @param chr a chromosome
	 * @return the index of the chromosome
	 */
	protected int getChromosomeIndex (final String chr) {
		int result = -1;
		// We get the index according to the name given in the VCF
		if (chromosomeIndexes.containsKey(chr)) {
			result = chromosomeIndexes.get(chr);
		}
		return result;
	}


	/**
	 * @param chr a chromosome
	 * @return the index of the chromosome
	 */
	protected int getParsedChromosomeIndex (final String chr) {
		// We get the short name of the chromosome
		String name;
		if (chromosomeNamesHistoryMap.containsKey(chr)) {	// it already may exists
			name = chromosomeNamesHistoryMap.get(chr);
		} else {											// or we create it
			int intOffset = Utils.getFirstIntegerOffset(chr, 0);
			if (intOffset != -1) {
				name = chr.substring(intOffset);
			} else {
				char lastChar = chr.charAt(chr.length() - 1);
				if ((lastChar == 'X') || (lastChar == 'Y') || (lastChar == 'M')) {
					name = "" + lastChar;
				} else {
					name = chr;
				}
			}
			chromosomeNamesHistoryMap.put(name, chr);
		}

		// We get the index according to the name given in the VCF
		int result = -1;
		if (chromosomeIndexes.containsKey(chromosomeNamesMap.get(name))) {
			result = chromosomeIndexes.get(chromosomeNamesMap.get(name));
		}

		return result;
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
		ret[0] = getParsedChromosomeIndex(reg.substring(0, colon));
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
		if (idx.getL().length > 0){
			min_off = ((beg>>TAD_LIDX_SHIFT) >= idx.getL().length)? idx.getL()[idx.getL().length-1] : idx.getL()[beg>>TAD_LIDX_SHIFT];
		} else {
			min_off = 0;
		}
		for (i = n_off = 0; i < n_bins; ++i) {
			if ((chunks = idx.getB().get(bins[i])) != null) {
				n_off += chunks.length;
			}
		}
		if (n_off == 0) {
			return null;
		}
		off = new TPair64[n_off];
		for (i = n_off = 0; i < n_bins; ++i) {
			if ((chunks = idx.getB().get(bins[i])) != null) {
				for (int j = 0; j < chunks.length; ++j) {
					if (less64(min_off, chunks[j].getV())){
						off[n_off++] = new TPair64(chunks[j]);
					}
				}
			}
		}
		if (n_off == 0) {
			return null;
		}
		Arrays.sort(off, 0, n_off);
		// resolve completely contained adjacent blocks
		for (i = 1, l = 0; i < n_off; ++i) {
			if (less64(off[l].getV(), off[i].getV())) {
				++l;
				off[l].setU(off[i].getU());
				off[l].setV(off[i].getV());
			}
		}
		n_off = l + 1;
		// resolve overlaps between adjacent blocks; this may happen due to the merge in indexing
		for (i = 1; i < n_off; ++i){
			if (!less64(off[i-1].getV(), off[i].getU())) {
				off[i-1].setV(off[i].getU());
			}
		}
		// merge adjacent blocks
		for (i = 1, l = 0; i < n_off; ++i) {
			if ((off[l].getV()>>16) == (off[i].getU()>>16)) {
				off[l].setV(off[i].getV());
			} else {
				++l;
				off[l].setU(off[i].getU());
				off[l].setV(off[i].getV());
			}
		}
		n_off = l + 1;
		// return
		TPair64[] ret = new TPair64[n_off];
		for (i = 0; i < n_off; ++i){
			ret[i] = new TPair64(off[i].getU(), off[i].getV()); // in C, this is inefficient
		}
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
		return preset;
	}


	/**
	 * @return the mSc
	 */
	protected int getmSc() {
		return chromosomeColumn;
	}


	/**
	 * @return the mBc
	 */
	protected int getmBc() {
		return startColumn;
	}


	/**
	 * @return the mEc
	 */
	protected int getmEc() {
		return stopColumn;
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
		info += "preset: " + preset + "\n";
		info += "chromosomeColumn: " + chromosomeColumn + "\n";
		info += "startColumn: " + startColumn + "\n";
		info += "stopColumn: " + stopColumn + "\n";
		info += "mMeta: " + mMeta + "\n";
		info += "lineToSkip: " + lineToSkip + "\n";

		for (int i = 0; i < chromosomeNames.length; i++) {
			info += "chromosomeNames[" + i + "]: " + chromosomeNames[i] + "\n";
		}

		int cpt = 0;
		for (String key: chromosomeIndexes.keySet()) {
			info += "chromosomeIndexes[" + cpt + "]: " + key + " -> " + chromosomeIndexes.get(key) + "\n";
			cpt++;
		}

		/*for (int i = 0; i < mIndex.length; i++) {
			info += "mIndex[" + i + "]:\n" + mIndex[i].getDescription();
		}*/

		info += "mIndex: " + mIndex.length + "\n";

		info += "=====";

		System.out.println(info);
	}


	protected TIntv getIntv(String line) {
		TIntv intv = new TIntv();
		int col = 0, end = 0, beg = 0;
		while (((end = line.indexOf('\t', beg)) >= 0) || (end == -1)) {
			++col;
			if (col == chromosomeColumn) {
				intv.setTid(getChromosomeIndex(line.substring(beg, end)));
			} else if (col == startColumn) {
				intv.setBeg(Integer.parseInt(line.substring(beg, end==-1?line.length():end)));
				intv.setEnd(intv.getBeg());
				if ((preset&0x10000) != 0){
					intv.incrementEnd();
				} else {
					intv.decrementBeg();
				}
				if (intv.getBeg() < 0){
					intv.setBeg(0);
				}
				if (intv.getEnd() < 1){
					intv.setEnd(1);
				}
			} else { // FIXME: SAM supports are not tested yet
				if ((preset&0xffff) == 0) { // generic
					if (col == stopColumn) {
						intv.setEnd(Integer.parseInt(line.substring(beg, end)));
					}
				} else if ((preset&0xffff) == 1) { // SAM
					if (col == 6) { // CIGAR
						int l = 0, i, j;
						String cigar = line.substring(beg, end);
						for (i = j = 0; i < cigar.length(); ++i) {
							if (cigar.charAt(i) > '9') {
								int op = cigar.charAt(i);
								if ((op == 'M') || (op == 'D') || (op == 'N')) {
									l += Integer.parseInt(cigar.substring(j, i));
								}
							}
						}
						intv.setEnd(intv.getBeg() + l);
					}
				} else if ((preset&0xffff) == 2) { // VCF
					String alt;
					alt = end >= 0? line.substring(beg, end) : line.substring(beg);
					if (col == 4) { // REF
						if (alt.length() > 0){
							intv.setEnd(intv.getBeg() + alt.length());
						}
					} else if (col == 8) { // INFO
						int e_off = -1, i = alt.indexOf("END=");
						if (i == 0) {
							e_off = 4;
						} else if (i > 0) {
							i = alt.indexOf(";END=");
							if (i >= 0){
								e_off = i + 5;
							}
						}
						if (e_off > 0) {
							i = alt.indexOf(";", e_off);
							intv.setEnd(Integer.parseInt(i > e_off? alt.substring(e_off, i) : alt.substring(e_off)));
						}
					}
				}
			}
			if (end == -1) {
				break;
			}
			beg = end + 1;
		}
		return intv;
	}
}
