/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.scatterPlot;

import java.awt.Color;
import java.util.Random;


/**
 * Data of a scatter plot
 * @author Chirag Gorasia
 * @version 0.1
 */
public class ScatterPlotData {

	private final double[][] 	data;	// data of the scatter plot
	private final String 		name;	// name of the scatter plot
	private Color				color;	// color of the scatter plot
	
	
	/**
	 * Creates an instance of {@link ScatterPlotData}
	 * @param data data of the scatter plot
	 * @param name name of the scatter plot
	 */
	public ScatterPlotData(double[][] data, String name) {
		this.data = data;
		this.name = name;
		this.color = generateRandomColor();
	}


	/**
	 * @return a color randomly generated
	 */
	private Color generateRandomColor() {
		Random randomGen = new Random();
		int red = randomGen.nextInt(255);
		int green = randomGen.nextInt(255);
		int blue = randomGen.nextInt(255);
		return new Color(red, green, blue);
	}


	/**
	 * @return the data of the scatter plot
	 */
	public double[][] getData() {
		return data;
	}
	
	
	/**
	 * @return the color of the graph
	 */
	public final Color getColor() {
		return color;
	}
	
	
	/**
	 * @return the name of the scatter plot
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * @param graphColor the color of the graph to set
	 */
	public final void setColor(Color color) {
		this.color = color;
	}
}
