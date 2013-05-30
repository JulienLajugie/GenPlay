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
package edu.yu.einstein.genplay.gui.dialog.SCWListStatsDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListStats.SCWListStats;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Dialog showing the statistics of a {@link SCWList}
 * @author Julien Lajugie
 */
public class SCWListStatsDialog extends JDialog {

	/** Generated serial ID */
	private static final long serialVersionUID = 8173952680722238489L;


	/**
	 * Shows the dialog
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param scwListStats {@link SCWListStats} to show in the dialog window
	 */
	public static void showDialog(Component parent, SCWListStats scwListStats) {
		SCWListStatsDialog scwListStatsDialog = new SCWListStatsDialog(scwListStats);
		scwListStatsDialog.setLocationRelativeTo(parent);
		scwListStatsDialog.setVisible(true);
	}


	/**
	 * Creates an instance of {@link SCWListStatsDialog}. Initialize the dialog and its subcomponents
	 * @param scwListStats stats to show in the dialog
	 */
	private SCWListStatsDialog(SCWListStats scwListStats) {
		super();
		// create the table
		TableModel statsTableModel = new SCWListStatsTableModel(scwListStats);
		JTable jtStats = new JTable(statsTableModel);
		jtStats.setDefaultRenderer(Object.class, new SCWListStatsRenderer());
		// other table properties
		jtStats.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		initializeColumnProperties(jtStats);

		// creates the scroll pane containing the table
		JScrollPane scrollPane = new JScrollPane(jtStats);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(jtStats.getPreferredSize().width + 20, scrollPane.getPreferredSize().height));

		// create the ok button
		JButton jbOK = new JButton("OK");
		jbOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// add the components
		BorderLayout border = new BorderLayout();
		setLayout(border);
		add(scrollPane, BorderLayout.CENTER);
		add(jbOK, BorderLayout.SOUTH);

		// set dialog properties
		setIconImage(Images.getApplicationImage());
		setModal(true);
		pack();
		getRootPane().setDefaultButton(jbOK);
	}


	/**
	 * Computes the width of the specified column.
	 * The width is equal to the largest row of the column
	 * @param jtStats
	 * @param column
	 * @param columnIndex
	 * @return the width of the largest row of the specified column
	 */
	private int computeColumnWidth(JTable jtStats, TableColumn column, int columnIndex) {
		// header row width
		Object value = column.getHeaderValue();
		TableCellRenderer renderer = column.getHeaderRenderer();
		renderer = jtStats.getTableHeader().getDefaultRenderer();
		Component component = renderer.getTableCellRendererComponent(jtStats, value, false, false, -1, columnIndex);
		int columnWidth = component.getPreferredSize().width;

		// other row width
		for (int rowIndex = 0; rowIndex < jtStats.getRowCount(); rowIndex++) {
			TableCellRenderer cellRenderer = jtStats.getCellRenderer(rowIndex, columnIndex);
			component = jtStats.prepareRenderer(cellRenderer, rowIndex, columnIndex);
			int rowWidth = component.getPreferredSize().width + jtStats.getIntercellSpacing().width;
			columnWidth = Math.max(columnWidth, rowWidth);
		}
		return columnWidth;
	}


	/**
	 * This method initializes column properties
	 */
	private void initializeColumnProperties (JTable jtStats) {
		TableColumn column = null;
		String[] columnHeaders = SCWListStatsTableModel.COLUMN_HEADERS;
		for (int columnIndex = 0; columnIndex < jtStats.getColumnCount(); columnIndex++) {
			column = jtStats.getColumnModel().getColumn(columnIndex);
			column.setResizable(false);
			column.setHeaderValue(columnHeaders[columnIndex]);

			int columnWidth = computeColumnWidth(jtStats, column, columnIndex) + 5;
			column.setPreferredWidth(columnWidth);
		}
	}
}
