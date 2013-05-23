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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotData;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.util.NumberFormats;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Saves the data of the ScatterPlot chart as TSV file
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPASaveData extends ScatterPlotAction {

	private static final long serialVersionUID = -8095455417205967110L;
	private static final String 	ACTION_NAME = "Save Data";		// action name
	private static final String 	DESCRIPTION =
			"Save the data of the charts in a TSV file";			// tooltip


	/**
	 * Creates an instance of {@link SPASaveData}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPASaveData(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		ScatterPlotData selectedData = null;
		if (getScatterPlotPane().getData().size() == 1) {
			selectedData = getScatterPlotPane().getData().get(0);
		} else {
			ScatterPlotData[] graphNames = getScatterPlotPane().getData().toArray(new ScatterPlotData[0]);
			selectedData = (ScatterPlotData) JOptionPane.showInputDialog(getScatterPlotPane(), "Select a graph", "Choose Color", JOptionPane.PLAIN_MESSAGE, null, graphNames, graphNames[0]);
		}
		if (selectedData != null) {
			String defaultDirectoryPath = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
			JFileChooser jfc = new JFileChooser(defaultDirectoryPath);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("TSV file (*.tsv)", "tsv");
			jfc.setFileFilter(filter);
			int retValue = jfc.showSaveDialog(getScatterPlotPane());
			if (retValue == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				if (!Utils.cancelBecauseFileExist(getScatterPlotPane(), file)) {
					file = Utils.addExtension(file, "tsv");
					writeData(file, selectedData);
				}
			}
		}
	}


	/**
	 * Writes the data of the specified {@link ScatterPlotData} in a specified file
	 * @param file {@link File}
	 */
	private void writeData(File file, ScatterPlotData scatterPlotData) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(getScatterPlotPane().getxAxis().getName());
			String graphName = scatterPlotData.getName();
			writer.write("\t" + graphName + " " + getScatterPlotPane().getyAxis().getName());
			writer.newLine();
			for (int i = 0; i < scatterPlotData.getData().length; i++){
				writer.write(NumberFormats.getWriterScoreFormat().format(scatterPlotData.getData()[i][0]));
				writer.write("\t");
				if (i < scatterPlotData.getData().length) {
					writer.write(NumberFormats.getWriterScoreFormat().format(scatterPlotData.getData()[i][1]));
				} else {
					writer.write(Integer.toString(0));
				}
				writer.newLine();
			}
			writer.close();
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error while saving the scatter plot data as a TSV file.");
		}
	}
}
