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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.newCurveLayerDialog;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;



/**
 * Panel for the score calculation method of a {@link NewCurveLayerDialog} 
 * @author Julien Lajugie
 * @version 0.1
 */
class CalculMethodPanel extends JPanel {

	private static final long serialVersionUID = -2863825210102188370L;	// generated ID
	private static final int 				PANEL_WIDTH = 150;	// width of the panel
	private final JComboBox 				jcbCalculMetod; 	// combo box for the score calculation method
	private static ScoreCalculationMethod 	defaultMethod = 
		ScoreCalculationMethod.SUM;								// default method of calculation
	
	
	/**
	 * Creates an instance of a {@link CalculMethodPanel}
	 */
	CalculMethodPanel() {
		super();
		jcbCalculMetod = new JComboBox(ScoreCalculationMethod.values());
		jcbCalculMetod.setSelectedItem(defaultMethod);
		add(jcbCalculMetod);
		setBorder(BorderFactory.createTitledBorder("Score Calculation"));
		setPreferredSize(new Dimension(PANEL_WIDTH, getPreferredSize().height));
	}
	
	
	/**
	 * @return the selected score calculation method
	 */
	ScoreCalculationMethod getScoreCalculationMethod() {
		return (ScoreCalculationMethod) jcbCalculMetod.getSelectedItem();
	}
	
	
	/**
	 * Saves the selected method of calculation as default
	 */
	void saveDefault() {
		defaultMethod = getScoreCalculationMethod();
	}
}
