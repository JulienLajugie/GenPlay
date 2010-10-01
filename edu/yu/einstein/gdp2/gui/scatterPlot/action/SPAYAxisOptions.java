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
 * Set the y-axis of the scatter plot chart 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPAYAxisOptions extends ScatterPlotAction {

	private static final long serialVersionUID = 217368043621971648L;	// generated ID
	private static final String 	ACTION_NAME = "Y-Axis Options";		// action name
	private static final String 	DESCRIPTION = 
		"Configure the Y-Axis";								// tooltip


	/**
	 * Creates an instance of {@link SPAYAxisOptions}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPAYAxisOptions(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		ScatterPlotAxis yAxis = getScatterPlotPane().getyAxis();
		AxisOptionDialog aod = new AxisOptionDialog();
		aod.setMin(yAxis.getMin());
		aod.setMax(yAxis.getMax());
		aod.setMajorUnit(yAxis.getMajorUnit());
		aod.setMinorUnit(yAxis.getMinorUnit());
		aod.setShowGrid(yAxis.isShowGrid());
		aod.setLogScale(yAxis.isLogScale());
		aod.setLogBase(yAxis.getLogBase());
		if (aod.showDialog(getRootPane(), "Y-Axis Options") == AxisOptionDialog.APPROVE_OPTION) {
			yAxis.setMin(aod.getMin());
			yAxis.setMax(aod.getMax());
			yAxis.setMajorUnit(aod.getMajorUnit());
			yAxis.setMinorUnit(aod.getMinorUnit());
			yAxis.setShowGrid(aod.isShowGrid());
			yAxis.setLogScale(aod.isLogScale());
			yAxis.setLogBase(aod.getLogBase());
			getScatterPlotPane().repaint();
		}
	}
}
