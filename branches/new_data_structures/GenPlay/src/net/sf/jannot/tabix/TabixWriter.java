package net.sf.jannot.tabix;

/* The MIT License

 Copyright (c) 2010 Broad Institute.
 Portions Copyright (c) 2011 University of Toronto.

 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jannot.picard.LineBlockCompressedInputStream;
import net.sf.samtools.seekablestream.SeekableFileStream;
import net.sf.samtools.util.BlockCompressedInputStream;
import net.sf.samtools.util.BlockCompressedOutputStream;

/**
 * Tabix writer, based on Heng Li's C implementation.
 * 
 * 
 * This class is part of the JAnnot API.
 * 
 * Here what has been modified:
 * - attributes names more explicit
 * - javadoc added
 * - punctuation added
 * - getters & setters added (no more direct access to attributes)
 * - few processing improvements
 * 
 * 
 * @author tarkvara
 * @author Nicolas Fourel (formatting)
 */
public class TabixWriter extends TabixReader {
	private static final Charset LATIN1 = Charset.forName("ISO-8859-1");

	/** The binning index. */
	List<Map<Integer, List<TPair64>>> binningIndex = new ArrayList<Map<Integer, List<TPair64>>>();

	/** The linear index. */
	List<List<Long>> linearIndex = new ArrayList<List<Long>>();


	/**
	 * Constructor of {@link TabixWriter}
	 * @param fn					the bgzip compressed file to use for the indexing
	 * @param tabixConfiguration	the tabix configuration
	 * @throws Exception
	 */
	public TabixWriter(File fn, TabixConfiguration tabixConfiguration) throws Exception {
		super(fn.getPath());
		applyTabixConfiguration(tabixConfiguration);
		chromosomeIndexes = new LinkedHashMap<String, Integer>();
	}


	private void applyTabixConfiguration(TabixConfiguration tabixConfiguration) {
		preset = tabixConfiguration.getPreset();
		chromosomeColumn = tabixConfiguration.getChrColumn();
		startColumn = tabixConfiguration.getStartColumn();
		stopColumn = tabixConfiguration.getEndColumn();
		mMeta = tabixConfiguration.getCommentChar();
		lineToSkip = tabixConfiguration.getLinesToSkip();
	}


	/**
	 * Indexes the bgzip file using the tabix algorithm
	 * @param idx			the output file
	 * @throws Exception
	 */
	public void createIndex(File idx) throws Exception {
		LineBlockCompressedInputStream fp = new LineBlockCompressedInputStream(new SeekableFileStream(new File(mFn)));
		makeIndex(fp);
		fp.close();
		File indexFile = idx;
		BlockCompressedOutputStream fpidx = new BlockCompressedOutputStream(indexFile);
		saveIndex(fpidx);
		fpidx.close();
	}


	/**
	 * Create the index
	 * @param fp		the bgzip input file
	 * @throws Exception
	 */
	private void makeIndex(BlockCompressedInputStream fp) throws Exception {
		int last_bin, save_bin;
		int last_coor, last_tid, save_tid;
		long save_off, last_off, lineno = 0, offset0 = -1;
		String str;

		save_bin = save_tid = last_tid = last_bin = 0xffffffff; // Was unsigned
		// in C
		// implementation.
		save_off = last_off = 0;
		last_coor = 0xffffffff; // Should be unsigned.
		while ((str = readLine(fp)) != null) {
			++lineno;
			if ((lineno <= lineToSkip) || (str.charAt(0) == mMeta)) {
				last_off = fp.getFilePointer();
				continue;
			}
			TIntv intv = getIntv(str);
			if ((intv.getBeg() < 0) || (intv.getEnd() < 0)) {
				throw new Exception("The indexes overlap or are out of bounds.");
			}
			if (last_tid != intv.getTid()) { // change of chromosomes
				if (last_tid > intv.getTid()) {
					intv.incrementBeg();
					throw new Exception(String.format(
							"The chromosome blocks are not continuous at line %d, is the file sorted? [pos %d].",
							lineno, intv.getBeg()));
				}
				last_tid = intv.getTid();
				last_bin = 0xffffffff;
			} else if (last_coor > intv.getBeg()) {
				throw new Exception(String.format("File out of order at line %d.", lineno));
			}
			long tmp = insertLinear(linearIndex.get(intv.getTid()), intv.getBeg(), intv.getEnd(), last_off);
			if (last_off == 0) {
				offset0 = tmp;
			}
			if (intv.getBin() != last_bin) { // then possibly write the binning index
				if (save_bin != 0xffffffff) { // save_bin==0xffffffffu only
					// happens to the first record
					insertBinning(binningIndex.get(save_tid), save_bin, save_off, last_off);
				}
				save_off = last_off;
				save_bin = intv.getBin();
				last_bin = intv.getBin();
				save_tid = intv.getTid();
				if (save_tid < 0) {
					break;
				}
			}
			if (fp.getFilePointer() <= last_off) {
				throw new Exception(String.format("Bug in BGZF: %x < %x.", fp.getFilePointer(), last_off));
			}
			last_off = fp.getFilePointer();
			last_coor = intv.getBeg();
		}
		if (save_tid >= 0) {
			insertBinning(binningIndex.get(save_tid), save_bin, save_off, fp.getFilePointer());
		}
		mergeChunks();
		fillMissing();
		if ((offset0 != -1) && !linearIndex.isEmpty() && (linearIndex.get(0) != null)) {
			int beg = (int) (offset0 >> 32), end = (int) (offset0 & 0xffffffff);
			for (int i = beg; i <= end; ++i) {
				linearIndex.get(0).set(i, 0L);
			}
		}
	}


	private void insertBinning(Map<Integer, List<TPair64>> binningForChr, int bin, long beg, long end) {
		if (!binningForChr.containsKey(bin)) {
			binningForChr.put(bin, new ArrayList<TPair64>());
		}
		List<TPair64> list = binningForChr.get(bin);
		list.add(new TPair64(beg, end));
	}


	private long insertLinear(List<Long> linearForChr, int beg, int end, long offset) {
		beg = beg >> TAD_LIDX_SHIFT;
			end = (end - 1) >> TAD_LIDX_SHIFT;

			// Expand the array if necessary.
			int newSize = Math.max(beg, end) + 1;
			while (linearForChr.size() < newSize) {
				linearForChr.add(0L);
			}
			if (beg == end) {
				if (linearForChr.get(beg) == 0L) {
					linearForChr.set(beg, offset);
				}
			} else {
				for (int i = beg; i <= end; ++i) {
					if (linearForChr.get(i) == 0L) {
						linearForChr.set(i, offset);
					}
				}
			}
			return ((long) beg << 32) | end;
	}


	private void mergeChunks() {
		for (int i = 0; i < binningIndex.size(); i++) {
			Map<Integer, List<TPair64>> binningForChr = binningIndex.get(i);
			for (Integer k : binningForChr.keySet()) {
				List<TPair64> p = binningForChr.get(k);
				int m = 0;
				for (int l = 1; l < p.size(); l++) {
					if ((p.get(m).getV() >> 16) == (p.get(l).getU() >> 16)) {
						p.get(m).setV(p.get(l).getV());
					} else {
						p.set(++m, p.get(l));
					}
				}
				while (p.size() > (m + 1)) {
					p.remove(p.size() - 1);
				}
			}
		}
	}


	private void fillMissing() {
		for (int i = 0; i < linearIndex.size(); ++i) {
			List<Long> linearForChr = linearIndex.get(i);
			for (int j = 1; j < linearForChr.size(); ++j) {
				if (linearForChr.get(j) == 0) {
					linearForChr.set(j, linearForChr.get(j - 1));
				}
			}
		}
	}


	/**
	 * Writes a int value into an output stream
	 * @param os		the output stream
	 * @param value		the value
	 * @throws IOException
	 */
	public static void writeInt(final OutputStream os, int value) throws IOException {
		byte[] buf = new byte[4];
		ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).putInt(value);
		os.write(buf);
	}


	/**
	 * Writes a long value into an output stream
	 * @param os		the output stream
	 * @param value		the value
	 * @throws IOException
	 */
	public static void writeLong(final OutputStream os, long value) throws IOException {
		byte[] buf = new byte[8];
		ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).putLong(value);
		os.write(buf);
	}


	private void saveIndex(BlockCompressedOutputStream fp) throws IOException {
		fp.write("TBI\1".getBytes(LATIN1));
		writeInt(fp, binningIndex.size());

		// Write the ti_conf_t
		writeInt(fp, preset);
		writeInt(fp, chromosomeColumn);
		writeInt(fp, startColumn);
		writeInt(fp, stopColumn);
		writeInt(fp, mMeta);
		writeInt(fp, lineToSkip);

		// Write sequence dictionary. Since mChr2tid is a LinkedHashmap, the
		// keyset
		// will be returned in insertion order.
		int l = 0;
		for (String k : chromosomeIndexes.keySet()) {
			l += k.length() + 1;
		}
		writeInt(fp, l);
		for (String k : chromosomeIndexes.keySet()) {
			fp.write(k.getBytes(LATIN1));
			fp.write(0);
		}

		for (int i = 0; i < chromosomeIndexes.size(); i++) {
			Map<Integer, List<TPair64>> binningForChr = binningIndex.get(i);

			// Write the binning index.
			writeInt(fp, binningForChr.size());
			for (int k : binningForChr.keySet()) {
				List<TPair64> p = binningForChr.get(k);
				writeInt(fp, k);
				writeInt(fp, p.size());
				for (TPair64 bin : p) {
					writeLong(fp, bin.getU());
					writeLong(fp, bin.getV());
				}
			}
			// Write the linear index.
			List<Long> linearForChr = linearIndex.get(i);
			writeInt(fp, linearForChr.size());
			for (int x = 0; x < linearForChr.size(); x++) {
				writeLong(fp, linearForChr.get(x));
			}
		}
	}


	/**
	 * Override chr2tid so that getInv() adds new chromosomes as we read the
	 * source file.
	 */
	@Override
	protected int getChromosomeIndex(String chr) {
		if (!chromosomeIndexes.containsKey(chr)) {
			// Doesn't exist yet.
			chromosomeIndexes.put(chr, chromosomeIndexes.size());

			// Expand our indices.
			binningIndex.add(new HashMap<Integer, List<TPair64>>());
			linearIndex.add(new ArrayList<Long>());
		}
		return chromosomeIndexes.get(chr);
	}


	/**
	 * Override getIntv because it's a good time to figure out which bin things
	 * should go into.
	 * 
	 * @param line a line read from the source file
	 * @return an object describing the interval
	 */
	@Override
	protected TIntv getIntv(String line) {
		TIntv result = super.getIntv(line);
		try {
			result.setBin(reg2bin(result.getBeg(), result.getEnd()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}


	private int reg2bin(int beg, int end) {
		--end;
		if ((beg >> 14) == (end >> 14)) {
			return 4681 + (beg >> 14);
		}
		if ((beg >> 17) == (end >> 17)) {
			return 585 + (beg >> 17);
		}
		if ((beg >> 20) == (end >> 20)) {
			return 73 + (beg >> 20);
		}
		if ((beg >> 23) == (end >> 23)) {
			return 9 + (beg >> 23);
		}
		if ((beg >> 26) == (end >> 26)) {
			return 1 + (beg >> 26);
		}
		return 0;
	}

}
