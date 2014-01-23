/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.peakFinderDialog.islandPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindIslands;
import edu.yu.einstein.genplay.dataStructure.enums.IslandResultType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.dialog.peakFinderDialog.PeakFinderPanel;



/**
 * A frame allowing to configure the properties of the island finder.
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class IslandFinderPanel extends JPanel implements PeakFinderPanel {

	private static final long serialVersionUID = 8143320501058939077L;

	private final IslandDialogInformation	idfInformation;					// panel to manage additional information
	private final IslandDialogInput			idfInput;						// panel to manage input parameters
	private final IslandDialogOutput		idfOutput;						// panel to manage output data
	private final BLOFindIslands 					bloFindIslands;					// Island Finder object needs to set parameters (ReadCountLimit, p-value, gap, cut-off)


	/**
	 * Constructor, create an instance of IslandDialog.
	 * @param bloFindIslands 	IslandFinder object, needs to define p-value/readCountLimit parameters.
	 */
	public IslandFinderPanel(BLOFindIslands bloFindIslands) {
		super();
		setName("Island Finder");
		this.bloFindIslands = bloFindIslands;

		//Fieldset initialization
		idfInformation = new IslandDialogInformation("Information", this.bloFindIslands.getIsland());
		idfInput = new IslandDialogInput("Input", this.bloFindIslands.getIsland(), idfInformation);
		idfOutput = new IslandDialogOutput("Output", this.bloFindIslands.getIsland());

		//Layout Manager
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (10, 0, 10, 0);

		// idfInformation
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = gbcInsets;
		this.add(idfInformation, gbc);

		// idfInput
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = gbcInsets;
		this.add(idfInput, gbc);

		// idfOuptut
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = gbcInsets;
		this.add(idfOutput, gbc);
	}


	@Override
	public void saveInput() {
		IslandDialogInput.windowMinValueStore = idfInput.getWindowLimitValue();
		IslandDialogInput.gapStore = idfInput.getGap();
		IslandDialogInput.IslandMinScoreStore = idfInput.getIslandLimitScore();
		IslandDialogInput.IslandMinLengthStore = idfInput.getMinIslandLength();
		IslandDialogOutput.FilteredStore = idfOutput.filteredSelected();
		IslandDialogOutput.IFScoreStore = idfOutput.IFScoreSelected();
		IslandDialogOutput.SummitStore = idfOutput.summitScoreSelected();
	}


	@Override
	public String toString() {
		return getName();
	}


	@Override
	public Operation<BinList[]> validateInput() {
		if (idfOutput.filteredSelected() | idfOutput.IFScoreSelected() | idfOutput.summitScoreSelected()) {	// requirements to approved
			//All islands finder parameters must be set
			bloFindIslands.getIsland().setWindowMinValue(idfInput.getWindowLimitValue());
			bloFindIslands.getIsland().setGap(idfInput.getGap());
			bloFindIslands.getIsland().setIslandMinScore(idfInput.getIslandLimitScore());
			bloFindIslands.getIsland().setIslandMinLength(idfInput.getMinIslandLength());
			//IslandResultType array to manage the right number of track for the BLAIslands object
			IslandResultType[] list = new IslandResultType[3];
			if (idfOutput.filteredSelected()) {
				list[0] = IslandResultType.FILTERED;
			}
			if (idfOutput.IFScoreSelected()) {
				list[1] = IslandResultType.IFSCORE;
			}
			if (idfOutput.summitScoreSelected()) {
				list[2] = IslandResultType.SUMMIT;
			}
			bloFindIslands.setList(list);
			return bloFindIslands;
		} else {
			JOptionPane.showMessageDialog(getRootPane(), "Please select at least one result type", "Invalid Input", JOptionPane.WARNING_MESSAGE);
			return null;
		}
	}
}
