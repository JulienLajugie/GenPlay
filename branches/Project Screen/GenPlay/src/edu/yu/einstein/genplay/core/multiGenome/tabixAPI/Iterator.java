package edu.yu.einstein.genplay.core.multiGenome.tabixAPI;

import java.io.IOException;

public class Iterator {
	
	private TabixReader tabixReader;
	private int i, n_seeks;
	private int tid, beg, end;
	private TPair64[] off;
	private long curr_off;
	private boolean iseof;

	
	public Iterator(final TabixReader tabixReader, final int _tid, final int _beg, final int _end, final TPair64[] _off) {
		i = -1;
		n_seeks = 0;
		curr_off = 0;
		iseof = false;
		off = _off;
		tid = _tid;
		beg = _beg;
		end = _end;
		this.tabixReader = tabixReader;
	}

	
	public String next() throws IOException {
		if (iseof) return null;
		for (;;) {
			if (curr_off == 0 || !TabixReader.less64(curr_off, off[i].v)) { // then jump to the next chunk
				if (i == off.length - 1) break; // no more chunks
				if (i >= 0) assert(curr_off == off[i].v); // otherwise bug
				if (i < 0 || off[i].v != off[i+1].u) { // not adjacent chunks; then seek
					tabixReader.getmFp().seek(off[i+1].u);
					curr_off = tabixReader.getmFp().getFilePointer();
					++n_seeks;
				}
				++i;
			}
			String s;
			if ((s = TabixReader.readLine(tabixReader.getmFp())) != null) {
				TIntv intv;
				char[] str = s.toCharArray();
				curr_off = tabixReader.getmFp().getFilePointer();
				if (str.length == 0 || str[0] == tabixReader.getmMeta()) continue;
				intv = getIntv(s);
				if (intv.tid != tid || intv.beg >= end) break; // no need to proceed
				else if (intv.end > beg && intv.beg < end) return s; // overlap; return
			} else break; // end of file
		}
		iseof = true;
		return null;
	}
	
	
	private TIntv getIntv(final String s) {
		TIntv intv = new TIntv();
		int col = 0, end = 0, beg = 0;
		while ((end = s.indexOf('\t', beg)) >= 0 || end == -1) {
			++col;
			if (col == tabixReader.getmSc()) {
				intv.tid = tabixReader.chr2tid(s.substring(beg, end));
			} else if (col == tabixReader.getmBc()) {
				intv.beg = intv.end = Integer.parseInt(s.substring(beg, end));
				if ((tabixReader.getmPreset()&0x10000) != 0) ++intv.end;
				else --intv.beg;
				if (intv.beg < 0) intv.beg = 0;
				if (intv.end < 1) intv.end = 1;
			} else { // FIXME: SAM supports are not tested yet
				if ((tabixReader.getmPreset()&0xffff) == 0) { // generic
					if (col == tabixReader.getmEc())
						intv.end = Integer.parseInt(s.substring(beg, end));
				} else if ((tabixReader.getmPreset()&0xffff) == 1) { // SAM
					if (col == 6) { // CIGAR
						int l = 0, i, j;
						String cigar = s.substring(beg, end);
						for (i = j = 0; i < cigar.length(); ++i) {
							if (cigar.charAt(i) > '9') {
								int op = cigar.charAt(i);
								if (op == 'M' || op == 'D' || op == 'N')
									l += Integer.parseInt(cigar.substring(j, i));
							}
						}
						intv.end = intv.beg + l;
					}
				} else if ((tabixReader.getmPreset()&0xffff) == 2) { // VCF
					String alt;
					alt = end >= 0? s.substring(beg, end) : s.substring(beg);
					if (col == 4) { // REF
						if (alt.length() > 0) intv.end = intv.beg + alt.length();
					} else if (col == 8) { // INFO
						int e_off = -1, i = alt.indexOf("END=");
						if (i == 0) e_off = 4;
						else if (i > 0) {
							i = alt.indexOf(";END=");
							if (i >= 0) e_off = i + 5;
						}
						if (e_off > 0) {
							i = alt.indexOf(";", e_off);
							intv.end = Integer.parseInt(i > e_off? alt.substring(e_off, i) : alt.substring(e_off));
						}
					}
				}
			}
			if (end == -1) break;
			beg = end + 1;
		}
		return intv;
	}
	
};