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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.selectionPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe.MultiGenomePanel;


/**
 * 
 * @author Nicolas Fourel
 */
class CellSelectionPanel extends JPanel {

	private static final long serialVersionUID = -9164432789457260560L;

	private JCheckBox checkBox;
	private JButton colorButton;
	private Color color;
	
	
	protected CellSelectionPanel (Color color) {
		//Dimension
		Dimension panelDim = new Dimension(MultiGenomePanel.getCellVcfTypeWidth(), MultiGenomePanel.getRowHeight());
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);
		
		
		//Layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		//Check box
		checkBox = new JCheckBox();
		checkBox.setBorder(null);
		checkBox.setMargin(new Insets(0, 0, 0, 0));
		
		//colorChooser
		
		//Color label
		colorButton = new JButton();
		Dimension labelDim = new Dimension(MultiGenomePanel.getColorButtonSide(), MultiGenomePanel.getColorButtonSide());
		colorButton.setSize(labelDim);
		colorButton.setPreferredSize(labelDim);
		colorButton.setMinimumSize(labelDim);
		colorButton.setMaximumSize(labelDim);
		colorButton.setBorder(null);
		colorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setColor(JColorChooser.showDialog(getCurrentInstance(), "Choose color", getColor()));
			}
		});
		setColor(color);
		
		
		//Fake label
		JLabel fake = new JLabel();
		Dimension fakeDim = new Dimension(MultiGenomePanel.getColorButtonSide()/2, MultiGenomePanel.getColorButtonSide());
		fake.setSize(fakeDim);
		fake.setPreferredSize(fakeDim);
		fake.setMinimumSize(fakeDim);
		fake.setMaximumSize(fakeDim);
		
		
		//checkBox
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(checkBox, gbc);
		
		//fake
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(fake, gbc);
		
		//colorLabel
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(colorButton, gbc);
		
	}
	
	
	/**
	 * Initializes cell elements when it has been already set during a previous selection:
	 * - checkbox
	 * - color
	 * @param color
	 */
	protected void initCell (Color color) {
		checkBox.setSelected(true);
		setColor(color);
	}
	
	
	/**
	 * @return the current CellSelectionPanel instance
	 */
	private CellSelectionPanel getCurrentInstance () {
		return this;
	}
	
	
	protected void betaLimit () {
		checkBox.setEnabled(false);
		colorButton.setEnabled(false);
	}
	
	
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
		colorButton.setBackground(color);
	}
	
	
	/**
	 * @return true if the checkbox has been selected
	 */
	protected boolean isSelected () {
		return checkBox.isSelected();
	}
	
}