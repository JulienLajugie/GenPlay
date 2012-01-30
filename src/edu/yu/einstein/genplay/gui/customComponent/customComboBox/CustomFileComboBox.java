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
package edu.yu.einstein.genplay.gui.customComponent.customComboBox;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxEvent;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class extends the {@link CustomComboBox} class.
 * It is specific for file content.
 * Developer can specify filters.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class CustomFileComboBox extends CustomComboBox<File> {

	/**
	 * Generated default serial version
	 */
	private static final long serialVersionUID = -872347430907803557L;

	private JFileChooser fc;
	private ExtendedFileFilter[] filters;			// filters for adding files to the combo box


	/**
	 * Constructor of {@link CustomFileComboBox}
	 */
	public CustomFileComboBox () {
		//Create a file chooser
		fc = new JFileChooser();
		fc.setCurrentDirectory(new File(ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogTitle("Select a file");
		filters = null;
	}


	/**
	 * Constructor of {@link CustomFileComboBox}
	 * @param filters filters for file
	 */
	public CustomFileComboBox (ExtendedFileFilter[] filters) {
		this.filters = filters;
	}


	/**
	 * @return the filters
	 */
	public ExtendedFileFilter[] getFilters() {
		return filters;
	}


	/**
	 * @param filters the filters to set
	 */
	public void setFilters(ExtendedFileFilter[] filters) {
		this.filters = filters;
		for (FileFilter filter: filters) {
			fc.addChoosableFileFilter(filter);
		}
	}


	@Override
	public void customComboBoxChanged(CustomComboBoxEvent evt) {
		if (evt.getAction() == CustomComboBoxEvent.SELECT_ACTION) {
			setSelectedItem(evt.getElement());
		} else if (evt.getAction() == CustomComboBoxEvent.ADD_ACTION) {
			addAction();
		} else if (evt.getAction() == CustomComboBoxEvent.REMOVE_ACTION) {
			removeAction((File)evt.getElement());
		} else if (evt.getAction() == CustomComboBoxEvent.REPLACE_ACTION) {
			replaceAction((File)evt.getElement());
		}
	}


	/**
	 * Adds a new element to the combo box.
	 * Shows a popup in order to define the new entry.
	 */
	@Override
	protected void addAction () {
		int returnVal = fc.showOpenDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getPath();
			File element = new File(path); //Utils.chooseFileToLoad(getRootPane(), "Select a file", ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory(), filters);
			if (element != null) {
				addElement(element);
				resetCombo();
				setSelectedItem(element);
			}
		}

	}


	/**
	 * Removes an element from the combo box.
	 * Shows a popup in order to confirm the action.
	 * @param element element to remove
	 */
	@Override
	protected void removeAction (File element) {
		Object[] options = {"Yes", "No"};
		int n = JOptionPane.showOptionDialog(this,
				"Do you really want to erase '" + element + "' ?",
				"Entry deletion",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null,
				options,
				options[0]);

		if (n == JOptionPane.YES_OPTION) {
			elements.remove(element);
			resetCombo();
			setSelectedIndex(0);
		}
	}


	/**
	 * Replaces an existing element by another one to the combo box.
	 * Shows a popup in order to define the new entry.
	 * @param element the element to replace
	 */
	@Override
	protected void replaceAction (File element) {
		File newElement = Utils.chooseFileToLoad(getRootPane(), "Select a file", ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory(), filters);

		if (newElement != null) {
			elements.remove(element);
			addElement(newElement);
			resetCombo();
			setSelectedItem(element);
		}
	}

}
