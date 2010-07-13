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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.scatterPlot.AxisOption;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPanel;
import yu.einstein.gdp2.util.Utils;

/**
 * This class is used to provide options for the graph 
 * @author Chirag Gorasia
 *
 */
public class ScatterPlotRightClickedMenu extends JPopupMenu implements ActionListener, PopupMenuListener {
	private static final long serialVersionUID = 5259896882194725264L;

	private final JMenuItem	jmiSaveImage;						// menu save plot as image
	private final JMenuItem	jmiSaveData;						// menu save plot as image
	private final JMenuItem	jmiSetYAxis;						// menu for setting y axis params
	private final JMenuItem	jmiSetXAxis;						// menu for setting x axis params
	private final JRadioButtonMenuItem	jmiBarGraph;			// draw bar graph
	private final JRadioButtonMenuItem	jmiScatterPlot;			// draw scatter plot
	private final JRadioButtonMenuItem jmiCurve;				// join points to form a curve
	private final JMenuItem jmiChangeColors;					// change graph colors
	private final JCheckBoxMenuItem	jcbmiXAxisGrid;				// menu X-Axis grid checkbox
	private final JCheckBoxMenuItem	jcbmiYAxisGrid;				// menu Y-Axis grid checkbox
	private JFileChooser jfc;
	private ScatterPlotPanel scatterPlotPanel;
	private ButtonGroup group;									// radio button group for bar graph, scatter plot and curve
	private int i = 0;

	public ScatterPlotRightClickedMenu(final ScatterPlotPanel scatterPlotPanel) {
		jmiSaveImage = new JMenuItem("Save Plot");
		jmiSaveData = new JMenuItem("Save Data");
		jmiSetYAxis = new JMenuItem("Set Y Axis");
		jmiSetXAxis = new JMenuItem("Set X Axis");
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
		add(jmiSaveImage);
		add(jmiSaveData);
		addSeparator();
		add(jmiChangeColors);
		addSeparator();
		add(jmiSetYAxis);
		add(jmiSetXAxis);
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
				
			    if (selectedValue != null) {
			    	for (i = 0; i < graphNames.length; i++) {
			    		if (selectedValue.toString().equals(graphNames[i])) {
			    			break; 
			    		}
			    	}			    
					Color chosenColor = JColorChooser.showDialog(null, "Select Color", Color.white);
					if (chosenColor != null) {
						ScatterPlotPanel.setGraphColors(chosenColor, i);
						scatterPlotPanel.repaint();
					}	
			    }
			}
		});		
		
		jmiSetYAxis.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				AxisOption axisOption = new AxisOption("Y-Axis");
				scatterPlotPanel.repaint();
			}
		});
		
		jmiSetXAxis.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				AxisOption axisOption = new AxisOption("X-Axis");
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
		jmiSaveImage.setVisible(true);
		jmiSaveImage.addActionListener(this);
		jmiSaveData.setVisible(true);
		jmiSaveData.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jmiSaveData.setVisible(false);
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV file", "csv");
				jfc.setFileFilter(filter);
				int retValue = jfc.showSaveDialog(getRootPane());
				if (retValue == JFileChooser.APPROVE_OPTION) {
					File fileName = jfc.getSelectedFile();
					if (!Utils.cancelBecauseFileExist(getRootPane(), fileName)) {
						fileName = Utils.addExtension(fileName, "csv");
						savePlotData(fileName);
					}
				}			
			}
		});
		this.scatterPlotPanel = scatterPlotPanel;
		this.addPopupMenuListener(this);
	}		

	@Override
	public void actionPerformed(ActionEvent event) {
		jmiSaveImage.setVisible(false);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG file", "jpg", "jpeg");
		jfc.setFileFilter(filter);
		int retValue = jfc.showSaveDialog(getRootPane());
		if (retValue == JFileChooser.APPROVE_OPTION) {
			File fileName = jfc.getSelectedFile();
			if (!Utils.cancelBecauseFileExist(getRootPane(), fileName)) {
				fileName = Utils.addExtension(fileName, "jpg");
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

	/**
	 * Save the scatter plot data as a CSV file.
	 * @param file output file
	 */
	public void savePlotData(File file) {
		try {
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
			bufWriter.write("Y-Axis " + ScatterPlotPanel.getyAxisName());
			int maxLength = ScatterPlotPanel.getGraphList().get(0).getDataPoints().length;
			int graphNumber = 0;
			for (int i = 0; i < ScatterPlotPanel.getGraphNames().length; i++) {
				bufWriter.write("," + ScatterPlotPanel.getGraphNames()[i]);
				if (maxLength < ScatterPlotPanel.getGraphList().get(i).getDataPoints().length) {
					maxLength = ScatterPlotPanel.getGraphList().get(i).getDataPoints().length;
					graphNumber = i;
				}
			}	
			bufWriter.newLine();
			for (int i = 0; i < maxLength; i++){
				if (ScatterPlotPanel.getGraphList().get(graphNumber).getDataPoints()[i][0] == 0 && i != 0) {
					break;
				}
				bufWriter.write(Double.toString(ScatterPlotPanel.getGraphList().get(graphNumber).getDataPoints()[i][0]));
				for (int j = 0; j < ScatterPlotPanel.getGraphList().size(); j++) {
					bufWriter.write(",");
					if (i < ScatterPlotPanel.getGraphList().get(j).getDataPoints().length) {
						bufWriter.write(Integer.toString((int)ScatterPlotPanel.getGraphList().get(j).getDataPoints()[i][1]));

					} else {
						bufWriter.write(Integer.toString(0));						
					}					
				}
				bufWriter.newLine();				
			}			
			bufWriter.close();
		} catch (IOException e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving the scatter plot data as a CSV file");;
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