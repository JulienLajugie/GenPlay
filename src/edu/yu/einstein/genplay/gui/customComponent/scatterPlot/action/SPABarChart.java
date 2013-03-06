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

import java.awt.event.ActionEvent;

import edu.yu.einstein.genplay.dataStructure.enums.GraphType;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;



/**
 * Shows the scatter plot as a bar chart
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPABarChart extends ScatterPlotAction {

	private static final long serialVersionUID = -1576959964205989128L;	// generated ID
	private static final String 	ACTION_NAME = "Bar Chart";			// action name
	private static final String 	DESCRIPTION = 
		"Show the scatter plot as a bar chart";							// tooltip


	/**
	 * Creates an instance of {@link SPABarChart}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPABarChart(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		getScatterPlotPane().setChartType(GraphType.BAR);
		getScatterPlotPane().repaint();
	}
}
