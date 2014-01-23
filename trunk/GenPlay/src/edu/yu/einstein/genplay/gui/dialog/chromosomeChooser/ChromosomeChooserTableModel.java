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
package edu.yu.einstein.genplay.gui.dialog.chromosomeChooser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;


/**
 * This class is the table model used in {@link VCFLoader} class.
 * The table shows 4 columns but only 3 objects are stored in the data list.
 * The first one is the number of the row,
 * the second one is the chromosome (who displays the name and the length -> 2 columns),
 * the last one is the boolean for selection.
 * @author Nicolas Fourel
 */
class ChromosomeChooserTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 136782955769801093L;

	private final String[] 			columnNames;	// Column names
	private final List<List<Object>> 	data;			// table data


	/**
	 * Constructor of {@link ChromosomeChooserTableModel}
	 */
	protected ChromosomeChooserTableModel () {
		super();
		columnNames = ChromosomeChooserDialog.COLUMN_NAMES;
		data = new ArrayList<List<Object>>();
	}


	/**
	 * This method adds a row to the table according to a file.
	 * @param row	the row to add
	 * @param chromosome the chromosome to add
	 */
	protected void addRow (int row, Chromosome chromosome) {
		setValueAt(row+1, row, 0);
		setValueAt(chromosome, row, 1);
		setValueAt(false, row, 2);
	}


	@Override
	public Class<?> getColumnClass(int c) {
		switch (c) {
		case 0:
			return Integer.class;
		case 1:
			return String.class;
		case 2:
			return Integer.class;
		case 3:
			return Boolean.class;
		default:
			return Integer.class;
		}
	}


	@Override
	public int getColumnCount() {
		return columnNames.length;
	}


	/**
	 * @return the data
	 */
	protected  List<List<Object>> getData () {
		return data;
	}


	/**
	 * @return the full list of chromosome
	 */
	protected List<Chromosome> getFullChromosomeList () {
		List<Chromosome> result = new ArrayList<Chromosome>();
		for (List<Object> row: data) {
			Chromosome chromosome = (Chromosome)row.get(1);
			result.add(chromosome);
		}
		return result;
	}


	@Override
	public int getRowCount() {
		if (data != null) {
			return data.size();
		} else {
			return 0;
		}
	}


	/**
	 * @return the list of selected chromosome
	 */
	protected List<Chromosome> getSelectedChromosome () {
		List<Chromosome> result = new ArrayList<Chromosome>();
		for (List<Object> row: data) {
			if ((Boolean) row.get(2)) {
				Chromosome chromosome = (Chromosome)row.get(1);
				result.add(chromosome);
			}
		}
		return result;
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < data.size()) {
			switch (columnIndex) {
			case 0:
				return data.get(rowIndex).get(0);
			case 1:
				return ((Chromosome)(data.get(rowIndex).get(1))).getName();
			case 2:
				return ((Chromosome)(data.get(rowIndex).get(1))).getLength();
			case 3:
				return data.get(rowIndex).get(2);
			default:
				return null;
			}
		}
		return null;
	}


	/**
	 * Defines if a cell is editable.
	 */
	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 3) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * This method moves (up or down) a list of row.
	 * @param list	the list of row numbers to move
	 * @param toUp	rows will be move up if true, down if false
	 */
	protected void move (int[] list, boolean toUp) {
		Object objChromosome;
		Object objSelected;
		if (toUp) {
			for (int i: list) {
				if (i > 0) {
					objChromosome = data.get(i-1).get(1);
					objSelected = data.get(i-1).get(2);

					setValueAt(i, i-1, 0);
					setValueAt(data.get(i).get(1), i-1, 1);
					setValueAt(data.get(i).get(2), i-1, 2);

					setValueAt(i+1, i, 0);
					setValueAt(objChromosome, i, 1);
					setValueAt(objSelected, i, 2);
				}
			}
		} else {
			list = edu.yu.einstein.genplay.util.Utils.reverse(list);
			for (int i: list) {
				if (i<(getRowCount()-1)){
					objChromosome = data.get(i+1).get(1);
					objSelected = data.get(i+1).get(2);

					setValueAt(i+2, i+1, 0);
					setValueAt(data.get(i).get(1), i+1, 1);
					setValueAt(data.get(i).get(2), i+1, 2);

					setValueAt(i+1, i, 0);
					setValueAt(objChromosome, i, 1);
					setValueAt(objSelected, i, 2);
				}
			}
		}
		fireTableDataChanged();
	}


	/**
	 * Select all basics chromosome.
	 * A basic chromosome is chr1....chrX, chrY and chrM
	 */
	protected void selectBasicChr () {
		String name;
		String end;
		Boolean select;
		for (List<Object> row: data){
			name = row.get(1).toString();
			if (name.length() >= 3) {
				if (name.substring(0, 3).equals("chr")) {
					select = true;
					end = name.substring(3, name.length());
					try {
						Integer.parseInt(end);
					} catch (Exception e) {
						if (!end.equals("X") && !end.equals("Y") && !end.equals("M")) {
							select = false;
						}
					}
					setValueAt(select, Integer.parseInt(row.get(0).toString())-1, 3);
				}
			}
		}
	}


	/**
	 * Sets the data
	 * @param fullList		list of chromosome available for selection
	 * @param selectedList	list of chromosome selected
	 */
	protected void setData (List<Chromosome> fullList, List<Chromosome> selectedList) {
		for (int i = 0; i < fullList.size(); i++) {
			Chromosome chromosome = fullList.get(i);
			addRow(i, chromosome);
			if (selectedList.contains(chromosome)) {
				setValueAt(true, i, 2);
			} else {
				setValueAt(false, i, 2);
			}
		}
	}


	/**
	 * Sets all selected column rows
	 * @param list
	 * @param value
	 */
	protected void setSelectedValue (int[] list, boolean value) {
		for (int i: list) {
			setValueAt(value, i, 3);
		}
	}


	/**
	 * This method sets the value of a precise cell.
	 * @param value	the value to add
	 * @param row	the row
	 * @param col	the column
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < data.size()) {
			if (col == 3) {
				data.get(row).set(2, value);
			} else {
				data.get(row).set(col, value);
			}
			fireTableCellUpdated(row, col);
		} else {
			List<Object> line = new ArrayList<Object>();
			line.add(row);
			line.add(null);
			line.add(false);
			data.add(row, line);
			setValueAt(value, row, col);
		}
	}

}
