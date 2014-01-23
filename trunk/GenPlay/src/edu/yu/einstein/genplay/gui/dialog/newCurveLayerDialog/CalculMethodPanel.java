/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.util.Images;


/**
 * Panel for the score calculation method of a {@link NewCurveLayerDialog}
 * @author Julien Lajugie
 */
class CalculMethodPanel extends JPanel {

	private static final long 		serialVersionUID = -2863825210102188370L;	// generated ID
	private static final int 		PANEL_WIDTH = 150;							// width of the panel
	private static ScoreOperation 	defaultMethod = ScoreOperation.ADDITION;	// default operation to compute the scores
	private final JComboBox 		jcbCalculMetod; 							// combo box for the score calculation method
	private final JLabel 			jlCalculMetodHelp;							// label calcul method help


	/**
	 * Creates an instance of a {@link CalculMethodPanel}
	 */
	CalculMethodPanel() {
		super();
		// combo box
		jcbCalculMetod = new JComboBox(ScoreOperation.getPileupOperations());
		jcbCalculMetod.setSelectedItem(defaultMethod);

		// tooltip
		jlCalculMetodHelp = new JLabel(new ImageIcon(Images.getHelpImage()));
		jlCalculMetodHelp.setToolTipText("<html>The selected operation will be used to compute the score of regions where windows overlap.<br/>" +
				"The score of the region can be the sum, the average, the maximum or the minimum of the overlapping windows.</html>");

		add(jcbCalculMetod, BorderLayout.LINE_START);
		add(jlCalculMetodHelp, BorderLayout.LINE_END);
		setBorder(BorderFactory.createTitledBorder("Score Calculation"));
		setPreferredSize(new Dimension(PANEL_WIDTH, getPreferredSize().height));
	}


	/**
	 * @return the selected score calculation method
	 */
	ScoreOperation getScoreCalculationMethod() {
		return (ScoreOperation) jcbCalculMetod.getSelectedItem();
	}


	/**
	 * Saves the selected method of calculation as default
	 */
	void saveDefault() {
		defaultMethod = getScoreCalculationMethod();
	}
}
