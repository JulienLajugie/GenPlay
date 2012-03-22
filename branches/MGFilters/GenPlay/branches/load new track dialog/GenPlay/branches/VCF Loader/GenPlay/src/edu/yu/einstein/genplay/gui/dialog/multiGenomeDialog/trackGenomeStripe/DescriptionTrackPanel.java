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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * 
 * @author Nicolas Fourel
 */
class DescriptionTrackPanel extends JPanel {

	private static final long serialVersionUID = 57437366439828552L;

	private JLabel nameLabel;
	private JLabel nameValue;
	private JLabel groupLabel;
	private JLabel groupValue;
	
	
	public DescriptionTrackPanel (int width) {
		
		//Dimension
		Dimension panelDim = new Dimension(width, MultiGenomeStripeSelectionDialog.getDescriptionPanelHeight());
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);
		
		
		//Labels
		nameLabel = new JLabel(MultiGenomeStripeSelectionDialog.getDescriptionTrackName());
		groupLabel = new JLabel(MultiGenomeStripeSelectionDialog.getDescriptionTrackGroup());
		nameValue = new JLabel();
		groupValue = new JLabel();
		
		
		//Label dimension
		Dimension labelDim = new Dimension(getLabelWidth(), MultiGenomeStripeSelectionDialog.getDescriptionLineHeight());
		Dimension valueDim = new Dimension(getValueWidth(), MultiGenomeStripeSelectionDialog.getDescriptionLineHeight());
		setLabelSize(nameLabel, labelDim);
		setLabelSize(groupLabel, labelDim);
		setLabelSize(nameValue, valueDim);
		setLabelSize(groupValue, valueDim);
		
		
		//Layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		
		
		//nameLabel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(MultiGenomeStripeSelectionDialog.getDescriptionInset(), getLeftInset(), 0, 0);
		add(nameLabel, gbc);
		
		//nameValue
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(MultiGenomeStripeSelectionDialog.getDescriptionInset(), 0, 0, 0);
		add(nameValue, gbc);
		
		//nameLabel
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, getLeftInset(), MultiGenomeStripeSelectionDialog.getDescriptionInset(), 0);
		add(groupLabel, gbc);
		
		//nameValue
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 0, MultiGenomeStripeSelectionDialog.getDescriptionInset(), 0);
		add(groupValue, gbc);
		
		
	}
	
	
	protected void setTrackName (String name) {
		nameValue.setText(name);
	}
	
	protected void setTrackGenomeGroupName (String name) {
		if (name == null) {
			groupValue.setText("Undefined");
		} else {
			groupValue.setText(name);
		}
	}
	
	private int getLeftInset () {
		return MultiGenomeStripeSelectionDialog.getHorizontalInset();
	}
	
	private int getLabelWidth () {
		return MultiGenomeStripeSelectionDialog.getDescriptionLabelWidth();
	}
	
	private int getValueWidth () {
		int width = MultiGenomeStripeSelectionDialog.getDialogWidth() - getLeftInset() - getLabelWidth();
		return width;
	}
	
	
	
	private void setLabelSize (JLabel label, Dimension dim) {
		label.setSize(dim);
		label.setPreferredSize(dim);
		label.setMinimumSize(dim);
		label.setMaximumSize(dim);
	}
}
