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
package edu.yu.einstein.genplay.gui.projectFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.genome.Assembly;
import edu.yu.einstein.genplay.core.genome.Clade;
import edu.yu.einstein.genplay.core.genome.Genome;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.gui.launcher.Launcher;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.projectFrame.loadProject.LoadProjectPanel;
import edu.yu.einstein.genplay.gui.projectFrame.newProject.NewProjectPanel;

/**
 * This class manages all the screen project.
 * It defines different sizes, colors, panels...
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ProjectFrame extends JFrame {

	private static final long serialVersionUID = -5785973410951935317L; // generated ID
	private final static 	String 	ICON_PATH = "edu/yu/einstein/genplay/resource/icon.png"; 	// path of the icon of the application
	private static			Image 	iconImage; 													// icon of the application
	//Ratio
	public static final Double BANNER_RATIO 			= 0.15;
	public static final Double TYPE_RATIO 				= 0.08;
	public static final Double CONTENT_RATIO 			= 0.7;
	public static final Double NAME_RATIO 				= 0.1;
	public static final Double ASSEMBLY_RATIO 			= 0.25;
	public static final Double GENOME_RATIO 			= 0.15;
	public static final Double VCF_RATIO 				= 0.5;
	public static final Double LINE_RATIO 				= 0.1;
	public static final Double PROJECT_CHOOSER_RATIO 	= 0.05;
	public static final Double CONFIRM_RATIO 			= 0.07;
	//Size
	public static final int SCREEN_WIDTH 				= 450;
	public static final int SCREEN_HEIGHT 				= 700;
	public static final int BANNER_HEIGHT 				= (int)Math.round(SCREEN_HEIGHT*BANNER_RATIO);
	public static final int TYPE_HEIGHT 				= (int)Math.round(SCREEN_HEIGHT*TYPE_RATIO);
	public static final int CONTENT_HEIGHT 			= (int)Math.round(SCREEN_HEIGHT*CONTENT_RATIO);
	public static final int NAME_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*NAME_RATIO);
	public static final int ASSEMBLY_HEIGHT 			= (int)Math.round(CONTENT_HEIGHT*ASSEMBLY_RATIO);
	public static final int GENOME_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*GENOME_RATIO);
	public static final int VCF_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*VCF_RATIO);
	public static final int LINE_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*LINE_RATIO);
	public static final int PROJECT_CHOOSER_HEIGHT 	= (int)Math.round(CONTENT_HEIGHT*PROJECT_CHOOSER_RATIO);
	public static final int CONFIRM_HEIGHT = (int)Math.round(SCREEN_HEIGHT*CONFIRM_RATIO);
	// Banner panel dimensions
	public static final Dimension BANNER_DIM 	= new Dimension (SCREEN_WIDTH, BANNER_HEIGHT);
	// Type panel dimensions
	public static final Dimension TYPE_DIM 		= new Dimension (SCREEN_WIDTH, TYPE_HEIGHT);
	// New project panel dimensions
	public static final Dimension NEW_DIM 		= new Dimension (SCREEN_WIDTH, CONTENT_HEIGHT);
	public static final Dimension NAME_DIM 		= new Dimension (SCREEN_WIDTH, NAME_HEIGHT);
	public static final Dimension ASSEMBLY_DIM 	= new Dimension (SCREEN_WIDTH, ASSEMBLY_HEIGHT);
	public static final Dimension GENOME_DIM 	= new Dimension (SCREEN_WIDTH, GENOME_HEIGHT);
	public static final Dimension VCF_DIM 		= new Dimension (SCREEN_WIDTH, VCF_HEIGHT);
	// Load project panel dimensions
	public static final Dimension LOAD_DIM 				= new Dimension (SCREEN_WIDTH, CONTENT_HEIGHT);
	public static final Dimension LINE_DIM 				= new Dimension (SCREEN_WIDTH, LINE_HEIGHT);
	public static final Dimension PROJECT_CHOOSER_DIM 	= new Dimension (SCREEN_WIDTH-70, PROJECT_CHOOSER_HEIGHT);
	public static final Dimension CONFIRM_DIM 			= new Dimension (SCREEN_WIDTH, CONFIRM_HEIGHT);
	//Tool tip text
	public static final String ADD_VCF_FILE 		= "Add vcf file";
	public static final String DEL_VCF_FILES 	= "Delete selection";
	public static final String CONFIRM_FILES		= "Confirm selection";
	public static final String CANCEL_FILES		= "Cancel modification";
	public static final String SELECT_FILES 		= "Enable selection";
	public static final String UNSELECT_FILES 	= "Disable selection";
	public static final String MOVE_UP_FILES 	= "Move up selection";
	public static final String MOVE_DOWN_FILES 	= "Move down selection";
	public static final String SELECT_BASIC_CHR 	= "Select basics chromosome";
	//Real colors
	public static final	Color COLOR 			= Color.white;
	public static final 	Color BANNER_COLOR 		= COLOR;
	public static final 	Color TYPE_COLOR 		= COLOR;
	public static final 	Color NEW_COLOR 		= COLOR;
	public static final 	Color NAME_COLOR 		= COLOR;
	public static final 	Color ASSEMBLY_COLOR 	= COLOR;
	public static final 	Color GENOME_COLOR 		= COLOR;
	public static final 	Color VCF_COLOR 		= COLOR;
	public static final 	Color TABLE_PANEL_COLOR = COLOR;
	public static final 	Color TABLE_BUTTON_COLOR = COLOR;
	public static final 	Color LOAD_COLOR 		= COLOR;
	public static final 	Color CONFIRM_COLOR 	= COLOR;
	// Validate button labels
	public static final 	String 	CREATE_BUTTON = "Create";	// Text of the button if you choose a new project
	public static final 	String 	LOAD_BUTTON = "Load";		// Text of the button if you choose to load a project

	private static ProjectFrame		instance = null;			// The instance of the class

	private NewProjectPanel 		newProjectPanel;			// Panel for a new project
	private LoadProjectPanel 		loadProjectPanel;			// Panel for loading a project
	private BannerPanel 			bannerPanel;				// The banner
	private ProjectTypePanel 		projectTypePanel;			// The type of the project (new/load)
	private ConfirmPanel 			confirmPanel;				// Panel to confirm the user choice
	private GridBagConstraints 		gbc;						// Constraints for the GriBagLayout


	/**
	 * Shows a popup
	 * @param title title of the popup
	 * @param info	information to display
	 */
	public static void showPopUp (String title, String info) {
		JOptionPane.showMessageDialog(instance, info, title, JOptionPane.WARNING_MESSAGE);
	}


	/**
	 * Shows a popup with several information
	 * @param title title of the popup
	 * @param info	information to display
	 */
	public static void showPopUp (String title, List<String> info) {
		String chaine = "";
		for (String s: info) {
			chaine = s + " - ";
		}
		JOptionPane.showMessageDialog(instance, chaine, title, JOptionPane.WARNING_MESSAGE);
	}


	/**
	 * Private constructor.
	 * Creates an instance of singleton {@link MainFrame}
	 * @throws HeadlessException
	 */
	private ProjectFrame() throws HeadlessException {
		super();
	}


	/**
	 * @return the instance of the singleton {@link ProjectFrame}.
	 */
	public static ProjectFrame getInstance () {
		if (instance == null) {
			instance = new ProjectFrame();
		}
		return instance;
	}


	/**
	 * Main method of the class.
	 * It initializes frame and panels.
	 */
	public void initScreen () {
		// set the icon of the frame
		iconImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource(ICON_PATH));
		setIconImage(iconImage);

		//Init panels
		bannerPanel = new BannerPanel();
		projectTypePanel = new ProjectTypePanel(this);
		newProjectPanel = new NewProjectPanel();
		loadProjectPanel = new LoadProjectPanel(ConfigurationManager.getInstance().getProjects());
		confirmPanel = new ConfirmPanel();

		//Layout
		setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();

		//bannerPanel
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.PAGE_START;
		add(bannerPanel, gbc);

		//projectType
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.PAGE_START;
		add(projectTypePanel, gbc);

		//newProject
		gbc.gridy = 2;
		add(newProjectPanel, gbc);

		//loadProject
		add(loadProjectPanel, gbc);

		//confirmPanel
		gbc.gridy = 3;
		add(confirmPanel, gbc);

		//Init frame
		instance.setTitle("GenPlay");
		instance.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		instance.setResizable(false);
		instance.setLocationRelativeTo(null);
		instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		instance.setVisible(true);
	}


	/**
	 * This method show the {@link LoadProjectPanel} panel
	 */
	protected void toLoadScreenProject () {
		newProjectPanel.setVisible(false);
		loadProjectPanel.setVisible(true);
		confirmPanel.setConfirmButton(LOAD_BUTTON);
	}


	/**
	 * This method show the {@link NewProjectPanel} panel
	 */
	protected void toNewScreenProject () {
		loadProjectPanel.setVisible(false);
		newProjectPanel.setVisible(true);
		confirmPanel.setConfirmButton(CREATE_BUTTON);
	}


	/**
	 * This method gather new project information. 
	 */
	protected void confirmCreate () {
		Boolean valid = true;
		// check that a project name is specified
		if (newProjectPanel.getProjectName() == null) {
			valid = false;
		}
		// in the case of the multi-genome project, check that the multi-genome information is correct
		if (!newProjectPanel.isSimpleProject()) {
			if (!newProjectPanel.isValidMultigenomeProject()) {
				valid = false;
			}
		}
		//start a new project
		if (valid) {
			Launcher.initiateNewProject();
		}
	}


	/**
	 * This method determines if user chose a simple or a multi genome project. 
	 * @return true if user chose a simple genome project.
	 */
	public boolean isSimpleProject () {
		return newProjectPanel.isSimpleProject();
	}


	/**
	 * This method gather loading project information. 
	 */
	protected void confirmLoading () {
		if (loadProjectPanel.getFileProjectToLoad() != null) {
			Launcher.startProjectFromFile(loadProjectPanel.getFileProjectToLoad());
		}
	}


	/**
	 * @return a {@link Map} containing the selected chromosomes.  Each chromosome is associated to its name in the map
	 */
	public Map<String, Chromosome> getSelectedChromosomes() {
		return newProjectPanel.getSelectedChromosomes();
	}


	/**
	 * @return the project name
	 */
	public String getProjectName() {
		return newProjectPanel.getProjectName();
	}


	/**
	 * @return the selected clade
	 */
	public Clade getSelectedClade() {
		return newProjectPanel.getClade();
	}


	/**
	 * @return the selected genome
	 */
	public Genome getSelectedGenome() {
		return newProjectPanel.getGenome();
	}


	/**
	 * @return the selected assembly
	 */
	public Assembly getSelectedAssembly() {
		return newProjectPanel.getAssembly();
	}


	/**
	 * @return the mapping between genome full names and their files.
	 */
	public Map<String, List<File>> getGenomeFileAssociation ()  {
		return newProjectPanel.getGenomeFileAssociation();
	}


	/**
	 * @return the mapping between files and their readers.
	 */
	public Map<File, VCFReader> getFileReadersAssociation () {
		return newProjectPanel.getFileReadersAssociation();
	}


	/**
	 * Displays or hides the var panel of the {@link NewProjectPanel}
	 * @param visible set to true to show the var table
	 */
	public void setVarTableVisible(boolean visible) {
		newProjectPanel.setVarTableVisible(visible);
	}
}
