/**
 * @author Chirag Gorasia
 * @version 0.1
 */
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
 * Saves the ScatterPlot chart as a JPEG image 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SPASaveImage extends ScatterPlotAction {

	private static final long serialVersionUID = -8313148262612777559L;	// generated ID
	private static final String 	ACTION_NAME = "Save As Image";		// action name
	private static final String 	DESCRIPTION = 
		"Save the chart as a JPG image";								// tooltip


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
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file (*.jpg, *.jpeg, *.jpe)", "jpg", "jpeg", "jpe");
		jfc.setFileFilter(filter);
		int retValue = jfc.showSaveDialog(getScatterPlotPane());
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			if (!Utils.cancelBecauseFileExist(getScatterPlotPane(), file)) {
				file = Utils.addExtension(file, "jpg");
				BufferedImage image = new BufferedImage(getScatterPlotPane().getWidth(), getScatterPlotPane().getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				getScatterPlotPane().paint(g);
				try {         
					ImageIO.write(image, "JPEG", file);
				}catch(Exception ex) {
					ExceptionManager.handleException(getScatterPlotPane(), ex, "Error while saving the scatter plot as an image");
				}
			}
		}
	}
}
