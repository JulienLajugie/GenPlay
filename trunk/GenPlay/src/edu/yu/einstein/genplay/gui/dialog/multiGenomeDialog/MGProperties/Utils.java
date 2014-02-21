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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class Utils {


	/**
	 * Creates a "no information available" panel.
	 * @return	the panel
	 */
	protected static JPanel getNoInformationPanel () {
		// The main panel
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setHgap(0);
		layout.setVgap(0);
		panel.setLayout(layout);
		panel.add(new JLabel("No information available"));

		return panel;
	}


	/**
	 * Creates a panel that contains table header
	 * @param columnNames	column names for the table
	 * @param widths		widths of the columns
	 * @return				the header panel
	 */
	public static JPanel getTableHeaderPanel (String[] columnNames, int[] widths) {
		// Initializes the panel and its layout
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setHgap(0);
		layout.setVgap(0);
		panel.setLayout(layout);

		// Initializes font metrics
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());

		// Adds panels
		for (int i = 0; i < widths.length; i++) {
			JLabel current = new JLabel();
			current.setPreferredSize(new Dimension(widths[i], fm.getHeight()));
			current.setText("<html><i>" + columnNames[i] + "</i></html>");
			panel.add(current);
		}

		// Returns the panel
		return panel;
	}


	/**
	 * Creates a panel that contains a table
	 * @param columnNames	column names for the table
	 * @param data			data for filling the table
	 * @return				the panel containing the table
	 */
	public static JPanel getTablePanel (String[] columnNames, Object[][] data) {
		// Initializes font metrics
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());

		// Initializes column width using column names
		int[] widths = new int[columnNames.length];
		for (int i = 0; i < widths.length; i++) {
			widths[i] = fm.stringWidth(columnNames[i].toString()) + 10;
		}

		// Retrieves max width of each columns
		for (int i = 0; i < data.length; i++) {				// scans the rows
			for (int j = 0; j < data[i].length; j++) {		// scans the columns
				int currentStringWidth = fm.stringWidth(data[i][j].toString()) + 10;
				if (currentStringWidth > widths[j]) {
					widths[j] = currentStringWidth;
				}
			}
		}

		// Gets the table header panel
		JPanel tableHeader = getTableHeaderPanel(columnNames, widths);

		// Gets the table panel
		JPanel tablePanel = getTablePanel(columnNames, data, widths);

		// Initializes the main panel
		JPanel panel = new JPanel();
		BorderLayout layout = new BorderLayout();
		panel.setLayout(layout);

		// Add panels to the main panel
		panel.add(tableHeader, BorderLayout.NORTH);
		panel.add(tablePanel, BorderLayout.CENTER);

		// Returns the panel
		return panel;
	}


	/**
	 * Creates a panel that contains a table
	 * @param columnNames	column names for the table
	 * @param data			data for filling the table
	 * @param widths		widths of the columns
	 * @return				the panel containing the table
	 */
	private static JPanel getTablePanel (String[] columnNames, Object[][] data, int[] widths) {
		// Initializes table
		MGDialogTableModel model = new MGDialogTableModel(columnNames, data);
		JTable table = new JTable(model);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.setCellSelectionEnabled(false);


		// Sets column width
		for (int i = 0; i < columnNames.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}

		// The main panel
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout();
		layout.setHgap(0);
		layout.setVgap(0);
		panel.setLayout(layout);
		panel.add(table);

		return panel;
	}


	/**
	 * Creates a label formatted for title section
	 * @param title	text of the label
	 * @return		the label
	 */
	public static JLabel getTitleLabel (String title) {
		JLabel label = new JLabel();
		label.setText("<html><u>" + title + "</u></html>");
		return label;
	}

}
