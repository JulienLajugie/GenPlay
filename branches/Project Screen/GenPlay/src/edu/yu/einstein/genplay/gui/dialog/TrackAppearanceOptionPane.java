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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.core.enums.GraphicsType;




/**
 * A frame allowing to configure the properties of a curve.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackAppearanceOptionPane extends JDialog {

	private static final long serialVersionUID = -479634976273674864L;	// Generated serial number

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private static final Dimension		APPEARANCE_FRAME_DIMENSION = 
		new Dimension(340, 200);									// Dimension of this frame
	private static final int 			MAX_LINE_COUNT = 100;		// maximum number of lines
	private final JLabel				jlHorizontalGrid;			// label horizontal grid
	private final JCheckBox				jcbHorizontalGrid;			// check box horizontal grid
	private final JLabel				jlYLineCount;				// label YLine	
	private final JFormattedTextField 	jftfYLineCount;				// textField YLine
	private final JLabel 				jlXLineCount;				// label XLine
	private final JFormattedTextField 	jftfXLineCount;				// textField XLine
	private final JLabel				jlGraphicsType;				// label type of graphics
	private final JComboBox				jcbGraphicsType;			// combo Box type of graphics
	private final JLabel				jlCurvesColor;				// label curve color
	private final JButton				jbCurvesColor;				// button to choose the color
	private final JButton 				jbOk;						// button OK
	private final JButton 				jbCancel;					// button cancel

	private boolean						showHorizontalGrid;			// horizontal grid showed or hid
	private int							xLineCount;					// number of vertical lines
	private int							yLineCount;					// number of horrizontal lines
	private GraphicsType				graphicsType;				// type of graphics
	private Color						curvesColor;				// color of the curves	
	private int							approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not


	/**
	 * Constructor, create an instance of TrackAppearanceOptionPane.
	 * @param showHorizontalLines Check or uncheck the show grid box.
	 * @param xLineCount a xLine value to show in the text corresponding field
	 * @param yLineCount a yLine value to show in the corresponding text field
	 * @param trackColor a common curve color
	 * @param trackType the type of the track
	 */
	public TrackAppearanceOptionPane(boolean showHorizontalLines, int xLineCount, int yLineCount, Color trackColor, GraphicsType trackType) {
		super();
		
		this.showHorizontalGrid = showHorizontalLines;
		this.xLineCount = xLineCount;
		this.yLineCount = yLineCount;
		this.curvesColor = trackColor;
		this.graphicsType = trackType;
		
		jlHorizontalGrid = new JLabel("Show horizontal grid:");
		jcbHorizontalGrid = new JCheckBox();
		jcbHorizontalGrid.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				showHorizontalGrid = jcbHorizontalGrid.isSelected();				
			}
		});
		jcbHorizontalGrid.setSelected(showHorizontalGrid);

		// format of the formated text fields
		NumberFormatter nf = new NumberFormatter();
		nf.setFormat(new DecimalFormat("#"));
		nf.setMinimum(0);
		nf.setMaximum(MAX_LINE_COUNT);

		jlYLineCount = new JLabel("Number of horrizontal lines");
		jftfYLineCount = new JFormattedTextField(nf);
		jftfYLineCount.setColumns(2);	
		jftfYLineCount.setValue(yLineCount);		

		jlXLineCount = new JLabel("Show vertical lines every (in bp):");
		jftfXLineCount = new JFormattedTextField(nf);
		jftfXLineCount.setColumns(2);	
		jftfXLineCount.setValue(xLineCount);		

		jlGraphicsType = new JLabel("Type of the graphics:");
		GraphicsType[] typesOfGraph = GraphicsType.values();
		jcbGraphicsType = new JComboBox(typesOfGraph);
		jcbGraphicsType.setSelectedItem(graphicsType);
		jcbGraphicsType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graphicsType = (GraphicsType)jcbGraphicsType.getSelectedItem();
			}
		});

		jlCurvesColor = new JLabel("Color of the curves:");
		jbCurvesColor = new JButton("Color");
		jbCurvesColor.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseCurveColor();				
			}
		});
		jbCurvesColor.setBackground(curvesColor);
		jbCurvesColor.setForeground(new Color(curvesColor.getRGB() ^ 0xffffff));
		jbCurvesColor.setBackground(curvesColor);
		jbCurvesColor.setForeground(new Color(curvesColor.getRGB() ^ 0xffffff));

		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();				
			}
		});

		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();				
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlHorizontalGrid, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(jcbHorizontalGrid, c);

		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlYLineCount, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jftfYLineCount, c);

		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlXLineCount, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jftfXLineCount, c);

		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlGraphicsType, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcbGraphicsType, c);	

		c.gridx = 0;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlCurvesColor, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbCurvesColor, c);		

		c.gridx = 0;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbOk, c);

		c.gridx = 1;
		add(jbCancel, c);

		setSize(APPEARANCE_FRAME_DIMENSION);
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setTitle("Track configuration");
		setVisible(false);
		jbOk.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOk);
	}


	/**
	 * @return if the show horizontal grid option has been checked.
	 */
	public final boolean getShowHorizontalGrid() {
		return showHorizontalGrid;
	}


	/**
	 * @return The value of xLineCount.
	 */
	public final int getXLineCount() {
		return xLineCount;
	}
	

	/**
	 * @return The value of yLineCount.
	 */
	public final int getYLineCount() {
		return yLineCount;
	}
	

	/**
	 * @return The value of curvesColor
	 */
	public final Color getCurvesColor() {
		return curvesColor;
	}
	

	/**
	 * @return The value graphicsType
	 */
	public final GraphicsType getGraphicsType() {
		return graphicsType;
	}

	/**
	 * Hides this frame when Cancel is pressed. 
	 */
	private void jbCancelActionPerformed() {
		this.setVisible(false);
	}


	/**
	 * Asks the user to choose a color.
	 */
	private void chooseCurveColor() {		
		Color newCurvewColor = JColorChooser.showDialog(getRootPane(), "Choose a color for the selected curves", curvesColor);
		if (newCurvewColor != null) {
			curvesColor = newCurvewColor;
			jbCurvesColor.setBackground(curvesColor);
			jbCurvesColor.setForeground(new Color(curvesColor.getRGB() ^ 0xffffff));
		}		
	}


	/**
	 * Called when OK is pressed.
	 */
	private void jbOkActionPerformed() {
		approved = APPROVE_OPTION;
		xLineCount = ((Number)jftfXLineCount.getValue()).intValue();
		yLineCount = ((Number)jftfYLineCount.getValue()).intValue();
		this.setVisible(false);
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showTrackConfiguration(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}

}
