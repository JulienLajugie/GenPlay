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

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FileInformationPanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4602586057160516008L;


	/**
	 * Constructor of {@link FileInformationPanel}
	 * @param reader the reader of the VCF file
	 */
	protected FileInformationPanel (VCFReader reader) {

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;


		// General information title
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = PropertiesDialog.FIRST_TITLE_INSET;
		add(Utils.getTitleLabel("General information"), gbc);

		// General information values
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getGeneralInformationPanel(reader), gbc);

		// Meta data header title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("Meta data"), gbc);

		// Meta data header values
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getMetaDataPanel(reader.getHeaderInfo()), gbc);

		// ALT header title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("ALT fields"), gbc);

		// ALT header values
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getVCFHeaderTypePanel(reader.getAltHeader()), gbc);

		// FILTER header title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("FILTER fields"), gbc);

		// FILTER header values
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getVCFHeaderTypePanel(reader.getFilterHeader()), gbc);

		// INFO header title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("FILTER fields"), gbc);

		// INFO header values
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		add(getVCFHeaderAdvancedTypePanel(reader.getInfoHeader()), gbc);

		// FORMAT header title
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.TITLE_INSET;
		add(Utils.getTitleLabel("FORMAT fields"), gbc);

		// FORMAT header values
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.insets = PropertiesDialog.PANEL_INSET;
		gbc.weighty = 1;
		add(getVCFHeaderAdvancedTypePanel(reader.getFormatHeader()), gbc);

	}


	/**
	 * Creates the panel that contains general information about the file
	 * @param reader the vcf file reader
	 * @return the panel
	 */
	private JPanel getGeneralInformationPanel (VCFReader reader) {
		// The main panel
		JPanel panel = new JPanel();

		// Key labels
		JLabel jlKeyFileName = new JLabel("File name: ");
		JLabel jlKeyFolder = new JLabel("Folder: ");
		JLabel jlKeyType = new JLabel("VCF file type: ");

		// Value labels
		JLabel jlValFileName = new JLabel(reader.getFile().getName());
		JLabel jlValFolder = new JLabel(reader.getFile().getParent());
		JLabel jlValType = new JLabel(reader.getVcfType().toString());

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		Insets keyInset = new Insets(0, 0, 0, 0);
		Insets valueInset = new Insets(0, 10, 0, 0);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;

		// jlKeyFileName
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = keyInset;
		panel.add(jlKeyFileName, gbc);

		// jlValFileName
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = valueInset;
		panel.add(jlValFileName, gbc);

		// jlKeyFolder
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = keyInset;
		panel.add(jlKeyFolder, gbc);

		// jlValFolder
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = valueInset;
		panel.add(jlValFolder, gbc);

		// jlKeyType
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.insets = keyInset;
		panel.add(jlKeyType, gbc);

		// jlValType
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.insets = valueInset;
		panel.add(jlValType, gbc);

		return panel;
	}



	/**
	 * Creates a panel that contains a table with meta data.
	 * It defines first:
	 * - column names
	 * - data from header values
	 * Then, it uses the another method in order to create the table into a panel and return it.
	 * [This method is specific for the meta data header.]
	 * 
	 * @param map	meta data list
	 * @return		a panel that contains a table using meta data information
	 */
	private JPanel getMetaDataPanel (Map<String, String> map) {
		if (map.size() > 0) {

			// Column names
			String[] columnNames = {"Key", "Description"};

			// Initializes data
			Object[][] data = new Object[map.size()][columnNames.length];

			// Fills data
			int rowIndex = 0;
			for (String key: map.keySet()) {
				data[rowIndex][0] = key;
				data[rowIndex][1] = map.get(key);
				rowIndex++;
			}

			// Returns the panel that contains the table
			return Utils.getTablePanel(columnNames, data);
		} else {
			return Utils.getNoInformationPanel();
		}
	}


	/**
	 * Creates a panel that contains a table from header type.
	 * It defines first:
	 * - column names
	 * - data from header values
	 * Then, it uses the another method in order to create the table into a panel and return it.
	 * [This method is specific for simple header type.]
	 * 
	 * @param list	list of header
	 * @return		a panel that contains a table using headers information
	 */
	private JPanel getVCFHeaderTypePanel (List<VCFHeaderType> list) {
		if (list.size() > 0) {

			// Column names
			String[] columnNames = {"ID", "Description"};

			// Initializes data
			Object[][] data = new Object[list.size()][columnNames.length];

			// Fills data
			int rowIndex = 0;
			for (VCFHeaderType header: list) {
				data[rowIndex][0] = header.getId();
				data[rowIndex][1] = header.getDescription();
				rowIndex++;
			}

			// Returns the panel that contains the table
			return Utils.getTablePanel(columnNames, data);
		} else {
			return Utils.getNoInformationPanel();
		}
	}


	/**
	 * Creates a panel that contains a table from header type.
	 * It defines first:
	 * - column names
	 * - data from header values
	 * Then, it uses the another method in order to create the table into a panel and return it.
	 * [This method is specific for advanced header type.]
	 * 
	 * @param list	list of header
	 * @return		a panel that contains a table using headers information
	 */
	private JPanel getVCFHeaderAdvancedTypePanel (List<VCFHeaderAdvancedType> list) {
		if (list.size() > 0) {
			// Column names
			String[] columnNames = {"ID", "Number", "Type", "Description"};

			// Initializes data
			Object[][] data = new Object[list.size()][columnNames.length];

			// Fills data
			int rowIndex = 0;
			for (VCFHeaderAdvancedType header: list) {
				data[rowIndex][0] = header.getId();
				data[rowIndex][1] = header.getNumber();
				Class<?> type = header.getType();
				String typeString = "";
				if (type.isInstance(new Integer(0))) {
					typeString = "Integer";
				} else if (type.isInstance(new Float(0))) {
					typeString = "Float";
				} else if (type.isInstance(new Boolean(true))) {
					typeString = "Flag";
				} else if (type.isInstance(new Character('0'))) {
					typeString = "Character";
				} else if (type.isInstance(new String())) {
					typeString = "String";
				}
				data[rowIndex][2] = typeString;
				data[rowIndex][3] = header.getDescription();
				rowIndex++;
			}

			// Returns the panel that contains the table
			return Utils.getTablePanel(columnNames, data);
		} else {
			return Utils.getNoInformationPanel();
		}
	}

}
