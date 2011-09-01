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
package edu.yu.einstein.genplay.gui.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.SwingUtilities;

import edu.yu.einstein.genplay.core.genome.Assembly;
import edu.yu.einstein.genplay.core.genome.Clade;
import edu.yu.einstein.genplay.core.genome.Genome;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.ProjectManager;
import edu.yu.einstein.genplay.core.manager.ProjectRecordingManager;
import edu.yu.einstein.genplay.core.manager.ZoomManager;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;
import edu.yu.einstein.genplay.gui.action.project.PALoadProject;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;


/**
 * This class loads GenPlay with a welcome screen.
 * Then, the main frame is loaded.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class Launcher {

	/**
	 * This constant can be set to a project file path in the resource folder of the jar.
	 * In this case this project will be directly loaded when GenPlay starts.
	 */
	private static final String DEMO_PROJECT_PATH = null; 


	/**
	 * Starts the application
	 * @param args a project file path can be specified. In this case the project  
	 * screen will be skipped and the project file will be directly loaded
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				// if the DEMO_PROJECT_PATH constant has been set it means that we're starting a demo project
				boolean isDemo = (DEMO_PROJECT_PATH != null);
				if (isDemo) {
					startDemoProject();
				} else if (args.length == 1) { // if a project file path has been specified to the main method we load this file
					File file = new File(args[0]);
					startProjectFromFile(file);
				} else { // normal execution of the software
					startProjectFrame();
				}
			}
		});
	}


	/**
	 * This method starts a demo project.  The project file needs to be in the resource folder and 
	 * the path to this file must be specified in the DEMO_PROJECT_PATH constant
	 */
	private static void startDemoProject() {
		InputStream is = MainFrame.getInstance().getClass().getClassLoader().getResourceAsStream(DEMO_PROJECT_PATH);
		try {
			ProjectRecordingManager.getInstance().initManagers(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MainFrame.getInstance().setVisible(true);
		PALoadProject load = new PALoadProject();
		load.setSkipFileSelection(true);
		load.actionPerformed(null);
	}


	/**
	 * This method starts a project from a file.
	 * @param file project file to load
	 */
	public static void startProjectFromFile(File file) {
		try {
			ProjectRecordingManager.getInstance().initManagers(file);
			MainFrame.getInstance().setVisible(true);
			PALoadProject load = new PALoadProject();
			load.setSkipFileSelection(true);
			load.actionPerformed(null);
		} catch (Exception e) {
			System.out.println("Invalid Project File: The specifed file is not a valid project file");
		}
	}


	/**
	 * Displays the project screen manager which is the first screen of GenPlay.
	 */
	private static void startProjectFrame() {
		//Welcome screen initialization
		ProjectFrame projectFrame = ProjectFrame.getInstance();
		ConfigurationManager.getInstance();
		ProjectManager.getInstance();
		ZoomManager.getInstance();
		// load the managers from the configuration files
		loadManagers();
		//Create a new thread to display the welcome screen
		projectFrame.initScreen();
	}


	/**
	 * This method is used by the ProjectFrame after closing 
	 * to initiate a new project.
	 */
	public static void initiateNewProject() {
		ProjectFrame projectFrame = ProjectFrame.getInstance();
		Clade clade = projectFrame.getSelectedClade();
		Genome genome = projectFrame.getSelectedGenome();
		Assembly assembly = projectFrame.getSelectedAssembly();
		assembly.setChromosomeList(projectFrame.getSelectedChromosomes());
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.setProjectName(projectFrame.getProjectName());
		projectManager.setCladeName(clade.getName());
		projectManager.setGenomeName(genome.getName());
		projectManager.setAssembly(assembly);
		projectFrame.setVisible(false);
		// generate the multi-genome manager if the user starts a multi-genome project
		if (!projectFrame.isSimpleProject()) {
			MultiGenomeManager multiGenomeManager = null;
			multiGenomeManager = MultiGenomeManager.getInstance();
			multiGenomeManager.init(projectFrame.getFileReadersAssociation(), projectFrame.getGenomeFileAssociation());
		}
		// reinit the MainFrame if needed (in the case where the user chose the new project option from the mainframe)
		MainFrame.reinit();
		// starts the main frame of the application
		MainFrame.getInstance().setVisible(true);
	}


	/**
	 * Loads the managers with the configuration files
	 */
	private static void loadManagers() {
		// load configuration manager
		try {
			ConfigurationManager.getInstance().loadConfigurationFile();
		} catch (Exception e) {
			// do nothing if the configuration file is not found
		}
		// load the zoom manager
		try {
			if (ConfigurationManager.getInstance().getZoomFile() != "") {
				ZoomManager.getInstance().loadConfigurationFile(new File(ConfigurationManager.getInstance().getZoomFile()));
			}
		} catch (IOException e) {
			ExceptionManager.handleException(ProjectFrame.getInstance().getRootPane(), e, "Zoom file not found.");
		} catch (Exception e) {
			ExceptionManager.handleException(ProjectFrame.getInstance().getRootPane(), e, "Zoom file corrupted");
		}
	}
}
