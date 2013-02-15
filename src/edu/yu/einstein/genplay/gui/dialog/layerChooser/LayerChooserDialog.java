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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import edu.yu.einstein.genplay.gui.customComponent.tableComponents.BooleanRadioButtonEditor;
import edu.yu.einstein.genplay.gui.customComponent.tableComponents.BooleanRadioButtonRenderer;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Dialog that prompt the user to choose layers.
 * A list of selectable layer types can be specified.
 * A list of layers already selected can be specified.
 * @author Julien Lajugie
 */

public class LayerChooserDialog extends JDialog {

	/**
	 * Simple {@link TableCellRenderer} that returns a disabled component for the lines where the layer type is not selectable
	 * @author Julien Lajugie
	 */
	private class LayerTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
		private static final long serialVersionUID = 9025676810770612025L; // generated serial ID
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component renderedComponent = table.getDefaultRenderer(value.getClass()).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			renderedComponent.setEnabled(true);
			LayerType layerType = (LayerType)layerTable.getValueAt(row, LAYER_TYPE_INDEX);
			if ((selectableLayerTypes != null) && !layerType.isContainedIn(selectableLayerTypes)) {
				renderedComponent.setEnabled(false);
			}
			return renderedComponent;
		}
	}


	/** Return value when OK has been clicked. */
	public static final int APPROVE_OPTION = 0;

	/** Return value when Cancel has been clicked. */
	public static final int CANCEL_OPTION = 1;

	/** List of the column names */
	protected static final String[] COLUMN_NAMES = {"Track #", "Track Name", "Layer Name", "Layer Type", "Selected"};	// Column names

	/** track number index */
	protected static final int TRACK_NUMBER_INDEX = 0;

	/** track name index */
	protected static final int TRACK_NAME_INDEX = 1;

	/** layer name index */
	protected static final int LAYER_NAME_INDEX = 2;

	/** layer type index */
	protected static final int LAYER_TYPE_INDEX = 3;

	/** layer selection index */
	protected static final int LAYER_SELECTION_INDEX = 4;

	private 	static 	final 	long 		serialVersionUID 			= -5444037655111247170L; 								// generated serial ID
	private 	static	final 	Dimension 	DIALOG_SIZE 				= new Dimension(600, 600);								// Window size
	private 	static	final 	Dimension 	BUTTON_PANEL_SIZE			= new Dimension(DIALOG_SIZE.width, 65);					// Button panel size

	private 	static 			JTable 							layerTable;					// layer table
	private 	static 			LayerChooserTableModel 			tableModel;					// layer table model
	private 					List<Layer<?>>					layers;						// List of layers displayed in the table
	private 					List<Layer<?>>					selectedLayers;				// List of layers selected
	private 					LayerType[]						selectableLayerTypes;		// type of layer types that can be selected.  Any type can be selected if null
	private						boolean							isMultiselectable;			// true if more than one layer can be selected
	private 					int								approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not



	/**
	 * Constructor of {@link LayerChooserDialog}
	 */
	public LayerChooserDialog () {
		super();
		layers = new ArrayList<Layer<?>>();
		selectedLayers = new ArrayList<Layer<?>>();
		isMultiselectable = false;
	}


	/**
	 * @return the list of selected layers
	 */
	public List<Layer<?>> getSelectedLayers() {
		return selectedLayers;
	}


	/**
	 * @return the first element of the list of selected layers.
	 * This method return the only layer selected if the property
	 * isMultiselectable is set to false.
	 */
	public Layer<?> getSelectedLayer() {
		if ((selectedLayers == null) || selectedLayers.isEmpty()) {
			return null;
		} else {
			return selectedLayers.get(0);
		}
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
		layerTable = new JTable();
		// table model
		tableModel = new LayerChooserTableModel(layers, selectedLayers, selectableLayerTypes, isMultiselectable);
		layerTable.setModel(tableModel);
		// other table properties
		layerTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scrollPane = new JScrollPane(layerTable);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		initializeColumnProperties();
		// table renderer and editors if the multiselectable property is set to false so we have radio buttons instead of check boxes
		if (!isMultiselectable) {
			layerTable.getColumnModel().getColumn(LAYER_SELECTION_INDEX).setCellRenderer(new BooleanRadioButtonRenderer());
			layerTable.getColumnModel().getColumn(LAYER_SELECTION_INDEX).setCellEditor(new BooleanRadioButtonEditor());
		}

		//Confirm button
		JButton jbConfirm = new JButton("Ok");
		jbConfirm.setToolTipText(ProjectFrame.CONFIRM_FILES);
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedLayers = tableModel.getSelectedLayers();
				approved = APPROVE_OPTION;
				dispose();
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

		////Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(BUTTON_PANEL_SIZE);
		buttonPanel.setPreferredSize(BUTTON_PANEL_SIZE);
		buttonPanel.setMinimumSize(BUTTON_PANEL_SIZE);
		buttonPanel.setMaximumSize(BUTTON_PANEL_SIZE);
		buttonPanel.setLayout(new GridLayout(2, 1));

		//TopPane
		JPanel topPane = new JPanel();
		topPane.add(jbSelect);
		topPane.add(jbUnselect);

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
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			column = layerTable.getColumnModel().getColumn(i);
			column.setHeaderValue(COLUMN_NAMES[i]);
			column.setResizable(true);
			column.setCellRenderer(tableCellRenderer);
		}
	}


	/**
	 * Sets the list of the layers displayed in the chooser dialog
	 * @param layers list of layer
	 */
	public void setLayers(List<Layer<?>> layers) {
		this.layers = layers;
	}


	/**
	 * @param isMultiselectable set to true so more than one layer can be selected
	 */
	public void setMultiselectable(boolean isMultiselectable) {
		this.isMultiselectable = isMultiselectable;
	}


	/**
	 * Sets the layer types that can be selected
	 * @param selectableLayerTypes list of layer type that can be selected
	 */
	public void setSelectableLayerTypes(LayerType[] selectableLayerTypes) {
		this.selectableLayerTypes = selectableLayerTypes;
	}


	/**
	 * Sets the list of selected layers
	 * @param selectedLayers list of layer
	 */
	public void setSelectedLayers(List<Layer<?>> selectedLayers) {
		this.selectedLayers = selectedLayers;
	}


	/**
	 * Displays the layer chooser dialog
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @param title		title of the window
	 * @return 			APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent, String title) {
		init();
		setLocationRelativeTo(parent);
		setTitle(title);
		setIconImage(Images.getApplicationImage());
		setVisible(true);
		return approved;
	}
}
