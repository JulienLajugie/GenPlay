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
package edu.yu.einstein.genplay.gui.dialog.layerChooser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;


/**
 * Model handling the list of layer that can be selected in a {@link LayerChooserDialog}
 * @author Julien Lajugie
 */
class LayerChooserTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 136782955769801093L; // generated serial ID

	private final List<Layer<?>> 			layers;					// list of layers
	private final List<Layer<?>>			selectedLayers;			// list of layers that are selected
	private final LayerType[]				selectableLayerTypes;	// type of layer types that can be selected.  Any type can be selected if null
	private final boolean					isMultiselectable;		// true if more than one layer can be selected


	/**
	 * Constructor of {@link LayerChooserTableModel}
	 * @param availableLayers		list of layers available for selection
	 * @param selectedLayers		list of layers selected. None selected if null
	 * @param selectableLayerTypes 	type of layer types that can be selected.  Any type can be selected if null
	 * @param isMultiselectable		true if more than one layer can be selected
	 */
	protected LayerChooserTableModel (List<Layer<?>> availableLayers, List<Layer<?>> selectedLayers, LayerType[] selectableLayerTypes, boolean isMultiselectable) {
		super();
		if (availableLayers == null) {
			layers = new ArrayList<Layer<?>>();
		} else {
			layers = availableLayers;
		}
		if (selectedLayers == null) {
			this.selectedLayers = new ArrayList<Layer<?>>();
		} else {
			this.selectedLayers = selectedLayers;
		}
		this.selectableLayerTypes = selectableLayerTypes;
		this.isMultiselectable = isMultiselectable;
	}


	/**
	 * Deselect all the rows except the specified one
	 * @param selectedRow
	 */
	private void deselectOtherRows(int selectedRow) {
		for (int row = 0; row < getRowCount(); row++) {
			if ((Boolean)getValueAt(row, LayerChooserDialog.LAYER_SELECTION_INDEX) && (row != selectedRow)) {
				setValueAt(false, row, LayerChooserDialog.LAYER_SELECTION_INDEX);
				// fireTableCellUpdated(row, LayerChooserDialog.LAYER_SELECTION_INDEX);
			}
		}
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case LayerChooserDialog.TRACK_NUMBER_INDEX:
			return Integer.class;
		case LayerChooserDialog.TRACK_NAME_INDEX:
			return String.class;
		case LayerChooserDialog.LAYER_NAME_INDEX:
			return String.class;
		case LayerChooserDialog.LAYER_TYPE_INDEX:
			return String.class;
		case LayerChooserDialog.LAYER_SELECTION_INDEX:
			return Boolean.class;
		default:
			return null;
		}
	}


	@Override
	public int getColumnCount() {
		return LayerChooserDialog.COLUMN_NAMES.length;
	}


	/**
	 * @return the list of all the layers
	 */
	protected  List<Layer<?>> getLayers() {
		return layers;
	}


	@Override
	public int getRowCount() {
		if (layers != null) {
			return layers.size();
		} else {
			return 0;
		}
	}


	/**
	 * @return the list of selected layers
	 */
	protected List<Layer<?>> getSelectedLayers() {
		return selectedLayers;
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < layers.size()) {
			switch (columnIndex) {
			case LayerChooserDialog.TRACK_NUMBER_INDEX:
				return layers.get(rowIndex).getTrack().getNumber();
			case LayerChooserDialog.TRACK_NAME_INDEX:
				return layers.get(rowIndex).getTrack().getName();
			case LayerChooserDialog.LAYER_NAME_INDEX:
				return layers.get(rowIndex).getName();
			case LayerChooserDialog.LAYER_TYPE_INDEX:
				return layers.get(rowIndex).getType();
			case LayerChooserDialog.LAYER_SELECTION_INDEX:
				boolean isLayerSelected = selectedLayers.contains(layers.get(rowIndex));
				return isLayerSelected;
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
		LayerType rowLayerType = layers.get(row).getType();
		boolean isSelectableLayerType = ((selectableLayerTypes == null) || rowLayerType.isContainedIn(selectableLayerTypes));
		if ((col == LayerChooserDialog.LAYER_SELECTION_INDEX) && isSelectableLayerType) {
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Sets all selected column rows
	 * @param list
	 * @param value
	 */
	protected void setSelectedValues(int[] list, boolean value) {
		for (int i: list) {
			setValueAt(value, i, LayerChooserDialog.LAYER_SELECTION_INDEX);
		}
	}


	/**
	 * Sets if a layer is selected or not
	 * @param value	the value to set (must be instance of Boolean)
	 * @param row	the row where the value needs to be set
	 * @param col	the column column where to set the value
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (row < layers.size()) {
			// only the layer selection column is editable
			if (col == LayerChooserDialog.LAYER_SELECTION_INDEX) {
				if (value instanceof Boolean) {
					Layer<?> rowLayer = layers.get(row);
					if ((Boolean)value) {
						if (!selectedLayers.contains(rowLayer)) {
							selectedLayers.add(rowLayer);
							if (!isMultiselectable) {
								deselectOtherRows(row);
							}
						}
					} else {
						selectedLayers.remove(rowLayer);
					}
				}
			}
			fireTableCellUpdated(row, col);
		}
	}
}
