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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.statistics;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFStatistics.VCFFileStatistics;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGStatisticsDialog extends JDialog {

	private static final long serialVersionUID = -4932470485711131874L;
	private final StatisticPanel statisticPanel;


	/**
	 * Constructor of {@link MGStatisticsDialog}
	 * @param statistics statistics to show
	 */
	public MGStatisticsDialog (VCFFileStatistics statistics) {
		statisticPanel = new StatisticPanel(statistics);
		JScrollPane scrollPane = new JScrollPane(statisticPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 10, 15);
		add(scrollPane, gbc);

		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle("Multi Genome Statistics");
		pack();
	}


	/**
	 * Show the dialog
	 * @param parent
	 */
	public void show (Component parent) {
		setLocationRelativeTo(parent);
		setVisible(true);
	}

}
