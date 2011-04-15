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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import edu.yu.einstein.genplay.core.enums.GraphicsType;
import edu.yu.einstein.genplay.gui.track.CurveTrackGraphics;
import edu.yu.einstein.genplay.gui.track.ScoredTrackGraphics;




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

	private static final int 			MAX_LINE_COUNT = 100;		// maximum number of lines
	
	private final JPanel				jpGrid;						// panel with grid options
	private final JLabel				jlHorizontalGrid;			// label horizontal grid
	private final JCheckBox				jcbHorizontalGrid;			// check box horizontal grid
	private final JLabel				jlYLineCount;				// label YLine	
	private final JFormattedTextField 	jftfYLineCount;				// textField YLine
	private final JLabel 				jlXLineCount;				// label XLine
	private final JFormattedTextField 	jftfXLineCount;				// textField XLine
	
	private final JPanel				jpCurve;					// panel with curve options
	private final JLabel				jlGraphicsType;				// label type of graphics
	private final JComboBox				jcbGraphicsType;			// combo Box type of graphics
	private final JLabel				jlCurvesColor;				// label curve color
	private final JButton				jbCurvesColor;				// button to choose the color
	
	private final JPanel				jpScore;					// panel with score options
	private final JLabel				jlScorePosition;			// label score position
	private final JRadioButton			jrbTopPosition;				// radio button top score position
	private final JRadioButton			jrbBottomPosition;			// radio button bottom score position
	private final ButtonGroup			scorePositionGroup;			// goup for the score position
	private final JLabel				jlScoreColor;				// label score color
	private final JButton				jbScoreColor;				// button score color
	
	private final JButton 				jbOk;						// button OK
	private final JButton 				jbCancel;					// button cancel

	private boolean						showHorizontalGrid;			// horizontal grid showed or hid
	private int							xLineCount;					// number of vertical lines
	private int							yLineCount;					// number of horrizontal lines
	private GraphicsType				graphicsType;				// type of graphics
	private Color						curvesColor;				// color of the curves	
	private int 						scorePosition;				// position of the score
	private Color						scoreColor;					// color of the score
	
	private int							approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not


	/**
	 * Constructor, create an instance of TrackAppearanceOptionPane.
	 * @param showHorizontalLines Check or uncheck the show grid box.
	 * @param xLineCount a xLine value to show in the text corresponding field
	 * @param yLineCount a yLine value to show in the corresponding text field
	 * @param trackColor a common curve color
	 * @param trackType the type of the track
	 * @param scorePosition position of the score
	 * @param scoreColor color of the score
	 */
	public TrackAppearanceOptionPane(boolean showHorizontalLines, int xLineCount, int yLineCount, Color trackColor, GraphicsType trackType, int scorePosition, Color scoreColor) {
		super();
		
		this.showHorizontalGrid = showHorizontalLines;
		this.xLineCount = xLineCount;
		this.yLineCount = yLineCount;
		this.curvesColor = trackColor;
		this.graphicsType = trackType;
		this.scorePosition = scorePosition;
		this.scoreColor = scoreColor;
		
		jpGrid = new JPanel();
		jpGrid.setBorder(BorderFactory.createTitledBorder("Grid Options"));
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

		jpCurve = new JPanel();
		jpCurve.setBorder(BorderFactory.createTitledBorder("Curve Options"));
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
		//jbCurvesColor.setBackground(curvesColor);
		//jbCurvesColor.setForeground(new Color(curvesColor.getRGB() ^ 0xffffff));

		jpScore = new JPanel();
		jpScore.setBorder(BorderFactory.createTitledBorder("Score Options"));
		jlScorePosition = new JLabel("Score Position:");
		jrbTopPosition = new JRadioButton("Top");
		jrbTopPosition.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (jrbTopPosition.isSelected()) {
					TrackAppearanceOptionPane.this.scorePosition = CurveTrackGraphics.TOP_SCORE_POSITION;
				}
			}
		});
		jrbBottomPosition = new JRadioButton("Bottom");
		jrbBottomPosition.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (jrbBottomPosition.isSelected()) {
					TrackAppearanceOptionPane.this.scorePosition = CurveTrackGraphics.BOTTOM_SCORE_POSITION;
				}
			}
		});
		// create the group for the radio buttons
		scorePositionGroup = new ButtonGroup();
		scorePositionGroup.add(jrbTopPosition);
		scorePositionGroup.add(jrbBottomPosition);
		// select the appropriate radio button
		if (scorePosition == ScoredTrackGraphics.TOP_SCORE_POSITION) {
			jrbTopPosition.setSelected(true);
		} else if (scorePosition == ScoredTrackGraphics.BOTTOM_SCORE_POSITION) {
			jrbBottomPosition.setSelected(true);
		}
		jlScoreColor = new JLabel("Color of the score:");
		jbScoreColor = new JButton("Color");
		jbScoreColor.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				chooseScoreColor();				
			}
		});
		jbScoreColor.setBackground(scoreColor);
		
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

		
		jpGrid.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		jpGrid.add(jlHorizontalGrid, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.CENTER;
		jpGrid.add(jcbHorizontalGrid, c);

		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		jpGrid.add(jlYLineCount, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		jpGrid.add(jftfYLineCount, c);

		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		jpGrid.add(jlXLineCount, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		jpGrid.add(jftfXLineCount, c);

		jpCurve.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		jpCurve.add(jlGraphicsType, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		jpCurve.add(jcbGraphicsType, c);	

		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		jpCurve.add(jlCurvesColor, c);

		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		jpCurve.add(jbCurvesColor, c);		

		jpScore.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		jpScore.add(jlScorePosition, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		jpScore.add(jrbTopPosition, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		jpScore.add(jrbBottomPosition, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		jpScore.add(jlScoreColor, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_END;
		jpScore.add(jbScoreColor, c);
		
		setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		Insets insets = new Insets(5, 5, 5, 5);
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets = insets;
		add(jpGrid, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.insets = insets;
		add(jpCurve, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.insets = insets;
		add(jpScore, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbOk, c);

		c.gridx = 1;
		add(jbCancel, c);

		pack();
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setTitle("Track configuration");
		setVisible(false);
		jbOk.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOk);
	}


	/**
	 * Asks the user to choose a color.
	 */
	private void chooseCurveColor() {		
		Color newCurveColor = JColorChooser.showDialog(getRootPane(), "Choose a color for the selected curves", curvesColor);
		if (newCurveColor != null) {
			curvesColor = newCurveColor;
			jbCurvesColor.setBackground(curvesColor);
			jbCurvesColor.setForeground(new Color(curvesColor.getRGB() ^ 0xffffff));
		}		
	}


	protected void chooseScoreColor() {
		Color newScoreColor = JColorChooser.showDialog(getRootPane(), "Choose a color for the selected track score", scoreColor);
		if (newScoreColor != null) {
			scoreColor = newScoreColor;
			jbScoreColor.setBackground(scoreColor);
		}	
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
	 * @return the scoreColor
	 */
	public final Color getScoreColor() {
		return scoreColor;
	}
	
	/**
	 * @return the scorePosition
	 */
	public final int getScorePosition() {
		return scorePosition;
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
	 * Hides this frame when Cancel is pressed. 
	 */
	private void jbCancelActionPerformed() {
		this.setVisible(false);
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
