/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot.action;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;

import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;


/**
 * Abstract class extended by the different classes 
 * defining action on a {@link ScatterPlotPane}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ScatterPlotAction extends AbstractAction {

	private static final long serialVersionUID = 7889036761995344801L;	// generated ID
	private final ScatterPlotPane scatterPlotPane;	// scatter plot pane
	
	
	/**
	 * Public constructor
	 * @param scatterPlotPane
	 */
	public ScatterPlotAction(ScatterPlotPane scatterPlotPane) {
		this.scatterPlotPane = scatterPlotPane;
	}


	/**
	 * @return the scatterPlotPane
	 */
	protected ScatterPlotPane getScatterPlotPane() {
		return scatterPlotPane;
	}
	
	
	/**
	 * @return the {@link JRootPane}
	 */
	protected JRootPane getRootPane() {
		return MainFrame.getInstance().getTrackList().getRootPane();
	}
}
