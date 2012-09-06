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
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFFileMixStatistic implements Serializable, VCFFileStatistics {

	/** Default generated serial version ID */
	private static final long serialVersionUID = -1037070449560631967L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	// Number of lines and columns
	private static final int LINE_NUMBER				= 8;		// Number of lines in the data object
	private static final int COLUMN_NUMBER				= 4;		// Number of columns in the data object

	// Column indexes
	private static final int SECTION_INDEX				= 0;		// Index for the section column
	private static final int NATIVE_INDEX 				= 1;		// Index for the first number column
	private static final int NEW_INDEX 					= 2;		// Index for the second number column
	private static final int DIFF_INDEX 				= 3;		// Index for the difference number column

	// Line indexes
	private static final int LINE_INDEX					= 0;		// Index for the line section
	private static final int SNP_INDEX 					= 1;		// Index for the SNP section
	private static final int INSERTION_INDEX 			= 2;		// Index for the Insertion section
	private static final int INSERTION_INDEL_INDEX 		= 3;		// Index for the Insertion indels sub-section
	private static final int INSERTION_SV_INDEX 		= 4;		// Index for the Insertion SV sub-section
	private static final int DELETION_INDEX 			= 5;		// Index for the Deletion section
	private static final int DELETION_INDEL_INDEX 		= 6;		// Index for the Deletion indels sub-section
	private static final int DELETION_SV_INDEX 			= 7;		// Index for the Deletion SV sub-section

	// Column names
	private static final String SECTION_NAME				= "Sections";					// Name for the section column
	private static final String FIRST_NAME 					= "From the file (A)";			// Name for the first number column
	private static final String SECOND_NAME 				= "From the track (B)";			// Name for the second number column
	private static final String DIFF_NAME 					= "A - B";						// Name for the difference number column

	// Line names
	private static final String LINE_NAME					= "Line";			// Name for the line section
	private static final String SNP_NAME 					= "SNP";			// Name for the SNP section
	private static final String INSERTION_NAME 				= "Insertion";		// Name for the Insertion section
	private static final String INSERTION_INDEL_NAME 		= "   Indel";		// Name for the Insertion indels sub-section
	private static final String INSERTION_SV_NAME 			= "   SV";			// Name for the Insertion SV sub-section
	private static final String DELETION_NAME 				= "Deletion";		// Name for the Deletion section
	private static final String DELETION_INDEL_NAME 		= "   Indel";		// Name for the Deletion indels sub-section
	private static final String DELETION_SV_NAME 			= "   SV";			// Name for the Deletion SV sub-section


	private final VCFFileStatistics firstStatistics;
	private final VCFFileStatistics secondStatistics;
	private boolean isValid;

	private Object[][] data;
	private Map<String, VCFSampleStatistics> genomeStatistics;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);

		out.writeObject(data);
		out.writeObject(genomeStatistics);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();

		data = (Object[][]) in.readObject();
		genomeStatistics = (Map<String, VCFSampleStatistics>) in.readObject();
	}


	/**
	 * Constructor of {@link VCFFileMixStatistic}
	 * @param firstStatistics	the first statistics to show
	 * @param secondStatistics 	the second statistics to show
	 */
	public VCFFileMixStatistic (VCFFileStatistics firstStatistics, VCFFileStatistics secondStatistics) {
		this.firstStatistics = firstStatistics;
		this.secondStatistics = secondStatistics;

		isValid = false;
		if ((this.firstStatistics != null) && (this.secondStatistics != null)) {
			if ((this.firstStatistics instanceof VCFFileFullStatistic) && (this.secondStatistics instanceof VCFFileFullStatistic)) {
				isValid = true;
				this.firstStatistics.processStatistics();
				this.secondStatistics.processStatistics();
			}
		}


		if (isValid) {
			genomeStatistics = new HashMap<String, VCFSampleStatistics>();

			List<String> firstSampleNames = new ArrayList<String>(firstStatistics.getGenomeStatistics().keySet());
			List<String> secondSampleNames = new ArrayList<String>(secondStatistics.getGenomeStatistics().keySet());

			for (String sampleName: firstSampleNames) {
				if (secondSampleNames.contains(sampleName)) {
					genomeStatistics.put(sampleName, new VCFSampleMixStatistic(firstStatistics.getSampleStatistics(sampleName), secondStatistics.getSampleStatistics(sampleName)));
				}
			}
		}

		data = null;
	}


	@Override
	public String[] getColumnNamesForData () {
		String[] columnNames = {SECTION_NAME, FIRST_NAME, SECOND_NAME, DIFF_NAME};
		return columnNames;
	}


	@Override
	public void processStatistics () {
		if ((data == null) && isValid) {
			data = new Object[LINE_NUMBER][COLUMN_NUMBER];

			data[LINE_INDEX][SECTION_INDEX] = LINE_NAME;
			data[SNP_INDEX][SECTION_INDEX] = SNP_NAME;
			data[INSERTION_INDEX][SECTION_INDEX] = INSERTION_NAME;
			data[INSERTION_INDEL_INDEX][SECTION_INDEX] = INSERTION_INDEL_NAME;
			data[INSERTION_SV_INDEX][SECTION_INDEX] = INSERTION_SV_NAME;
			data[DELETION_INDEX][SECTION_INDEX] = DELETION_NAME;
			data[DELETION_INDEL_INDEX][SECTION_INDEX] = DELETION_INDEL_NAME;
			data[DELETION_SV_INDEX][SECTION_INDEX] = DELETION_SV_NAME;

			data[LINE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(LINE_INDEX);
			data[SNP_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(SNP_INDEX);
			data[INSERTION_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_INDEX);
			data[INSERTION_INDEL_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_INDEL_INDEX);
			data[INSERTION_SV_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_SV_INDEX);
			data[DELETION_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_INDEX);
			data[DELETION_INDEL_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_INDEL_INDEX);
			data[DELETION_SV_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_SV_INDEX);

			data[LINE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(LINE_INDEX);
			data[SNP_INDEX][NEW_INDEX] = secondStatistics.getDataInt(SNP_INDEX);
			data[INSERTION_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_INDEX);
			data[INSERTION_INDEL_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_INDEL_INDEX);
			data[INSERTION_SV_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_SV_INDEX);
			data[DELETION_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_INDEX);
			data[DELETION_INDEL_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_INDEL_INDEX);
			data[DELETION_SV_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_SV_INDEX);

			for (int i = 0; i < LINE_NUMBER; i++) {
				data[i][DIFF_INDEX] = getDataInt(i, NATIVE_INDEX) - getDataInt(i, NEW_INDEX);
			}
		}
		for (VCFSampleStatistics sampleStatistics: genomeStatistics.values()) {
			sampleStatistics.processStatistics();
		}
	}


	@Override
	public int getDataInt (int indexLine) {
		return -1;
	}


	/**
	 * @param indexLine		index of a line
	 * @param indexColumn	index of a column
	 * @return				the associated integer, -1 otherwise
	 */
	private int getDataInt (int indexLine, int indexColumn) {
		int result = -1;
		try {
			result = Integer.parseInt(data[indexLine][indexColumn].toString());
		} catch (Exception e) {}
		return result;
	}


	@Override
	public Object[][] getData() {
		return data;
	}


	@Override
	public void addGenomeName (String genomeName) {}


	@Override
	public VCFSampleStatistics getSampleStatistics (String sample) {
		return genomeStatistics.get(sample);
	}


	@Override
	public Map<String, VCFSampleStatistics> getGenomeStatistics() {
		return genomeStatistics;
	}


	@Override
	public void incrementNumberOfSNPs() {}

	@Override
	public void incrementNumberOfShortInsertions() {}

	@Override
	public void incrementNumberOfLongInsertions() {}

	@Override
	public void incrementNumberOfShortDeletions() {}

	@Override
	public void incrementNumberOfLongDeletions() {}

	@Override
	public void incrementNumberOfLines() {}


	@Override
	public void show () {
		String info = "";
		info += "File Statistics\n";
		info += SECTION_NAME + "\t" + FIRST_NAME + "\t" + SECOND_NAME + "\t" + DIFF_NAME + "\n";
		for (int i = 0; i < LINE_NUMBER; i++) {
			for (int j = 0; j < COLUMN_NUMBER; j++) {
				info += data[i][j];
				if (j < (COLUMN_NUMBER - 1)) {
					info += "\t";
				}
			}
			info += "\n";
		}
		System.out.println("===== FILE Statistics");
		System.out.println(info);
		for (String sample: genomeStatistics.keySet()) {
			System.out.println("===== " + sample + " Statistics");
			genomeStatistics.get(sample).show();
		}
	}


	@Override
	public String getString() {
		String info = "";
		info += "File Statistics:\n";
		info += SECTION_NAME + "\t" + FIRST_NAME + "\t" + SECOND_NAME + "\t" + DIFF_NAME + "\n";
		for (int i = 0; i < LINE_NUMBER; i++) {
			for (int j = 0; j < COLUMN_NUMBER; j++) {
				info += data[i][j];
				if (j < (COLUMN_NUMBER - 1)) {
					info += "\t";
				}
			}
			if (i < (LINE_NUMBER - 1)) {
				info += "\n";
			}
		}
		return info;
	}


	@Override
	public String getFullString () {
		String info = getString();
		for (String sample: genomeStatistics.keySet()) {
			info += "\nGenome Statistics \"" + sample + "\":\n" +  genomeStatistics.get(sample).getString();
		}
		return info;
	}
}
