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

import javax.swing.AbstractAction;
import javax.swing.JRootPane;

import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;



/**
 * Abstract class extended by the different classes
 * defining action on a {@link ScatterPlotPane}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ScatterPlotAction extends AbstractAction {

	private static final long serialVersionUID = 7889036761995344801L;	// generated ID
	private final ScatterPlotPane scatterPlotPane;	// scatter plot pane


	/**
	 * Public constructor
	 * @param scatterPlotPane
	 */
	public ScatterPlotAction(ScatterPlotPane scatterPlotPane) {
		this.scatterPlotPane = scatterPlotPane;
	}


	/**
	 * @return the {@link JRootPane}
	 */
	protected JRootPane getRootPane() {
		return MainFrame.getInstance().getTrackListPanel().getRootPane();
	}


	/**
	 * @return the scatterPlotPane
	 */
	protected ScatterPlotPane getScatterPlotPane() {
		return scatterPlotPane;
	}
}
