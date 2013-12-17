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
package edu.yu.einstein.genplay.gui.dialog.DASDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Panel of a DAS dialog allowing the select the type of the generated track
 * @author Julien Lajugie
 */
public class GenerateTrackTypePanel extends JPanel {
	
	private static final long serialVersionUID = 7393732354670720380L;	// generated ID
	private final JRadioButton 	jrbGeneListResult;	// radio button gene list 
	private final JRadioButton 	jrbSCWListResult;	// radio button SCW list
	private int 				generateType = DASDialog.GENERATE_GENE_LIST;	// the type of list to generate
	
	
	/**
	 * Creates an instance of {@link GenerateTrackTypePanel}
	 */
	public GenerateTrackTypePanel() {
	super();
		jrbGeneListResult = new JRadioButton("Gene Track");
		jrbGeneListResult.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				resultTypeChanged();				
			}
		});
		jrbSCWListResult = new JRadioButton("Variable Window Track");
		jrbSCWListResult.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				resultTypeChanged();				
			}
		});
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(jrbGeneListResult);
		radioGroup.add(jrbSCWListResult);
		radioGroup.setSelected(jrbSCWListResult.getModel(), true);
		
		// we add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrbSCWListResult, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrbGeneListResult, c);
		
		setBorder(BorderFactory.createTitledBorder("Select Track Type"));
		resultTypeChanged();
	}
	
	
	/**
	 * Method called when the selected result type changes
	 */
	private void resultTypeChanged() {
		if (jrbGeneListResult.isSelected()) {
			generateType = DASDialog.GENERATE_GENE_LIST;
		} else if (jrbSCWListResult.isSelected()) {
			generateType = DASDialog.GENERATE_SCW_LIST;
		}
	}
	
	
	/**
	 * @return generateType (GENERATE_GENE_LIST or GENERATE_SCW_LIST)
	 */
	public final int getGenerateType() {
		return generateType;
	}
}
