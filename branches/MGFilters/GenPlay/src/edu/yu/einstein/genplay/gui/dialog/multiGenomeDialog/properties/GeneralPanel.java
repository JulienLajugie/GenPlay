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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader.VCFData;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class GeneralPanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 538823729476349982L;

	
	/**
	 * Constructor of {@link GeneralPanel}
	 */
	protected GeneralPanel () {
		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
		
		// Global information header
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = PropertiesDialog.FIRST_TITLE_INSET;
		add(Utils.getTitleLabel("Global project information"), gbc);
		
		// Global information 
		gbc.gridy = 1;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getGlobalInformationPanel(), gbc);
		
		// VCF mapping information header
		gbc.gridy = 2;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("VCF files mapping information"), gbc);
		
		// VCF mapping information
		gbc.gridy = 3;
		gbc.weighty = 1;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getVCFMappingPanel(), gbc);
	}
	
	
	/**
	 * Creates the panel that contains all global information about the project
	 * @return the panel
	 */
	private JPanel getGlobalInformationPanel () {
		// The main panel
		JPanel panel = new JPanel();
		
		// Key labels
		JLabel jlKeyProjectName = new JLabel("Project name: ");
		JLabel jlKeyAssembly = new JLabel("Assembly: ");
		
		// Value labels
		JLabel jlValProjectName = new JLabel(ProjectManager.getInstance().getProjectName());
		JLabel jlValAssembly = new JLabel(ProjectManager.getInstance().getAssembly().getDisplayName());
		
		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		Insets keyInset = new Insets(0, 0, 0, 0);
		Insets valueInset = new Insets(0, 10, 0, 0);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		
		// jlKeyProjectName
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = keyInset;
		panel.add(jlKeyProjectName, gbc);
		
		// jlValProjectName
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = valueInset;
		panel.add(jlValProjectName, gbc);
		
		// jlKeyAssembly
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = keyInset;
		panel.add(jlKeyAssembly, gbc);
		
		// jlValAssembly
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = valueInset;
		panel.add(jlValAssembly, gbc);
		
		return panel;
	}
	
	
	/**
	 * Creates the panel that contains information about VCF files and how user decided to load them
	 * @return the panel
	 */
	private JPanel getVCFMappingPanel () {
		// Association between full genome names and their readers
		Map<String, List<VCFReader>> map = ProjectManager.getInstance().getMultiGenomeProject().getGenomeFileAssociation();
		
		// Column names
		String[] columnNames = {VCFData.GROUP_NAME,
				VCFData.NICKNAME,
				VCFData.RAW_NAME,
				VCFData.FILE_NAME};
		
		// Counts the number of row
		int rowNumber = 0;
		for (List<VCFReader> readers: map.values()) {
			rowNumber += readers.size();
		}
		
		// Initializes data
		Object[][] data = new Object[rowNumber][columnNames.length];
		
		// Fills data
		int rowIndex = 0;
		for (String fullGenomeName: map.keySet()) {
			for (VCFReader reader: map.get(fullGenomeName)) {
				data[rowIndex][0] = FormattedMultiGenomeName.getGroupName(fullGenomeName);
				data[rowIndex][1] = FormattedMultiGenomeName.getUsualName(fullGenomeName);
				data[rowIndex][2] = FormattedMultiGenomeName.getRawName(fullGenomeName);
				data[rowIndex][3] = reader.getFile().getName();
				rowIndex++;
			}
		}
		
		return Utils.getTablePanel(columnNames, data);
	}
	
}
