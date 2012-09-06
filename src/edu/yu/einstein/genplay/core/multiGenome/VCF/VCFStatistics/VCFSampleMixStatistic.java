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


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFSampleMixStatistic implements Serializable, VCFSampleStatistics {

	/** Default generated serial version ID */
	private static final long serialVersionUID = -1037070449560631967L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	// Number of lines and columns
	private static final int LINE_NUMBER				= 18;		// Number of lines in the data object
	private static final int COLUMN_NUMBER				= 4;		// Number of columns in the data object

	// Column indexes
	private static final int SECTION_INDEX				= 0;		// Index for the section column
	private static final int NATIVE_INDEX 				= 1;		// Index for the first number column
	private static final int NEW_INDEX 					= 2;		// Index for the second number column
	private static final int DIFF_INDEX 				= 3;		// Index for the difference number column

	// Line indexes
	private static final int VARIATION_INDEX 					= 0;
	private static final int SNP_VARIATION_INDEX 				= 1;
	private static final int INSERTION_VARIATION_INDEX 			= 2;
	private static final int INSERTION_INDEL_INDEX 				= 3;
	private static final int INSERTION_SV_INDEX 				= 4;
	private static final int DELETION_VARIATION_INDEX 			= 5;
	private static final int DELETION_INDEL_INDEX 				= 6;
	private static final int DELETION_SV_INDEX 					= 7;
	private static final int GENOTYPE_INDEX 					= 8;
	private static final int SNP_GENOTYPE_INDEX 				= 9;
	private static final int SNP_HOMOZYGOTE_INDEX 				= 10;
	private static final int SNP_HETEROZYGOTE_INDEX 			= 11;
	private static final int INSERTION_GENOTYPE_INDEX 			= 12;
	private static final int INSERTION_HOMOZYGOTE_INDEX 		= 13;
	private static final int INSERTION_HETEROZYGOTE_INDEX 		= 14;
	private static final int DELETION_GENOTYPE_INDEX 			= 15;
	private static final int DELETION_HOMOZYGOTE_INDEX 			= 16;
	private static final int DELETION_HETEROZYGOTE_INDEX 		= 17;

	// Column names
	private static final String SECTION_NAME				= "Sections";					// Name for the section column
	private static final String FIRST_NAME 					= "From the file (A)";			// Name for the first number column
	private static final String SECOND_NAME 				= "From the track (B)";			// Name for the second number column
	private static final String DIFF_NAME 					= "A - B";						// Name for the difference number column

	// Line names
	private static final String VARIATION_TITLE						= "Number of variations";
	private static final String GENOTYPE_TITLE						= "Genotype variations";
	private static final String SNP_LABEL							= "   SNP";
	private static final String INSERTION_LABEL						= "   Insertion";
	private static final String DELETION_LABEL						= "   Deletion";
	private static final String HOMOZYGOTE_LABEL					= "      Homozygote";
	private static final String HETEROZYGOTE_LABEL					= "      Heterozygote";
	private static final String INDEL_LABEL							= "      Indel";
	private static final String SV_LABEL							= "      SV";


	private final VCFSampleStatistics firstStatistics;
	private final VCFSampleStatistics secondStatistics;
	private boolean isValid;

	private Object[][] data;



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
	 * Constructor of {@link VCFSampleMixStatistic}
	 */
	protected VCFSampleMixStatistic (VCFSampleStatistics firstStatistics, VCFSampleStatistics secondStatistics) {
		this.firstStatistics = firstStatistics;
		this.secondStatistics = secondStatistics;

		isValid = false;
		if ((this.firstStatistics != null) && (this.secondStatistics != null)) {
			if ((this.firstStatistics instanceof VCFSampleFullStatistic) && (this.secondStatistics instanceof VCFSampleFullStatistic)) {
				isValid = true;
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

			data[VARIATION_INDEX][SECTION_INDEX] = VARIATION_TITLE;
			data[SNP_VARIATION_INDEX][SECTION_INDEX] = SNP_LABEL;
			data[INSERTION_VARIATION_INDEX][SECTION_INDEX] = INSERTION_LABEL;
			data[INSERTION_INDEL_INDEX][SECTION_INDEX] = INDEL_LABEL;
			data[INSERTION_SV_INDEX][SECTION_INDEX] = SV_LABEL;
			data[DELETION_VARIATION_INDEX][SECTION_INDEX] = DELETION_LABEL;
			data[DELETION_INDEL_INDEX][SECTION_INDEX] = INDEL_LABEL;
			data[DELETION_SV_INDEX][SECTION_INDEX] = SV_LABEL;
			data[GENOTYPE_INDEX][SECTION_INDEX] = GENOTYPE_TITLE;
			data[SNP_GENOTYPE_INDEX][SECTION_INDEX] = SNP_LABEL;
			data[SNP_HETEROZYGOTE_INDEX][SECTION_INDEX] = HETEROZYGOTE_LABEL;
			data[SNP_HOMOZYGOTE_INDEX][SECTION_INDEX] = HOMOZYGOTE_LABEL;
			data[INSERTION_GENOTYPE_INDEX][SECTION_INDEX] = INSERTION_LABEL;
			data[INSERTION_HETEROZYGOTE_INDEX][SECTION_INDEX] = HETEROZYGOTE_LABEL;
			data[INSERTION_HOMOZYGOTE_INDEX][SECTION_INDEX] = HOMOZYGOTE_LABEL;
			data[DELETION_GENOTYPE_INDEX][SECTION_INDEX] = DELETION_LABEL;
			data[DELETION_HETEROZYGOTE_INDEX][SECTION_INDEX] = HETEROZYGOTE_LABEL;
			data[DELETION_HOMOZYGOTE_INDEX][SECTION_INDEX] = HOMOZYGOTE_LABEL;

			data[VARIATION_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(VARIATION_INDEX);
			data[SNP_VARIATION_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(SNP_VARIATION_INDEX);
			data[INSERTION_VARIATION_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_VARIATION_INDEX);
			data[INSERTION_INDEL_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_INDEL_INDEX);
			data[INSERTION_SV_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_SV_INDEX);
			data[DELETION_VARIATION_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_VARIATION_INDEX);
			data[DELETION_INDEL_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_INDEL_INDEX);
			data[DELETION_SV_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_SV_INDEX);
			data[GENOTYPE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(GENOTYPE_INDEX);
			data[SNP_GENOTYPE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(SNP_GENOTYPE_INDEX);
			data[SNP_HETEROZYGOTE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(SNP_HETEROZYGOTE_INDEX);
			data[SNP_HOMOZYGOTE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(SNP_HOMOZYGOTE_INDEX);
			data[INSERTION_GENOTYPE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_GENOTYPE_INDEX);
			data[INSERTION_HETEROZYGOTE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_HETEROZYGOTE_INDEX);
			data[INSERTION_HOMOZYGOTE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(INSERTION_HOMOZYGOTE_INDEX);
			data[DELETION_GENOTYPE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_GENOTYPE_INDEX);
			data[DELETION_HETEROZYGOTE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_HETEROZYGOTE_INDEX);
			data[DELETION_HOMOZYGOTE_INDEX][NATIVE_INDEX] = firstStatistics.getDataInt(DELETION_HOMOZYGOTE_INDEX);

			data[VARIATION_INDEX][NEW_INDEX] = secondStatistics.getDataInt(VARIATION_INDEX);
			data[SNP_VARIATION_INDEX][NEW_INDEX] = secondStatistics.getDataInt(SNP_VARIATION_INDEX);
			data[INSERTION_VARIATION_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_VARIATION_INDEX);
			data[INSERTION_INDEL_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_INDEL_INDEX);
			data[INSERTION_SV_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_SV_INDEX);
			data[DELETION_VARIATION_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_VARIATION_INDEX);
			data[DELETION_INDEL_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_INDEL_INDEX);
			data[DELETION_SV_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_SV_INDEX);
			data[GENOTYPE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(GENOTYPE_INDEX);
			data[SNP_GENOTYPE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(SNP_GENOTYPE_INDEX);
			data[SNP_HETEROZYGOTE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(SNP_HETEROZYGOTE_INDEX);
			data[SNP_HOMOZYGOTE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(SNP_HOMOZYGOTE_INDEX);
			data[INSERTION_GENOTYPE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_GENOTYPE_INDEX);
			data[INSERTION_HETEROZYGOTE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_HETEROZYGOTE_INDEX);
			data[INSERTION_HOMOZYGOTE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(INSERTION_HOMOZYGOTE_INDEX);
			data[DELETION_GENOTYPE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_GENOTYPE_INDEX);
			data[DELETION_HETEROZYGOTE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_HETEROZYGOTE_INDEX);
			data[DELETION_HOMOZYGOTE_INDEX][NEW_INDEX] = secondStatistics.getDataInt(DELETION_HOMOZYGOTE_INDEX);

			for (int i = 0; i < LINE_NUMBER; i++) {
				data[i][DIFF_INDEX] = getDataInt(i, NATIVE_INDEX) - getDataInt(i, NEW_INDEX);
			}
		}
	}


	/**
	 * @param indexLine index of a line
	 * @return			the integer located in the column containing the number, -1 otherwise
	 */
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
	public void incrementNumberOfHomozygoteSNPs() {}

	@Override
	public void incrementNumberOfHomozygoteInsertions() {}

	@Override
	public void incrementNumberOfHomozygoteDeletions() {}

	@Override
	public void incrementNumberOfHeterozygoteSNPs() {}

	@Override
	public void incrementNumberOfHeterozygoteInsertions() {}

	@Override
	public void incrementNumberOfHeterozygoteDeletions() {}


	@Override
	public void show () {
		String info = "";
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
		System.out.println(info);
	}


	@Override
	public String getString () {
		String info = "";
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
	public String getFullString() {
		return "";
	}

}
