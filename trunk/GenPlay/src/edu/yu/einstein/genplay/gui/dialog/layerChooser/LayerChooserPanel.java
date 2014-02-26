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
package edu.yu.einstein.genplay.gui.dialog.layerChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import edu.yu.einstein.genplay.gui.customComponent.tableComponents.BooleanRadioButtonEditor;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.BooleanRadioButtonRenderer;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.gui.track.layer.ColoredLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * Panel to select or unselect layers.
 * A list of selectable layer types can be specified.
 * A list of layers already selected can be specified.
 * @author Julien Lajugie
 */
public class LayerChooserPanel extends JPanel implements TableModelListener {

	/**
	 * Simple {@link TableCellRenderer} that returns a disabled component for the lines where the layer type is not selectable
	 * @author Julien Lajugie
	 */
	private class LayerTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
		private static final long serialVersionUID = 9025676810770612025L; // generated serial ID
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			if ((table == null) || (value == null)) {
				return null;
			}
			Component renderedComponent = table.getDefaultRenderer(value.getClass()).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			renderedComponent.setEnabled(true);
			Layer<?> layer = layers.get(row);
			// write the layer name with the color of the layer
			if ((layer != null) && (layer instanceof ColoredLayer) && !isSelected) {
				Color layerColor = ((ColoredLayer) layer).getColor();
				renderedComponent.setForeground(layerColor);
			}
			// make the unselectable row (because the layer type is not accepted) different
			if (layer != null) {
				LayerType layerType = layer.getType();
				if ((selectableLayerTypes != null) && !layerType.isContainedIn(selectableLayerTypes)) {
					renderedComponent.setForeground(Colors.GREY);
					renderedComponent.setFont(renderedComponent.getFont().deriveFont(Font.ITALIC));
				} else {
					renderedComponent.setFont(renderedComponent.getFont().deriveFont(Font.BOLD));
				}
			}
			return renderedComponent;
		}
	}


	/** Generated ID */
	private static final long serialVersionUID = 3444530544106287254L;

	/** Selected layers change property name */
	public static final String SELECTED_LAYERS_PROPERTY_NAME = "Selected Layers Change";

	private final 	JTable 							layerTable;					// layer table
	private final 	LayerChooserTableModel 			tableModel;					// layer table model
	private final 	List<Layer<?>>					layers;						// List of layers displayed in the table
	private final 	List<Layer<?>>					selectedLayers;				// List of layers selected
	private final 	LayerType[]						selectableLayerTypes;		// type of layer types that can be selected.  Any type can be selected if null
	private	final 	boolean							isMultiselectable;			// true if more than one layer can be selected


	/**
	 * Constructor of {@link LayerChooserDialog}
	 * @param layers List of layers displayed in the table
	 * @param selectedLayers List of layers selected
	 * @param selectableLayerTypes type of layer types that can be selected.  Any type can be selected if null
	 * @param isMultiselectable true if more than one layer can be selected
	 */
	public LayerChooserPanel (List<Layer<?>> layers,
			List<Layer<?>> selectedLayers,
			LayerType[]	selectableLayerTypes,
			boolean	isMultiselectable) {
		super();
		this.layers = layers;
		if (selectedLayers == null) {
			this.selectedLayers = new ArrayList<Layer<?>>();
		} else {
			this.selectedLayers = selectedLayers;
		}
		this.selectableLayerTypes = selectableLayerTypes;
		this.isMultiselectable = isMultiselectable;
		// table
		layerTable = new JTable();
		// table model
		tableModel = new LayerChooserTableModel(layers, this.selectedLayers, selectableLayerTypes, isMultiselectable);
		tableModel.addTableModelListener(this);
		init();
	}


	/**
	 * @return the first element of the list of selected layers.
	 * This method return the only layer selected if the property
	 * isMultiselectable is set to false.
	 */
	public Layer<?> getSelectedLayer() {
		if (selectedLayers.isEmpty()) {
			return null;
		} else {
			return selectedLayers.get(0);
		}
	}


	/**
	 * @return the list of selected layers
	 */
	public List<Layer<?>> getSelectedLayers() {
		return selectedLayers;
	}


	/**
	 * @return the table displaying the layers
	 */
	public JTable getTable() {
		return layerTable;
	}


	/**
	 * Initializes dialog components
	 */
	private void init() {
		layerTable.setModel(tableModel);
		// other table properties
		layerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(layerTable);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		initializeColumnProperties();
		// table renderer and editors if the multiselectable property is set to false so we have radio buttons instead of check boxes
		if (!isMultiselectable) {
			layerTable.getColumnModel().getColumn(LayerChooserTableModel.LAYER_SELECTION_INDEX).setCellRenderer(new BooleanRadioButtonRenderer());
			layerTable.getColumnModel().getColumn(LayerChooserTableModel.LAYER_SELECTION_INDEX).setCellEditor(new BooleanRadioButtonEditor());
		}

		//Select button
		JButton jbSelect = new JButton("Select");
		jbSelect.setToolTipText(ProjectFrame.SELECT_FILES);
		jbSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.setSelectedValues(layerTable.getSelectedRows(), true);
			}
		});

		//Unselect button
		JButton jbUnselect = new JButton("Unselect");
		jbUnselect.setToolTipText(ProjectFrame.UNSELECT_FILES);
		jbUnselect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.setSelectedValues(layerTable.getSelectedRows(), false);
			}
		});

		//TopPane
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(jbSelect);
		buttonPanel.add(jbUnselect);

		//Add panels
		BorderLayout border = new BorderLayout();
		setLayout(border);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}


	/**
	 * This method initializes the column properties:
	 * - name
	 * - width
	 * - resizable
	 */
	private void initializeColumnProperties () {
		TableCellRenderer tableCellRenderer = new LayerTableCellRenderer();
		TableColumn column = null;
		for (int i = 0; i < LayerChooserTableModel.COLUMN_NAMES.length; i++) {
			column = layerTable.getColumnModel().getColumn(i);
			column.setHeaderValue(LayerChooserTableModel.COLUMN_NAMES[i]);
			column.setResizable(true);
			column.setCellRenderer(tableCellRenderer);
		}
	}


	@Override
	public void tableChanged(TableModelEvent e) {
		//if (e.getColumn() == LayerChooserTableModel.LAYER_SELECTION_INDEX) {
		firePropertyChange(SELECTED_LAYERS_PROPERTY_NAME, null, selectedLayers);
		//}
	}
}
