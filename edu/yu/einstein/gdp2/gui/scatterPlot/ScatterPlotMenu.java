package yu.einstein.gdp2.gui.scatterPlot;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import yu.einstein.gdp2.gui.scatterPlot.action.SPABarChart;
import yu.einstein.gdp2.gui.scatterPlot.action.SPAChangeColor;
import yu.einstein.gdp2.gui.scatterPlot.action.SPACurveChart;
import yu.einstein.gdp2.gui.scatterPlot.action.SPAPointChart;
import yu.einstein.gdp2.gui.scatterPlot.action.SPASaveData;
import yu.einstein.gdp2.gui.scatterPlot.action.SPASaveImage;

public class ScatterPlotMenu extends JPopupMenu {

	private static final long serialVersionUID = -2272831002261208835L;	// generated ID
	private final JRadioButtonMenuItem	jrbmiBarChart;		// draw a bar chart
	private final JRadioButtonMenuItem	jrbmiPointChart;	// draw a point chart
	private final JRadioButtonMenuItem 	jrbmiCurveChart;	// draw join points to form a curve
	private final JMenuItem				jmiSaveImage;		// menu save plot as image
	private final JMenuItem				jmiSaveData;		// menu save the data in CSV file
	private final JMenuItem 			jmiChangeColor;		// change graph colors
	//	private final JMenuItem				jmiSetXAxis;		// menu for setting x axis parameters
	//	private final JMenuItem				jmiSetYAxis;		// menu for setting y axis parameters
	//	private final JCheckBoxMenuItem		jcbmiXAxisGrid;		// menu X-Axis grid checkbox
	//	private final JCheckBoxMenuItem		jcbmiYAxisGrid;		// menu Y-Axis grid checkbox
	
	
	public ScatterPlotMenu(ScatterPlotPane scatterPlotPane) {
		jrbmiBarChart = new JRadioButtonMenuItem(new SPABarChart(scatterPlotPane));
		jrbmiPointChart = new JRadioButtonMenuItem(new SPAPointChart(scatterPlotPane));
		jrbmiCurveChart = new JRadioButtonMenuItem(new SPACurveChart(scatterPlotPane));

		jmiSaveData = new JMenuItem(new SPASaveData(scatterPlotPane));
		jmiSaveImage = new JMenuItem(new SPASaveImage(scatterPlotPane));
		
		jmiChangeColor = new JMenuItem(new SPAChangeColor(scatterPlotPane));

		add(jrbmiBarChart);
		add(jrbmiPointChart);
		add(jrbmiCurveChart);
		addSeparator();
		add(jmiSaveData);
		add(jmiSaveImage);
		addSeparator();
		add(jmiChangeColor);
		
		switch (scatterPlotPane.getChartType()) {
		case BAR:
			jrbmiBarChart.setSelected(true);
			break;
		case CURVE:
			jrbmiCurveChart.setSelected(true);
			break;
		case POINTS:
			jrbmiPointChart.setSelected(true);		
		}

	}

}
