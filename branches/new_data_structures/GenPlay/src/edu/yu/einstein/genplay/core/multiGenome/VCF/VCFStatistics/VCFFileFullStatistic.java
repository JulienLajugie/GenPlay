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
import java.util.HashMap;
import java.util.Map;

import edu.yu.einstein.genplay.util.NumberFormats;



/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFFileFullStatistic implements Serializable, VCFFileStatistics {

	/** Default generated serial version ID */
	private static final long serialVersionUID = -1037070449560631967L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	// Number of lines and columns
	private static final int LINE_NUMBER				= 8;		// Number of lines in the data object
	private static final int COLUMN_NUMBER				= 4;		// Number of columns in the data object

	// Column indexes
	private static final int SECTION_INDEX				= 0;		// Index for the section column
	private static final int NUMBER_INDEX 				= 1;		// Index for the number column
	private static final int PERCENTAGE_SECTION_INDEX 	= 2;		// Index for the section percentage column
	private static final int PERCENTAGE_TOTAL_INDEX 	= 3;		// Index for the total percentage column

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
	private static final String NUMBER_NAME 				= "Number";						// Name for the number column
	private static final String PERCENTAGE_SECTION_NAME 	= "% on the section type";		// Name for the section percentage column
	private static final String PERCENTAGE_TOTAL_NAME 		= "% on the whole genome";		// Name for the total percentage column

	// Line names
	private static final String LINE_NAME					= "Line";			// Name for the line section
	private static final String SNP_NAME 					= "SNP";			// Name for the SNP section
	private static final String INSERTION_NAME 				= "Insertion";		// Name for the Insertion section
	private static final String INSERTION_INDEL_NAME 		= "   Short (indels)";		// Name for the Insertion indels sub-section
	private static final String INSERTION_SV_NAME 			= "   Long (SV)";			// Name for the Insertion SV sub-section
	private static final String DELETION_NAME 				= "Deletion";		// Name for the Deletion section
	private static final String DELETION_INDEL_NAME 		= "   Short (indels)";		// Name for the Deletion indels sub-section
	private static final String DELETION_SV_NAME 			= "   Long (SV)";			// Name for the Deletion SV sub-section


	/**
	 * @param number an int
	 * @return the local formatted value of the given int
	 */
	protected static String getNumberFormat (Object o) {
		Integer number = null;
		try {
			number = Integer.parseInt(o.toString());
		} catch (Exception e) {
			return o.toString();
		}
		return NumberFormats.getPositionFormat().format(number);
	}
	private Object[][] data;
	private String[][] dataDisplay;

	private Map<String, VCFSampleStatistics> genomeStatistics;
	private int numberOfSNPs;
	private int numberOfShortInsertions;
	private int numberOfLongInsertions;
	private int numberOfShortDeletions;

	private int numberOfLongDeletions;


	private int numberOfLines;


	/**
	 * Constructor of {@link VCFFileFullStatistic}
	 */
	public VCFFileFullStatistic () {
		genomeStatistics = new HashMap<String, VCFSampleStatistics>();

		numberOfSNPs = 0;
		numberOfShortInsertions = 0;
		numberOfLongInsertions = 0;
		numberOfShortDeletions = 0;
		numberOfLongDeletions = 0;

		numberOfLines = 0;

		data = null;
		dataDisplay = null;
	}


	@Override
	public void addGenomeName (String genomeName) {
		if (!genomeStatistics.containsKey(genomeName)) {
			genomeStatistics.put(genomeName, new VCFSampleFullStatistic());
		}
	}


	/**
	 * Format the data for display purposes to the dataDisplay attribute.
	 */
	private void formatData () {
		if (data != null) {
			dataDisplay = new String[LINE_NUMBER][COLUMN_NUMBER];

			for (int row = 0; row < LINE_NUMBER; row++) {
				for (int col = 0; col < COLUMN_NUMBER; col++) {
					if (col == SECTION_INDEX) {
						dataDisplay[row][col] = data[row][col].toString();
					} else {
						dataDisplay[row][col] = getNumberFormat(data[row][col]);
					}
				}
			}
		}
	}


	@Override
	public String[] getColumnNamesForData () {
		String[] columnNames = {SECTION_NAME, NUMBER_NAME, PERCENTAGE_SECTION_NAME, PERCENTAGE_TOTAL_NAME};
		return columnNames;
	}


	@Override
	public Object[][] getData() {
		return data;
	}


	@Override
	public int getDataInt (int indexLine) {
		return getDataInt(indexLine, NUMBER_INDEX);
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
	public String[][] getDisplayData() {
		return dataDisplay;
	}


	@Override
	public String getFullString () {
		String info = getString();
		for (String sample: genomeStatistics.keySet()) {
			info += "\nGenome Statistics \"" + sample + "\":\n" +  genomeStatistics.get(sample).getString();
		}
		return info;
	}


	@Override
	public Map<String, VCFSampleStatistics> getGenomeStatistics() {
		return genomeStatistics;
	}


	/**
	 * @param value	the value
	 * @param total	the total
	 * @return		the percentage between the value and its total, 0 otherwise
	 */
	private int getPercentage (int value, int total) {
		int result = 0;
		if ((total == 0) && (value == total)) {
			result = 100;
		} else {
			try {
				result = (value * 100) / total;
			} catch (Exception e) {}
		}
		return result;
	}


	@Override
	public VCFSampleStatistics getSampleStatistics (String sample) {
		return genomeStatistics.get(sample);
	}


	@Override
	public String getString() {
		String info = "";
		info += "File Statistics:\n";
		info += SECTION_NAME + "\t" + NUMBER_NAME + "\t" + PERCENTAGE_SECTION_NAME +  "\t" + PERCENTAGE_TOTAL_NAME + "\n";
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
	public void incrementNumberOfLines() {
		numberOfLines++;
	}


	@Override
	public void incrementNumberOfLongDeletions() {
		numberOfLongDeletions++;
	}


	@Override
	public void incrementNumberOfLongInsertions() {
		numberOfLongInsertions++;
	}


	@Override
	public void incrementNumberOfShortDeletions() {
		numberOfShortDeletions++;
	}


	@Override
	public void incrementNumberOfShortInsertions() {
		numberOfShortInsertions++;
	}


	@Override
	public void incrementNumberOfSNPs() {
		numberOfSNPs++;
	}


	@Override
	public void processStatistics () {
		if (data == null) {
			data = new Object[LINE_NUMBER][COLUMN_NUMBER];

			data[LINE_INDEX][SECTION_INDEX] = LINE_NAME;
			data[SNP_INDEX][SECTION_INDEX] = SNP_NAME;
			data[INSERTION_INDEX][SECTION_INDEX] = INSERTION_NAME;
			data[INSERTION_INDEL_INDEX][SECTION_INDEX] = INSERTION_INDEL_NAME;
			data[INSERTION_SV_INDEX][SECTION_INDEX] = INSERTION_SV_NAME;
			data[DELETION_INDEX][SECTION_INDEX] = DELETION_NAME;
			data[DELETION_INDEL_INDEX][SECTION_INDEX] = DELETION_INDEL_NAME;
			data[DELETION_SV_INDEX][SECTION_INDEX] = DELETION_SV_NAME;

			data[LINE_INDEX][NUMBER_INDEX] = numberOfLines;
			data[SNP_INDEX][NUMBER_INDEX] = numberOfSNPs;
			data[INSERTION_INDEX][NUMBER_INDEX] = numberOfShortInsertions + numberOfLongInsertions;
			data[INSERTION_INDEL_INDEX][NUMBER_INDEX] = numberOfShortInsertions;
			data[INSERTION_SV_INDEX][NUMBER_INDEX] = numberOfLongInsertions;
			data[DELETION_INDEX][NUMBER_INDEX] = numberOfShortDeletions + numberOfLongDeletions;
			data[DELETION_INDEL_INDEX][NUMBER_INDEX] = numberOfShortDeletions;
			data[DELETION_SV_INDEX][NUMBER_INDEX] = numberOfLongDeletions;

			data[LINE_INDEX][PERCENTAGE_SECTION_INDEX] = "-";
			data[SNP_INDEX][PERCENTAGE_SECTION_INDEX] = "100";
			data[INSERTION_INDEX][PERCENTAGE_SECTION_INDEX] = "100";
			data[INSERTION_INDEL_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(INSERTION_INDEL_INDEX), getDataInt(INSERTION_INDEX));
			data[INSERTION_SV_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(INSERTION_SV_INDEX), getDataInt(INSERTION_INDEX));
			data[DELETION_INDEX][PERCENTAGE_SECTION_INDEX] = "100";
			data[DELETION_INDEL_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(DELETION_INDEL_INDEX), getDataInt(DELETION_INDEX));
			data[DELETION_SV_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(DELETION_SV_INDEX), getDataInt(DELETION_INDEX));

			int totalVariation = getDataInt(SNP_INDEX) + getDataInt(INSERTION_INDEX) + getDataInt(DELETION_INDEX);
			data[LINE_INDEX][PERCENTAGE_TOTAL_INDEX] = "-";
			data[SNP_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(SNP_INDEX), totalVariation);
			data[INSERTION_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(INSERTION_INDEX), totalVariation);
			data[INSERTION_INDEL_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(INSERTION_INDEL_INDEX), totalVariation);
			data[INSERTION_SV_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(INSERTION_SV_INDEX), totalVariation);
			data[DELETION_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(DELETION_INDEX), totalVariation);
			data[DELETION_INDEL_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(DELETION_INDEL_INDEX), totalVariation);
			data[DELETION_SV_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(DELETION_SV_INDEX), totalVariation);
		}
		formatData();
		for (VCFSampleStatistics sampleStatistics: genomeStatistics.values()) {
			sampleStatistics.processStatistics();
		}
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
		dataDisplay = (String[][]) in.readObject();
		genomeStatistics = (Map<String, VCFSampleStatistics>) in.readObject();
	}


	@Override
	public void show () {
		String info = "";
		info += "File Statistics\n";
		info += SECTION_NAME + "\t" + NUMBER_NAME + "\t" + PERCENTAGE_SECTION_NAME +  "\t" + PERCENTAGE_TOTAL_NAME + "\n";
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


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);

		out.writeObject(data);
		out.writeObject(dataDisplay);
		out.writeObject(genomeStatistics);
	}
}
