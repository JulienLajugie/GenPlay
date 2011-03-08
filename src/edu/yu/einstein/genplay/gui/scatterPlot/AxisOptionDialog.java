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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.core.enums.LogBase;



/**
 * Option dialog for the axis of a {@link ScatterPlotPane} 
 * @author Julien Lajugie
 * @version 0.1
 */
public class AxisOptionDialog extends JDialog {

	private static final long serialVersionUID = -989613090051981876L; // generated ID
	private static final DecimalFormat 	DF = new DecimalFormat("###,###,###,###.###"); // decimal format for the input
	private static final int 			PAD = 10;			// padding between elements
	private final JLabel 				jlMin;				// min label
	private final JFormattedTextField 	jftfMin;			// min text field
	private final JLabel 				jlMax;				// max label
	private final JFormattedTextField 	jftfMax;			// max text field
	private final JLabel 				jlMajorUnit;		// major unit label
	private final JFormattedTextField 	jftfMajorUnit;		// major unit text field
	private final JLabel 				jlMinorUnit;		// minor unit label
	private final JFormattedTextField 	jftfMinorUnit;		// minor unit text field
	private final JCheckBox 			jcbShowGrid;		// show grid check box
	private final JCheckBox 			jchbLog;			// log check box
	private final JComboBox 			jcbLog;				// log combo box
	private final JPanel 				topPanel;			// panel with min, max, major, minor unit options
	private final JPanel 				middlePanel;		// panel with grid option
	private final JPanel 				bottomPanel;		// panel with log options
	private final JButton 				jbCancel;			// cancel button
	private final JButton 				jbOk;				// ok button
	private int							approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 0;
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 1;


	/**
	 * Creates an instance of {@link AxisOptionDialog}
	 */
	public AxisOptionDialog() {
		jlMin = new JLabel("Minimum:");
		jftfMin = new JFormattedTextField(DF);
		// min must be strictly smaller than the max
		jftfMin.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Number min = (Number) jftfMin.getValue();
				((NumberFormatter) (jftfMax.getFormatter())).setMinimum(min.doubleValue() + Double.MIN_NORMAL);
				super.focusLost(e);
			}
		});

		jlMax = new JLabel("Maximum:");
		jftfMax = new JFormattedTextField(DF);
		// max must be strictly greater than the min
		jftfMax.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Number max = (Number) jftfMax.getValue();
				((NumberFormatter) (jftfMin.getFormatter())).setMaximum(max.doubleValue() - Double.MIN_NORMAL);
				super.focusLost(e);
			}
		});

		jlMajorUnit = new JLabel("Major unit:");
		jftfMajorUnit = new JFormattedTextField(DF);
		// major unit must be greater or equal than minor unit
		jftfMajorUnit.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Number major = (Number) jftfMajorUnit.getValue();
				((NumberFormatter) (jftfMinorUnit.getFormatter())).setMaximum(major.doubleValue());
				super.focusLost(e);
			}
		});
		
		jlMinorUnit = new JLabel("Minor unit:");
		jftfMinorUnit = new JFormattedTextField(DF);
		// we just accept values > 0
		((NumberFormatter) (jftfMinorUnit.getFormatter())).setMinimum(Double.MIN_NORMAL);
		// minor unit must be smaller or equal than major unit
		jftfMinorUnit.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Number minor = (Number) jftfMinorUnit.getValue();
				((NumberFormatter) (jftfMajorUnit.getFormatter())).setMinimum(minor.doubleValue());
				super.focusLost(e);
			}
		});
		// panel for min, max, major and minor units
		topPanel = new JPanel();
		topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		
		jcbShowGrid = new JCheckBox("Show Grid");
		
		// panel for the show grid check box
		middlePanel = new JPanel();

		jchbLog = new JCheckBox("Logarithnmic scale");		
		jchbLog.setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.LIGHT_GRAY));
		// disable the combo box for the log base if the log scale is not selected
		jchbLog.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				jcbLog.setEnabled(jchbLog.isSelected());
				// not
				if (jchbLog.isSelected()) {
					if (getMax() < 1) {
						jftfMax.setValue(((LogBase) jcbLog.getSelectedItem()).getValue());
						jftfMin.setValue(1d);
					}
				}
				
			}
		});
		jcbLog = new JComboBox(LogBase.values());
		jcbLog.setEnabled(false);

		// panel for the log option
		bottomPanel = new JPanel();
		bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));

		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);				
			}
		});
		jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});
		// fill the top panel
		topPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(PAD, PAD, 0, 0);
		topPanel.add(jlMin, c);		

		c = new GridBagConstraints();
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(PAD, 0, 0, PAD);
		topPanel.add(jftfMin, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 1;
		c.insets = new Insets(0, PAD, 0, 0);
		topPanel.add(jlMax, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, PAD);
		topPanel.add(jftfMax, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 2;
		c.insets = new Insets(0, PAD, 0, 0);
		topPanel.add(jlMajorUnit, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, PAD);
		topPanel.add(jftfMajorUnit, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 3;
		c.insets = new Insets(0, PAD, PAD, 0);
		topPanel.add(jlMinorUnit, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, PAD, PAD);
		topPanel.add(jftfMinorUnit, c);

		// fill the middle panel
		middlePanel.setLayout(new GridBagLayout());

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(PAD, PAD, PAD, PAD);
		middlePanel.add(jcbShowGrid, c);

		// fill the bottom panel
		bottomPanel.setLayout(new GridBagLayout());

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(PAD, PAD, 0, PAD);
		bottomPanel.add(jchbLog, c);

		c = new GridBagConstraints();
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, PAD, PAD, PAD);
		bottomPanel.add(jcbLog, c);

		// fill the dialog
		setLayout(new GridBagLayout());

		c = new GridBagConstraints();
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, PAD, 0, PAD);
		add(topPanel, c);

		c = new GridBagConstraints();
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(0, PAD, 0, PAD);
		add(middlePanel, c);

		c = new GridBagConstraints();
		c.gridy = 2;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, PAD, 0, PAD);
		add(bottomPanel, c);

		c = new GridBagConstraints();
		c.gridy = 3;
		c.weightx = 1;
		c.insets = new Insets(PAD, PAD, PAD, 0);
		c.anchor = GridBagConstraints.LINE_END;
		add(jbOk, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 3;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(PAD, 0, PAD, PAD);
		add(jbCancel, c);

		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(jbOk);
		pack();
	}

	
	/**
	 * @return the selected log base
	 */
	public LogBase getLogBase() {
		return (LogBase) jcbLog.getSelectedItem();
	}


	/**
	 * @return the major unit
	 */
	public double getMajorUnit() {
		return ((Number) jftfMajorUnit.getValue()).doubleValue();
	}


	/**
	 * @return the max
	 */
	public double getMax() {
		return ((Number) jftfMax.getValue()).doubleValue();
	}


	/**
	 * @return the min
	 */
	public double getMin() {
		return ((Number) jftfMin.getValue()).doubleValue();
	}


	/**
	 * @return the minor unit
	 */
	public double getMinorUnit() {
		return ((Number) jftfMinorUnit.getValue()).doubleValue();
	}


	/**
	 * @return true if the logarithm scale option is selected
	 */
	public boolean isLogScale() {
		return jchbLog.isSelected();
	}


	/**
	 * @return true if the grid option is selected
	 */
	public boolean isShowGrid() {
		return jcbShowGrid.isSelected();
	}


	/**
	 * Sets the log base
	 * @param logBase log base to set 
	 */
	public void setLogBase(LogBase logBase) {
		jcbLog.setSelectedItem(logBase);
	}


	/**
	 * Sets to true to select the log scale option
	 * @param logScale 
	 */
	public void setLogScale(boolean logScale) {
		jchbLog.setSelected(logScale);
		// disable the combo box for the log base if the log scale is not selected
		jcbLog.setEnabled(logScale);
	}


	/**
	 * Sets the major unit
	 * @param majorUnit major unit to set
	 */
	public void setMajorUnit(double majorUnit) {
		jftfMajorUnit.setValue(majorUnit);
		// minor unit must be smaller than major unit
		((NumberFormatter) (jftfMinorUnit.getFormatter())).setMaximum(majorUnit);
	}


	/**
	 * Sets the max
	 * @param max max to set
	 */
	public void setMax(double max) {
		jftfMax.setValue(max);
		// min must be strictly smaller than the max
		((NumberFormatter) (jftfMin.getFormatter())).setMaximum(max - Double.MIN_NORMAL);
	}


	/**
	 * Sets the min
	 * @param min min to set
	 */
	public void setMin(double min) {
		jftfMin.setValue(min);
		// max must be strictly greater than the min
		((NumberFormatter) (jftfMax.getFormatter())).setMinimum(min + Double.MIN_NORMAL);
	}


	/**
	 * Sets the minor unit
	 * @param minorUnit minor unit to set
	 */
	public void setMinorUnit(double minorUnit) {
		jftfMinorUnit.setValue(minorUnit);
		// major unit must be greater than minor
		((NumberFormatter) (jftfMajorUnit.getFormatter())).setMinimum(minorUnit);
	}


	/**
	 * Sets the show grid
	 * @param showGrid true to select the show grid option
	 */
	public void setShowGrid(boolean showGrid) {
		jcbShowGrid.setSelected(showGrid);
	}


	/**
	 * Shows the component
	 * @param parent parent component
	 * @param title title of the dialog
	 * @return APPROVED if okay has been pressed, CANCELED otherwise
	 */
	public int showDialog(Component parent, String title) {
		setTitle(title);
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
