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
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotData;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * 
 * @author Administrator
 *
 */
public class SPALoadData extends ScatterPlotAction {

	private static final long serialVersionUID = 1879651187524309395L; // generated ID
	private static final String 	ACTION_NAME = "Load Data";		// action name
	private static final String 	DESCRIPTION =
			"Load data in the charts from a CSV file";					// tooltip
	private ScatterPlotData 		extractedData;					// data extracted from the file


	/**
	 * Creates an instance of {@link SPASaveData}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPALoadData(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JFileChooser jfc = new JFileChooser();
		Utils.setFileChooserSelectedDirectory(jfc);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV file (*.csv)", "csv");
		jfc.setFileFilter(filter);
		int retValue = jfc.showOpenDialog(getScatterPlotPane());
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			loadData(file);
		}
	}


	/**
	 * Load data from a specified file
	 * @param file file containing the data to load
	 */
	private void loadData(File file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			String[] splittedLine = null;
			if (line == null) {
				JOptionPane.showMessageDialog(getRootPane(), "The input file is empty", "Invalid File", ImageObserver.ERROR);
			} else {
				//splittedLine = line.split(",");
				splittedLine = Utils.split(line, ',');
				// we need a file with two fields
				if (splittedLine.length != 2) {
					JOptionPane.showMessageDialog(getRootPane(), "The input file needs to contain two fields", "Invalid File", ImageObserver.ERROR);
				} else {
					extractedData = new ScatterPlotData(splittedLine[1].trim(), Colors.BLACK);
					List<Double> xData= new ArrayList<Double>();
					List<Double> yData= new ArrayList<Double>();
					while ((line = reader.readLine()) != null) {
						if (!line.trim().isEmpty()) {
							//splittedLine = line.split(",");
							splittedLine = Utils.split(line, ',');
							if (splittedLine.length != 2) {
								JOptionPane.showMessageDialog(getRootPane(), "The file needs to contain 2 fields", "Invalid File", ImageObserver.ERROR);
							} else {
								xData.add(Double.parseDouble(splittedLine[0].trim()));
								yData.add(Double.parseDouble(splittedLine[1].trim()));
							}
						}
					}
					double[][] dataToAdd = new double[xData.size()][2];
					for (int i = 0; i < xData.size(); i++) {
						dataToAdd[i][0] = xData.get(i);
						dataToAdd[i][1] = yData.get(i);
					}
					extractedData.setData(dataToAdd);
				}
			}
			reader.close();
			getScatterPlotPane().addData(extractedData);
		} catch (Exception e) {
			try {
				reader.close();
			} catch (IOException e1) {
				ExceptionManager.getInstance().caughtException(e);
			}
			JOptionPane.showMessageDialog(getRootPane(), "The specified file is not valid", "Invalid File", JOptionPane.ERROR_MESSAGE);
		}
	}
}
