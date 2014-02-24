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
package edu.yu.einstein.genplay.gui.dialog.trackSettings.layerPanel;

import java.awt.Color;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import edu.yu.einstein.genplay.dataStructure.enums.GraphType;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.GraphLayer;


/**
 * Model managing the data of a layer setting table row
 * @author Julien Lajugie
 */
public class LayerSettingsModel extends AbstractTableModel implements TableModel {

	/** generated serial ID */
	private static final long serialVersionUID = -3008057257010488995L;

	/** Column headers */
	protected static final String[] COLUMN_NAMES = {"#", "Name", "Type", "Color", "Graph Type", "Visible", "Active", "Set For Deletion"};

	/** Index of the layer number column */
	protected static final int LAYER_NUMBER_INDEX = 0;

	/** Index of the layer name column */
	protected static final int LAYER_NAME_INDEX = 1;

	/** Index of the layer type column */
	protected static final int LAYER_TYPE_INDEX = 2;

	/** Index of the layer color column */
	protected static final int LAYER_COLOR_INDEX = 3;

	/** Index of the layer graph type column */
	protected static final int LAYER_GRAPH_TYPE_INDEX = 4;

	/** Index of the "is layer visible" column */
	protected static final int IS_LAYER_VISIBLE_INDEX = 5;

	/** Index of the "is layer active" column */
	protected static final int IS_LAYER_ACTIVE_INDEX = 6;

	/** Index of the set for deletion column*/
	protected static final int IS_LAYER_SET_FOR_DELETION_INDEX = 7;

	private final LayerSettingsRow[] data; // data managed by the model


	/**
	 * Creates an instance of {@link LayerSettingsModel}
	 * @param data {@link LayerSettingsRow} managed by the model
	 */
	public LayerSettingsModel(LayerSettingsRow[] data) {
		this.data = data;
	}


	/**
	 * Sets to false all the active layers that are not the one with the specified index
	 * @param rowIndex index of a layer in the table
	 */
	private void deselectOtherActiveLayers(int rowIndex) {
		for (int row = 0; row < getRowCount(); row++) {
			if ((Boolean)getValueAt(row, IS_LAYER_ACTIVE_INDEX) && (row != rowIndex)) {
				setValueAt(false, row, IS_LAYER_ACTIVE_INDEX);
			}
		}
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case LAYER_NUMBER_INDEX:
			return Integer.class;
		case LAYER_NAME_INDEX:
			return String.class;
		case LAYER_TYPE_INDEX:
			return String.class;
		case LAYER_COLOR_INDEX:
			return Color.class;
		case LAYER_GRAPH_TYPE_INDEX:
			return GraphType.class;
		case IS_LAYER_VISIBLE_INDEX:
			return Boolean.class;
		case IS_LAYER_ACTIVE_INDEX:
			return Boolean.class;
		case IS_LAYER_SET_FOR_DELETION_INDEX:
			return Boolean.class;
		default:
			return null;
		}
	}


	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}


	@Override
	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}


	/**
	 * @return the data managed by this table model
	 */
	public LayerSettingsRow[] getData() {
		return data;
	}


	@Override
	public int getRowCount() {
		return data.length;
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case LAYER_NUMBER_INDEX:
			return rowIndex + 1;
		case LAYER_NAME_INDEX:
			return data[rowIndex].getLayerName();
		case LAYER_TYPE_INDEX:
			return data[rowIndex].getLayer().getType();
		case LAYER_COLOR_INDEX:
			return data[rowIndex].getLayerColor();
		case LAYER_GRAPH_TYPE_INDEX:
			return data[rowIndex].getLayerGraphType();
		case IS_LAYER_VISIBLE_INDEX:
			return data[rowIndex].isLayerVisible();
		case IS_LAYER_ACTIVE_INDEX:
			return data[rowIndex].isLayerActive();
		case IS_LAYER_SET_FOR_DELETION_INDEX:
			return data[rowIndex].isLayerSetForDeletion();
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case LAYER_NUMBER_INDEX:
			return false;
		case LAYER_NAME_INDEX:
			return true;
		case LAYER_TYPE_INDEX:
			return false;
		case LAYER_COLOR_INDEX:
			return data[rowIndex].getLayer() instanceof ColoredLayer;
		case LAYER_GRAPH_TYPE_INDEX:
			return data[rowIndex].getLayer() instanceof GraphLayer;
		case IS_LAYER_VISIBLE_INDEX:
			return true;
		case IS_LAYER_ACTIVE_INDEX:
			return true;
		case IS_LAYER_SET_FOR_DELETION_INDEX:
			return true;
		default:
			return false;
		}
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


	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex < data.length) {
			switch (columnIndex) {
			case LAYER_NAME_INDEX:
				if (aValue instanceof String) {
					data[rowIndex].setLayerName((String) aValue);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				break;
			case LAYER_COLOR_INDEX:
				if (aValue instanceof Color) {
					data[rowIndex].setLayerColor((Color) aValue);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				break;
			case LAYER_GRAPH_TYPE_INDEX:
				if (aValue instanceof GraphType) {
					data[rowIndex].setLayerGraphType((GraphType) aValue);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
			case IS_LAYER_VISIBLE_INDEX:
				if(aValue instanceof Boolean) {
					data[rowIndex].setLayerVisible((Boolean) aValue);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				break;
			case IS_LAYER_ACTIVE_INDEX:
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
			case IS_LAYER_SET_FOR_DELETION_INDEX:
				if(aValue instanceof Boolean) {
					data[rowIndex].setLayerSetForDeletion((Boolean) aValue);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
			}
		}
	}
}
