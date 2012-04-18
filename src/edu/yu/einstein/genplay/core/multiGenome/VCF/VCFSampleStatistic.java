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
package edu.yu.einstein.genplay.core.multiGenome.VCF;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFSampleStatistic implements Serializable {

	/** Default generated serial version ID */
	private static final long serialVersionUID = -1037070449560631967L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version	

	// Number of lines and columns
	private static final int LINE_NUMBER				= 13;		// Number of lines in the data object
	private static final int COLUMN_NUMBER				= 4;		// Number of columns in the data object

	// Column indexes
	private static final int SECTION_INDEX				= 0;		// Index for the section column
	private static final int NUMBER_INDEX 				= 1;		// Index for the number column
	private static final int PERCENTAGE_SECTION_INDEX 	= 2;		// Index for the section percentage column
	private static final int PERCENTAGE_TOTAL_INDEX 	= 3;		// Index for the total percentage column

	// Line indexes
	private static final int SNP_INDEX 						= 0;		// Index for the SNP section
	private static final int SNP_HOMOZYGOTE_INDEX 			= 1;		// Index for the SNP homozygote sub-section
	private static final int SNP_HETEROZYGOTE_INDEX 		= 2;		// Index for the SNP heterozygote sub-section
	private static final int INSERTION_INDEX 				= 3;		// Index for the Insertion section
	private static final int INSERTION_INDEL_INDEX 			= 4;		// Index for the Insertion indels sub-section
	private static final int INSERTION_SV_INDEX 			= 5;		// Index for the Insertion SV sub-section
	private static final int INSERTION_HOMOZYGOTE_INDEX 	= 6;		// Index for the Insertion homozygote sub-section
	private static final int INSERTION_HETEROZYGOTE_INDEX 	= 7;		// Index for the Insertion heterozygote sub-section
	private static final int DELETION_INDEX 				= 8;		// Index for the Deletion section
	private static final int DELETION_INDEL_INDEX 			= 9;		// Index for the Deletion indels sub-section
	private static final int DELETION_SV_INDEX 				= 10;		// Index for the Deletion SV sub-section
	private static final int DELETION_HOMOZYGOTE_INDEX 		= 11;		// Index for the Deletion homozygote sub-section
	private static final int DELETION_HETEROZYGOTE_INDEX 	= 12;		// Index for the Deletion heterozygote sub-section

	// Column names
	private static final String SECTION_NAME				= "";							// Name for the section column
	private static final String NUMBER_NAME 				= "Number";							// Name for the number column
	private static final String PERCENTAGE_SECTION_NAME 	= "% on the variation type";	// Name for the section percentage column
	private static final String PERCENTAGE_TOTAL_NAME 		= "% on the whole genome";		// Name for the total percentage column

	// Line names
	private static final String SNP_NAME 						= "SNP";				// Name for the SNP section
	private static final String SNP_HOMOZYGOTE_NAME 			= "- Homozygote";		// Name for the SNP homozygote sub-section
	private static final String SNP_HETEROZYGOTE_NAME 			= "- Heterozygote";		// Name for the SNP heterozygote sub-section
	private static final String INSERTION_NAME 					= "Insertion";			// Name for the Insertion section
	private static final String INSERTION_INDEL_NAME 			= "- Indel";			// Name for the Insertion indels sub-section
	private static final String INSERTION_SV_NAME 				= "- SV";				// Name for the Insertion SV sub-section
	private static final String INSERTION_HOMOZYGOTE_NAME 		= "- Homozygote";		// Name for the Insertion homozygote sub-section
	private static final String INSERTION_HETEROZYGOTE_NAME 	= "- Heterozygote";		// Name for the Insertion heterozygote sub-section
	private static final String DELETION_NAME 					= "Deletion";			// Name for the Deletion section
	private static final String DELETION_INDEL_NAME 			= "- Indel";			// Name for the Deletion indels sub-section
	private static final String DELETION_SV_NAME 				= "- SV";				// Name for the Deletion SV sub-section
	private static final String DELETION_HOMOZYGOTE_NAME 		= "- Homozygote";		// Name for the Deletion homozygote sub-section
	private static final String DELETION_HETEROZYGOTE_NAME 		= "- Heterozygote";		// Name for the Deletion heterozygote sub-section

	private Object[][] data;

	private int numberOfSNPs;
	private int numberOfShortInsertions;
	private int numberOfLongInsertions;
	private int numberOfShortDeletions;
	private int numberOfLongDeletions;

	private int numberOfHomozygoteSNPs;
	private int numberOfHomozygoteInsertions;
	private int numberOfHomozygoteDeletions;
	private int numberOfHeterozygoteSNPs;
	private int numberOfHeterozygoteInsertions;
	private int numberOfHeterozygoteDeletions;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);

		out.writeObject(data);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();

		data = (Object[][]) in.readObject();
	}


	/**
	 * Constructor of {@link VCFSampleStatistic}
	 */
	protected VCFSampleStatistic () {
		numberOfSNPs = 0;
		numberOfShortInsertions = 0;
		numberOfLongInsertions = 0;
		numberOfShortDeletions = 0;
		numberOfLongDeletions = 0;

		numberOfHomozygoteSNPs = 0;
		numberOfHomozygoteInsertions = 0;
		numberOfHomozygoteDeletions = 0;
		numberOfHeterozygoteSNPs = 0;
		numberOfHeterozygoteInsertions = 0;
		numberOfHeterozygoteDeletions = 0;
	}


	/**
	 * @return the array of column names for the data array
	 */
	public String[] getColumnNamesForData () {
		String[] columnNames = {SECTION_NAME, NUMBER_NAME, PERCENTAGE_SECTION_NAME, PERCENTAGE_TOTAL_NAME};
		return columnNames;
	}


	/**
	 * Processes all statistics
	 */
	public void processStatistics () {
		if (data == null) {
			data = new Object[LINE_NUMBER][COLUMN_NUMBER];

			data[SNP_INDEX][SECTION_INDEX] = SNP_NAME;
			data[SNP_HOMOZYGOTE_INDEX][SECTION_INDEX] = SNP_HOMOZYGOTE_NAME;
			data[SNP_HETEROZYGOTE_INDEX][SECTION_INDEX] = SNP_HETEROZYGOTE_NAME;
			data[INSERTION_INDEX][SECTION_INDEX] = INSERTION_NAME;
			data[INSERTION_INDEL_INDEX][SECTION_INDEX] = INSERTION_INDEL_NAME;
			data[INSERTION_SV_INDEX][SECTION_INDEX] = INSERTION_SV_NAME;
			data[INSERTION_HOMOZYGOTE_INDEX][SECTION_INDEX] = INSERTION_HOMOZYGOTE_NAME;
			data[INSERTION_HETEROZYGOTE_INDEX][SECTION_INDEX] = INSERTION_HETEROZYGOTE_NAME;
			data[DELETION_INDEX][SECTION_INDEX] = DELETION_NAME;
			data[DELETION_INDEL_INDEX][SECTION_INDEX] = DELETION_INDEL_NAME;
			data[DELETION_SV_INDEX][SECTION_INDEX] = DELETION_SV_NAME;
			data[DELETION_HOMOZYGOTE_INDEX][SECTION_INDEX] = DELETION_HOMOZYGOTE_NAME;
			data[DELETION_HETEROZYGOTE_INDEX][SECTION_INDEX] = DELETION_HETEROZYGOTE_NAME;

			data[SNP_INDEX][NUMBER_INDEX] = numberOfSNPs;
			data[SNP_HOMOZYGOTE_INDEX][NUMBER_INDEX] = numberOfHomozygoteSNPs;
			data[SNP_HETEROZYGOTE_INDEX][NUMBER_INDEX] = numberOfHeterozygoteSNPs;
			data[INSERTION_INDEX][NUMBER_INDEX] = numberOfShortInsertions + numberOfLongInsertions;
			data[INSERTION_INDEL_INDEX][NUMBER_INDEX] = numberOfShortInsertions;
			data[INSERTION_SV_INDEX][NUMBER_INDEX] = numberOfLongInsertions;
			data[INSERTION_HOMOZYGOTE_INDEX][NUMBER_INDEX] = numberOfHomozygoteInsertions;
			data[INSERTION_HETEROZYGOTE_INDEX][NUMBER_INDEX] = numberOfHeterozygoteInsertions;
			data[DELETION_INDEX][NUMBER_INDEX] = numberOfShortDeletions + numberOfLongDeletions;
			data[DELETION_INDEL_INDEX][NUMBER_INDEX] = numberOfShortDeletions;
			data[DELETION_SV_INDEX][NUMBER_INDEX] = numberOfLongDeletions;
			data[DELETION_HOMOZYGOTE_INDEX][NUMBER_INDEX] = numberOfHomozygoteDeletions;
			data[DELETION_HETEROZYGOTE_INDEX][NUMBER_INDEX] = numberOfHeterozygoteDeletions;

			data[SNP_INDEX][PERCENTAGE_SECTION_INDEX] = "100";
			data[SNP_HOMOZYGOTE_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(SNP_HOMOZYGOTE_INDEX), getDataInt(SNP_INDEX));
			data[SNP_HETEROZYGOTE_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(SNP_HETEROZYGOTE_INDEX), getDataInt(SNP_INDEX));
			data[INSERTION_INDEX][PERCENTAGE_SECTION_INDEX] = "100";
			data[INSERTION_INDEL_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(INSERTION_INDEL_INDEX), getDataInt(INSERTION_INDEX));
			data[INSERTION_SV_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(INSERTION_SV_INDEX), getDataInt(INSERTION_INDEX));
			data[INSERTION_HOMOZYGOTE_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(INSERTION_HOMOZYGOTE_INDEX), getDataInt(INSERTION_INDEX));
			data[INSERTION_HETEROZYGOTE_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(INSERTION_HETEROZYGOTE_INDEX), getDataInt(INSERTION_INDEX));
			data[DELETION_INDEX][PERCENTAGE_SECTION_INDEX] = "100";
			data[DELETION_INDEL_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(DELETION_INDEL_INDEX), getDataInt(DELETION_INDEX));
			data[DELETION_SV_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(DELETION_SV_INDEX), getDataInt(DELETION_INDEX));
			data[DELETION_HOMOZYGOTE_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(DELETION_HOMOZYGOTE_INDEX), getDataInt(DELETION_INDEX));
			data[DELETION_HETEROZYGOTE_INDEX][PERCENTAGE_SECTION_INDEX] = getPercentage(getDataInt(DELETION_HETEROZYGOTE_INDEX), getDataInt(DELETION_INDEX));

			int totalVariation = getDataInt(SNP_INDEX) + getDataInt(INSERTION_INDEX) + getDataInt(DELETION_INDEX);
			data[SNP_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(SNP_INDEX), totalVariation);
			data[SNP_HOMOZYGOTE_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(SNP_HOMOZYGOTE_INDEX), totalVariation);
			data[SNP_HETEROZYGOTE_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(SNP_HETEROZYGOTE_INDEX), totalVariation);
			data[INSERTION_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(INSERTION_INDEX), totalVariation);
			data[INSERTION_INDEL_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(INSERTION_INDEL_INDEX), totalVariation);
			data[INSERTION_SV_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(INSERTION_SV_INDEX), totalVariation);
			data[INSERTION_HOMOZYGOTE_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(INSERTION_HOMOZYGOTE_INDEX), totalVariation);
			data[INSERTION_HETEROZYGOTE_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(INSERTION_HETEROZYGOTE_INDEX) , totalVariation);
			data[DELETION_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(DELETION_INDEX), totalVariation);
			data[DELETION_INDEL_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(DELETION_INDEL_INDEX), totalVariation);
			data[DELETION_SV_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(DELETION_SV_INDEX), totalVariation);
			data[DELETION_HOMOZYGOTE_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(DELETION_HOMOZYGOTE_INDEX) , totalVariation);
			data[DELETION_HETEROZYGOTE_INDEX][PERCENTAGE_TOTAL_INDEX] = getPercentage(getDataInt(DELETION_HETEROZYGOTE_INDEX) , totalVariation);
		}
	}


	/**
	 * @param value	the value
	 * @param total	the total
	 * @return		the percentage between the value and its total, 0 otherwise
	 */
	private int getPercentage (int value, int total) {
		int result = 0;
		if (total == 0 && value == total) {
			result = 100;
		} else {
			try {
				result = value * 100 / total;
			} catch (Exception e) {}
		}
		return result;
	}


	/**
	 * @param indexLine index of a line
	 * @return			the integer located in the column containing the number, -1 otherwise
	 */
	private int getDataInt (int indexLine) {
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


	/**
	 * @return the data
	 */
	public Object[][] getData() {
		return data;
	}


	/**
	 * increment the numberOfSNPs
	 */
	public void incrementNumberOfSNPs() {
		this.numberOfSNPs++;
	}


	/**
	 * increment the numberOfShortInsertions
	 */
	public void incrementNumberOfShortInsertions() {
		this.numberOfShortInsertions++;
	}


	/**
	 * increment the numberOfLongInsertions
	 */
	public void incrementNumberOfLongInsertions() {
		this.numberOfLongInsertions++;
	}


	/**
	 * increment the numberOfShortDeletions
	 */
	public void incrementNumberOfShortDeletions() {
		this.numberOfShortDeletions++;
	}


	/**
	 * increment the numberOfLongDeletions
	 */
	public void incrementNumberOfLongDeletions() {
		this.numberOfLongDeletions++;
	}


	/**
	 * increment the numberOfHomozygoteSNPs
	 */
	public void incrementNumberOfHomozygoteSNPs() {
		this.numberOfHomozygoteSNPs++;
	}


	/**
	 * increment the numberOfHomozygoteInsertions
	 */
	public void incrementNumberOfHomozygoteInsertions() {
		this.numberOfHomozygoteInsertions++;
	}


	/**
	 * increment the numberOfHomozygoteDeletions
	 */
	public void incrementNumberOfHomozygoteDeletions() {
		this.numberOfHomozygoteDeletions++;
	}


	/**
	 * increment the numberOfHeterozygoteSNPs
	 */
	public void incrementNumberOfHeterozygoteSNPs() {
		this.numberOfHeterozygoteSNPs++;
	}


	/**
	 * increment the numberOfHeterozygoteInsertions
	 */
	public void incrementNumberOfHeterozygoteInsertions() {
		this.numberOfHeterozygoteInsertions++;
	}


	/**
	 * increment the numberOfHeterozygoteDeletions
	 */
	public void incrementNumberOfHeterozygoteDeletions() {
		this.numberOfHeterozygoteDeletions++;
	}


	/**
	 * Shows all statistics
	 */
	public void show () {
		String info = "";
		info += SECTION_NAME + "\t" + NUMBER_NAME + "\t" + PERCENTAGE_SECTION_NAME +  "\t" + PERCENTAGE_TOTAL_NAME + "\n";
		for (int i = 0; i < LINE_NUMBER; i++) {
			for (int j = 0; j < COLUMN_NUMBER; j++) {
				info += data[i][j];
				if (j < COLUMN_NUMBER - 1) {
					info += "\t";
				}
			}
			info += "\n";
		}
		System.out.println(info);
	}

}
