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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.MGDisplaySettings.FiltersData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.AddOrEditVariantFiltersDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterTable.FilterTable;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterTable.TableHeaderPanel;

/**
 * @author Nicolas Fourel
 */
public class FiltersPanel extends JPanel implements ActionListener, MouseListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 159376731917929812L;

	// Insets
	protected Insets titleInset = new Insets(10, 15, 0, 0);
	protected Insets tableTitleInset = new Insets(15, 5, 5, 0);
	protected Insets contentInset = new Insets(0, 5, 0, 0);

	protected GridBagConstraints gbc;				// Layout constraints

	private final 	TableHeaderPanel 	tableHeader;	// the header panel of the table
	private final 	JPanel 				tablePanel;		// the panel that contains the table
	private			JPanel 				buttonPanel;	// the button panel to handle the table
	private final	JLabel				tableLabel;
	private final 	FilterTable		 	table;			// the table that summarize all stripes/filters settings
	private final 	AddOrEditVariantFiltersDialog editingDialogManager;

	private		JButton				jbAddRows;		// button to add row(s)
	private		JButton				jbDeleteRows;	// button to delete row(s)
	private		JButton				jbMoveRowsUp;	// button to move row(s) to the top of the table
	private		JButton				jbMoveRowsDown;	// button to move row(s) to the bottom of the table


	/**
	 * Constructor of {@link FiltersPanel}
	 * @param title the title of the panel
	 * @param table the table to use
	 * @param editingDialogManager editing dialog manager
	 */
	public FiltersPanel (String title, FilterTable table, AddOrEditVariantFiltersDialog editingDialogManager) {
		// Sets class parameters
		this.table = table;
		this.editingDialogManager = editingDialogManager;

		// Adds listeners
		this.table.addMouseListener(this);
		addMouseListener(this);

		// Create header panel
		String[] columnNames = table.getModel().getColumnNames();
		tableHeader = new TableHeaderPanel(columnNames);

		// Create label for empty table
		tableLabel = new JLabel("No information available");

		// Create the table panel
		tablePanel = new JPanel();
		((FlowLayout)(tablePanel.getLayout())).setHgap(0);
		((FlowLayout)(tablePanel.getLayout())).setVgap(0);
		tablePanel.add(tableLabel);
		tablePanel.add(this.table);

		// Sets the table components
		tableLabel.setVisible(true);
		this.table.setVisible(false);

		//initializeTable();
		refreshPanel();
		initializeButtonPanel();

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Panel title
		gbc.gridy = 0;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel(title), gbc);

		// Table header panel
		gbc.gridy++;
		gbc.insets = contentInset;
		add(tableHeader, gbc);

		// Table panel
		gbc.gridy++;
		add(tablePanel, gbc);

		// Button panel
		gbc.gridy++;
		gbc.weighty = 1;
		add(buttonPanel, gbc);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton)e.getSource();
		if (button.equals(jbAddRows)) {
			List<FiltersData> tableData = editingDialogManager.showDialog(getRootPane());
			if (tableData != null) {
				for (FiltersData k: tableData) {
					addRow(k);
				}
			}
		} else if (button.equals(jbDeleteRows)) {
			removeRows(table.getSelectedRows());
		} else if (button.equals(jbMoveRowsUp)) {
			// Move row(s) to the top of the table
			table.moveRowsUp();

			// Graphical validation
			tablePanel.repaint();
			validate();
		} else if (button.equals(jbMoveRowsDown)) {
			// Move row(s) to the bottom of the table
			table.moveRowsDown();

			// Graphical validation
			tablePanel.repaint();
			validate();
		}

		// Update enable state of all buttons
		updateEnableButtons();
	}


	/**
	 * Add a row in the table
	 * @param data data to add
	 */
	public void addRow (FiltersData data) {
		table.addRow(data);
		refreshPanel();
	}


	/**
	 * @return the list of data
	 */
	public List<FiltersData> getData () {
		return table.getData();
	}


	/**
	 * @return the table
	 */
	public FilterTable getTable() {
		return table;
	}


	/**
	 * Creates and initializes the button panel
	 */
	private void initializeButtonPanel () {
		// Create buttons
		jbAddRows = new JButton("Add");
		jbAddRows.addActionListener(this);
		jbDeleteRows = new JButton("Delete");
		jbDeleteRows.addActionListener(this);
		jbMoveRowsUp = new JButton("Move up");
		jbMoveRowsUp.addActionListener(this);
		jbMoveRowsDown = new JButton("Move down");
		jbMoveRowsDown.addActionListener(this);

		// Create the button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		buttonPanel.add(jbAddRows);
		buttonPanel.add(jbDeleteRows);
		buttonPanel.add(jbMoveRowsUp);
		buttonPanel.add(jbMoveRowsDown);

		// Update enable state of all buttons
		updateEnableButtons();
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		int column = table.getColumnModel().getColumnIndexAtX(e.getX());	// get the column that has been clicked
		int row = e.getY()/table.getRowHeight();							// get the row that has been clicked

		boolean isIn = false;
		if ((row < table.getRowCount()) &&									// if row and column are valid
				(row >= 0) &&
				(column < table.getColumnCount()) &&
				(column >= 0)) {
			isIn = true;
		}

		if (!isIn) {														// if the click is not in the table
			table.clearSelection();											// unselect all rows and columns
		} else {
			FiltersData k = table.getModel().getCurrentData();
			if (k != null) {
				editingDialogManager.setData(k);
				//List<K> tableData = editingDialogManager.showDialog();
				editingDialogManager.showDialog(getRootPane());
				table.getModel().resetCurrentData();
				refreshPanel();
			}
		}
		updateEnableButtons();							// update the state of the panel button used for table handling
	}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mousePressed(MouseEvent e) {}


	@Override
	public void mouseReleased(MouseEvent e) {}
	/**
	 * Refreshes the whole panel
	 */
	public void refreshPanel () {
		if (table.getData().size() == 0) {
			// The header must not be visible
			tableHeader.setVisible(false);
			table.setVisible(false);

			// No information label
			tableLabel.setVisible(true);
		} else {
			// Update the column size of the table
			table.updateColumnSize();
			tableHeader.updateHeaderWidths(table.getColumnSize());

			// No information label
			tableLabel.setVisible(false);

			// The header must not be visible
			tableHeader.setVisible(true);
			table.setVisible(true);
		}
		repaint();
	}
	/**
	 * Remove rows from the table
	 * @param rows row indexes
	 */
	public void removeRows (int[] rows) {
		table.removeRows(rows);
		refreshPanel();
	}
	/**
	 * Set a list of data to the table
	 * @param data list of data
	 */
	public void setData (List<FiltersData> data) {
		// Set the data to the table
		table.setData(data);
		refreshPanel();
	}
	/**
	 * This methods enables or disables buttons whether a row is selected
	 */
	public void updateEnableButtons () {
		boolean enableDelete;
		boolean enableUp;
		boolean enableDown;
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length > 0) {					// if at least one row is selected
			enableDelete = true;						// delete button is enabled

			if (selectedRows[0] == 0) {					// if the uppest selected row is the first one in the table
				enableUp = false;						// move up button cannot be enable
			} else {
				enableUp = true;
			}

			int dataSize = table.getData().size();
			if (selectedRows[selectedRows.length - 1] == (dataSize - 1)) {	// if the lowest selected row is the last one in the table
				enableDown = false;						// move down button cannot be enable
			} else {
				enableDown = true;
			}

		} else {										// if no row is selected
			enableDelete = false;						// buttons must be disabled
			enableUp = false;
			enableDown = false;
		}

		// Enable/Disable buttons
		jbDeleteRows.setEnabled(enableDelete);
		jbMoveRowsUp.setEnabled(enableUp);
		jbMoveRowsDown.setEnabled(enableDown);
	}

}
