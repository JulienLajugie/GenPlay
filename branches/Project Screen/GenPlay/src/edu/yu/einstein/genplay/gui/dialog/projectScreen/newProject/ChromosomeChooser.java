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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenManager;

/**
 * This class displays every chromosome of the choosen assembly.
 * They can be selected or unselected before creating a project.
 * @author Nicolas Fourel
 */
class ChromosomeChooser extends JDialog {
	
	private static final long serialVersionUID = -6288396580036623890L;
	
	private static final 	Dimension 	DIALOG_SIZE 				= new Dimension(430, 600);					// Window size
	private static final 	Dimension 	BUTTON_PANEL_SIZE			= new Dimension(DIALOG_SIZE.width, 65);		// Button panel size
	private static final 	Color 		CHROMOSOME_CHOOSER_COLOR 	= ProjectScreenManager.getAssemblyColor();	// Chromosome chooser color
	private static final 	Color 		TABLE_COLOR 				= ProjectScreenManager.getAssemblyColor();	// Table color
	private static final 	Color 		SCROLL_COLOR 				= ProjectScreenManager.getAssemblyColor();	// Scroll bar color
	private static final 	Color 		BUTTON_PANEL_COLOR 			= ProjectScreenManager.getAssemblyColor();	// Button panel color
	private final 			String[] 					columnNames = {"#", "Name", "Length", ""};				// Column names
	private 				JTable 						chromosomeTable;										// Chromosome table
	private 				ChromosomeChooserTableModel tableModel;												// Chromosome table model
	private 				AssemblyPanel 				assemblyPanel;											// Assembly panel object
	
	
	/**
	 * Constructor of {@link ChromosomeChooser}
	 */
	protected ChromosomeChooser (AssemblyPanel assemblyPanel, String title, Map<Integer, Map<Integer, Object>> data) {
		this.assemblyPanel = assemblyPanel;
		init(title, data);
	}
	
	
	/**
	 * Init method initializes dialog components
	 * @param title	dialog title
	 * @param data	table data
	 */
	protected void init (String title, Map<Integer, Map<Integer, Object>> data) {
		//JDialog information
		setTitle(title);
		setSize(DIALOG_SIZE);
		setBackground(CHROMOSOME_CHOOSER_COLOR);
		setResizable(false);
		setLocationRelativeTo(null);
		
		//Table
		tableModel = new ChromosomeChooserTableModel(columnNames);
		tableModel.setData(data);
		chromosomeTable = new JTable();
		chromosomeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		chromosomeTable.setBackground(TABLE_COLOR);
		JScrollPane scrollPane = new JScrollPane(chromosomeTable);
		scrollPane.setBackground(SCROLL_COLOR);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		chromosomeTable.setModel(tableModel);
		chromosomeTable.repaint();
		initColumn();
		
		//Confirm button
		JButton confirmChr = new JButton("Ok");
		confirmChr.setToolTipText(ProjectScreenManager.getConfirmFiles());
		confirmChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setData(tableModel.getData());
				closeDialog();
			}
		});
		
		//Cancel button
		JButton cancelChr = new JButton("Cancel");
		cancelChr.setToolTipText(ProjectScreenManager.getCancelFiles());
		cancelChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeDialog();
			}
		});
		
		//Select button
		JButton selectChr = new JButton("Select");
		selectChr.setToolTipText(ProjectScreenManager.getSelectFiles());
		selectChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.setSelectedValue(chromosomeTable.getSelectedRows(), true);
			}
		});
		
		//Unselect button
		JButton unselectChr = new JButton("Unselect");
		unselectChr.setToolTipText(ProjectScreenManager.getUnselectFiles());
		unselectChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.setSelectedValue(chromosomeTable.getSelectedRows(), false);
			}
		});
		
		//Up button
		JButton upChr = new JButton("Up");
		upChr.setToolTipText(ProjectScreenManager.getMoveUpFiles());
		upChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.move(chromosomeTable.getSelectedRows(), true);
			}
		});
		
		//Down button
		JButton downChr = new JButton("Down");
		downChr.setToolTipText(ProjectScreenManager.getMoveDownFiles());
		downChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.move(chromosomeTable.getSelectedRows(), false);
			}
		});
		
		//Down button
		JButton basicChr = new JButton("Basics");
		basicChr.setToolTipText(ProjectScreenManager.getSelectBasicChr());
		basicChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.selectBasicChr();
			}
		});
		
		////Button panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(BUTTON_PANEL_SIZE);
		buttonPanel.setPreferredSize(BUTTON_PANEL_SIZE);
		buttonPanel.setMinimumSize(BUTTON_PANEL_SIZE);
		buttonPanel.setMaximumSize(BUTTON_PANEL_SIZE);
		buttonPanel.setBackground(BUTTON_PANEL_COLOR);
		buttonPanel.setLayout(new GridLayout(2, 1));
		
		//TopPane
		JPanel topPane = new JPanel();
		topPane.add(selectChr);
		topPane.add(unselectChr);
		topPane.add(upChr);
		topPane.add(downChr);
		topPane.add(basicChr);
		
		//BotPane
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
		
		setVisible(false);
	}
	
	
	/**
	 * This method manage the dialog closing.
	 */
	private void closeDialog () {
		this.setVisible(false);
		assemblyPanel.destruct();
	}
	
	
	/**
	 * This method initializes the column properties:
	 * - name
	 * - width
	 * - resizable
	 */
	private void initColumn () {
		TableColumn column = null;
		for (int i = 0; i < 4; i++) {
		    column = chromosomeTable.getColumnModel().getColumn(i);
		    column.setHeaderValue(columnNames[i]);
		    column.setResizable(false);
		    int width = DIALOG_SIZE.width - 26;
		    switch (i) {
			case 0:
				column.setPreferredWidth((int)Math.round(width * 0.1));
				break;
			case 1:
				column.setPreferredWidth((int)Math.round(width * 0.5));
				break;
			case 2:
				column.setPreferredWidth((int)Math.round(width * 0.3));
				break;
			case 3:
				column.setPreferredWidth((int)Math.round(width * 0.1));
				break;
			default:
				break;
			}
		}
	}
	
	
	/**
	 * @param data the data to set
	 */
	protected void setData(Map<Integer, Map<Integer, Object>> data) {
		assemblyPanel.setData(data);
		tableModel = null;
	}
	
}