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
package yu.einstein.gdp2.gui.scatterPlot.action;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;
import yu.einstein.gdp2.util.Utils;


/**
 * Saves the ScatterPlot chart as a PNG image 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPASaveImage extends ScatterPlotAction {

	private static final long serialVersionUID = -8313148262612777559L;	// generated ID
	private static final String 	ACTION_NAME = "Save As Image";		// action name
	private static final String 	DESCRIPTION = 
		"Save the chart as a PNG image";								// tooltip


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
		String defaultDirectoryPath = ConfigurationManager.getInstance().getDefaultDirectory();		
		JFileChooser jfc = new JFileChooser(defaultDirectoryPath);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG file (*.PNG)", "png");
		jfc.setFileFilter(filter);
		jfc.setDialogTitle("Save chart as a PNG image");
		int retValue = jfc.showSaveDialog(getScatterPlotPane());
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (!Utils.cancelBecauseFileExist(getScatterPlotPane(), file)) {
				file = Utils.addExtension(file, "png");
				BufferedImage image = new BufferedImage(getScatterPlotPane().getWidth(), getScatterPlotPane().getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				getScatterPlotPane().paint(g);
				try {         
					ImageIO.write(image, "png", file);
				}catch(Exception ex) {
					ExceptionManager.handleException(getScatterPlotPane(), ex, "Error while saving the scatter plot as an image");
				}
			}
		}
	}
}
