/**
 * @author Chirag Gorasia
 */

package yu.einstein.gdp2.gui.popupMenu;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPanel;
import yu.einstein.gdp2.util.Utils;

/**
 * This class is used to provide options (save, print etc) for the scatter plot 
 * @author Chirag
 *
 */
public class ScatterPlotRightClickedMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 5259896882194725264L;

	private final JMenuItem	jmiSaveAs;							// menu save plot as image
	private JFileChooser jfc;
	private ScatterPlotPanel scatterPlotPanel;

	public ScatterPlotRightClickedMenu(ScatterPlotPanel scatterPlotPanel) {
		jmiSaveAs = new JMenuItem("Save As");		
		add(jmiSaveAs);
		jfc = new JFileChooser();		
		jmiSaveAs.setVisible(true);
		jmiSaveAs.addActionListener(this);
		this.scatterPlotPanel = scatterPlotPanel;
	}		

	@Override
	public void actionPerformed(ActionEvent event) {
		jmiSaveAs.setVisible(false);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg");
		jfc.setFileFilter(filter);
		int retValue = jfc.showSaveDialog(getRootPane());
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File fileName = jfc.getSelectedFile();
			if (!Utils.cancelBecauseFileExist(getRootPane(), fileName)) {
				saveAsImage(fileName);
			}
		}			
	}


	/**
	 * Save the scatter plot as a JPG image.
	 * @param file output file
	 */
	public void saveAsImage(File file) {
		BufferedImage image = new BufferedImage(scatterPlotPanel.getWidth(), scatterPlotPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		scatterPlotPanel.paint(g);
		try {         
			ImageIO.write(image, "JPEG", file);
		}catch(Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving the scatter plot as an image");
		}		
	}
}
