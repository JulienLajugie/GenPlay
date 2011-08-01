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
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFTableTools extends JPanel {

	private static final long serialVersionUID = -3988945009145488219L;

	private JComboBox columnBox;
	private String[] columnNames;
	private VCFLoader loader;


	protected VCFTableTools (VCFLoader loader) {
		this.loader = loader;
		this.columnNames = loader.getVCFData().getColumnNames();


		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(layout);


		Dimension buttonDim = new Dimension(VCFLoader.BUTTON_SIDE * 4, VCFLoader.BUTTON_SIDE);
		Dimension labelDim = new Dimension(VCFLoader.BUTTON_SIDE * 6, VCFLoader.BUTTON_SIDE);

		JLabel rowLabel = new JLabel("Row edition: ");
		rowLabel.setSize(labelDim);
		rowLabel.setMinimumSize(labelDim);
		rowLabel.setMaximumSize(labelDim);
		rowLabel.setPreferredSize(labelDim);


		JButton addRow = new JButton("Add");
		addRow.setSize(buttonDim);
		addRow.setMinimumSize(buttonDim);
		addRow.setMaximumSize(buttonDim);
		addRow.setPreferredSize(buttonDim);
		addRow.setToolTipText(VCFLoader.ADD_ROW);
		addRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getLoader().addRow();
			}
		});

		JButton removeRow = new JButton("Remove");
		removeRow.setSize(buttonDim);
		removeRow.setMinimumSize(buttonDim);
		removeRow.setMaximumSize(buttonDim);
		removeRow.setPreferredSize(buttonDim);
		removeRow.setToolTipText(VCFLoader.REMOVE_ROW);
		removeRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int[] rows = getLoader().getTable().getSelectedRows();
				int[] rowsInv = new int[rows.length];
				int cpt = 0; 
				for (int i = rows.length - 1; i >= 0; i--) {
					rowsInv[cpt] = rows[i];
					cpt++;
				}
				getLoader().removeRows(rowsInv);
			}
		});




		JLabel editLabel = new JLabel("Column list edition: ");
		editLabel.setSize(labelDim);
		editLabel.setMinimumSize(labelDim);
		editLabel.setMaximumSize(labelDim);
		editLabel.setPreferredSize(labelDim);


		columnBox = new JComboBox();
		for (int i = 0; i < columnNames.length; i++) {
			if (i != 2 && i != 4) {
				columnBox.addItem(columnNames[i]);
			}
		}


		JButton editList = new JButton("Edit");
		editList.setSize(buttonDim);
		editList.setMinimumSize(buttonDim);
		editList.setMaximumSize(buttonDim);
		editList.setPreferredSize(buttonDim);
		editList.setToolTipText(VCFLoader.EDIT_COLUMN);
		editList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String value = columnBox.getSelectedItem().toString();
				String[] names = getLoader().getVCFData().getColumnNames();
				int col = -1;
				for (int i = 0; i < names.length; i++) {
					if (value.equals(names[i])) {
						col = i;
						break;
					}
				}
				if (col != -1) {
					getLoader().getVCFData().displayList(col);
				}
			}
		});



		//rowLabel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(15, 0, 0, 0);
		add(rowLabel, gbc);

		//addRow
		gbc.gridx = 1;
		gbc.gridy = 0;
		add(addRow, gbc);

		//removeRow
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(15, 10, 0, 0);
		add(removeRow, gbc);


		//editLabel
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(15, 0, 15, 0);
		add(editLabel, gbc);

		//columnBox
		gbc.gridx = 1;
		gbc.gridy = 1;
		add(columnBox, gbc);

		//editList
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.insets = new Insets(15, 10, 15, 0);
		add(editList, gbc);



	}


	/**
	 * @return the loader
	 */
	private VCFLoader getLoader() {
		return loader;
	}


}
