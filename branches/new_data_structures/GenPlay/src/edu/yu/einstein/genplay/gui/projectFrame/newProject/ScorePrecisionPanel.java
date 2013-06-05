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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Images;


/**
 * Panel to choose the precision of the scores of the project
 * @author Julien Lajugie
 */
class ScorePrecisionPanel extends JPanel {

	/** Generated serial ID */
	private static final long serialVersionUID = -1173788286992028912L;

	/** Default project score precision */
	private static final ScorePrecision DEFAULT_PRECISION = ScorePrecision.PRECISION_32BIT;

	private static final int COMBO_WIDTH = 200;	// Combo box width value
	private static final int COMBO_HEIGTH = 20;	// Combo box height value

	private final JComboBox 	jcbScorePrecision;	// combobox to choose the score precision


	/**
	 * Creates an instance of {@link ScorePrecisionPanel}
	 */
	ScorePrecisionPanel() {
		setSize(ProjectFrame.PRECISION_DIM);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());

		setBackground(ProjectFrame.PRECISION_COLOR);
		JLabel jlScorePrecision = new JLabel("      Precision:        ");
		jcbScorePrecision = new JComboBox(ScorePrecision.values());
		jcbScorePrecision.setPreferredSize(new Dimension(COMBO_WIDTH, COMBO_HEIGTH));
		jcbScorePrecision.setBackground(ProjectFrame.PRECISION_COLOR);
		jcbScorePrecision.setSelectedItem(DEFAULT_PRECISION);

		// tooltip
		JLabel jlHelp = new JLabel(new ImageIcon(Images.getHelpImage()));
		jlHelp.setToolTipText("<html>High-precision scores are more precised but take up more memory.<br>" +
				"Low-precision scores are less precised but more memory efficient.<br>" +
				"The maximum low-precision score is 65,504.</html>");

		//Layout
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlScorePrecision, gbc);

		gbc.gridx++;
		add(jcbScorePrecision, gbc);

		gbc.gridx++;
		gbc.insets = new Insets(0, 5, 0, 0);
		add(jlHelp, gbc);
	}


	/**
	 * @return the score precision selected
	 */
	public ScorePrecision getProjectScorePrecision() {
		return (ScorePrecision) jcbScorePrecision.getSelectedItem();
	}
}
