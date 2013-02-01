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
package edu.yu.einstein.genplay.gui.dialog.chromosomeChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Images;

/**
 * This class displays a list of chromosome in order to make a selection.
 * This class uses two list of chromosome, one for the display and another one to store the selected chromosome.
 * Accessors are available for these two list.
 * It is to the caller component to use these accessors in order to give the desired behavior.
 * 
 * Be careful, the displayed list can be ordered, in order to keep this order in some case, do not forget to store the list of displayed chromosome.
 * To ability to ordering the displayed list can be disabled (enabled by default).
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ChromosomeChooserDialog extends JDialog {

	private static final long serialVersionUID = -6288396580036623890L; //generated ID

	/**
	 * Return value when OK has been clicked.
	 */
	public 		static 	final 	int 		APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public 		static 	final 	int 		CANCEL_OPTION = 1;

	protected 	static	final 	String[] 	COLUMN_NAMES 				= {"#", "Name", "Length", ""};	// Column names
	private 	static	final 	Dimension 	DIALOG_SIZE 				= new Dimension(430, 600);					// Window size
	private 	static	final 	Dimension 	BUTTON_PANEL_SIZE			= new Dimension(DIALOG_SIZE.width, 65);		// Button panel size
	private 	static	final 	Color 		CHROMOSOME_CHOOSER_COLOR 	= ProjectFrame.ASSEMBLY_COLOR;	// Chromosome chooser color


	private 	static 			JTable 							chromosomeTable;			// Chromosome table
	private 	static 			ChromosomeChooserTableModel 	tableModel;					// Chromosome table model
	private 					List<Chromosome> 				fullChromosomeList;			// List of chromosome to display
	private 					List<Chromosome>				selectedChromosome;			// List of selected chromosome
	private						boolean							ordering;					// Allow user to enable the ordering
	private 					int								approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not



	/**
	 * Constructor of {@link ChromosomeChooserDialog}
	 */
	public ChromosomeChooserDialog () {
		super();
		ordering = true;
	}


	/**
	 * Sets the list of all chromosome available for selection
	 * @param list list of chromosome
	 */
	public void setFullChromosomeList (List<Chromosome> list) {
		if (list == null) {
			fullChromosomeList = new ArrayList<Chromosome>();
		} else {
			fullChromosomeList = list;
		}
	}


	/**
	 * Sets the list of selected chromosome
	 * @param list list of chromosome
	 */
	public void setListOfSelectedChromosome (List<Chromosome> list) {
		if (list == null) {
			selectedChromosome = new ArrayList<Chromosome>();
		} else {
			selectedChromosome = list;
		}
	}


	/**
	 * Sets the ordering features.
	 * Allow users to order the list of displayed chromosome.
	 * @param bool	boolean (true: enable; false:disable)
	 */
	public void setOrdering(boolean bool) {
		ordering = bool;
	}


	/**
	 * @return the list of every chromosome
	 */
	public List<Chromosome> getFullChromosomeList () {
		return fullChromosomeList;
	}


	/**
	 * @return the list of selected chromosome
	 */
	public List<Chromosome> getListOfSelectedChromosome () {
		return selectedChromosome;
	}


	/**
	 * Displays the chromosome chooser dialog
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @return 			APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		init();
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	/**
	 * Initializes dialog components
	 */
	private void init() {
		//JDialog information
		setSize(DIALOG_SIZE);
		setBackground(CHROMOSOME_CHOOSER_COLOR);
		setTitle("Choose Chromosomes");
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setModal(true);

		//Table
		tableModel = new ChromosomeChooserTableModel();
		tableModel.setData(fullChromosomeList, selectedChromosome);
		chromosomeTable = new JTable();
		chromosomeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		chromosomeTable.setBackground(CHROMOSOME_CHOOSER_COLOR);
		JScrollPane scrollPane = new JScrollPane(chromosomeTable);
		scrollPane.setBackground(CHROMOSOME_CHOOSER_COLOR);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chromosomeTable.setModel(tableModel);
		chromosomeTable.repaint();
		initializeColumnProperties();

		//Confirm button
		JButton confirmChr = new JButton("Ok");
		confirmChr.setToolTipText(ProjectFrame.CONFIRM_FILES);
		confirmChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fullChromosomeList = tableModel.getFullChromosomeList();
				selectedChromosome = tableModel.getSelectedChromosome();
				approved = APPROVE_OPTION;
				dispose();
			}
		});

		//Cancel button
		JButton cancelChr = new JButton("Cancel");
		cancelChr.setToolTipText(ProjectFrame.CANCEL_FILES);
		cancelChr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = CANCEL_OPTION;
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

		JButton upChr = null;
		JButton downChr = null;
		if (ordering) {
			//Up button
			upChr = new JButton("Up");
			upChr.setToolTipText(ProjectFrame.MOVE_UP_FILES);
			upChr.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					tableModel.move(chromosomeTable.getSelectedRows(), true);
				}
			});

			//Down button
			downChr = new JButton("Down");
			downChr.setToolTipText(ProjectFrame.MOVE_DOWN_FILES);
			downChr.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					tableModel.move(chromosomeTable.getSelectedRows(), false);
				}
			});
		}

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
		buttonPanel.setBackground(CHROMOSOME_CHOOSER_COLOR);
		buttonPanel.setLayout(new GridLayout(2, 1));

		//TopPane
		JPanel topPane = new JPanel();
		topPane.add(selectChr);
		topPane.add(unselectChr);
		if (ordering) {
			topPane.add(upChr);
			topPane.add(downChr);
		}
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
	private void initializeColumnProperties () {
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
}
