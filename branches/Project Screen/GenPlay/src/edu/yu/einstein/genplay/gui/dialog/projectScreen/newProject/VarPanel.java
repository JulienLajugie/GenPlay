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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenManager;
import edu.yu.einstein.genplay.gui.fileFilter.VarFilter;

/**
 * This class displays the var files table.
 * @author Nicolas Fourel
 */
class VarPanel extends JPanel{
	
	private static final long serialVersionUID = -2403582909509760003L;
	
	private static final Double 	GLOBAL_WIDTH_RATIO = 0.9;		// Global panel width ratio
	private static final Double 	PANEL_WIDTH_RATIO = 0.893;		// Panel width ratio
	private static final Double 	TABLE_HEIGHT_RATIO = 0.75;		// Table panel height ratio
	private static final Double 	BUTTON_HEIGHT_RATIO = 0.15;		// Button panel height ratio
	private static final Dimension 	VAR_DIM = ProjectScreenManager.getVarDim();							// Var panel dimension
	private static final Dimension 	TABLE_DIM = new Dimension(											// Table panel dimension
												(int)Math.round(VAR_DIM.width * PANEL_WIDTH_RATIO), 
												(int)Math.round(VAR_DIM.height * TABLE_HEIGHT_RATIO));
	private static final Dimension 	BUTTOM_DIM = new Dimension(											// Button panel dimension
												(int)Math.round(VAR_DIM.width * PANEL_WIDTH_RATIO), 
												(int)Math.round(VAR_DIM.height * BUTTON_HEIGHT_RATIO));
	private static final Dimension 	GLOBAL_DIM = new Dimension(											// Global panel dimension
												(int)Math.round(VAR_DIM.width * GLOBAL_WIDTH_RATIO), 
												(int)Math.round(VAR_DIM.height));
	private final 	String[] 		columnNames = {"#", "Name"};	// Table column names
	private 		VarTableModel 	tableModel;						// Table model
	private 		JTable 			varTable;						// Var files table
	private 		JScrollPane 	scrollPane;						// Scroll bar panel
	
	
	/**
	 * Constructor of {@link VarPanel}
	 */
	protected VarPanel () {
		//Background color
		setBackground(ProjectScreenManager.getVarColor());
		
		//Panel size
		setSize(ProjectScreenManager.getVarDim());
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());
		
		//Layout
		FlowLayout flow = new FlowLayout(FlowLayout.CENTER, 0, 0);
		setLayout(flow);
		
		//Table
		tableModel = new VarTableModel(columnNames);
		varTable = new JTable(tableModel);
		varTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		varTable.setBackground(ProjectScreenManager.getVarColor());
		scrollPane = new JScrollPane(varTable);
		scrollPane.setSize(TABLE_DIM);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBackground(ProjectScreenManager.getVarColor());
		initColumn();
		
		//FileChooser
		final JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(ConfigurationManager.getInstance().getDefaultDirectory()));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		VarFilter filter = new VarFilter();
	    chooser.setFileFilter(filter);
		
		//Add button
		JButton addVarFiles = new JButton("Add");
		addVarFiles.setToolTipText(ProjectScreenManager.getAddVarFiles());
		addVarFiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = chooser.showOpenDialog(ProjectScreenManager.getInstance());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					int row = tableModel.getRowCount();
					for (File file: chooser.getSelectedFiles()) {
						tableModel.addRow(row, file);
						row++;
					}
					varTable.revalidate();
				}
			}
		});
		
		//Del button
		JButton delVarFiles = new JButton("Del");
		delVarFiles.setToolTipText(ProjectScreenManager.getDelVarFiles());
		delVarFiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.removeRows(varTable.getSelectedRows());
			}
		});
		
		//Up button
		JButton upVarFiles = new JButton("Up");
		upVarFiles.setToolTipText(ProjectScreenManager.getMoveUpFiles());
		upVarFiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.move(varTable.getSelectedRows(), true);
			}
		});
		
		//Down button
		JButton downVarFiles = new JButton("Down");
		downVarFiles.setToolTipText(ProjectScreenManager.getMoveDownFiles());
		downVarFiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.move(varTable.getSelectedRows(), false);
			}
		});
		
		//Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(BUTTOM_DIM);
		buttonPanel.setPreferredSize(BUTTOM_DIM);
		buttonPanel.setMaximumSize(BUTTOM_DIM);
		buttonPanel.setBackground(ProjectScreenManager.getTableButtonColor());
		buttonPanel.add(addVarFiles);
		buttonPanel.add(delVarFiles);
		buttonPanel.add(upVarFiles);
		buttonPanel.add(downVarFiles);
		
		//Global panel
		JPanel globalPanel = new JPanel();
		globalPanel.setSize(GLOBAL_DIM);
		globalPanel.setPreferredSize(GLOBAL_DIM);
		BorderLayout border = new BorderLayout();
		border.setHgap(0);
		border.setVgap(0);
		globalPanel.setLayout(border);
		
		//Add panels
		globalPanel.add(scrollPane, BorderLayout.CENTER);
		globalPanel.add(buttonPanel, BorderLayout.SOUTH);
		add(globalPanel);
		setVisible(false);
	}
	
	
	/**
	 * This method initializes the column properties:
	 * - name
	 * - width
	 * - resizable
	 */
	private void initColumn () {
		TableColumn column = null;
		for (int i = 0; i < 2; i++) {
		    column = varTable.getColumnModel().getColumn(i);
		    column.setHeaderValue(columnNames[i]);
		    column.setResizable(false);
		    if (i == 1) {
		        column.setPreferredWidth((int)Math.round(scrollPane.getWidth() * 0.9));
		    } else {
		    	column.setPreferredWidth((int)Math.round(scrollPane.getWidth() * 0.1));
		    }
		}
	}
	
	
	/**
	 * @return the var file list
	 */
	protected List<File> getFiles () {
		return tableModel.getFiles();
	}
	
}