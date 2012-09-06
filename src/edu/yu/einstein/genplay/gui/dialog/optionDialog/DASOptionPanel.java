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
package edu.yu.einstein.genplay.gui.dialog.optionDialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.xml.sax.SAXParseException;

import edu.yu.einstein.genplay.core.DAS.DASServerList;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * Panel of the {@link OptionDialog} that allows to choose the DAS server file
 * @author Chirag Gorasia
 * @version 0.1
 */

final class DASOptionPanel extends OptionPanel {

	private static final long serialVersionUID = -4695486600325761680L; // generated ID
	private static final int TABLE_WIDTH = 330;
	private static final int TABLE_HEIGHT = 270;
	private JTable jtserverurl;
	private JButton jbadd;
	private JButton jbremove;
	static boolean tableChangedFlag = false;
	private final String[] headerNames = { "Server Name", "URL" };
	static Object[][] tableData;
	private File file;

	
	/**
	 * Inner class to do the JTable operations
	 */
	private class DASTableModel extends DefaultTableModel {

		private static final long serialVersionUID = -8041866821280601850L; // generated ID

		
		@Override
		public String getColumnName(int col) {
			return headerNames[col];
		}
		

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return tableData[rowIndex][columnIndex];
		}
		

		@Override
		public int getRowCount() {
			return tableData.length;
		}
		

		@Override
		public int getColumnCount() {
			return 2;
		}
		

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex < 2) {
				return false;
			} else {
				return true;
			}
		}
		

		@Override
		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
		

		@Override
		public void setValueAt(Object value, int row, int col) {
			tableData[row][col] = value;
			fireTableCellUpdated(row, col);
		}
		

		@Override
		public void addRow(Object[] row) {

			Object[][] temp = new Object[tableData.length][2];
			for (int i = 0; i < tableData.length; i++) {
				temp[i][0] = tableData[i][0];
				temp[i][1] = tableData[i][1];
			}
			tableData = new Object[tableData.length + 1][2];
			for (int i = 0; i < tableData.length - 1; i++) {
				tableData[i][0] = temp[i][0];
				tableData[i][1] = temp[i][1];
			}
			for (int i = 0; i < 2; i++) {
				tableData[tableData.length - 1][i] = row[i];
			}
			fireTableRowsUpdated(tableData.length + 1, tableData.length + 1);
			tableChangedFlag = true;
		}
		

		@Override
		public void removeRow(int row) {
			Object[][] temp = new Object[tableData.length - 1][2];
			int j = 0;
			for (int i = 0; i < tableData.length; i++) {
				if (i != row) {
					temp[j][0] = tableData[i][0];
					temp[j][1] = tableData[i][1];
					j++;
				}
			}
			tableData = new Object[temp.length][2];
			for (int i = 0; i < temp.length; i++) {
				tableData[i][0] = temp[i][0];
				tableData[i][1] = temp[i][1];
			}
			fireTableRowsUpdated(tableData.length, tableData.length);
			tableChangedFlag = true;
		}
	};

	
	/**
	 * Creates an instance of {@link DASOptionPanel}
	 */
	DASOptionPanel() {
		super("DAS Server");
		file = new File(projectConfiguration.getDASServerListFile());
		try {
			DASServerList dasServerList;
			if (file.exists()) {
				try {
					dasServerList = new DASServerList(file.toURI().toURL());
				} catch (SAXParseException e) {
					ExceptionManager.handleException(MainFrame.getInstance().getRootPane(), e, "DAS Server File Corrupted...loading default file");
					dasServerList = new DASServerList(new URL(projectConfiguration.getDefaultDasServerListFile()));
				}
			} else {
				dasServerList = new DASServerList(new URL(projectConfiguration.getDefaultDasServerListFile()));
			}
			tableData = new Object[dasServerList.size()][2];
			for (int i = 0; i < dasServerList.size(); i++) {
				tableData[i][0] = dasServerList.get(i).getName();
				tableData[i][1] = dasServerList.get(i).getURL();
			}

			final DefaultTableModel model = new DASTableModel();
			jtserverurl = new JTable(model);
			jtserverurl.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
			jtserverurl.setFillsViewportHeight(true);
			TableColumn column = null;
			column = jtserverurl.getColumnModel().getColumn(0);
			column.setPreferredWidth(TABLE_WIDTH / 3);
			column = jtserverurl.getColumnModel().getColumn(1);
			column.setPreferredWidth(2 * TABLE_WIDTH / 3);
			JScrollPane scrollPane = new JScrollPane(jtserverurl);
			jtserverurl.setFillsViewportHeight(true);
			jbadd = new JButton("Add");
			jbadd.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String dasServerName = "Enter DAS Server Name: ";
					String newServerName = (String) JOptionPane.showInputDialog(getRootPane(), dasServerName, "Track Name", JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (newServerName != null) {
						String dasURL = "Enter DAS Server URL: ";
						String newURL = (String) JOptionPane.showInputDialog(getRootPane(), dasURL, "Track Name", JOptionPane.QUESTION_MESSAGE, null, null, null);
						if (newURL != null) {
							Object[] newRow = { newServerName, newURL };
							model.addRow(newRow);
							jtserverurl.revalidate();
							jtserverurl.repaint();
						}
					}
				}
			});

			jbremove = new JButton("Remove");
			jbremove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int row = jtserverurl.getSelectedRow();
					model.removeRow(row);
					jtserverurl.revalidate();
					jtserverurl.repaint();
				}
			});

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 3;
			add(scrollPane, c);

			JPanel content = new JPanel();
			content.setLayout(new GridLayout(1, 3));
			content.add(jbadd);
			content.add(jbremove);
			c.gridx = 0;
			c.gridy = 4;
			c.gridwidth = 1;
			c.anchor = GridBagConstraints.LINE_START;
			add(content, c);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error loading DAS Server file");
		}
	}
}
