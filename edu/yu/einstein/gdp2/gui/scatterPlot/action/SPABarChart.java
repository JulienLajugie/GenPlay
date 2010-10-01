/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot.action;

import java.awt.event.ActionEvent;

import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;


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
		getScatterPlotPane().setChartType(GraphicsType.BAR);
		getScatterPlotPane().repaint();
	}
}
