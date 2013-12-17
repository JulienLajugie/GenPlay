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
package edu.yu.einstein.genplay.gui.dialog.SCWListStatsDialog;

import javax.swing.table.AbstractTableModel;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListStats.SCWListStats;
import edu.yu.einstein.genplay.util.NumberFormats;

/**
 * Model for the table containing the stats of a {@link SCWList}
 * @author Julien Lajugie
 */
class SCWListStatsTableModel extends AbstractTableModel {

	/** Generated serial ID */
	private static final long serialVersionUID = 8964468609896421142L;

	/** Column headers */
	static final String[] COLUMN_HEADERS = {"", "Minimum", "Maximum", "Average", "Std Dev", "Window #", "Window Length", "Score Sum"};

	/* Index of the columns */
	private final int ROW_HEADER_INDEX = 0;
	private final int MINIMUM_COLUMN_INDEX = 1;
	private final int MAXIMUM_COLUMN_INDEX = 2;
	private final int AVERAGE_COLUMN_INDEX = 3;
	private final int STANDARD_DEVIATION_COLUMN_INDEX = 4;
	private final int WINDOW_COUNT_COLUMN_INDEX = 5;
	private final int WINDOW_COUNT_COLUMN_LENGTH = 6;
	private final int SCORE_SUM_LENGTH = 7;

	private final SCWListStats 			scwListStats;		// statistics values of the model
	private final ProjectChromosomes 	projectChromosomes; // project chromosomes


	/**
	 * Creates an instance of {@link SCWListStatsTableModel}
	 * @param scwListStats
	 */
	SCWListStatsTableModel(SCWListStats scwListStats) {
		this.scwListStats = scwListStats;
		projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
	}


	/**
	 * @param rowIndex
	 * @param columnIndex
	 * @return the non-genome-wide element at the specified row and column
	 */
	private String getChromosomeElement(int rowIndex, int columnIndex) {
		int chromosomeIndex = rowIndex - 1;
		switch (columnIndex) {
		case MINIMUM_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getMinimums()[chromosomeIndex]);
		case MAXIMUM_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getMaximums()[chromosomeIndex]);
		case AVERAGE_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getAverages()[chromosomeIndex]);
		case STANDARD_DEVIATION_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getStandardDeviations()[chromosomeIndex]);
		case WINDOW_COUNT_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getWindowCounts()[chromosomeIndex]);
		case WINDOW_COUNT_COLUMN_LENGTH:
			return NumberFormats.getScoreFormat().format(scwListStats.getWindowLengths()[chromosomeIndex]);
		case SCORE_SUM_LENGTH:
			return NumberFormats.getScoreFormat().format(scwListStats.getScoreSums()[chromosomeIndex]);
		default:
			return null;
		}
	}


	@Override
	public int getColumnCount() {
		return COLUMN_HEADERS.length;
	}


	/**
	 * 
	 * @param columnIndex
	 * @return the genome-wide element at the specified row and column
	 */
	private String getGenomeWideElement(int columnIndex) {
		switch (columnIndex) {
		case MINIMUM_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getMinimum());
		case MAXIMUM_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getMaximum());
		case AVERAGE_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getAverage());
		case STANDARD_DEVIATION_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getStandardDeviation());
		case WINDOW_COUNT_COLUMN_INDEX:
			return NumberFormats.getScoreFormat().format(scwListStats.getWindowCount());
		case WINDOW_COUNT_COLUMN_LENGTH:
			return NumberFormats.getScoreFormat().format(scwListStats.getWindowLength());
		case SCORE_SUM_LENGTH:
			return NumberFormats.getScoreFormat().format(scwListStats.getScoreSum());
		default:
			return null;
		}
	}


	@Override
	public int getRowCount() {
		return projectChromosomes.size() + 1;
	}


	/**
	 * @param rowIndex
	 * @return the value of the header of a row
	 */
	private String getRowHeader(int rowIndex) {
		if (rowIndex == 0) {
			return "Genome Wide";
		} else {
			return projectChromosomes.get(rowIndex - 1).getName();
		}
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == ROW_HEADER_INDEX) {
			return getRowHeader(rowIndex);
		}
		if (rowIndex == 0) {
			return getGenomeWideElement(columnIndex);
		}
		return getChromosomeElement(rowIndex, columnIndex);
	}
}
