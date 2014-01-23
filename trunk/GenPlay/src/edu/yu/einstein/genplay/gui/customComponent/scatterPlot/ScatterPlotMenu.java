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
package edu.yu.einstein.genplay.gui.customComponent.scatterPlot;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPABarChart;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPAChangeColor;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPACurveChart;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPALoadData;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPAPointChart;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPASaveData;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPASaveImage;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPAXAxisOptions;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action.SPAYAxisOptions;



/**
 * Popup menu of a {@link ScatterPlotPane}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ScatterPlotMenu extends JPopupMenu {

	private static final long serialVersionUID = -2272831002261208835L;	// generated ID
	private final JRadioButtonMenuItem	jrbmiBarChart;			// draw a bar chart
	private final JRadioButtonMenuItem	jrbmiPointChart;		// draw a point chart
	private final JRadioButtonMenuItem 	jrbmiCurveChart;		// draw join points to form a curve
	private final JMenuItem				jmiSaveImage;			// menu save plot as image
	private final JMenuItem				jmiSaveData;			// menu save the data in CSV file
	private final JMenuItem				jmiLoadData;			// menu load data from a CSV file
	private final JMenuItem 			jmiChangeColor;			// change graph colors
	private final JMenuItem				jmiXAxisOptions;		// menu for setting x axis parameters
	private final JMenuItem				jmiYAxisOptions;		// menu for setting y axis parameters


	/**
	 * Creates an instance of {@link ScatterPlotMenu}
	 * @param scatterPlotPane {@link ScatterPlotPane}
	 */
	public ScatterPlotMenu(ScatterPlotPane scatterPlotPane) {
		jrbmiBarChart = new JRadioButtonMenuItem(new SPABarChart(scatterPlotPane));
		jrbmiPointChart = new JRadioButtonMenuItem(new SPAPointChart(scatterPlotPane));
		jrbmiCurveChart = new JRadioButtonMenuItem(new SPACurveChart(scatterPlotPane));

		jmiSaveData = new JMenuItem(new SPASaveData(scatterPlotPane));
		jmiSaveImage = new JMenuItem(new SPASaveImage(scatterPlotPane));
		jmiLoadData = new JMenuItem(new SPALoadData(scatterPlotPane));

		jmiChangeColor = new JMenuItem(new SPAChangeColor(scatterPlotPane));

		jmiXAxisOptions = new JMenuItem(new SPAXAxisOptions(scatterPlotPane));
		jmiYAxisOptions = new JMenuItem(new SPAYAxisOptions(scatterPlotPane));

		add(jrbmiBarChart);
		add(jrbmiPointChart);
		add(jrbmiCurveChart);
		addSeparator();
		add(jmiXAxisOptions);
		add(jmiYAxisOptions);
		addSeparator();
		add(jmiSaveData);
		add(jmiSaveImage);
		add(jmiLoadData);
		addSeparator();
		add(jmiChangeColor);

		switch (scatterPlotPane.getChartType()) {
		case BAR:
			jrbmiBarChart.setSelected(true);
			break;
		case CURVE:
			jrbmiCurveChart.setSelected(true);
			break;
		case POINTS:
			jrbmiPointChart.setSelected(true);
		default:
			break;
		}
	}
}
