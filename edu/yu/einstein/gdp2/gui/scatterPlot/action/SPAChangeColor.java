/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot.action;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;


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
		int selectedIndex = -1; 
		if (getScatterPlotPane().getData().size() == 1) {
			selectedIndex = 0;
		} else {
			String[] graphNames = getScatterPlotPane().getGraphNames();
			String selectedValue = (String) JOptionPane.showInputDialog(getScatterPlotPane(), "Select a graph", "Choose Color", JOptionPane.PLAIN_MESSAGE, null, graphNames, graphNames[0]);
			if (selectedValue != null) {
				selectedIndex = Arrays.binarySearch(graphNames, selectedValue);
			}
		}
		if (selectedIndex >= 0) {
			Color currentColor = getScatterPlotPane().getData().get(selectedIndex).getColor();
			Color chosenColor = JColorChooser.showDialog(getScatterPlotPane(), "Select Chart Color", currentColor);
			if (chosenColor != null) {
				getScatterPlotPane().getData().get(selectedIndex).setColor(chosenColor);
				getScatterPlotPane().repaint();
			}
		}		
	}
}
