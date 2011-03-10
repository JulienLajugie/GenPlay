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
package edu.yu.einstein.genplay.gui.dialog.newCurveTrackDialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.Strand;



/**
 * A dialog for the input for a curve track loading
 * @author Julien Lajugie
 * @version 0.1
 */
public class NewCurveTrackDialog extends JDialog {

	private static final long serialVersionUID = -4896476921693184496L; // generated ID
	private static final int 			INSET = 7;					// inset between the components
	private final TrackNamePanel 		trackNamePanel;				// panel for the track name
	private final BinSizePanel			binSizePanel;				// panel for the binsize
	private final ChromoSelectionPanel 	chromoSelectionPanel;		// panel for selecting chromosomes
	private final CalculMethodPanel 	calculMethodPanel;			// panel for the method of score calculation 
	private final DataPrecisionPanel 	dataPrecisionPanel;			// panel for the precision of the data
	private final StrandSelectionPanel	strandSelectionPanel;		// panel for the selection of the strand to extract
	private final JButton 				jbOk; 						// Button OK
	private final JButton 				jbCancel; 					// Button cancel
	private int 						approved = CANCEL_OPTION;	// indicate if the user canceled or validated	

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 0;

	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 1;


	/**
	 * Main method for the tests
	 * @param args
	 */
	public static void main(String[] args) {
		NewCurveTrackDialog ctd = new NewCurveTrackDialog("test", false, false, false, false, true, true);
		ctd.showDialog(null);
		ctd.dispose();
	}


	/**
	 * Creates an instance of {@link NewCurveTrackDialog}
	 * @param trackName the default name of the track
	 * @param isNameNeeded true if the track name need to be asked
	 * @param isBinSizeNeeded true if the binsize need to be asked
	 * @param isPrecisionNeeded true if the precision need to be asked
	 * @param isMethodNeeded true if the method of calculation need to be asked
	 * @param isStrandNeeded true if the strand selection need to be asked 
	 * @param isChromoSelectionNeeded true if the chromosome selection need to be asked
	 */
	public NewCurveTrackDialog(String trackName, boolean isNameNeeded, boolean isBinSizeNeeded, boolean isPrecisionNeeded, 
			boolean isMethodNeeded, boolean isStrandNeeded, boolean isChromoSelectionNeeded) {
		super();
		// create panels
		trackNamePanel = new TrackNamePanel(trackName);
		binSizePanel = new BinSizePanel();
		chromoSelectionPanel = new ChromoSelectionPanel();
		calculMethodPanel = new CalculMethodPanel();
		dataPrecisionPanel = new DataPrecisionPanel();
		strandSelectionPanel = new StrandSelectionPanel();

		// create the OK button
		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				binSizePanel.saveDefault();
				calculMethodPanel.saveDefault();
				dataPrecisionPanel.saveDefault();
				chromoSelectionPanel.saveDefault();
				strandSelectionPanel.saveDefault();
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		// create the cancel button
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		// if there is no chromosome selection panel the other panels 
		int leftPanelsGridWidth = 1;
		if (!isChromoSelectionNeeded) {
			leftPanelsGridWidth = 2;
		}

		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c;
		
		if (isNameNeeded) {
			c = new GridBagConstraints();
			c.anchor = GridBagConstraints.LINE_END;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = leftPanelsGridWidth;
			c.insets = new Insets(INSET, INSET, INSET, INSET);
			c.weightx = 1;
			c.weighty = 1;
			add(trackNamePanel, c);
		}

		if (isChromoSelectionNeeded) {
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridheight = 5;
			c.gridwidth = 2;
			if (!isNameNeeded && !isBinSizeNeeded && !isMethodNeeded && !isPrecisionNeeded && !isStrandNeeded) {
				c.gridx = 0;
			} else {
				c.gridx = 1;
			}
			c.gridy = 0;
			c.insets = new Insets(INSET, INSET, INSET, INSET);
			c.weightx = 1;
			c.weighty = 1;
			add(chromoSelectionPanel, c);
		}

		if (isBinSizeNeeded) {
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = leftPanelsGridWidth;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(INSET, INSET, INSET, INSET);
			c.weightx = 1;
			c.weighty = 1;
			add(binSizePanel, c);
		}

		if (isMethodNeeded) {
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = leftPanelsGridWidth;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = new Insets(INSET, INSET, INSET, INSET);
			c.weightx = 1;
			c.weighty = 1;
			add(calculMethodPanel, c);
		}

		if (isPrecisionNeeded) {
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = leftPanelsGridWidth;
			c.gridx = 0;
			c.gridy = 3;
			c.insets = new Insets(INSET, INSET, INSET, INSET);
			c.weightx = 1;
			c.weighty = 1;
			add(dataPrecisionPanel, c);
		}
		
		
		if (isStrandNeeded) {
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridwidth = leftPanelsGridWidth;
			c.gridx = 0;
			c.gridy = 4;
			c.insets = new Insets(INSET, INSET, INSET, INSET);
			c.ipadx = INSET * 2;
			c.weightx = 1;
			c.weighty = 1;
			add(strandSelectionPanel, c);
		}

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(INSET, INSET, INSET, INSET);
		c.weightx = 1;
		c.weighty = 1;
		add(jbOk, c);

		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 1;
		c.gridy = 5;
		c.insets = new Insets(INSET, INSET, INSET, INSET);
		c.weightx = 1;
		c.weighty = 1;
		add(jbCancel, c);		

		setTitle("New Track");
		pack();
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(getRootPane());
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(jbOk);
	}


	/**
	 * @return the selected BinSize
	 */
	public int getBinSize() {
		return binSizePanel.getBinSize();
	}


	/**
	 * @return the selected {@link DataPrecision}
	 */
	public DataPrecision getDataPrecision() {
		return dataPrecisionPanel.getDataPrecision();
	}


	/**
	 * @return the selected {@link ScoreCalculationMethod}
	 */
	public ScoreCalculationMethod getScoreCalculationMethod() {
		return calculMethodPanel.getScoreCalculationMethod();
	}


	/**
	 * @return an array of booleans set to true for the selected {@link Chromosome}
	 */
	public boolean[] getSelectedChromosomes() {
		return chromoSelectionPanel.getSelectedChromosomes();
	}


	/**
	 * @return the strand shifting value
	 */
	public int getStrandShiftValue () {
		return strandSelectionPanel.getShiftValue();
	}


	/**
	 * @return the Strand to extract. Null if both
	 */
	public Strand getStrandToExtract() {
		return strandSelectionPanel.getStrandToExtract();
	}
	
	
	/**
	 * @return the name of the track
	 */
	public String getTrackName() {
		return trackNamePanel.getTrackName();
	}
	
	
	/**
	 * Shows the component
	 * @param parent parent component. Can be null
	 * @return OK or CANCEL
	 */
	public int showDialog(Component parent) {
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
