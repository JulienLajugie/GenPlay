package edu.yu.einstein.genplay.core.multiGenome.tabixAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
 * 
 * 
 * Iterator for a {@link TabixReader}
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class Iterator {

	private final BufferedReader reader;
	private final TabixReader tabixReader;
	private int i, n_seeks;
	private final int chromosomeIndex;
	private final int start;
	private final int stop;
	private final TPair64[] offsets;
	private long currentOffset;
	private boolean isEndOfFile;


	/**
	 * Creates an instance of {@link Iterator}
	 * @param tabixReader
	 * @param tid
	 * @param beg
	 * @param end
	 * @param off
	 */
	public Iterator(final TabixReader tabixReader, final int tid, final int beg, final int end, final TPair64[] off) {
		i = -1;
		n_seeks = 0;
		currentOffset = 0;
		isEndOfFile = false;
		this.offsets = off;
		this.chromosomeIndex = tid;
		this.start = beg;
		this.stop = end;
		this.tabixReader = tabixReader;
		reader = new BufferedReader(new InputStreamReader(tabixReader.getmFp()));
	}


	/**
	 * @return the next element in the {@link TabixReader}
	 * @throws IOException
	 */
	public String next() throws IOException {
		if (isEndOfFile) {
			return null;
		}
		for (;;) {
			if ((currentOffset == 0) || !TabixReader.less64(currentOffset, offsets[i].getV())) { // then jump to the next chunk
				if (i == (offsets.length - 1)) {
					break; // no more chunks
				}
				if (i >= 0) {
					assert(currentOffset == offsets[i].getV()); // otherwise bug
				}
				if ((i < 0) || (offsets[i].getV() != offsets[i+1].getU())) { // not adjacent chunks; then seek
					tabixReader.getmFp().seek(offsets[i+1].getU());
					currentOffset = tabixReader.getmFp().getFilePointer();
					++n_seeks;
				}
				++i;
			}
			String s;
			if ((s = tabixReader.readLine(reader)) != null) {
				TIntv intv;
				char[] str = s.toCharArray();
				currentOffset += s.length() + 2;
				if ((str.length == 0) || (str[0] == tabixReader.getmMeta())) {
					continue;
				}
				intv = getIntv(s);
				if ((intv.getTid() != chromosomeIndex) || (intv.getBeg() >= stop)) {
					break; // no need to proceed
				} else if ((intv.getEnd() > start) && (intv.getBeg() < stop))
				{
					return s; // overlap; return
				}
			}
			else {
				break; // end of file
			}
		}
		isEndOfFile = true;
		return null;
	}


	private TIntv getIntv(final String s) {
		TIntv intv = new TIntv();
		int col = 0, end = 0, beg = 0;
		while (((end = s.indexOf('\t', beg)) >= 0) || (end == -1)) {
			++col;
			if (col == tabixReader.getmSc()) {
				intv.setTid(tabixReader.getChromosomeIndex(s.substring(beg, end)));
			} else if (col == tabixReader.getmBc()) {
				intv.setBeg(Integer.parseInt(s.substring(beg, end)));
				intv.setEnd(intv.getBeg());
				if ((tabixReader.getmPreset()&0x10000) != 0) {
					intv.setEnd(intv.getEnd() + 1);
					intv.incrementEnd();
				} else {
					intv.decrementBeg();
				}
				if (intv.getBeg() < 0) {
					intv.setBeg(0);
				}
				if (intv.getEnd() < 1) {
					intv.setEnd(1);
				}
			} else { // FIXME: SAM supports are not tested yet
				if ((tabixReader.getmPreset()&0xffff) == 0) { // generic
					if (col == tabixReader.getmEc()) {
						intv.setEnd(Integer.parseInt(s.substring(beg, end)));
					}
				} else if ((tabixReader.getmPreset()&0xffff) == 1) { // SAM
					if (col == 6) { // CIGAR
						@SuppressWarnings("unused")
						int l = 0, i, j;
						String cigar = s.substring(beg, end);
						for (i = j = 0; i < cigar.length(); ++i) {
							if (cigar.charAt(i) > '9') {
								int op = cigar.charAt(i);
								if ((op == 'M') || (op == 'D') || (op == 'N')) {
									l += Integer.parseInt(cigar.substring(j, i));
								}
							}
						}
						intv.setEnd(intv.getBeg() + 1);
					}
				} else if ((tabixReader.getmPreset()&0xffff) == 2) { // VCF
					String alt;
					alt = end >= 0? s.substring(beg, end) : s.substring(beg);
					if (col == 4) { // REF
						if (alt.length() > 0) {
							intv.setEnd(intv.getBeg() + alt.length());
						}
					} else if (col == 8) { // INFO
						int e_off = -1, i = alt.indexOf("END=");
						if (i == 0) {
							e_off = 4;
						}
						else if (i > 0) {
							i = alt.indexOf(";END=");
							if (i >= 0) {
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


	/**
	 * Show all class parameters.
	 */
	public void show () {
		String info = "";
		info += "i: " + i + "\n";
		info += "n_seeks: " + n_seeks + "\n";
		info += "tid: " + chromosomeIndex + "\n";
		info += "beg: " + start + "\n";
		info += "end: " + stop + "\n";
		info += "off: ";
		for (TPair64 pair: offsets) {
			info += pair.getDescription() + "; ";
		}
		info += "\n";
		info += "curr_off: " + currentOffset + "\n";
		info += "iseof: " + isEndOfFile + "\n";
		System.out.println(info);
	}

}
