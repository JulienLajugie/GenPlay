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
package edu.yu.einstein.genplay.gui.menu;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.project.PAAbout;
import edu.yu.einstein.genplay.gui.action.project.PACheckForUpdates;
import edu.yu.einstein.genplay.gui.action.project.PAExit;
import edu.yu.einstein.genplay.gui.action.project.PAFullScreen;
import edu.yu.einstein.genplay.gui.action.project.PAHelp;
import edu.yu.einstein.genplay.gui.action.project.PALoadProject;
import edu.yu.einstein.genplay.gui.action.project.PANewProject;
import edu.yu.einstein.genplay.gui.action.project.PAOption;
import edu.yu.einstein.genplay.gui.action.project.PARNAPosToDNAPos;
import edu.yu.einstein.genplay.gui.action.project.PASaveProject;
import edu.yu.einstein.genplay.gui.action.project.PASaveProjectAs;
import edu.yu.einstein.genplay.gui.action.project.PAShowErrorReport;
import edu.yu.einstein.genplay.gui.action.project.PAShowWarningReport;
import edu.yu.einstein.genplay.gui.action.project.PASortFile;


/**
 * Main menu of the application
 * @author Julien Lajugie
 */
public final class MainMenu extends JPopupMenu {

	private static final long serialVersionUID = -8543113416095307670L; // generated ID

	private final JMenuItem jmiNewProject;		// menu new project
	private final JMenuItem jmiSaveProject;		// menu save project
	private final JMenuItem jmiSaveProjectAs;	// menu save project
	private final JMenuItem jmiLoadProject;		// menu load project
	private final JMenuItem jmiFullScreen;		// full screen
	private final JMenuItem jmiWarningReport;	// warning report
	private final JMenuItem jmiErrorReport;		// exception report
	private final JMenuItem jmiOption;			// option
	private final JMenuItem jmiSortFile;		// sort file
	private final JMenuItem jmiRNAToDNAPos;		// option
	private final JMenuItem jmiUpdate;			// update
	private final JMenuItem jmiHelp;			// help
	private final JMenuItem jmiAbout;			// about
	private final JMenuItem jmiExit;			// exit


	/**
	 * Creates an instance of {@link MainMenu}
	 * @param actionMap {@link ActionMap} containing the {@link Action} of this menu
	 */
	public MainMenu(ActionMap actionMap) {
		super("Main Menu");
		jmiNewProject = new JMenuItem(actionMap.get(PANewProject.ACTION_KEY));
		jmiLoadProject = new JMenuItem(actionMap.get(PALoadProject.ACTION_KEY));
		jmiSaveProject = new JMenuItem(actionMap.get(PASaveProject.ACTION_KEY));
		jmiSaveProjectAs = new JMenuItem(actionMap.get(PASaveProjectAs.ACTION_KEY));
		jmiFullScreen = new JMenuItem(actionMap.get(PAFullScreen.ACTION_KEY));
		jmiWarningReport = new JMenuItem(actionMap.get(PAShowWarningReport.ACTION_KEY));
		jmiErrorReport = new JMenuItem(actionMap.get(PAShowErrorReport.ACTION_KEY));
		jmiOption = new JMenuItem(actionMap.get(PAOption.ACTION_KEY));
		jmiRNAToDNAPos = new JMenuItem(actionMap.get(PARNAPosToDNAPos.ACTION_KEY));
		jmiSortFile = new JMenuItem(actionMap.get(PASortFile.ACTION_KEY));
		jmiUpdate = new JMenuItem(actionMap.get(PACheckForUpdates.ACTION_KEY));
		jmiHelp = new JMenuItem(actionMap.get(PAHelp.ACTION_KEY));
		jmiAbout = new JMenuItem(actionMap.get(PAAbout.ACTION_KEY));
		jmiExit = new JMenuItem(actionMap.get(PAExit.ACTION_KEY));

		add(jmiNewProject);
		add(jmiLoadProject);
		add(jmiSaveProject);
		add(jmiSaveProjectAs);
		addSeparator();
		add(jmiFullScreen);
		add(jmiWarningReport);
		add(jmiErrorReport);
		addSeparator();
		add(jmiSortFile);
		add(jmiRNAToDNAPos);
		addSeparator();
		if (PACheckForUpdates.isUpdaterAvailable()) {
			add(jmiUpdate);
		}
		add(jmiOption);
		add(jmiHelp);
		add(jmiAbout);
		addSeparator();
		add(jmiExit);

		addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				jmiSaveProject.setEnabled(ProjectManager.getInstance().getProjectDirectory() != null);
			}
		});
	}
}
