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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;

import edu.yu.einstein.genplay.gui.dialog.exceptionDialog.WarningReportDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * Shows the warning report
 * @author Julien Lajugie
 */
public final class PAShowWarningReport extends AbstractAction {

	private static final long serialVersionUID = -2922039476574345222L;			// generated ID
	private static final String 	ACTION_NAME = "Warning Report"; 			// action name
	private static final String 	DESCRIPTION = "Show the warning report"; 	// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PAShowWarningReport.class.getName();


	/**
	 * Creates an instance of {@link PAShowWarningReport}
	 */
	public PAShowWarningReport() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		WarningReportDialog.getInstance().showDialog(MainFrame.getInstance().getRootPane());
		WarningReportDialog.getInstance().setVisible(true);
	}
}
