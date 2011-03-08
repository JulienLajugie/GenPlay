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
package yu.einstein.gdp2.gui.dialog.peakFinderDialog.islandPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;

import yu.einstein.gdp2.core.list.binList.operation.peakFinder.IslandFinder;

/**
 * This panel shows additional information for the island finder frame settings
 * 
 * @author Nicolas
 * @version 0.1
 */
final class IslandDialogInformation extends IslandDialogFieldset{
	
	private static final long serialVersionUID = -1616307602412859645L;
	
	//Constant values
	private static final int NAME_WIDTH = (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.35);
	private static final int UNIT_WIDTH = (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.4);
	private static final int MIN_VALUE_WIDTH = (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.1);
	
	//Graphics elements
	private final JLabel jlWindowSizeName;		//for the window size
	private final JLabel jlWindowSizeValue;
	private final JLabel jlWindowSizeUnit;
	
	private final JLabel jlAverageName;			//for the average
	private final JLabel jlAverageValue;
	private final JLabel jlAverageUnit;
	
	private final JLabel jlPValueName;			//for the p-value
	private final JLabel jlPValueValue;
	
	
	/**
	 * Constructor for IslandDialogInformation
	 * @param title		fieldset title
	 */
	IslandDialogInformation(String title, IslandFinder island) {
		super(title, island);
		
		//Set "window size" information
		this.jlWindowSizeName = new JLabel("Window size");
		this.jlWindowSizeValue = new JLabel("" + island.getBinList().getBinSize());
		this.jlWindowSizeUnit = new JLabel("bp");
		
		//Set "average" information
		this.jlAverageName = new JLabel("Average");
		Long average = Math.round(island.getLambda()*100)/100;
		this.jlAverageValue = new JLabel(average.toString());
		this.jlAverageUnit = new JLabel("values / window");
		
		//Set "p-value" information
		this.jlPValueName = new JLabel("P-value");
		this.jlPValueValue = new JLabel("-");
		
		//Dimension PreferredSize
		this.jlWindowSizeName.setPreferredSize(new Dimension(IslandDialogInformation.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlWindowSizeValue.setPreferredSize(new Dimension(IslandDialogInformation.MIN_VALUE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlWindowSizeUnit.setPreferredSize(new Dimension(IslandDialogInformation.UNIT_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlAverageName.setPreferredSize(new Dimension(IslandDialogInformation.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlAverageValue.setPreferredSize(new Dimension(IslandDialogInformation.MIN_VALUE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlAverageUnit.setPreferredSize(new Dimension(IslandDialogInformation.UNIT_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlPValueName.setPreferredSize(new Dimension(IslandDialogInformation.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		
		//Tool Tip Text
		String sWindowSize = "Window size";
		String sAverage = "Means of value by window";
		String sPValue = "Probability to get false results";
		this.jlWindowSizeName.setToolTipText(sWindowSize);
		this.jlWindowSizeValue.setToolTipText(sWindowSize);
		this.jlAverageName.setToolTipText(sAverage);
		this.jlAverageValue.setToolTipText(sAverage);
		this.jlPValueName.setToolTipText(sPValue);
		this.jlPValueValue.setToolTipText(sPValue);
		
		//Layout
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (IslandDialogFieldset.LINE_TOP_INSET_HEIGHT, 0, IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT, 0);
		
		// jlWindowSizeName
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlWindowSizeName, gbc);
		
		// jlWindowSizeValue
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jlWindowSizeValue, gbc);
		
		// jlWindowSizeUnit
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlWindowSizeUnit, gbc);
		
		// jlAverageName
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlAverageName, gbc);
		
		// jlAverageValue
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jlAverageValue, gbc);
		
		// jlAverageUnit
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlAverageUnit, gbc);
		
		// jlPValueName
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlPValueName, gbc);
		
		// jlPValueValue
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jlPValueValue, gbc);
		
		// Dimension
		this.setRows(gbc.gridy + 1);
		
		this.setVisible(true);
	}

	//Getter
	protected JLabel getJlPValueValue() {
		return jlPValueValue;
	}
}
