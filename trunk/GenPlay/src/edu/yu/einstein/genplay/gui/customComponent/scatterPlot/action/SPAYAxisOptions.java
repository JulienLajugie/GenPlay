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
package edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action;

import java.awt.event.ActionEvent;

import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.AxisOptionDialog;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotAxis;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;



/**
 * Set the y-axis of the scatter plot chart
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPAYAxisOptions extends ScatterPlotAction {

	private static final long serialVersionUID = 217368043621971648L;	// generated ID
	private static final String 	ACTION_NAME = "Y-Axis Options";		// action name
	private static final String 	DESCRIPTION =
			"Configure the Y-Axis";								// tooltip


	/**
	 * Creates an instance of {@link SPAYAxisOptions}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPAYAxisOptions(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		ScatterPlotAxis yAxis = getScatterPlotPane().getyAxis();
		AxisOptionDialog aod = new AxisOptionDialog();
		aod.setMin(yAxis.getMin());
		aod.setMax(yAxis.getMax());
		aod.setMajorUnit(yAxis.getMajorUnit());
		aod.setMinorUnit(yAxis.getMinorUnit());
		aod.setShowGrid(yAxis.isShowGrid());
		aod.setLogScale(yAxis.isLogScale());
		aod.setLogBase(yAxis.getLogBase());
		if (aod.showDialog(getRootPane(), "Y-Axis Options") == AxisOptionDialog.APPROVE_OPTION) {
			yAxis.setMin(aod.getMin());
			yAxis.setMax(aod.getMax());
			yAxis.setMajorUnit(aod.getMajorUnit());
			yAxis.setMinorUnit(aod.getMinorUnit());
			yAxis.setShowGrid(aod.isShowGrid());
			yAxis.setLogScale(aod.isLogScale());
			yAxis.setLogBase(aod.getLogBase());
			getScatterPlotPane().repaint();
		}
	}
}
