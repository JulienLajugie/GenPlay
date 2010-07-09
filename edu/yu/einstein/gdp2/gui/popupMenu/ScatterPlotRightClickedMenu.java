/**
 * @author Chirag Gorasia
 */

package yu.einstein.gdp2.gui.popupMenu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPanel;
import yu.einstein.gdp2.util.Utils;

/**
 * This class is used to provide options for the graph 
 * @author Chirag Gorasia
 *
 */
public class ScatterPlotRightClickedMenu extends JPopupMenu implements ActionListener, PopupMenuListener {
	private static final long serialVersionUID = 5259896882194725264L;

	private final JMenuItem	jmiSaveAs;							// menu save plot as image
	private final JRadioButtonMenuItem	jmiBarGraph;						// draw bar graph
	private final JRadioButtonMenuItem	jmiScatterPlot;						// draw scatter plot
	private final JRadioButtonMenuItem jmiCurve;							// join points to form a curve
	private final JMenuItem jmiChangeColors;					// change graph colors
	private final JCheckBoxMenuItem	jcbmiXAxisGrid;				// menu X-Axis grid checkbox
	private final JCheckBoxMenuItem	jcbmiYAxisGrid;				// menu Y-Axis grid checkbox
	private JFileChooser jfc;
	private JDialog jd;
	private ScatterPlotPanel scatterPlotPanel;
	private ButtonGroup group;
	private int i = 0;

	public ScatterPlotRightClickedMenu(final ScatterPlotPanel scatterPlotPanel) {
		jmiSaveAs = new JMenuItem("Save As");
		jmiBarGraph = new JRadioButtonMenuItem("Plot Bar Graph");
		jmiScatterPlot = new JRadioButtonMenuItem("Plot Points");
		jmiCurve = new JRadioButtonMenuItem("Plot Curve");
		jmiChangeColors = new JMenuItem("Change Colors");
		jcbmiXAxisGrid = new JCheckBoxMenuItem("Show Vertical Grid Lines", true);
		jcbmiYAxisGrid = new JCheckBoxMenuItem("Show Horizontal Grid Lines", true);
		group = new ButtonGroup();
		group.add(jmiScatterPlot);
		group.add(jmiBarGraph);
		group.add(jmiCurve);
		if (ScatterPlotPanel.isBarGraph()) {
			jmiBarGraph.setSelected(true);
		} else if (ScatterPlotPanel.isCurve()) {
			jmiCurve.setSelected(true);
		} else {
			jmiScatterPlot.setSelected(true);
		}		
		
		add(jmiBarGraph);
		add(jmiScatterPlot);
		add(jmiCurve);
		addSeparator();
		add(jmiSaveAs);
		addSeparator();
		add(jmiChangeColors);
		addSeparator();
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
				jmiScatterPlot.setSelected(true);				
			}
		});
		
		jmiCurve.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ScatterPlotPanel.setCurve(true);
				ScatterPlotPanel.setBarGraph(false);
				ScatterPlotPanel.setChangeColors(false);				
				scatterPlotPanel.repaint();
				jmiCurve.setSelected(true);
			}
		});
		
		jmiChangeColors.addActionListener(new ActionListener() {
					
			@Override
			public void actionPerformed(ActionEvent e) {
				ScatterPlotPanel.setChangeColors(true);		
				String[] graphNames = ScatterPlotPanel.getGraphNames();
				String selectedValue = (String) JOptionPane.showInputDialog(null, "Select a graph", "Choose Color", JOptionPane.PLAIN_MESSAGE, null, graphNames, graphNames[0]);
				
			    if (selectedValue == null) {
			    	selectedValue = graphNames[0];
			    } else {
			    	for (i = 0; i < graphNames.length; i++) {
			    		if (selectedValue.toString().equals(graphNames[i])) {
			    			break; 
			    		}
			    	}
			    }			    
				Color chosenColor = JColorChooser.showDialog(null, "Select Color", Color.white);
				if (chosenColor != null) {
					ScatterPlotPanel.setGraphColors(chosenColor, i);
					scatterPlotPanel.repaint();
				}
				jd.setVisible(true);
				jd.setModal(true);		
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
