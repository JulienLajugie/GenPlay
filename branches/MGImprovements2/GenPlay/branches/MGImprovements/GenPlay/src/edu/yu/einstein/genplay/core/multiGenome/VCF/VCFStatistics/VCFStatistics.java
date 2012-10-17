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


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public interface VCFStatistics {

	/**
	 * @return the array of column names for the data array
	 */
	public String[] getColumnNamesForData ();


	/**
	 * Processes all statistics
	 */
	public void processStatistics ();


	/**
	 * @return the data
	 */
	public Object[][] getData();


	/**
	 * @param indexLine index of a line
	 * @return			the integer located in the column containing the number, -1 otherwise
	 */
	int getDataInt (int indexLine);


	/**
	 * Shows all statistics
	 */
	public void show ();


	/**
	 * @return a String of the {@link VCFFileFullStatistic}
	 */
	public String getString();


	/**
	 * @return a String of the {@link VCFFileFullStatistic} including its {@link VCFSampleFullStatistic}
	 */
	public String getFullString ();

}
