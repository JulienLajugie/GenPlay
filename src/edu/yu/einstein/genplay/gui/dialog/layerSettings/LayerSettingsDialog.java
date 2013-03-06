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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;

import edu.yu.einstein.genplay.dataStructure.enums.GraphType;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.BooleanRadioButtonEditor;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.BooleanRadioButtonRenderer;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.ColorEditor;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.ColorRenderer;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.ComboBoxEditor;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Dialog for the settings of the layers of a track
 * @author Julien Lajugie
 */
public class LayerSettingsDialog extends JDialog {

	/** Return value when OK has been clicked. */
	public static final int APPROVE_OPTION = 0;

	/** Return value when Cancel has been clicked. */
	public static final int CANCEL_OPTION = 1;

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
	private static	final 	Dimension 	DIALOG_SIZE 		= new Dimension(800, 600);				// Window size
	private static	final 	Dimension 	BUTTON_PANEL_SIZE	= new Dimension(DIALOG_SIZE.width, 65);	// Button panel size

	private JTable 						layerSettingsTable;			// table displaying the layer settings
	private int							approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private final LayerSettingsRow[] 	data;						// data displayed in the table
	private final LayerSettingsModel 	model;						// model managing the data


	/**
	 * Creates an instance of {@link LayerSettingsDialog}
	 * @param data data displayed in the table
	 */
	public LayerSettingsDialog(LayerSettingsRow[] data) {
		this.data = data;
		model = new LayerSettingsModel(data);
	}


	/**
	 * @return the data displayed in the layer settings table
	 */
	public LayerSettingsRow[] getData() {
		return data;
	}


	/**
	 * Initializes dialog components
	 */
	private void init() {
		//JDialog information
		setSize(DIALOG_SIZE);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);

		// table
		layerSettingsTable = new JTable(model);

		// other table properties
		layerSettingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(layerSettingsTable);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		layerSettingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// allows the table to save data when a cell loses focus
		layerSettingsTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		initializeColumnProperties();

		//Confirm button
		JButton jbConfirm = new JButton("Ok");
		jbConfirm.setToolTipText(ProjectFrame.CONFIRM_FILES);
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (confirmLayerDeletion()) {
					approved = APPROVE_OPTION;
					dispose();
				}
			}
		});

		//Cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.setToolTipText(ProjectFrame.CANCEL_FILES);
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = CANCEL_OPTION;
				dispose();
			}
		});

		// we want the size of the two buttons to be equal
		jbConfirm.setPreferredSize(jbCancel.getPreferredSize());

		// Move up button
		JButton jbUp = new JButton("Up");
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
		buttonPanel.setSize(BUTTON_PANEL_SIZE);
		buttonPanel.setPreferredSize(BUTTON_PANEL_SIZE);
		buttonPanel.setMinimumSize(BUTTON_PANEL_SIZE);
		buttonPanel.setMaximumSize(BUTTON_PANEL_SIZE);
		buttonPanel.setLayout(new GridLayout(2, 1));

		//TopPane
		JPanel topPane = new JPanel();
		topPane.add(jbUp);
		topPane.add(jbDown);

		//BotPane
		getRootPane().setDefaultButton(jbConfirm);
		JPanel botPane = new JPanel();
		botPane.add(jbConfirm);
		botPane.add(jbCancel);

		//Add panels
		buttonPanel.add(topPane);
		buttonPanel.add(botPane);
		BorderLayout border = new BorderLayout();
		setLayout(border);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);


		getRootPane().setDefaultButton(jbConfirm);
	}


	/**
	 * If some layers have been set for deletion this method ask the user to confirm the deletion
	 * @return true if:<br>
	 *  - the user want to delete the layers <br>
	 *  - there is no layers set for deletion<br>
	 *  Return false otherwise.
	 */
	private boolean confirmLayerDeletion() {
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


	/**
	 * Displays the layer settings dialog
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @return 			APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		init();
		setLocationRelativeTo(parent);
		setTitle("Layer Settings");
		setIconImage(Images.getApplicationImage());
		setVisible(true);
		return approved;
	}
}
