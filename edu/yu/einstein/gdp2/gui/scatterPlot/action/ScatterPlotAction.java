package yu.einstein.gdp2.gui.scatterPlot.action;

import javax.swing.AbstractAction;
import javax.swing.JRootPane;

import yu.einstein.gdp2.gui.mainFrame.MainFrame;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;

public abstract class ScatterPlotAction extends AbstractAction {

	private static final long serialVersionUID = 7889036761995344801L;
	private final ScatterPlotPane scatterPlotPane; 
	
	
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
