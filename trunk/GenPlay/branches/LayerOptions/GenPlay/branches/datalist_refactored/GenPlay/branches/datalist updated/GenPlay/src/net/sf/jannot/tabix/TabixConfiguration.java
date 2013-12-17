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

/**
 * This class gathers parameters to configure tabix.
 * 
 * @author tarkvara
 * @author Nicolas Fourel (formatting)
 */
public class TabixConfiguration {
	
	/** Generic preset */
	public static final int TI_PRESET_GENERIC = 0;
	/** Preset for SAM files */
	public static final int TI_PRESET_SAM = 1;
	/** Preset for VCF files */
	public static final int TI_PRESET_VCF = 2;
	/** Preset for UCSC flag files */
	public static final int TI_FLAG_UCSC = 0x10000;

	/** Tabix configuration for GFF files */
	public static final TabixConfiguration GFF_CONF = new TabixConfiguration(0, 1, 4, 5, '#', 0);
	/** Tabix configuration for BED files */
	public static final TabixConfiguration BED_CONF = new TabixConfiguration(TI_FLAG_UCSC, 1, 2, 3, '#', 0);
	/** Tabix configuration for PSL files */
	public static final TabixConfiguration PSLTBL_CONF = new TabixConfiguration(TI_FLAG_UCSC, 15, 17, 18, '#', 0);
	/** Tabix configuration for SAM files */
	public static final TabixConfiguration SAM_CONF = new TabixConfiguration(TI_PRESET_SAM, 3, 4, 0, '@', 0);
	/** Tabix configuration for VCF files */
	public static final TabixConfiguration VCF_CONF = new TabixConfiguration(TI_PRESET_VCF, 1, 2, 0, '#', 0);
	
	
	private final int preset;			// the tabix preset
	private final int chrColumn;		// the column for the chromosome
	private final int startColumn;		// the start column
	private final int endColumn;		// the stop column
	private final char commentChar;		// the character for comments
	private final int linesToSkip;		// the number of lines to skip

	
	/**
	 * @param preset		the tabix preset
	 * @param chrColumn		the column for the chromosome
	 * @param startColumn	the start column
	 * @param endColumn		the stop column
	 * @param commentChar	the character for comments
	 * @param linesToSkip	the number of lines to skip
	 */
	public TabixConfiguration(int preset, int chrColumn, int startColumn, int endColumn, char commentChar, int linesToSkip) {
		this.preset = preset;
		this.chrColumn = chrColumn;
		this.startColumn = startColumn;
		this.endColumn = endColumn;
		this.commentChar = commentChar;
		this.linesToSkip = linesToSkip;
	}


	/**
	 * @return the preset
	 */
	public int getPreset() {
		return preset;
	}


	/**
	 * @return the chrColumn
	 */
	public int getChrColumn() {
		return chrColumn;
	}


	/**
	 * @return the startColumn
	 */
	public int getStartColumn() {
		return startColumn;
	}


	/**
	 * @return the endColumn
	 */
	public int getEndColumn() {
		return endColumn;
	}


	/**
	 * @return the commentChar
	 */
	public char getCommentChar() {
		return commentChar;
	}


	/**
	 * @return the linesToSkip
	 */
	public int getLinesToSkip() {
		return linesToSkip;
	}
	
}
