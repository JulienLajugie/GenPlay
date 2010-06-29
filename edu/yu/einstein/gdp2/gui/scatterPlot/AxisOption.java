/**
 * @author Chirag Gorasia
 * @version 0.1
 */

package yu.einstein.gdp2.gui.scatterPlot;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.NumberFormatter;

/**
 * Class to provide options to modify the tick size and min-max values of the x-y axes
 * @author Chirag Gorasia
 *
 */
public class AxisOption {

	private JLabel jlYAxisTickSize = null;					// JLabel for Y-axis tick size
	private JLabel jlYAxisMax = null;						// JLabel for Y-axis max value
	private JLabel jlYAxisMin = null;						// JLabel for Y-axis min value
	private JFormattedTextField jftYAxisTickSize = null;	// JFormattedTextField to accept user input for y-axis tick size
	private JFormattedTextField jftYAxisMax = null;			// JFormattedTextField to accept user input for y-axis max value
	private JFormattedTextField jftYAxisMin = null;			// JFormattedTextField to accept user input for y-axis min value
	private JButton jbYAxisOk = null;						// JButton OK
	private JButton jbYAxisClear = null;					// JButton Clear
	private JButton jbYAxisCancel = null;					// JButton Cancel
	
	private JLabel jlXAxisTickSize = null;					// JLabel for X-axis tick size
	private JLabel jlXAxisMax = null;						// JLabel for X-axis max value
	private JLabel jlXAxisMin = null;						// JLabel for X-axis min value
	private JFormattedTextField jftXAxisTickSize = null;	// JFormattedTextField to accept user input for x-axis tick size
	private JFormattedTextField jftXAxisMax = null;			// JFormattedTextField to accept user input for x-axis max value
	private JFormattedTextField jftXAxisMin = null;			// JFormattedTextField to accept user input for x-axis min value
	private JButton jbXAxisOk = null;						// JButton OK
	private JButton jbXAxisClear = null;					// JButton Clear
	private JButton jbXAxisCancel = null;					// JButton Cancel
	
	private JDialog jd = null;								// JDialog dialog box for x-y axes options

	public AxisOption(String axisName) {
		super();
		if (axisName.equals("Y-Axis")) {
			handleYAxisOption(axisName);
		} else {
			handleXAxisOption(axisName);
		}
	}
	
	private void handleXAxisOption(String axisName) {
		jlXAxisTickSize = new JLabel("Tick Size: ");
		jlXAxisMax = new JLabel("Max Value: ");
		jlXAxisMin = new JLabel("Min Value: ");
		NumberFormatter nF = new NumberFormatter(new DecimalFormat("###,###,###.##"));
		nF.setAllowsInvalid(false);
		jftXAxisTickSize = new JFormattedTextField(nF);
		jftXAxisTickSize.setText(Double.toString(ScatterPlotPanel.getXAxisStepSize()));
		jftXAxisMax = new JFormattedTextField(nF);
		jftXAxisMax.setText(Double.toString(ScatterPlotPanel.getXAxisEnd()));
		jftXAxisMin = new JFormattedTextField(nF);
		jftXAxisMin.setText(Double.toString(ScatterPlotPanel.getXAxisStart()));
		jftXAxisTickSize.setMinimumSize(new Dimension(100, 20));
		jftXAxisMax.setMinimumSize(new Dimension(100, 20));
		jftXAxisMin.setMinimumSize(new Dimension(100, 20));
		
		jbXAxisOk = new JButton("OK");
		jbXAxisOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jd.setVisible(false);
				if (jftXAxisTickSize.getText().length() != 0 && (Double.parseDouble(jftXAxisTickSize.getValue().toString())) > 0) {
					ScatterPlotPanel.setXAxisStepSize(Double.parseDouble(jftXAxisTickSize.getValue().toString()));					
				}
				if (jftXAxisMax.getText().length() != 0 && (Double.parseDouble(jftXAxisMax.getValue().toString())) > 0) {
					ScatterPlotPanel.setXAxis(ScatterPlotPanel.getXAxisStart(),(Double.parseDouble(jftXAxisMax.getValue().toString())));					
				}
				if (jftXAxisMin.getText().length() != 0)/* && (Double.parseDouble(jftXAxisMin.getValue().toString())) > 0)*/ {
					ScatterPlotPanel.setXAxis((Double.parseDouble(jftXAxisMin.getValue().toString())),ScatterPlotPanel.getXAxisEnd());					
				}
			}
		});
		
		jbXAxisClear = new JButton("Clear");
		jbXAxisClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jftXAxisTickSize.setText("");
				jftXAxisMax.setText("");
				jftXAxisMin.setText("");
			}
		});
		
		jbXAxisCancel = new JButton("Cancel");
		jbXAxisCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jd.setVisible(false);
			}
		});
		
		jd = new JDialog(); 
		jd.setTitle(axisName + " Option");
		
		jd.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;		
		jlXAxisTickSize.setVisible(true);
		jd.add(jlXAxisTickSize, c);
		
		c.gridx = 1;
		jftXAxisTickSize.setMinimumSize(new Dimension(100, 20));
		jftXAxisTickSize.setPreferredSize(new Dimension(100, 20));
		jd.add(jftXAxisTickSize,c);
		
		c.gridx = 0;
		c.gridy = 1;		
		jlXAxisMax.setVisible(true);
		jd.add(jlXAxisMax, c);
		
		c.gridx = 1;
		jftXAxisMax.setMinimumSize(new Dimension(100, 20));
		jftXAxisMax.setPreferredSize(new Dimension(100, 20));
		jd.add(jftXAxisMax,c);
		
		c.gridx = 0;
		c.gridy = 2;		
		jlXAxisMin.setVisible(true);
		jd.add(jlXAxisMin, c);
		
		c.gridx = 1;
		jftXAxisMin.setMinimumSize(new Dimension(100, 20));
		jftXAxisMin.setPreferredSize(new Dimension(100, 20));
		jd.add(jftXAxisMin,c);
		
		c.gridx = 0;
		c.gridy = 3;
		jd.add(jbXAxisOk,c);
		
		c.gridx = 1;
		c.gridy = 3;
		jd.add(jbXAxisClear,c);
		
		c.gridx = 2;
		c.gridy = 3;
		jd.add(jbXAxisCancel,c);
		
		jd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		jd.getRootPane().setDefaultButton(jbXAxisOk);
		jd.setPreferredSize(new Dimension (250, 125));
		jd.setMinimumSize(new Dimension (250, 125));
		jd.setResizable(false);
		jd.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - jd.getWidth())/2, (Toolkit.getDefaultToolkit().getScreenSize().height - jd.getHeight())/2);
		jd.pack();
		jd.setModal(true);
		jd.setVisible(true);
	}


	public void handleYAxisOption(String axisName) {
		jlYAxisTickSize = new JLabel("Tick Size: ");
		jlYAxisMax = new JLabel("Max Value: ");	
		jlYAxisMin = new JLabel("Min Value: ");	
		NumberFormatter nF = new NumberFormatter(new DecimalFormat("###,###,###.#"));
		nF.setAllowsInvalid(false);
		nF.setMinimum(0);
		jftYAxisTickSize = new JFormattedTextField(nF);
		jftYAxisTickSize.setText(Double.toString(ScatterPlotPanel.getYAxisStepSize()));
		jftYAxisMax = new JFormattedTextField(nF);
		jftYAxisMax.setText(Double.toString(ScatterPlotPanel.getYAxisEnd()));
		jftYAxisMin = new JFormattedTextField(nF);
		jftYAxisMin.setText(Double.toString(ScatterPlotPanel.getYAxisStart()));
		jftYAxisTickSize.setMinimumSize(new Dimension(100, 20));
		jftYAxisMax.setMinimumSize(new Dimension(100, 20));
		jftYAxisMin.setMinimumSize(new Dimension(100, 20));
		
		jbYAxisOk = new JButton("OK");
		jbYAxisOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jd.setVisible(false);
				if (jftYAxisTickSize.getText().length() != 0 && (Integer)(jftYAxisTickSize.getValue()) > 0) {
					ScatterPlotPanel.setYAxisStepSize((Integer)(jftYAxisTickSize.getValue()));					
				}
				if (jftYAxisMax.getText().length() != 0 && (Integer)(jftYAxisMax.getValue()) > 0) {
					ScatterPlotPanel.setYAxis(ScatterPlotPanel.getYAxisStart(),(Integer)(jftYAxisMax.getValue()));					
				}
				if (jftYAxisMin.getText().length() != 0) {
					ScatterPlotPanel.setYAxis((Integer)(jftYAxisMin.getValue()),ScatterPlotPanel.getYAxisEnd());					
				}
			}
		});
		
		jbYAxisClear = new JButton("Clear");
		jbYAxisClear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jftYAxisTickSize.setText("");
				jftYAxisMax.setText("");
				jftYAxisMin.setText("");
			}
		});
		
		jbYAxisCancel = new JButton("Cancel");
		jbYAxisCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jd.setVisible(false);
			}
		});
		
		jd = new JDialog(); 
		jd.setTitle(axisName + " Option");
		
		jd.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;		
		jlYAxisTickSize.setVisible(true);
		jd.add(jlYAxisTickSize, c);
		
		c.gridx = 1;
		jftYAxisTickSize.setMinimumSize(new Dimension(100, 20));
		jftYAxisTickSize.setPreferredSize(new Dimension(100, 20));
		jd.add(jftYAxisTickSize,c);
		
		c.gridx = 0;
		c.gridy = 1;		
		jlYAxisMax.setVisible(true);
		jd.add(jlYAxisMax, c);
		
		c.gridx = 1;
		jftYAxisMax.setMinimumSize(new Dimension(100, 20));
		jftYAxisMax.setPreferredSize(new Dimension(100, 20));
		jd.add(jftYAxisMax,c);
		
		c.gridx = 0;
		c.gridy = 2;		
		jlYAxisMin.setVisible(true);
		jd.add(jlYAxisMin, c);
		
		c.gridx = 1;
		jftYAxisMin.setMinimumSize(new Dimension(100, 20));
		jftYAxisMin.setPreferredSize(new Dimension(100, 20));
		jd.add(jftYAxisMin,c);
		
		c.gridx = 0;
		c.gridy = 3;
		jd.add(jbYAxisOk,c);
		
		c.gridx = 1;
		c.gridy = 3;
		jd.add(jbYAxisClear,c);
		
		c.gridx = 2;
		c.gridy = 3;
		jd.add(jbYAxisCancel,c);
		
		jd.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		jd.getRootPane().setDefaultButton(jbYAxisOk);
		jd.setPreferredSize(new Dimension (250, 125));
		jd.setMinimumSize(new Dimension (250, 125));
		jd.setResizable(false);
		jd.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - jd.getWidth())/2, (Toolkit.getDefaultToolkit().getScreenSize().height - jd.getHeight())/2);
		jd.pack();
		jd.setModal(true);
		jd.setVisible(true);
	}
}

