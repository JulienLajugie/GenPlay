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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import edu.yu.einstein.genplay.core.enums.VCFType;
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
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreen;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ScreenThread;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * This class loads GenPlay with a welcome screen.
 * Then, the main frame is loaded.
 * @author Nicolas Fourel
 */
public class Launcher {

	private static final String DEMO_PROJECT_PATH = null;

	private static ProjectScreen 	screenProject;
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
				//generateRealMultiGenomeManager();
				//generateTestMultiGenomeManager();
				//generateHg18ToHg19MultiGenomeManager();
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


	@SuppressWarnings("unused")	//Development
	private static void generateRealMultiGenomeManager() {
		// Declaration
		Map<String, List<String>> genomeGroupAssociation = new HashMap<String, List<String>>();
		Map<String, List<File>> genomeFilesAssociation = new HashMap<String, List<File>>();
		Map<String, String> genomeNamesAssociation = new HashMap<String, String>();
		Map<VCFType, List<File>> filesTypeAssociation = new HashMap<VCFType, List<File>>();
		List<File> vcfIndel = new ArrayList<File>();
		List<File> vcfCEU = new ArrayList<File>();
		List<File> vcfYRI = new ArrayList<File>();

		// File type list
		File ceuFile = new File("D:\\Documents\\VCF\\ftp\\trio\\indels\\CEU.trio.2010_10.indel.genotypes.vcf.gz");
		File yriFile = new File("D:\\Documents\\VCF\\ftp\\trio\\indels\\YRI.trio.2010_10.indel.genotypes.vcf.gz");
		vcfIndel.add(ceuFile);
		vcfIndel.add(yriFile);
		filesTypeAssociation.put(VCFType.INDELS, vcfIndel);

		// File group list
		vcfCEU.add(ceuFile);
		vcfYRI.add(yriFile);

		// CEU
		List<String> listCEU = new ArrayList<String>();
		listCEU.add("NA12891");
		listCEU.add("NA12892");
		listCEU.add("NA12878");
		genomeGroupAssociation.put("CEU", listCEU);
		genomeFilesAssociation.put("CEU", vcfCEU);
		genomeNamesAssociation.put("NA12891", "Father");
		genomeNamesAssociation.put("NA12892", "Mother");
		genomeNamesAssociation.put("NA12878", "Daughter");

		// YRI
		List<String> listYRI = new ArrayList<String>();
		listYRI.add("NA19239");
		listYRI.add("NA19238");
		listYRI.add("NA19240");
		genomeGroupAssociation.put("YRI", listYRI);
		genomeFilesAssociation.put("YRI", vcfYRI);
		genomeNamesAssociation.put("NA19239", "Father");
		genomeNamesAssociation.put("NA19238", "Mother");
		genomeNamesAssociation.put("NA19240", "Daughter");

		MultiGenomeManager multiGenomeManager = null;

		multiGenomeManager = MultiGenomeManager.getInstance();
		multiGenomeManager.setGenomes(genomeGroupAssociation,
				genomeFilesAssociation,
				genomeNamesAssociation,
				filesTypeAssociation);
	}


	@SuppressWarnings("unused")	//Development
	private static void generateTestMultiGenomeManager() {
		// Declaration
		Map<String, List<String>> genomeGroupAssociation = new HashMap<String, List<String>>();
		Map<String, List<File>> genomeFilesAssociation = new HashMap<String, List<File>>();
		Map<String, String> genomeNamesAssociation = new HashMap<String, String>();
		Map<VCFType, List<File>> filesTypeAssociation = new HashMap<VCFType, List<File>>();
		List<File> vcfIndel = new ArrayList<File>();
		List<File> vcfGenome01 = new ArrayList<File>();
		List<File> vcfGenome02 = new ArrayList<File>();
		List<File> vcfGenome03 = new ArrayList<File>();

		// File type list
		File genome01File = new File("D:\\Documents\\VCF\\test3\\personnal_genome_01.sites.vcf.gz");
		File genome02File = new File("D:\\Documents\\VCF\\test3\\personnal_genome_02.sites.vcf.gz");
		File genome03File = new File("D:\\Documents\\VCF\\test3\\personnal_genome_03.sites.vcf.gz");
		vcfIndel.add(genome01File);
		vcfIndel.add(genome02File);
		vcfIndel.add(genome03File);
		filesTypeAssociation.put(VCFType.INDELS, vcfIndel);

		// File group list
		vcfGenome01.add(genome01File);
		vcfGenome02.add(genome02File);
		vcfGenome03.add(genome03File);

		// Famille 1
		List<String> listGenome01 = new ArrayList<String>();
		listGenome01.add("G101");
		genomeGroupAssociation.put("Family 1", listGenome01);
		genomeFilesAssociation.put("Family 1", vcfGenome01);
		genomeNamesAssociation.put("G101", "Person 01");

		// Famille 2
		List<String> listGenome02 = new ArrayList<String>();
		listGenome02.add("G201");
		genomeGroupAssociation.put("Family 2", listGenome02);
		genomeFilesAssociation.put("Family 2", vcfGenome02);
		genomeNamesAssociation.put("G201", "Person 01");
		
		// Famille 3
		List<String> listGenome03 = new ArrayList<String>();
		listGenome03.add("G301");
		genomeGroupAssociation.put("Family 3", listGenome03);
		genomeFilesAssociation.put("Family 3", vcfGenome03);
		genomeNamesAssociation.put("G301", "Person 01");

		MultiGenomeManager multiGenomeManager = null;

		multiGenomeManager = MultiGenomeManager.getInstance();
		multiGenomeManager.setGenomes(genomeGroupAssociation,
				genomeFilesAssociation,
				genomeNamesAssociation,
				filesTypeAssociation);
	}
	
	
	@SuppressWarnings("unused")	//Development
	private static void generateHg18ToHg19MultiGenomeManager() {
		// Declaration
		Map<String, List<String>> genomeGroupAssociation = new HashMap<String, List<String>>();
		Map<String, List<File>> genomeFilesAssociation = new HashMap<String, List<File>>();
		Map<String, String> genomeNamesAssociation = new HashMap<String, String>();
		Map<VCFType, List<File>> filesTypeAssociation = new HashMap<VCFType, List<File>>();
		List<File> vcfIndel = new ArrayList<File>();
		List<File> vcfGenome01 = new ArrayList<File>();

		// File type list
		//File genome01File = new File("D:\\Documents\\VCF\\Hg18ToHg19\\hg18ToHg19_chr1.vcf.gz");
		File genome01File = new File("D:\\Documents\\VCF\\Hg18ToHg19\\hg18ToHg19.vcf.gz");
		vcfIndel.add(genome01File);
		filesTypeAssociation.put(VCFType.SV, vcfIndel);

		// File group list
		vcfGenome01.add(genome01File);

		// Famille 1
		List<String> listGenome01 = new ArrayList<String>();
		listGenome01.add("NCBI36");
		genomeGroupAssociation.put("Reference", listGenome01);
		genomeFilesAssociation.put("Reference", vcfGenome01);
		genomeNamesAssociation.put("NCBI36", "Hg18");

		MultiGenomeManager multiGenomeManager = null;

		multiGenomeManager = MultiGenomeManager.getInstance();
		multiGenomeManager.setGenomes(genomeGroupAssociation,
				genomeFilesAssociation,
				genomeNamesAssociation,
				filesTypeAssociation);
	}


	private static void generateMultiGenomeManager() {
		MultiGenomeManager multiGenomeManager = null;
		multiGenomeManager = MultiGenomeManager.getInstance();
		multiGenomeManager.setGenomes(screenProject.getGenomeGroupAssociation(),
				screenProject.getGenomeFilesAssociation(),
				screenProject.getGenomeNamesAssociation(),
				screenProject.getFilesTypeAssociation());
		
		//multiGenomeManager.showAllAssociation();
	}


	//@SuppressWarnings("unused")	// Development
	/*private static void memoryTest () {
		MemoryTestBench bench = new MemoryTestBench();
		ObjectFactory multiGenome = new MultiGenomeObjectFactory();
		bench.showMemoryUsage(multiGenome);
		//((MultiGenomeObjectFactory)multiGenome).compute();
		//bench.showMemoryUsage(multiGenome);
	}*/


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
		screenProject = ProjectScreen.getInstance();
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