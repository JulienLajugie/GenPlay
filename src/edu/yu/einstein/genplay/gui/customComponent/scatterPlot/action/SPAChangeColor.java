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
package edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotData;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.util.colors.GenPlayColorChooser;


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
		ScatterPlotData selectedData = null; 
		if (getScatterPlotPane().getData().size() == 1) {
			selectedData = getScatterPlotPane().getData().get(0);
		} else {
			ScatterPlotData[] graphNames = getScatterPlotPane().getData().toArray(new ScatterPlotData[0]);
			selectedData = (ScatterPlotData) JOptionPane.showInputDialog(getScatterPlotPane(), "Select a graph", "Choose Color", JOptionPane.PLAIN_MESSAGE, null, graphNames, graphNames[0]);
		}
		if (selectedData != null) {
			Color currentColor = selectedData.getColor();
			//Color chosenColor = JColorChooser.showDialog(getScatterPlotPane(), "Select Chart Color", currentColor);
			Color chosenColor = GenPlayColorChooser.showDialog(getScatterPlotPane(), "Select Chart Color", currentColor);
			if (chosenColor != null) {
				selectedData.setColor(chosenColor);
				getScatterPlotPane().repaint();
			}
		}		
	}
}
