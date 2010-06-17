package yu.einstein.gdp2.gui.scatterPlot;

public class ScatterPlotData {

	private final double[][] dataPoints;
	private final String graphName;
	
	public ScatterPlotData(double[][] dataPoints, String graphName) {
		this.dataPoints = dataPoints;
		this.graphName = graphName;
	}
	
	public String getGraphName() {
		return graphName;
	}
	
	public double[][] getDataPoints() {
		return dataPoints;
	}
}
