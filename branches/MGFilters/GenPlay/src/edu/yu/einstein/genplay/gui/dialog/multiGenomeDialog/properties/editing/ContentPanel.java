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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K>
 */
public abstract class ContentPanel<K> extends JPanel implements ActionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 159376731917929812L;

	// Insets
	protected Insets firstInset = new Insets(10, 15, 0, 0);
	protected Insets titleInset = new Insets(15, 5, 5, 0);
	protected Insets panelInset = new Insets(0, 5, 0, 0);

	protected GridBagConstraints gbc;				// Layout constraints

	private 	JPanel 				tableHeader;	// the header panel of the table
	private 	JPanel 				tablePanel;		// the panel that contains the table
	private		JPanel 				buttonPanel;	// the button panel to handle the table
	private 	ContentTable<K> 	table;			// the table that summarize all stripes settings
	private		JButton				jbDeleteRows;	// button to delete row(s)
	private		JButton				jbMoveRowsUp;	// button to move row(s) to the top of the table
	private		JButton				jbMoveRowsDown;	// button to move row(s) to the bottom of the table


	/**
	 * Constructor of {@link ContentPanel}
	 */
	protected ContentPanel () {
		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
	}


	/**
	 * Creates the table panel and its header
	 * @param table 		the table to add
	 * @param columnNames 	column names of the table
	 * @return				the panel containing the table
	 */
	protected JPanel getTablePanel (ContentTable<K> table, String[] columnNames) {
		this.table = table;

		// Creates the panel
		JPanel panel = new JPanel();
		BorderLayout layout = new BorderLayout(0, 0);
		panel.setLayout(layout);

		// Create the table
		table.updateColumnSize();

		// Create the table panel
		tablePanel = new JPanel();
		((FlowLayout)(tablePanel.getLayout())).setHgap(0);
		((FlowLayout)(tablePanel.getLayout())).setVgap(0);

		// Get column header
		tableHeader = new JPanel();
		((FlowLayout)(tableHeader.getLayout())).setHgap(0);
		((FlowLayout)(tableHeader.getLayout())).setVgap(0);
		tableHeader.add(Utils.getTableHeaderPanel(columnNames, table.getColumnSize()));

		// Create buttons
		jbDeleteRows = new JButton("Delete");
		jbDeleteRows.addActionListener(this);
		jbMoveRowsUp = new JButton("Move up");
		jbMoveRowsUp.addActionListener(this);
		jbMoveRowsDown = new JButton("Move down");
		jbMoveRowsDown.addActionListener(this);

		// Create the button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		buttonPanel.add(jbDeleteRows);
		buttonPanel.add(jbMoveRowsUp);
		buttonPanel.add(jbMoveRowsDown);

		// Add the table if data exists
		if (table.getData().size() > 0) {
			tablePanel.add(table);
		} else {
			tableHeader.setVisible(false);
			buttonPanel.setVisible(false);
			tablePanel.add(new JLabel("No information available"));
		}

		// Add components to the main panel
		panel.add(tableHeader, BorderLayout.NORTH);
		panel.add(tablePanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		// Update enable state of all buttons
		updateEnableButtons();

		// Return the panel
		return panel;
	}


	/**
	 * Set a list of data to the table
	 * @param data list of data
	 */
	protected void setData (List<K> data) {
		// Set the data to the table
		table.setData(data);

		// Reset the table panel to zero
		tablePanel.removeAll();

		// Set layout gaps
		((FlowLayout)(tablePanel.getLayout())).setHgap(0);
		((FlowLayout)(tablePanel.getLayout())).setVgap(0);

		// Add the table to the panel
		tablePanel.add(table);

		// Table is visible so other panel must be visible too
		tableHeader.setVisible(true);
		buttonPanel.setVisible(true);

		// Graphical validation
		tablePanel.repaint();
		validate();
	}


	/**
	 * Add a row in the table
	 * @param data data to add
	 */
	public void addRow (K data) {
		if (table.getData().size() == 0) {
			refreshTable(false);
		}
		table.addRow(data);
		updateTableSize();
		updateEnableButtons();
	}


	/**
	 * Remove rows from the table
	 * @param rows row indexes
	 */
	public void removeRows (int[] rows) {
		table.removeRows(rows);
		if (table.getData().size() == 0) {
			refreshTable(true);
		}
		updateTableSize();
	}


	/**
	 * Updates the size of the table and its header according to longest elements.
	 */
	public void updateTableSize () {
		// Update the column size of the table 
		table.updateColumnSize();

		// Reset the table header panel to zero
		tableHeader.removeAll();

		// Set layout gaps
		((FlowLayout)(tableHeader.getLayout())).setHgap(0);
		((FlowLayout)(tableHeader.getLayout())).setVgap(0);

		// Gets the header panel
		@SuppressWarnings("unchecked")
		String[] columnNames = ((ContentTableModel<K>)table.getModel()).getColumnNames();
		tableHeader.add(Utils.getTableHeaderPanel(columnNames, table.getColumnSize()));

		// Graphical validation
		tableHeader.repaint();
		validate();
	}


	/**
	 * @return the table
	 */
	public ContentTable<K> getTable() {
		return table;
	}


	/**
	 * Add an action listener to the 'Add' and 'Apply' buttons
	 * @param el the action listener
	 */
	public void addListener (EventListener el) {
		table.addMouseListener((MouseListener) el);
		this.addMouseListener((MouseListener) el);
		jbDeleteRows.addActionListener((ActionListener) el);
	}


	/**
	 * Refresh the panel:
	 * - set to an "unselected" state the editing panel (empty)
	 * - refresh the content pane (table, headers, buttons)
	 */
	public void refresh () {
		if (table.getData().size() == 0) {
			refreshTable(true);
		} else {
			refreshTable(false);
		}
	}


	/**
	 * Refreshes the table panel adding the table or a label wether the table is empty
	 * If the table is signaled as empty, a label is shown, if not, the table is shown.
	 * @param isEmpty signal for showing the table
	 */
	private void refreshTable (boolean isEmpty) {

		// Reset the table panel to zero
		tablePanel.removeAll();

		// Set layout gaps
		((FlowLayout)(tablePanel.getLayout())).setHgap(0);
		((FlowLayout)(tablePanel.getLayout())).setVgap(0);

		if (isEmpty) {
			// No information label
			tablePanel.add(new JLabel("No information available"));
		} else {
			// Add the table to the panel
			tablePanel.add(table);
		}

		// Table is not visible so other panel must not be visible
		tableHeader.setVisible(!isEmpty);
		buttonPanel.setVisible(!isEmpty);

		// Graphical validation
		tablePanel.repaint();
		
		updateTableSize();
		
		validate();
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
			if (selectedRows[selectedRows.length - 1] == dataSize - 1) {	// if the lowest selected row is the last one in the table
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


	/**
	 * @return the jbDeleteRows
	 */
	public JButton getDeleteRowsButton() {
		return jbDeleteRows;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton)e.getSource();
		if (button.equals(jbDeleteRows)) {
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
	 * Reset the panel to an "empty" state
	 */
	//protected void clearSelection () {
	//}

}
