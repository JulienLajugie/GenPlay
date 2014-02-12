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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.util.Images;


/**
 * Components to choose the precision of the scores of the project
 * @author Julien Lajugie
 */
class ScorePrecisionComponents {

	/** Default project score precision */
	private static final ScorePrecision DEFAULT_PRECISION = ScorePrecision.PRECISION_32BIT;

	private final JComboBox 	jcbScorePrecision;	// combobox to choose the score precision
	private final JLabel 		jlHelp;				// label help
	private final JLabel 		jlScorePrecision;	// label score precision


	/**
	 * Creates an instance of {@link ScorePrecisionComponents}
	 */
	ScorePrecisionComponents() {
		jlScorePrecision = new JLabel("Precision:");
		jcbScorePrecision = new JComboBox(ScorePrecision.values());
		jcbScorePrecision.setSelectedItem(DEFAULT_PRECISION);

		// tooltip
		jlHelp = new JLabel(new ImageIcon(Images.getHelpImage()));
		jlHelp.setToolTipText("<html>High-precision scores are more precised but take up more memory.<br>" +
				"Low-precision scores are less precised but more memory efficient.<br>" +
				"The maximum low-precision score is 65,504.</html>");
	}


	/**
	 * @return  jcbScorePrecision
	 */
	JComboBox getJcbScorePrecision() {
		return jcbScorePrecision;
	}


	/**
	 * @return  jlHelp
	 */
	JLabel getJlHelp() {
		return jlHelp;
	}


	/**
	 * @return  jlScorePrecision
	 */
	JLabel getJlScorePrecision() {
		return jlScorePrecision;
	}


	/**
	 * @return the score precision selected
	 */
	public ScorePrecision getProjectScorePrecision() {
		return (ScorePrecision) jcbScorePrecision.getSelectedItem();
	}
}
