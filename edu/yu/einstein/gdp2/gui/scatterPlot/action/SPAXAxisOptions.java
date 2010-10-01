/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot.action;

import java.awt.event.ActionEvent;

import yu.einstein.gdp2.gui.scatterPlot.AxisOptionDialog;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotAxis;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;


/**
 * Set the x-axis of the scatter plot chart 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPAXAxisOptions extends ScatterPlotAction {

	private static final long serialVersionUID = 217368043621971648L;	// generated ID
	private static final String 	ACTION_NAME = "X-Axis Options";		// action name
	private static final String 	DESCRIPTION = 
		"Configure the X-Axis";								// tooltip


	/**
	 * Creates an instance of {@link SPAXAxisOptions}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPAXAxisOptions(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		ScatterPlotAxis xAxis = getScatterPlotPane().getxAxis();
		AxisOptionDialog aod = new AxisOptionDialog();
		aod.setMin(xAxis.getMin());
		aod.setMax(xAxis.getMax());
		aod.setMajorUnit(xAxis.getMajorUnit());
		aod.setMinorUnit(xAxis.getMinorUnit());
		aod.setShowGrid(xAxis.isShowGrid());
		aod.setLogScale(xAxis.isLogScale());
		aod.setLogBase(xAxis.getLogBase());
		if (aod.showDialog(getRootPane(), "X-Axis Options") == AxisOptionDialog.APPROVE_OPTION) {
			xAxis.setMin(aod.getMin());
			xAxis.setMax(aod.getMax());
			xAxis.setMajorUnit(aod.getMajorUnit());
			xAxis.setMinorUnit(aod.getMinorUnit());
			xAxis.setShowGrid(aod.isShowGrid());
			xAxis.setLogScale(aod.isLogScale());
			xAxis.setLogBase(aod.getLogBase());
			getScatterPlotPane().repaint();
		}
	}
}
