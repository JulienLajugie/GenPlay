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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.security.InvalidParameterException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;

/**
 * Panel to select the file to filter
 * @author Julien Lajugie
 */
public class FilePanel extends JPanel implements ItemListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;

	/** File change property name */
	public static final String FILE_CHANGE_PROPERTY_NAME = "VCF File Change";

	/** Combo box to select the file */
	private final JComboBox	jcbFileSelection;

	/** Selected VCF file */
	private VCFFile selectedFile;


	/**
	 * Constructor of {@link FilePanel}
	 */
	public FilePanel(List<VCFFile> fileList, VCFFile selectedFile) {
		super(new GridBagLayout());

		if ((fileList == null) || fileList.isEmpty()) {
			throw new InvalidParameterException("The list of VCF cannot be empty or null");
		}
		if (fileList.size() > 1) {
			jcbFileSelection = new JComboBox(fileList.toArray(new VCFFile[0]));
			jcbFileSelection.addItemListener(this);
			setBorder(BorderFactory.createTitledBorder("Select VCF to Filter"));

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.LINE_START;
			add(jcbFileSelection, gbc);
		} else { // no file to choose if there is only one so we hide this panel
			jcbFileSelection = null;
			setVisible(false);
		}
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		VCFFile oldValue = selectedFile;
		selectedFile = (VCFFile) jcbFileSelection.getSelectedItem();
		if (oldValue != selectedFile) {
			firePropertyChange(FILE_CHANGE_PROPERTY_NAME, oldValue, selectedFile);
		}
	}
}
