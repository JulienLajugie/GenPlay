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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.stripesEditing;

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
 */
class StripesContentPanel extends JPanel implements ActionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 159376731917929812L;

	private 	JPanel 			tableHeader;	// the header panel of the table
	private 	JPanel 			tablePanel;		// the panel that contains the table
	private		JPanel 			buttonPanel;	// the button panel to handle the table
	private 	StripesTable 	table;			// the table that summarize all stripes settings
	private		JButton			jbDeleteRows;	// button to delete row(s)
	private		JButton			jbMoveRowsUp;	// button to move row(s) to the top of the table
	private		JButton			jbMoveRowsDown;	// button to move row(s) to the bottom of the table


	/**
	 * Constructor of {@link StripesContentPanel}
	 */
	protected StripesContentPanel () {
		// Insets
		Insets firstInset = new Insets(10, 15, 0, 0);
		Insets titleInset = new Insets(15, 5, 5, 0);
		Insets panelInset = new Insets(0, 5, 0, 0);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Panel title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = firstInset;
		add(Utils.getTitleLabel("Stripes settings"), gbc);

		// Genome selection title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = titleInset;
		add(Utils.getTitleLabel("Table"), gbc);

		// Genome selection box
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = panelInset;
		gbc.weighty = 1;
		add(getTablePanel(), gbc);
	}


	/**
	 * Creates the table panel and its header
	 * @return
	 */
	private JPanel getTablePanel () {
		// Creates the panel
		JPanel panel = new JPanel();
		BorderLayout layout = new BorderLayout(0, 0);
		panel.setLayout(layout);

		// Create the table
		table = new StripesTable();
		table.updateColumnSize();

		// Create the table panel
		tablePanel = new JPanel();
		((FlowLayout)(tablePanel.getLayout())).setHgap(0);
		((FlowLayout)(tablePanel.getLayout())).setVgap(0);

		// Get column header
		tableHeader = new JPanel();
		((FlowLayout)(tableHeader.getLayout())).setHgap(0);
		((FlowLayout)(tableHeader.getLayout())).setVgap(0);
		String[] columnNames = ((StripesTableModel)table.getModel()).getColumnNames();
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
	protected void setData (List<StripeData> data) {
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
	protected void addRow (StripeData data) {
		if (table.getData().size() == 0) {
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
		table.addRow(data);
		updateTableSize();
	}


	/**
	 * Remove a row from the table
	 * @param row row index
	 */
	protected void removeRows (int[] rows) {
		table.removeRows(rows);
		if (table.getData().size() == 0) {
			// Reset the table panel to zero
			tablePanel.removeAll();
			
			// Set layout gaps
			((FlowLayout)(tablePanel.getLayout())).setHgap(0);
			((FlowLayout)(tablePanel.getLayout())).setVgap(0);
			
			// No information label
			tablePanel.add(new JLabel("No information available"));
			
			// Table is not visible so other panel must not be visible
			tableHeader.setVisible(false);
			buttonPanel.setVisible(false);
			
			// Graphical validation
			tablePanel.repaint();
			validate();
		}
		updateTableSize();
	}


	/**
	 * Updates the size of the table and its header according to longest elements.
	 */
	protected void updateTableSize () {
		// Update the column size of the table 
		table.updateColumnSize();
		
		// Reset the table header panel to zero
		tableHeader.removeAll();
		
		// Set layout gaps
		((FlowLayout)(tableHeader.getLayout())).setHgap(0);
		((FlowLayout)(tableHeader.getLayout())).setVgap(0);
		
		// Gets the header panel
		String[] columnNames = ((StripesTableModel)table.getModel()).getColumnNames();
		tableHeader.add(Utils.getTableHeaderPanel(columnNames, table.getColumnSize()));
		
		// Graphical validation
		tableHeader.repaint();
		validate();
	}


	/**
	 * @return the table
	 */
	protected StripesTable getTable() {
		return table;
	}


	/**
	 * Add an action listener to the 'Add' and 'Apply' buttons
	 * @param al the action listener
	 */
	protected void addListener (EventListener el) {
		table.addMouseListener((MouseListener) el);
		jbDeleteRows.addActionListener((ActionListener) el);
	}

	
	/**
	 * This methods enables or disables buttons whether a row is selected
	 */
	protected void updateEnableButtons () {
		boolean enable;
		if (table.getSelectedRows().length > 0) {	// if at least one row is selected
			enable = true;							// buttons must be enabled
		} else {									// if no row is selected
			enable = false;							// buttons must be disabled
		}
		
		// Enable/Disable buttons
		jbDeleteRows.setEnabled(enable);
		jbMoveRowsUp.setEnabled(enable);
		jbMoveRowsDown.setEnabled(enable);
	}
	

	/**
	 * @return the jbDeleteRows
	 */
	protected JButton getDeleteRowsButton() {
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

}
