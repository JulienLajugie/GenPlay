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

import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.gui.action.actionWaiting.ActionWaiter;
import edu.yu.einstein.genplay.gui.action.actionWaiting.SwingWorkerActionWaiting;
import edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction.MGAVCFToTBI;
import edu.yu.einstein.genplay.gui.customComponent.customComboBox.customComboBoxEvent.CustomComboBoxListener;
import edu.yu.einstein.genplay.util.Utils;

/**
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class CustomFileComboBoxMG extends CustomFileComboBox implements ActionWaiter {

	/**
	 * Generated default serial version
	 */
	private static final long serialVersionUID = -6704320621396590562L;

	private MGAVCFToTBI action;
	private final Object owner;


	/**
	 * Constructor of {@link CustomFileComboBoxMG}
	 */
	public CustomFileComboBoxMG () {
		super();
		owner = null;
	}


	/**
	 * Constructor of {@link CustomFileComboBoxMG}
	 * @param owner the owner object of the box
	 */
	public CustomFileComboBoxMG (Object owner) {
		super();
		this.owner = owner;
	}


	@Override
	protected File actionPostSelection (File file) {
		File result = file;
		if (file != null) {
			String ext = Utils.getExtension(file);
			if (ext.equals("vcf")) {
				result = null;
				if (showMessage()) {
					action = new MGAVCFToTBI(file);
					action.setLoadingPassBy(true);
					SwingWorkerActionWaiting swAction = new SwingWorkerActionWaiting(this, action);
					swAction.execute();
				}
			}
		}

		return result;
	}


	private boolean showMessage () {
		Object[] options = {"Yes", "No"};

		String title = "Do you want GenPlay to compress and index your file?";
		String message = "The selected file is a VCF file (.vcf).\n" +
				"GenPlay Multi Genome works with BGZIP compressed VCF files (.vcf.gz) and their Tabix indexed files (.tbi).\n" +
				"GenPlay can create those two files at the same location as the selected file.";

		int n = JOptionPane.showOptionDialog(null,
				message,
				title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[0]);

		if (n == JOptionPane.YES_OPTION) {
			return true;
		}
		return false;
	}


	@Override
	public void doAtTheEnd() {
		addElementToCombo(action.getBgzipFile());
		if (owner != null) {
			lastEvent.setElement(action.getBgzipFile());
			if (owner instanceof CustomComboBoxListener) {
				((CustomComboBoxListener) owner).customComboBoxChanged(lastEvent);
			}
		}
	}

}
