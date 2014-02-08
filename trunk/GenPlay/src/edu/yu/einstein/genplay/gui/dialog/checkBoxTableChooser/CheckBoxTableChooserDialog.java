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
package edu.yu.einstein.genplay.gui.dialog.checkBoxTableChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.table.TableColumn;

import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class displays a dialog windows containing a table with two columns:
 * The first column contains a list of item of a specified type (generic parameter)
 * The second column shows a list of combo-boxes to select items
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 * @param <T> type of the items displayed in the table
 */
public class CheckBoxTableChooserDialog<T> extends JDialog {

	private static final long serialVersionUID = -6288396580036623890L; //generated ID

	/**
	 * Return value when OK has been clicked.
	 */
	public 		static 	final 	int 		APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public 		static 	final 	int 		CANCEL_OPTION = 1;

	protected 	static	final 	String[] 	COLUMN_NAMES 				= {"Name", "Selected"};			// Column names
	private 	static	final 	Color 		ITEM_CHOOSER_COLOR 			= ProjectFrame.ASSEMBLY_COLOR;	// Item chooser color

	private JTable 					itemTable;					// table containing the items and the check-boxes
	private CheckBoxTableModel<T> 	tableModel;					// table model
	private List<T> 				fullItemList;				// list of items to display
	private List<T>					selectedItem;				// List of selected items
	private	boolean					ordering;					// Allow user to enable the ordering
	private int						approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not



	/**
	 * Constructor of {@link CheckBoxTableChooserDialog}
	 */
	public CheckBoxTableChooserDialog() {
		super();
		ordering = true;
	}


	/**
	 * @return the list of all items
	 */
	public List<T> getItems() {
		return this.fullItemList;
	}


	/**
	 * @return the list of selected items
	 */
	public List<T> getSelectedItems() {
		return this.selectedItem;
	}


	/**
	 * Initializes dialog components
	 */
	private void init() {
		//JDialog information
		setBackground(ITEM_CHOOSER_COLOR);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setIconImages(Images.getApplicationImages());
		//Table
		tableModel = new CheckBoxTableModel<T>();
		tableModel.setData(fullItemList, selectedItem);
		itemTable = new JTable();
		itemTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		itemTable.setBackground(ITEM_CHOOSER_COLOR);
		JScrollPane scrollPane = new JScrollPane(itemTable);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		scrollPane.setBackground(ITEM_CHOOSER_COLOR);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		itemTable.setModel(tableModel);
		itemTable.repaint();
		initializeColumnProperties();

		//Confirm button
		JButton confirmChr = new JButton("Ok");
		confirmChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fullItemList = tableModel.getFullItemList();
				selectedItem = tableModel.getSelectedItems();
				approved = APPROVE_OPTION;
				dispose();
			}
		});

		//Cancel button
		JButton cancelChr = new JButton("Cancel");
		cancelChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = CANCEL_OPTION;
				dispose();
			}
		});

		// we want the size of the two buttons to be equal
		confirmChr.setPreferredSize(cancelChr.getPreferredSize());

		//Select button
		JButton jbSelect = new JButton("Select");
		jbSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.setSelectedValue(itemTable.getSelectedRows(), true);
			}
		});

		//Unselect button
		JButton jbUnselect = new JButton("Unselect");
		jbUnselect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.setSelectedValue(itemTable.getSelectedRows(), false);
			}
		});

		JButton jbUp = null;
		JButton jbDownChr = null;
		if (ordering) {
			//Up button
			jbUp = new JButton("Up");
			jbUp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					tableModel.move(itemTable.getSelectedRows(), true);
				}
			});

			//Down button
			jbDownChr = new JButton("Down");
			jbDownChr.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					tableModel.move(itemTable.getSelectedRows(), false);
				}
			});
		}

		////Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(ITEM_CHOOSER_COLOR);
		buttonPanel.setLayout(new GridLayout(2, 1));

		//TopPane
		JPanel topPane = new JPanel();
		topPane.add(jbSelect);
		topPane.add(jbUnselect);
		if (ordering) {
			topPane.add(jbUp);
			topPane.add(jbDownChr);
		}

		//BotPane
		getRootPane().setDefaultButton(confirmChr);
		JPanel botPane = new JPanel();
		botPane.add(confirmChr);
		botPane.add(cancelChr);

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
		TableColumn column = null;
		for (int i = 0; i < 2; i++) {
			column = itemTable.getColumnModel().getColumn(i);
			column.setHeaderValue(COLUMN_NAMES[i]);
			column.setResizable(true);
		}
	}


	/**
	 * Sets the list of all items available for selection
	 * @param list list of items
	 */
	public void setItems(List<T> list) {
		if (list == null) {
			this.fullItemList = new ArrayList<T>();
		} else {
			this.fullItemList = list;
		}
	}


	/**
	 * Sets the ordering features.
	 * Allow users to order the list of displayed items.
	 * @param bool	boolean (true: enable; false:disable)
	 */
	public void setOrdering(boolean bool) {
		ordering = bool;
	}


	/**
	 * Sets the list of selected items
	 * @param list list of item
	 */
	public void setSelectedItems(List<T> list) {
		if (list == null) {
			this.selectedItem = new ArrayList<T>();
		} else {
			this.selectedItem = list;
		}
	}


	/**
	 * Displays the item chooser dialog
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @return 			APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		init();
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
