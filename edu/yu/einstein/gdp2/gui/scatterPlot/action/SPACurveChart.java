/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot.action;

import java.awt.event.ActionEvent;

import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;


/**
 * Shows the scatter plot as a curve chart
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPACurveChart extends ScatterPlotAction {

	private static final long serialVersionUID = -7800737988668596956L;	// generated ID
	private static final String 	ACTION_NAME = "Curve Chart";		// action name
	private static final String 	DESCRIPTION = 
		"Show the scatter plot as a curve chart";						// tooltip


	/**
	 * Creates an instance of {@link SPACurveChart}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPACurveChart(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		getScatterPlotPane().setChartType(GraphicsType.CURVE);
		getScatterPlotPane().repaint();
	}
}
