/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.gwBookmarkDialog;

import java.awt.Image;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.gwBookmark.GWBookmark;
import edu.yu.einstein.genplay.util.Images;

public class BookmarkTableModel extends AbstractTableModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7301277783113566818L;
	private static final int CHROMOSOME_COLUMN = 0;
	private static final int START_COLUMN = 1;
	private static final int STOP_COLUMN = 2;
	private static final int DESCRIPTION_COLUMN = 3;
	private static final int DELETE_COLUMN = 4;


	private final List<GWBookmark> bookmarkList;


	public BookmarkTableModel(List<GWBookmark> bookmarkList) {
		this.bookmarkList = bookmarkList;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case CHROMOSOME_COLUMN:
			return Chromosome.class;
		case START_COLUMN:
			return Integer.class;
		case STOP_COLUMN:
			return Integer.class;
		case DESCRIPTION_COLUMN:
			return String.class;
		case DELETE_COLUMN:
			return Image.class;
		default:
			throw new IndexOutOfBoundsException();
		}
	}


	@Override
	public int getColumnCount() {
		return 4;
	}


	@Override
	public int getRowCount() {
		return bookmarkList.size();
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= bookmarkList.size()) {
			throw new IndexOutOfBoundsException();
		}
		switch (columnIndex) {
		case CHROMOSOME_COLUMN:
			return bookmarkList.get(rowIndex).getGenomeWindow().getChromosome();
		case START_COLUMN:
			return bookmarkList.get(rowIndex).getGenomeWindow().getStart();
		case STOP_COLUMN:
			return bookmarkList.get(rowIndex).getGenomeWindow().getStop();
		case DESCRIPTION_COLUMN:
			return bookmarkList.get(rowIndex).getDescription();
		case DELETE_COLUMN:
			return Images.getDeleteImage();
		default:
			throw new IndexOutOfBoundsException();
		}
	}

}
