/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.scatterPlot;

import java.awt.Color;
import java.util.Random;


/**
 * Data of a scatter plot
 * @author Chirag Gorasia
 * @author Julien Lajugie
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
	 * @param color color of the plot
	 */
	public ScatterPlotData(double[][] data, String name, Color color) {
		this.data = data;
		this.name = name;
		this.color = color;
	}


	/**
	 * @return a color randomly generated
	 */
	public static Color generateRandomColor() {
		Random randomGen = new Random();
		int red = randomGen.nextInt(255);
		int green = randomGen.nextInt(255);
		int blue = randomGen.nextInt(255);
		if (red + green + blue > 510) {
			// we want dark colors
			return generateRandomColor();
		} else {
			return new Color(red, green, blue);
		}
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
	 * @param color the color of the graph to set
	 */
	public final void setColor(Color color) {
		this.color = color;
	}
}
