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
package yu.einstein.gdp2.gui.scatterPlot.action;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;


/**
 * Changes the color of a selected chart 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPAChangeColor extends ScatterPlotAction {

	private static final long serialVersionUID = 1381228234332420940L;	// generated ID
	private static final String 	ACTION_NAME = "Change Chart Color";	// action name
	private static final String 	DESCRIPTION = 
		"Change the colors of a chart";									// tooltip


	/**
	 * Creates an instance of {@link SPAChangeColor} 
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPAChangeColor(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		int selectedIndex = -1; 
		if (getScatterPlotPane().getData().size() == 1) {
			selectedIndex = 0;
		} else {
			String[] graphNames = getScatterPlotPane().getGraphNames();
			String selectedValue = (String) JOptionPane.showInputDialog(getScatterPlotPane(), "Select a graph", "Choose Color", JOptionPane.PLAIN_MESSAGE, null, graphNames, graphNames[0]);
			if (selectedValue != null) {
				selectedIndex = Arrays.binarySearch(graphNames, selectedValue);
			}
		}
		if (selectedIndex >= 0) {
			Color currentColor = getScatterPlotPane().getData().get(selectedIndex).getColor();
			Color chosenColor = JColorChooser.showDialog(getScatterPlotPane(), "Select Chart Color", currentColor);
			if (chosenColor != null) {
				getScatterPlotPane().getData().get(selectedIndex).setColor(chosenColor);
				getScatterPlotPane().repaint();
			}
		}		
	}
}
