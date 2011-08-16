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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;

/**
 * This class displays every chromosome of the choosen assembly.
 * They can be selected or unselected before creating a project.
 * @author Nicolas Fourel
 * @version 0.1
 */
class ChromosomeChooserDialog extends JDialog {
	
	private static final long serialVersionUID = -6288396580036623890L; //generated ID
	
	private static final 	Dimension 	DIALOG_SIZE 				= new Dimension(430, 600);					// Window size
	private static final 	Dimension 	BUTTON_PANEL_SIZE			= new Dimension(DIALOG_SIZE.width, 65);		// Button panel size
	private static final 	Color 		CHROMOSOME_CHOOSER_COLOR 	= ProjectFrame.ASSEMBLY_COLOR;	// Chromosome chooser color
	private static final 	Color 		TABLE_COLOR 				= ProjectFrame.ASSEMBLY_COLOR;	// Table color
	private static final 	Color 		SCROLL_COLOR 				= ProjectFrame.ASSEMBLY_COLOR;	// Scroll bar color
	private static final 	Color 		BUTTON_PANEL_COLOR 			= ProjectFrame.ASSEMBLY_COLOR;	// Button panel color
	private static final 	String[] 	COLUMN_NAMES 				= {"#", "Name", "Length", ""};	// Column names
	
	private static JTable 						chromosomeTable;	// Chromosome table
	private static ChromosomeChooserTableModel 	tableModel;			// Chromosome table model
	private static boolean 						validated = false;	// false if the dialog was canceled
	
	
	/**
	 * Constructor of {@link ChromosomeChooserDialog}
	 * @param title 		the chromosome chooser title
	 * @param data 			the data to display
	 */
	private ChromosomeChooserDialog (String title, List<List<Object>> data) {
		super();
		init(title, data);
	}
	
	
	/**
	 * Init method initializes dialog components
	 * @param title	dialog title
	 * @param data	table data
	 */
	private void init(String title, List<List<Object>> data) {
		//JDialog information
		setTitle(title);
		setSize(DIALOG_SIZE);
		setBackground(CHROMOSOME_CHOOSER_COLOR);
		setResizable(false);
		setModal(true);
		
		//Table
		tableModel = new ChromosomeChooserTableModel(COLUMN_NAMES);
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
		confirmChr.setToolTipText(ProjectFrame.CONFIRM_FILES);
		confirmChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				validated = true;
				dispose();
			}
		});
		
		//Cancel button
		JButton cancelChr = new JButton("Cancel");
		cancelChr.setToolTipText(ProjectFrame.CANCEL_FILES);
		cancelChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		//Select button
		JButton selectChr = new JButton("Select");
		selectChr.setToolTipText(ProjectFrame.SELECT_FILES);
		selectChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.setSelectedValue(chromosomeTable.getSelectedRows(), true);
			}
		});
		
		//Unselect button
		JButton unselectChr = new JButton("Unselect");
		unselectChr.setToolTipText(ProjectFrame.UNSELECT_FILES);
		unselectChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.setSelectedValue(chromosomeTable.getSelectedRows(), false);
			}
		});
		
		//Up button
		JButton upChr = new JButton("Up");
		upChr.setToolTipText(ProjectFrame.MOVE_UP_FILES);
		upChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.move(chromosomeTable.getSelectedRows(), true);
			}
		});
		
		//Down button
		JButton downChr = new JButton("Down");
		downChr.setToolTipText(ProjectFrame.MOVE_DOWN_FILES);
		downChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tableModel.move(chromosomeTable.getSelectedRows(), false);
			}
		});
		
		//Down button
		JButton basicChr = new JButton("Basics");
		basicChr.setToolTipText(ProjectFrame.SELECT_BASIC_CHR);
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
	private void initColumn () {
		TableColumn column = null;
		for (int i = 0; i < 4; i++) {
		    column = chromosomeTable.getColumnModel().getColumn(i);
		    column.setHeaderValue(COLUMN_NAMES[i]);
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
	 * Shows a {@link ChromosomeChooserDialog} that allows the user to select some chromosomes  
	 * @param parent parent component of the dialog
	 * @param title title of the dialog
	 * @param data list of the chromosomes with their state (selected/not selected)
	 * @return a list of chromosomes with their state (selected/not selected)
	 */
	public static List<List<Object>> getSelectedChromosomes(Component parent, String title, List<List<Object>> data) {
		ChromosomeChooserDialog ccd = new ChromosomeChooserDialog(title, data);
		ccd.setLocationRelativeTo(parent);
		ccd.setVisible(true);		
		if(validated) {
			return tableModel.getData();
		}
		else
			return null;
	}
}
