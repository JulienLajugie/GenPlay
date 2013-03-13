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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import edu.yu.einstein.genplay.core.enums.GraphType;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.BooleanRadioButtonEditor;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.BooleanRadioButtonRenderer;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.ColorEditor;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.ColorRenderer;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.ComboBoxEditor;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;

/**
 * Dialog for the settings of the layers of a track
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class LayerSettingsPanel extends JPanel {

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

	private static 	final 	long 		serialVersionUID 	= 5640779725244792401L; 				// generated ID

	private JTable 						layerSettingsTable;			// table displaying the layer settings
	private LayerSettingsRow[] 			data;						// data displayed in the table
	private LayerSettingsModel 			model;						// model managing the data


	/**
	 * @return the data displayed in the layer settings table
	 */
	public LayerSettingsRow[] getData() {
		return data;
	}


	/**
	 * Initializes dialog components
	 * @param data
	 */
	public void initialize (LayerSettingsRow[] data) {
		this.data = data;

		if (data == null) {
			add(new JLabel("No layers found."));
		} else {
			model = new LayerSettingsModel(data);

			// table
			layerSettingsTable = new JTable(model);
			layerSettingsTable.setFillsViewportHeight(true);

			// other table properties
			layerSettingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// allows the table to save data when a cell loses focus
			layerSettingsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
			initializeColumnProperties();

			// Move up button
			JButton jbUp = new JButton("Up");
			jbUp.setEnabled(data.length > 1);
			jbUp.setToolTipText(ProjectFrame.SELECT_FILES);
			jbUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (model.moveLayerUp(layerSettingsTable.getSelectedRow())) {
						layerSettingsTable.setRowSelectionInterval(layerSettingsTable.getSelectedRow() - 1, layerSettingsTable.getSelectedRow() - 1);
					}
				}
			});

			// Move down button
			JButton jbDown = new JButton("Down");
			jbDown.setEnabled(data.length > 1);
			jbDown.setToolTipText(ProjectFrame.UNSELECT_FILES);
			jbDown.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (model.moveLayerDown(layerSettingsTable.getSelectedRow())) {
						layerSettingsTable.setRowSelectionInterval(layerSettingsTable.getSelectedRow() + 1, layerSettingsTable.getSelectedRow() + 1);
					}
				}
			});

			////Button panel
			JPanel buttonPanel = new JPanel();
			Dimension buttonPanelDimension = new Dimension(layerSettingsTable.getPreferredSize().width, 65);
			buttonPanel.setSize(buttonPanelDimension);
			buttonPanel.setPreferredSize(buttonPanelDimension);
			buttonPanel.setMinimumSize(buttonPanelDimension);
			buttonPanel.setMaximumSize(buttonPanelDimension);
			buttonPanel.setLayout(new GridLayout(2, 1));

			//TopPane
			JPanel topPane = new JPanel();
			topPane.add(jbUp);
			topPane.add(jbDown);

			//Add panels
			buttonPanel.add(topPane);
			BorderLayout border = new BorderLayout();
			setLayout(border);
			add(layerSettingsTable.getTableHeader(), BorderLayout.NORTH);
			add(layerSettingsTable, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);
		}
	}


	/**
	 * If some layers have been set for deletion this method ask the user to confirm the deletion
	 * @return true if:<br>
	 *  - the user want to delete the layers <br>
	 *  - there is no layers set for deletion<br>
	 *  Return false otherwise.
	 */
	public boolean confirmLayerDeletion() {
		String confirmQuestion = "Do you really want to delete the following layers: \n";
		boolean deletionRequested = false;
		for (LayerSettingsRow currentRow: data) {
			if (currentRow.isLayerSetForDeletion()) {
				deletionRequested = true;
				confirmQuestion += currentRow.getLayer().getName() + "\n";
			}
		}
		if (deletionRequested) {
			int confirmOption = JOptionPane.showConfirmDialog(getRootPane(), confirmQuestion, "Delete Layers", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			return confirmOption == JOptionPane.YES_OPTION;
		} else {
			return true;
		}
	}


	/**
	 * This method initializes the column properties:
	 * - name
	 * - width
	 * - resizable
	 */
	private void initializeColumnProperties () {
		TableColumn column = null;
		for (int columnIndex = 0; columnIndex < COLUMN_NAMES.length; columnIndex++) {
			column = layerSettingsTable.getColumnModel().getColumn(columnIndex);
			column.setHeaderValue(COLUMN_NAMES[columnIndex]);
			column.setResizable(true);
			switch (columnIndex) {
			case LAYER_NUMBER_INDEX:
				//column.setWidth(5);
				column.setPreferredWidth(5);
				break;
			case LAYER_COLOR_INDEX:
				column.setCellEditor(new ColorEditor("Select"));
				column.setCellRenderer(new ColorRenderer("Select", true));
				break;
			case LAYER_GRAPH_TYPE_INDEX:
				column.setCellEditor(new ComboBoxEditor(GraphType.values()));
				break;
			case IS_LAYER_ACTIVE_INDEX:
				column.setCellEditor(new BooleanRadioButtonEditor());
				column.setCellRenderer(new BooleanRadioButtonRenderer());
				break;
			}
		}
	}
}
