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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import edu.yu.einstein.genplay.core.genome.Assembly;
import edu.yu.einstein.genplay.core.genome.Clade;
import edu.yu.einstein.genplay.core.genome.Genome;
import edu.yu.einstein.genplay.core.genome.RetrieveAssemblies;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.ProjectManager;
import edu.yu.einstein.genplay.core.manager.ProjectRecordingManager;
import edu.yu.einstein.genplay.core.manager.ZoomManager;
import edu.yu.einstein.genplay.core.manager.multiGenomeManager.MultiGenomeManager;
import edu.yu.einstein.genplay.gui.action.project.PALoadProject;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenFrame;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ScreenThread;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * This class loads GenPlay with a welcome screen.
 * Then, the main frame is loaded.
 * @author Nicolas Fourel
 */
public class Launcher {

	private static final String DEMO_PROJECT_PATH = null;

	private static ProjectScreenFrame 	screenProject;
	private static Map<String, Clade> 		cladeList;


	/**
	 * Starts the application
	 * @param args
	 */
	public static void main(final String[] args) {

		final boolean isDemo = (DEMO_PROJECT_PATH != null);

		boolean isProjectLoaded = false;
		File f = new File("");

		if (isDemo) {
			InputStream is = MainFrame.getInstance().getClass().getClassLoader().getResourceAsStream(DEMO_PROJECT_PATH);
			try {
				ProjectRecordingManager.getInstance().initManagers(is);
				isProjectLoaded = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (args.length == 1) {
			try {
				f = new File(args[0]);
				ProjectRecordingManager.getInstance().initManagers(new File(args[0]));
				isProjectLoaded = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			projectScreen();

			if (!screenProject.isSimpleProject()) {
				generateMultiGenomeManager();
			}

			mainScreen();
		}

		if (isProjectLoaded) {
			mainScreen();
			PALoadProject load = new PALoadProject();
			load.setSelectedFile(f);
			load.actionPerformed(null);
		}
	}


	private static void generateMultiGenomeManager() {
		MultiGenomeManager multiGenomeManager = null;
		multiGenomeManager = MultiGenomeManager.getInstance();
		multiGenomeManager.init(screenProject.getFileReadersAssociation(), screenProject.getGenomeFileAssociation());
	}


	/**
	 * Displays the project screen manager which is the first screen of GenPlay.
	 */
	private static void projectScreen() {
		//Get assemblies from xml files
		cladeList = new HashMap<String, Clade>();
		try {
			RetrieveAssemblies genomeHandler = new RetrieveAssemblies();
			cladeList = genomeHandler.getCladeList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Welcome screen initialization
		CountDownLatch projectSignal = new CountDownLatch(1);
		screenProject = ProjectScreenFrame.getInstance();
		screenProject.setProjectSignal(projectSignal);
		ConfigurationManager.getInstance();
		ProjectManager projectManager = ProjectManager.getInstance();
		ZoomManager.getInstance();
		// load the managers from the configuration files
		loadManagers();

		//Create a new thread to display the welcome screen
		Thread thread = new ScreenThread();
		thread.start();

		//Wait for the thread stop
		try {
			projectSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (screenProject.isLoadingEvent()) {
			try {
				ProjectRecordingManager.getInstance().setFileToLoad(screenProject.getProject());
				ProjectRecordingManager.getInstance().setLoadingEvent(true);
				ProjectRecordingManager.getInstance().initManagers();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Clade clade = cladeList.get(screenProject.getClade());
			Genome genome = clade.getGenomeList().get(screenProject.getGenome());
			Assembly assembly = genome.getAssemblyList().get(screenProject.getAssembly());
			assembly.setChromosomeList(screenProject.getNewChromosomeList());

			projectManager.setProjectName(screenProject.getName());
			projectManager.setCladeName(clade.getName());
			projectManager.setGenomeName(genome.getName());
			//projectManager.setVarFiles(screenProject.getVarFiles());
			projectManager.setAssembly(assembly);
		}

		screenProject.dispose();
	}


	/**
	 * Displays the main screen of GenPlay.
	 */
	private static void mainScreen() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// create and show a singleton instance of MainFrame
				final MainFrame mainFrame = MainFrame.getInstance();
				mainFrame.setVisible(true);
			}
		});
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
			ExceptionManager.handleException(screenProject.getRootPane(), e, "Zoom file not found.");
		} catch (Exception e) {
			ExceptionManager.handleException(screenProject.getRootPane(), e, "Zoom file corrupted");
		}
	}


	/**
	 * @return the cladeList
	 */
	public static Map<String, Clade> getCladeList() {
		return cladeList;
	}

}