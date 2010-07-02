/**
 * @author Chirag Gorasia
 */

package yu.einstein.gdp2.gui.popupMenu;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPanel;
import yu.einstein.gdp2.util.Utils;

/**
 * This class is used to provide options (save, print etc) for the scatter plot 
 * @author Chirag
 *
 */
public class ScatterPlotRightClickedMenu extends JPopupMenu implements ActionListener, PopupMenuListener {
	private static final long serialVersionUID = 5259896882194725264L;

	private final JMenuItem	jmiSaveAs;							// menu save plot as image
	private final JMenuItem	jmiBarGraph;						// draw bar graph
	private final JMenuItem	jmiScatterPlot;						// draw scatter plot
	private final JMenuItem jmiCurve;							// join points to form a curve
	private final JMenuItem jmiChangeColors;					// change graph colors
	private final JCheckBoxMenuItem	jcbmiXAxisGrid;				// menu X-Axis grid checkbox
	private final JCheckBoxMenuItem	jcbmiYAxisGrid;				// menu Y-Axis grid checkbox
	private JFileChooser jfc;
	private ScatterPlotPanel scatterPlotPanel;

	public ScatterPlotRightClickedMenu(final ScatterPlotPanel scatterPlotPanel) {
		jmiSaveAs = new JMenuItem("Save As");
		jmiBarGraph = new JMenuItem("Plot Bar Graph");
		jmiScatterPlot = new JMenuItem("Plot Points");
		jmiCurve = new JMenuItem("Plot Curve");
		jmiChangeColors = new JMenuItem("Change Colors");
		jcbmiXAxisGrid = new JCheckBoxMenuItem("Show Vertical Grid Lines", true);
		jcbmiYAxisGrid = new JCheckBoxMenuItem("Show Horizontal Grid Lines", true);
		add(jmiSaveAs);
		add(jmiBarGraph);
		add(jmiScatterPlot);
		add(jmiCurve);
		add(jmiChangeColors);
		add(jcbmiXAxisGrid);
		add(jcbmiYAxisGrid);		
		
		jmiBarGraph.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ScatterPlotPanel.setBarGraph(true);
				ScatterPlotPanel.setCurve(false);	
				ScatterPlotPanel.setChangeColors(false);
				scatterPlotPanel.repaint();
			}
		});
		
		jmiScatterPlot.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ScatterPlotPanel.setBarGraph(false);
				ScatterPlotPanel.setCurve(false);
				ScatterPlotPanel.setChangeColors(false);
				scatterPlotPanel.repaint();
			}
		});
		
		jmiCurve.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ScatterPlotPanel.setCurve(true);
				ScatterPlotPanel.setBarGraph(false);
				ScatterPlotPanel.setChangeColors(false);
				scatterPlotPanel.repaint();
			}
		});
		
		jmiChangeColors.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ScatterPlotPanel.setChangeColors(true);
				scatterPlotPanel.repaint();
			}
		});

		jcbmiXAxisGrid.setSelected(ScatterPlotPanel.isxAxisGridLines());
		jcbmiXAxisGrid.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {				
					ScatterPlotPanel.setxAxisGridLines(true);					
				}
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					ScatterPlotPanel.setxAxisGridLines(false);
				}
				ScatterPlotPanel.setChangeColors(false);
				scatterPlotPanel.repaint();
			}
		});
		
		jcbmiYAxisGrid.setSelected(ScatterPlotPanel.isyAxisGridLines());
		jcbmiYAxisGrid.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {				
					ScatterPlotPanel.setyAxisGridLines(true);					
				}
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					ScatterPlotPanel.setyAxisGridLines(false);
				}
				ScatterPlotPanel.setChangeColors(false);
				scatterPlotPanel.repaint();
			}
		});
		
		jfc = new JFileChooser();		
		jmiSaveAs.setVisible(true);
		jmiSaveAs.addActionListener(this);
		this.scatterPlotPanel = scatterPlotPanel;
		this.addPopupMenuListener(this);
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

	@Override
	public void popupMenuCanceled(PopupMenuEvent arg0) {}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		jcbmiXAxisGrid.setState(ScatterPlotPanel.isxAxisGridLines());		
	}	
}
