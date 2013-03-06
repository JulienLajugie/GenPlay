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
package edu.yu.einstein.genplay.gui.dialog.peakFinderDialog.islandPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import edu.yu.einstein.genplay.core.operation.binList.peakFinder.IslandFinder;


/**
 * This panel shows output options for the island finder frame settings
 * 
 * @author Nicolas
 * @version 0.1
 */
final class IslandDialogOutput extends IslandDialogFieldset{
	
	private static final long serialVersionUID = -1616307602412859645L;
	
	//Constant values
	private static final int NAME_WIDTH = (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.35);
	private static final int GARBAGE_WIDTH = (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.40);
	
	//Storage values
	protected static boolean 	FilteredStore;
	protected static boolean 	IFScoreStore;
	protected static boolean 	SummitStore;
	
	//Graphics elements
	private final JLabel					jlResultType;					// label for result type 
	private final JCheckBox					jcbIFScore;						// check box to choose Island Finder Score output value
	private final JCheckBox					jcbFiltered;					// check box to choose original date filtered output value
	private final JCheckBox					jcbSummit;						// check box to choose summit of the island output
	
	
	/**
	 * Constructor for IslandDialogOutput
	 * @param title		fieldset title
	 * @param island	IslandFinder object to set some information
	 */
	IslandDialogOutput(String title, IslandFinder island) {
		super(title, island);
		
		//Set "window size" information
		this.jlResultType = new JLabel("Result Type");
		this.jcbFiltered = new JCheckBox("Start values");
		this.jcbIFScore = new JCheckBox("Island score");
		this.jcbSummit = new JCheckBox("Island Summit");
		
		//Dimension
		this.jlResultType.setPreferredSize(new Dimension(IslandDialogOutput.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jcbFiltered.setPreferredSize(new Dimension(IslandDialogOutput.GARBAGE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jcbIFScore.setPreferredSize(new Dimension(IslandDialogOutput.GARBAGE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jcbSummit.setPreferredSize(new Dimension(IslandDialogOutput.GARBAGE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		
		//Set selected boxes
		this.jcbFiltered.setSelected(this.getBoolStoredValue(FilteredStore, false));
		this.jcbIFScore.setSelected(this.getBoolStoredValue(IFScoreStore, false));
		this.jcbSummit.setSelected(this.getBoolStoredValue(SummitStore, false));
		
		//Tool Tip Text
		String sResultType = "Output result type";
		String sFiltered = "Windows value will be the windows value";
		String sIFScore = "Windows value will be the island score";
		String sSummit = "Windows value will be the island summit";
		this.jlResultType.setToolTipText(sResultType);
		this.jcbFiltered.setToolTipText(sFiltered);
		this.jcbIFScore.setToolTipText(sIFScore);
		this.jcbSummit.setToolTipText(sSummit);
		
		//Layout
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (IslandDialogFieldset.LINE_TOP_INSET_HEIGHT, 0, IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT, 0);
		
		//jlResultType
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		add(jlResultType, gbc);
		
		//jcbFiltered
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		add(jcbFiltered, gbc);
		
		//jcbIFScore
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = new Insets (0, 0, IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT, 0);
		add(jcbIFScore, gbc);
		
		//jcbSummit
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = new Insets (0, 0, IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT, 0);
		add(jcbSummit, gbc);
		
		// Dimension
		this.setRows(gbc.gridy + 1);
		
		this.setVisible(true);
	}

	/**
	 * This method check an Boolean value to return it if it is not null, or return the initial value
	 * 
	 * @param value		value to return
	 * @param initial	returned value if 'value' is null
	 * @return			Integer
	 */
	private Boolean getBoolStoredValue (Boolean value, Boolean initial) {
		if (value != null) {
			return value;
		} else {
			return initial;
		}
	}
	
	//Getters for selected checkboxes
	protected boolean filteredSelected () {
		return this.jcbFiltered.isSelected();
	}
	
	protected boolean IFScoreSelected () {
		return this.jcbIFScore.isSelected();
	}
	
	protected boolean summitScoreSelected () {
		return this.jcbSummit.isSelected();
	}
	
}
