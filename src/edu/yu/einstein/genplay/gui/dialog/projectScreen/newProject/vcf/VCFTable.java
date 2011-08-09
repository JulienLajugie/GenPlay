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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * This class manages the table information of the VCF loader
 * @author Nicolas Fourel
 * @version 0.1
 */
class VCFTable extends JTable {

	private static final long serialVersionUID = -287266865363088084L;

	private VCFTableModel 	model;


	/**
	 * Constructor of {@link VCFTable}
	 * @param model the table model
	 */
	protected VCFTable (VCFTableModel model) {
		super(model);
		this.model = model;
	}


	/**
	 * Updates the table
	 */
	protected void updateTable () {
		model.fireTableDataChanged();
	}


	@Override
	public TableCellEditor getCellEditor(int row, int col) {
		if (col == 4) {
			TableCellEditor editor = model.getCellEditor(row, col);
			if (editor != null) {
				return editor;
			}
		}
		return super.getCellEditor(row, col);
	}


	@Override
	public String getToolTipText(MouseEvent e) {
		String tip = null;
		java.awt.Point p = e.getPoint();
		int rowIndex = rowAtPoint(p);
		int colIndex = columnAtPoint(p);
		int realColumnIndex = convertColumnIndexToModel(colIndex);

		Object o = model.getValueAt(rowIndex, realColumnIndex);
		if (o != null) {
			tip = o.toString();
		} else {
			tip = super.getToolTipText(e);
		}

		return tip;
	}

}
