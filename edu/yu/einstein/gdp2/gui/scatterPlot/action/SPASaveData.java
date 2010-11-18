/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot.action;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotData;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;
import yu.einstein.gdp2.util.Utils;


/**
 * Saves the data of the ScatterPlot chart as CSV file
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPASaveData extends ScatterPlotAction {

	private static final long serialVersionUID = -8095455417205967110L;
	private static final String 	ACTION_NAME = "Save Data";		// action name
	private static final String 	DESCRIPTION = 
		"Save the data of the charts in a CSV file";				// tooltip


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
		String defaultDirectoryPath = ConfigurationManager.getInstance().getDefaultDirectory();		
		JFileChooser jfc = new JFileChooser(defaultDirectoryPath);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV file (*.csv)", "csv");
		jfc.setFileFilter(filter);
		int retValue = jfc.showSaveDialog(getScatterPlotPane());
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (!Utils.cancelBecauseFileExist(getScatterPlotPane(), file)) {
				file = Utils.addExtension(file, "csv");
				writeData(file);
			}
		}
	}


	/**
	 * Writes the data of the {@link ScatterPlotPane} in a specified file
	 * @param file {@link File}
	 */
	private void writeData(File file) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(getScatterPlotPane().getxAxis().getName());
			int maxLength = getScatterPlotPane().getData().get(0).getData().length;
			int graphNumber = 0;
			String[] graphNames = getScatterPlotPane().getGraphNames();
			List<ScatterPlotData> data = getScatterPlotPane().getData();
			for (int i = 0; i < graphNames.length; i++) {
				writer.write(", " + graphNames[i] + " " + getScatterPlotPane().getyAxis().getName());
				if (maxLength < data.get(i).getData().length) {
					maxLength = data.get(i).getData().length;
					graphNumber = i;
				}
			}	
			writer.newLine();
			for (int i = 0; i < maxLength; i++){
				writer.write(Double.toString(data.get(graphNumber).getData()[i][0]));
				for (int j = 0; j < data.size(); j++) {
					writer.write(", ");
					if (i < data.get(j).getData().length) {
						writer.write(Integer.toString((int)data.get(j).getData()[i][1]));

					} else {
						writer.write(Integer.toString(0));						
					}					
				}
				writer.newLine();
			}			
			writer.close();
		} catch (FileNotFoundException e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving the scatter plot data as a CSV file. \n" + e.getLocalizedMessage());
		} catch (IOException e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving the scatter plot data as a CSV file");
		}
	}
}