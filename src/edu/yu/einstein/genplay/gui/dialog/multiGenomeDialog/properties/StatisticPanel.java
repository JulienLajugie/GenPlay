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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.comparator.StringComparator;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFileStatistic;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFSampleStatistic;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class StatisticPanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4602586057160516008L;


	/**
	 * Constructor of {@link StatisticPanel}
	 * @param vcfFile the reader of the VCF file
	 */
	protected StatisticPanel (VCFFile vcfFile) {
		final VCFFileStatistic statistic = vcfFile.getStatistics();
		final Map<String, VCFSampleStatistic> genomeStatistics = statistic.getGenomeStatistics();


		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;


		// Create copy buttons
		JButton jbFileTableCopy = new JButton("Copy this table");
		JButton jbAllTablesFileCopy = new JButton("Copy all tables");
		jbFileTableCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String stat = statistic.getString();
				copyToClipboard(stat);
			}
		});
		jbAllTablesFileCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String stat = statistic.getFullString();
				copyToClipboard(stat);
			}
		});

		// Add the file table title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = PropertiesDialog.FIRST_TITLE_INSET;
		add(Utils.getTitleLabel("File"), gbc);

		// Add the file table
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		gbc.weightx = 1;
		add(Utils.getTablePanel(statistic.getColumnNamesForData(), statistic.getData()), gbc);

		// Add the copy buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		buttonPanel.add(jbFileTableCopy);
		buttonPanel.add(jbAllTablesFileCopy);
		gbc.gridy++;
		add(buttonPanel, gbc);
		/*gbc.gridy++;
		add(jbAllTablesFileCopy, gbc);
		gbc.gridx++;
		add(jbFileTableCopy, gbc);
		gbc.gridx = 0;*/


		List<String> genomeNames = new ArrayList<String>(genomeStatistics.keySet());
		Collections.sort(genomeNames, new StringComparator());
		int genomeNumber = genomeStatistics.size();
		int cpt = 0;
		for (final String genomeName: genomeNames) {
			cpt++;

			JButton jbCopy = new JButton("Copy this table");
			jbCopy.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String stat = "\nGenome Statistics \"" + genomeName + "\":\n";
					stat += genomeStatistics.get(genomeName).getString();
					copyToClipboard(stat);
				}
			});

			// Add the genome table title
			gbc.gridy++;
			gbc.insets = PropertiesDialog.TITLE_INSET;
			add(Utils.getTitleLabel(genomeName), gbc);

			// Add the genome table
			gbc.gridy++;
			gbc.insets = PropertiesDialog.PANEL_INSET;
			if (cpt == genomeNumber) {
				gbc.weighty = 1;
			}
			add(Utils.getTablePanel(genomeStatistics.get(genomeName).getColumnNamesForData(), genomeStatistics.get(genomeName).getData()), gbc);

			// Add the copy button
			gbc.gridy++;
			add(jbCopy, gbc);
		}
	}


	private void copyToClipboard (String info) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(info), null);
	}

}
