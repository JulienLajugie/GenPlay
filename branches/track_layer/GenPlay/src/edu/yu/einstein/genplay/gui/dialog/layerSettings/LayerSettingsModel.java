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
package edu.yu.einstein.genplay.gui.dialog.layerSettings;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;


/**
 * Model managing the data of a layer setting table row
 * @author Julien Lajugie
 */
public class LayerSettingsModel extends AbstractTableModel implements TableModel {

	private static final long serialVersionUID = -3008057257010488995L;	// generated serial ID

	private final LayerSettingsRow[] data; // data managed by the model


	/**
	 * Creates an instance of {@link LayerSettingsModel}
	 * @param data {@link LayerSettingsRow} managed by the model
	 */
	public LayerSettingsModel(LayerSettingsRow[] data) {
		this.data = data;
	}


	@Override
	public int getRowCount() {
		return data.length;
	}


	@Override
	public int getColumnCount() {
		return LayerSettingsDialog.COLUMN_NAMES.length;
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case LayerSettingsDialog.LAYER_NUMBER_INDEX:
			return rowIndex + 1;
		case LayerSettingsDialog.LAYER_NAME_INDEX:
			return data[rowIndex].getLayerName();
		case LayerSettingsDialog.LAYER_TYPE_INDEX:
			return data[rowIndex].getLayer().getType();
		case LayerSettingsDialog.LAYER_COLOR_INDEX:
			return data[rowIndex].getLayerColor();
		case LayerSettingsDialog.IS_LAYER_VISIBLE_INDEX:
			return data[rowIndex].isLayerVisible();
		case LayerSettingsDialog.IS_LAYER_ACTIVE_INDEX:
			return data[rowIndex].isLayerActive();
		default:
			return null;
		}
	}


	@Override
	public String getColumnName(int columnIndex) {
		return LayerSettingsDialog.COLUMN_NAMES[columnIndex];
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case LayerSettingsDialog.LAYER_NUMBER_INDEX:
			return Integer.class;
		case LayerSettingsDialog.LAYER_NAME_INDEX:
			return String.class;
		case LayerSettingsDialog.LAYER_TYPE_INDEX:
			return String.class;
		case LayerSettingsDialog.LAYER_COLOR_INDEX:
			return Color.class;
		case LayerSettingsDialog.IS_LAYER_VISIBLE_INDEX:
			return Boolean.class;
		case LayerSettingsDialog.IS_LAYER_ACTIVE_INDEX:
			return Boolean.class;
		default:
			return null;
		}
	}


	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case LayerSettingsDialog.LAYER_NUMBER_INDEX:
			return false;
		case LayerSettingsDialog.LAYER_NAME_INDEX:
			return true;
		case LayerSettingsDialog.LAYER_TYPE_INDEX:
			return false;
		case LayerSettingsDialog.LAYER_COLOR_INDEX:
			return data[rowIndex].getLayer() instanceof ColoredLayer;
		case LayerSettingsDialog.IS_LAYER_VISIBLE_INDEX:
			return true;
		case LayerSettingsDialog.IS_LAYER_ACTIVE_INDEX:
			return true;
		default:
			return false;
		}
	}


	/**
	 * @return the data managed by this table model
	 */
	public LayerSettingsRow[] getData() {
		return data;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex < data.length) {
			switch (columnIndex) {
			case LayerSettingsDialog.LAYER_NAME_INDEX:
				if (aValue instanceof String) {
					data[rowIndex].setLayerName((String) aValue);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				break;
			case LayerSettingsDialog.LAYER_COLOR_INDEX:
				if (aValue instanceof Color) {
					data[rowIndex].setLayerColor((Color) aValue);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				break;
			case LayerSettingsDialog.IS_LAYER_VISIBLE_INDEX:
				if(aValue instanceof Boolean) {
					data[rowIndex].setLayerVisible((Boolean) aValue);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				break;
			case LayerSettingsDialog.IS_LAYER_ACTIVE_INDEX:
				// only the layer selection column is editable
				if (aValue instanceof Boolean) {
					if ((Boolean) aValue) {
						data[rowIndex].setLayerActive(true);
						deselectOtherActiveLayers(rowIndex);
					} else {
						data[rowIndex].setLayerActive(false);
					}
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				break;
			}
		}
	}


	/**
	 * Sets to false all the active layers that are not the one with the specified index
	 * @param rowIndex index of a layer in the table
	 */
	private void deselectOtherActiveLayers(int rowIndex) {
		for (int row = 0; row < getRowCount(); row++) {
			if ((Boolean)getValueAt(row, LayerSettingsDialog.IS_LAYER_ACTIVE_INDEX) && (row != rowIndex)) {
				setValueAt(false, row, LayerSettingsDialog.IS_LAYER_ACTIVE_INDEX);
			}
		}
	}


	/**
	 * Inverts the specified row and the row above it
	 * @param selectedRow
	 * @return true if the selected row was moved up
	 */
	public boolean moveLayerUp(int selectedRow) {
		if (selectedRow > 0) {
			LayerSettingsRow rowTmp = data[selectedRow - 1];
			data[selectedRow - 1] = data[selectedRow];
			data[selectedRow] = rowTmp;
			fireTableRowsUpdated(selectedRow - 1, selectedRow);
			return true;
		}
		return false;
	}


	/**
	 * Inverts the specified row and the row under it
	 * @param selectedRow
	 * @return true if the selected row was moved down
	 */
	public boolean moveLayerDown(int selectedRow) {
		if ((selectedRow + 1) < data.length) {
			LayerSettingsRow rowTmp = data[selectedRow + 1];
			data[selectedRow + 1] = data[selectedRow];
			data[selectedRow] = rowTmp;
			fireTableRowsUpdated(selectedRow, selectedRow + 1);
			return true;
		}
		return false;
	}
}
