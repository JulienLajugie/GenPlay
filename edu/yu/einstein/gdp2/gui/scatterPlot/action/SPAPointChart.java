/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot.action;

import java.awt.event.ActionEvent;

import yu.einstein.gdp2.core.enums.GraphicsType;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;


/**
 * Shows the scatter plot as a point chart
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPAPointChart extends ScatterPlotAction {

	private static final long serialVersionUID = 5634078182419382909L;	// generated ID
	private static final String 	ACTION_NAME = "Point Chart";		// action name
	private static final String 	DESCRIPTION = 
		"Show the scatter plot as a point chart";						// tooltip


	/**
	 * Creates an instance of {@link SPAPointChart}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPAPointChart(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		getScatterPlotPane().setChartType(GraphicsType.POINTS);
		getScatterPlotPane().repaint();
	}
}
