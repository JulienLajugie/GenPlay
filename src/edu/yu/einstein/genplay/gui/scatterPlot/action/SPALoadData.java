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
package edu.yu.einstein.genplay.gui.scatterPlot.action;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.gui.scatterPlot.ScatterPlotData;
import edu.yu.einstein.genplay.gui.scatterPlot.ScatterPlotPane;


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
		String defaultDirectoryPath = ConfigurationManager.getInstance().getDefaultDirectory();		
		JFileChooser jfc = new JFileChooser(defaultDirectoryPath);
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
				JOptionPane.showMessageDialog(getRootPane(), "The input file is empty", "Invalid File", JDialog.ERROR);
			} else {
				splittedLine = line.split(",");
				// we need a file with two fields
				if (splittedLine.length != 2) {
					JOptionPane.showMessageDialog(getRootPane(), "The input file needs to contain two fields", "Invalid File", JDialog.ERROR);
				} else {
					extractedData = new ScatterPlotData(splittedLine[1].trim(), Color.BLACK);
					List<Double> xData= new ArrayList<Double>();
					List<Double> yData= new ArrayList<Double>();
					while ((line = reader.readLine()) != null) {
						if (!line.trim().isEmpty()) {
							splittedLine = line.split(",");
							if (splittedLine.length != 2) {
								JOptionPane.showMessageDialog(getRootPane(), "The file needs to contain 2 fields", "Invalid File", JDialog.ERROR);
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
			JOptionPane.showMessageDialog(getRootPane(), "The specified file is not valid", "Invalid File", JOptionPane.ERROR_MESSAGE);
		}
	}
}
