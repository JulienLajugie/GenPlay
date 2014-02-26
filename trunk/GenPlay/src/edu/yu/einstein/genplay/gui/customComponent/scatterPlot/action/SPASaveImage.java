/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.customComponent.scatterPlot.action;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.customComponent.scatterPlot.ScatterPlotPane;
import edu.yu.einstein.genplay.util.FileChooser;



/**
 * Saves the ScatterPlot chart as a PNG image
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPASaveImage extends ScatterPlotAction {

	private static final long serialVersionUID = -8313148262612777559L;	// generated ID
	private static final String 	ACTION_NAME = "Save As Image";		// action name
	private static final String 	DESCRIPTION =
			"Save the chart as a PNG image";							// tooltip


	/**
	 * Creates an instance of {@link SPASaveImage}
	 * @param scatterPlotPane a {@link ScatterPlotPane}
	 */
	public SPASaveImage(ScatterPlotPane scatterPlotPane) {
		super(scatterPlotPane);
		putValue(NAME, ACTION_NAME);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		FileFilter[] filters = {new FileNameExtensionFilter("PNG file (*.PNG)", "png")};
		File selectedFile = FileChooser.chooseFile(getRootPane(), FileChooser.SAVE_FILE_MODE, "Save Image As", filters, false);
		if (selectedFile != null) {
			BufferedImage image = new BufferedImage(getScatterPlotPane().getWidth(), getScatterPlotPane().getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			getScatterPlotPane().paint(g);
			try {
				ImageIO.write(image, "png", selectedFile);
			}catch(Exception ex) {
				ExceptionManager.getInstance().caughtException(Thread.currentThread(), ex, "Error while saving the scatter plot as an image");
			}
		}
	}
}
