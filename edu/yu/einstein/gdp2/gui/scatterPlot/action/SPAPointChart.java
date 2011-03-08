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

import java.awt.event.ActionEvent;

import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;


/**
 * Shows the scatter plot as a point chart
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPAPointChart extends ScatterPlotAction {

	private static final long serialVersionUID = 5634078182419382909L;	// generated ID
	private static final String 	ACTION_NAME = "Point Chart";		// action name
	private static final String 	DESCRIPTION = 
		"Show the scatter plot as a point chart";						// tooltip


	/**
	 * Creates an instance of {@link SPAPointChart}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPAPointChart(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		getScatterPlotPane().setChartType(GraphicsType.POINTS);
		getScatterPlotPane().repaint();
	}
}
