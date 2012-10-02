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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class LegendPane extends JPanel {

	/** Generated default serial version ID */
	private static final long serialVersionUID = -5372729762581187391L;


	/**
	 * Constructor of {@link LegendPane}
	 * @param panel a panel {@link ExportVCFPane} or
	 */
	protected LegendPane (Map<String, List<VariantType>> variationMap, List<VCFFile> fileList) {
		// Create the field set effect
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Stripes content");
		setBorder(titledBorder);

		// Create the genomes information
		JLabel genomesTitle = new JLabel("Genomes:");
		List<String> genomesInformation = getGenomesInformation(variationMap);

		// Create the files information
		JLabel filesTitle = new JLabel("Files:");
		List<String> filesInformation = getFilesInformation(fileList);

		// Create the inset
		Insets titleInset = new Insets(2, 6, 2, 0);
		Insets valueInset = new Insets(0, 15, 0, 10);
		Insets lastValueInset = new Insets(0, 15, 10, 10);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Genomes title
		gbc.insets = titleInset;
		add(genomesTitle, gbc);

		// Genomes values
		gbc.insets = valueInset;
		for (int i = 0; i < genomesInformation.size(); i++) {
			if (i == (genomesInformation.size() - 1)) {
				gbc.insets = lastValueInset;
			}
			gbc.gridy++;
			add(new JLabel(genomesInformation.get(i)), gbc);
		}

		// Files title
		gbc.insets = titleInset;
		gbc.gridy++;
		add(filesTitle, gbc);

		// Files values
		gbc.insets = valueInset;
		for (int i = 0; i < filesInformation.size(); i++) {
			if (i == (filesInformation.size() - 1)) {
				gbc.weighty = 1;
				gbc.insets = lastValueInset;
			}
			gbc.gridy++;
			add(new JLabel(filesInformation.get(i)), gbc);
		}
	}


	/**
	 * @param variationMap the variation map with genome names
	 * @return a description of the requested genomes and their variations
	 */
	private List<String> getGenomesInformation (Map<String, List<VariantType>> variationMap) {
		List<String> genomesInformation = new ArrayList<String>();
		for (String genomeName: variationMap.keySet()) {
			String current = genomeName + ": ";
			for (int j = 0; j < variationMap.get(genomeName).size(); j++) {
				current += variationMap.get(genomeName).get(j);
				if (j < (variationMap.get(genomeName).size() - 1)) {
					current += ", ";
				}
			}
			genomesInformation.add(current);
		}
		return genomesInformation;
	}


	/**
	 * @param fileList a list of files
	 * @return a description of the requested files
	 */
	private List<String> getFilesInformation (List<VCFFile> fileList) {
		List<String> filesInformation = new ArrayList<String>();
		for (VCFFile currentFile: fileList) {
			filesInformation.add(currentFile.getFile().getName());
		}
		return filesInformation;
	}
}
