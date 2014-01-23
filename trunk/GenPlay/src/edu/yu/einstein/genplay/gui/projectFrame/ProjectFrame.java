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
package edu.yu.einstein.genplay.gui.projectFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.ScorePrecision;
import edu.yu.einstein.genplay.dataStructure.genome.Assembly;
import edu.yu.einstein.genplay.dataStructure.genome.Clade;
import edu.yu.einstein.genplay.dataStructure.genome.Genome;
import edu.yu.einstein.genplay.gui.launcher.Launcher;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.projectFrame.loadProject.LoadProjectPanel;
import edu.yu.einstein.genplay.gui.projectFrame.newProject.NewProjectPanel;
import edu.yu.einstein.genplay.util.Images;

/**
 * This class manages all the screen project.
 * It defines different sizes, colors, panels...
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ProjectFrame extends JFrame {

	private static final long serialVersionUID = -5785973410951935317L; // generated ID

	//Ratio
	/** Ratio for the height of the banner section */
	public static final Double BANNER_RATIO 			= 0.15;
	/** Ratio for the height of the type section */
	public static final Double TYPE_RATIO 				= 0.08;
	/** Ratio for the height of the content section */
	public static final Double CONTENT_RATIO 			= 0.7;
	/** Ratio for the height of the name section in the content part */
	public static final Double NAME_RATIO 				= 0.1;
	/** Ratio for the height of the score precision section in the content part */
	public static final Double PRECISION_RATIO			= 0.1;
	/** Ratio for the height of the assembly section in the content part */
	public static final Double ASSEMBLY_RATIO 			= 0.22;
	/** Ratio for the height of the genome section in the content part */
	public static final Double GENOME_RATIO 			= 0.15;
	/** Ratio for the height of the VCF section in the content part */
	public static final Double VCF_RATIO 				= 0.5;
	/** Ratio for the height of the line section in the content part */
	public static final Double LINE_RATIO 				= 0.1;
	/** Ratio for the height of the project chooser section */
	public static final Double PROJECT_CHOOSER_RATIO 	= 0.05;
	/** Ratio for the height of the confirmation section */
	public static final Double CONFIRM_RATIO 			= 0.07;

	//Size
	/** Width of the dialog */
	public static final int SCREEN_WIDTH 					= 450;
	/** Height of the dialog */
	public static final int SCREEN_HEIGHT 					= 700;
	/** Height of the banner section */
	public static final int BANNER_HEIGHT 					= (int)Math.round(SCREEN_HEIGHT*BANNER_RATIO);
	/** Height of the type section */
	public static final int TYPE_HEIGHT 					= (int)Math.round(SCREEN_HEIGHT*TYPE_RATIO);
	/** Height of the content section */
	public static final int CONTENT_HEIGHT 					= (int)Math.round(SCREEN_HEIGHT*CONTENT_RATIO);
	/** Height of the name section in the content part */
	public static final int NAME_HEIGHT 					= (int)Math.round(CONTENT_HEIGHT*NAME_RATIO);
	/** Height of the score precision section in the content part */
	public static final int PRECISION_HEIGHT				= (int)Math.round(CONTENT_HEIGHT*PRECISION_RATIO);
	/** Height of the assembly section in the content part */
	public static final int ASSEMBLY_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*ASSEMBLY_RATIO);
	/** Height of the genome section in the content part */
	public static final int GENOME_HEIGHT 					= (int)Math.round(CONTENT_HEIGHT*GENOME_RATIO);
	/** Height of the VCF section in the content part */
	public static final int VCF_HEIGHT 						= (int)Math.round(CONTENT_HEIGHT*VCF_RATIO);
	/** Height of the line section in the content part */
	public static final int LINE_HEIGHT 					= (int)Math.round(CONTENT_HEIGHT*LINE_RATIO);
	/** Height of the project chooser section */
	public static final int PROJECT_CHOOSER_HEIGHT 			= (int)Math.round(CONTENT_HEIGHT*PROJECT_CHOOSER_RATIO);
	/** Height of the confirmation section */
	public static final int CONFIRM_HEIGHT 					= (int)Math.round(SCREEN_HEIGHT*CONFIRM_RATIO);

	/** Banner panel dimensions */
	public static final Dimension BANNER_DIM 	= new Dimension (SCREEN_WIDTH, BANNER_HEIGHT);
	/** Type panel dimensions */
	public static final Dimension TYPE_DIM 		= new Dimension (SCREEN_WIDTH, TYPE_HEIGHT);

	// New project panel dimensions
	/** Dimension of the new project section */
	public static final Dimension NEW_DIM 		= new Dimension (SCREEN_WIDTH, CONTENT_HEIGHT);
	/** Dimension of the name section */
	public static final Dimension NAME_DIM 		= new Dimension (SCREEN_WIDTH, NAME_HEIGHT);
	/** Dimension of the score precision section */
	public static final Dimension PRECISION_DIM = new Dimension (SCREEN_WIDTH, PRECISION_HEIGHT);
	/** Dimension of the assembly section */
	public static final Dimension ASSEMBLY_DIM 	= new Dimension (SCREEN_WIDTH, ASSEMBLY_HEIGHT);
	/** Dimension of the genome section */
	public static final Dimension GENOME_DIM 	= new Dimension (SCREEN_WIDTH, GENOME_HEIGHT);
	/** Dimension of the VCF section */
	public static final Dimension VCF_DIM 		= new Dimension (SCREEN_WIDTH, VCF_HEIGHT);

	// Load project panel dimensions
	/** Dimension of the load project section */
	public static final Dimension LOAD_DIM 				= new Dimension (SCREEN_WIDTH, CONTENT_HEIGHT);
	/** Dimension of the line section */
	public static final Dimension LINE_DIM 				= new Dimension (SCREEN_WIDTH, LINE_HEIGHT);
	/** Dimension of the project chooser section */
	public static final Dimension PROJECT_CHOOSER_DIM 	= new Dimension (SCREEN_WIDTH-70, PROJECT_CHOOSER_HEIGHT);
	/** Dimension of the confirmation section */
	public static final Dimension CONFIRM_DIM 			= new Dimension (SCREEN_WIDTH, CONFIRM_HEIGHT);

	//Tool tip text
	/** Name for adding a vcf file */
	public static final String ADD_VCF_FILE 		= "Add vcf file";
	/** Name for deleting g a vcf file */
	public static final String DEL_VCF_FILES 		= "Delete selection";
	/** Name to confirm selection */
	public static final String CONFIRM_FILES		= "Confirm selection";
	/** Name to cancel modification */
	public static final String CANCEL_FILES			= "Cancel modification";
	/** Name to enable the selection */
	public static final String SELECT_FILES 		= "Enable selection";
	/** Name to disable the selection */
	public static final String UNSELECT_FILES 		= "Disable selection";
	/** Name to move the files up */
	public static final String MOVE_UP_FILES 		= "Move up selection";
	/** Name to move the files down */
	public static final String MOVE_DOWN_FILES 		= "Move down selection";
	/** Name to select the basic chromosomes */
	public static final String SELECT_BASIC_CHR 	= "Select basics chromosome";

	//Real colors
	/** White color */
	public static final		Color COLOR 			= Color.white;
	/** Color of the banner section */
	public static final 	Color BANNER_COLOR 		= COLOR;
	/** Color of the type section */
	public static final 	Color TYPE_COLOR 		= COLOR;
	/** Color of the new section */
	public static final 	Color NEW_COLOR 		= COLOR;
	/** Color of the name section */
	public static final 	Color NAME_COLOR 		= COLOR;
	/** Color of the score precision section */
	public static final 	Color PRECISION_COLOR	= COLOR;
	/** Color of the assembly section */
	public static final 	Color ASSEMBLY_COLOR 	= COLOR;
	/** Color of the genome section */
	public static final 	Color GENOME_COLOR 		= COLOR;
	/** Color of the VCF section */
	public static final 	Color VCF_COLOR 		= COLOR;
	/** Color of the table section */
	public static final 	Color TABLE_PANEL_COLOR = COLOR;
	/** Color of the button section */
	public static final 	Color TABLE_BUTTON_COLOR = COLOR;
	/** Color of the load section */
	public static final 	Color LOAD_COLOR 		= COLOR;
	/** Color of the confirmation section */
	public static final 	Color CONFIRM_COLOR 	= COLOR;

	// Validate button labels
	/** Text of the button if you choose a new project */
	public static final 	String 	CREATE_BUTTON = "Create";
	/** Text of the button if you choose to load a project */
	public static final 	String 	LOAD_BUTTON = "Load";

	// The instance of the class
	private static ProjectFrame		instance = null;


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
	 * Shows a popup
	 * @param title title of the popup
	 * @param info	information to display
	 */
	public static void showPopUp (String title, String info) {
		JOptionPane.showMessageDialog(instance, info, title, JOptionPane.WARNING_MESSAGE);
	}
	private NewProjectPanel 		newProjectPanel;			// Panel for a new project
	private LoadProjectPanel 		loadProjectPanel;			// Panel for loading a project
	private BannerPanel 			bannerPanel;				// The banner
	private ProjectTypePanel 		projectTypePanel;			// The type of the project (new/load)
	private ConfirmPanel 			confirmPanel;				// Panel to confirm the user choice
	private GridBagConstraints 		gbc;						// Constraints for the GriBagLayout


	/**
	 * Private constructor.
	 * Creates an instance of singleton {@link MainFrame}
	 * @throws HeadlessException
	 */
	private ProjectFrame() throws HeadlessException {
		super();
	}


	/**
	 * This method gather new project information.
	 */
	protected void confirmCreate () {
		Boolean valid = true;
		// check that a project name is specified
		if (newProjectPanel.getProjectName().equals(""))  {
			JOptionPane.showMessageDialog(getRootPane(), "Please fill the project name field", "Invalid Project Name", JOptionPane.WARNING_MESSAGE);
			valid = false;
		}
		// in the case of the multi-genome project, check that the multi-genome information is correct
		if (!newProjectPanel.isSingleProject()) {
			if (!newProjectPanel.isValidMultigenomeProject()) {
				valid = false;
			}
		}
		if (newProjectPanel.getSelectedChromosomes().size() == 0) {
			JOptionPane.showMessageDialog(getRootPane(), "Please select at least one chromosome", "Invalid Chromosome Selection", JOptionPane.WARNING_MESSAGE);
			valid = false;
		}
		//start a new project
		if (valid) {
			Launcher.initiateNewProject();
		}
	}


	/**
	 * This method gather loading project information.
	 */
	protected void confirmLoading () {
		if (loadProjectPanel.getFileProjectToLoad() != null) {
			setVisible(false);
			Launcher.startProjectFromFile(loadProjectPanel.getFileProjectToLoad());
		}
	}


	/**
	 * @return the mapping between genome full names and their readers.
	 */
	public Map<String, List<VCFFile>> getGenomeFileAssociation ()  {
		return newProjectPanel.getGenomeFileAssociation();
	}


	/**
	 * @return the project name
	 */
	public String getProjectName() {
		return newProjectPanel.getProjectName();
	}


	/**
	 * @return the selected score precision
	 */
	public ScorePrecision getProjectScorePrecision() {
		return newProjectPanel.getProjectScorePrecision();
	}


	/**
	 * @return the selected assembly
	 */
	public Assembly getSelectedAssembly() {
		return newProjectPanel.getAssembly();
	}


	/**
	 * @return a {@link Map} containing the selected chromosomes.  Each chromosome is associated to its name in the map
	 */
	public List<Chromosome> getSelectedChromosomes() {
		return newProjectPanel.getSelectedChromosomes();
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
	 * Main method of the class.
	 * It initializes frame and panels.
	 */
	public void initScreen () {
		// set the icon of the frame
		setIconImage(Images.getApplicationImage());

		//Init panels
		bannerPanel = new BannerPanel();
		projectTypePanel = new ProjectTypePanel(this);
		newProjectPanel = new NewProjectPanel();
		loadProjectPanel = new LoadProjectPanel();
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
	 * This method determines if user chose a simple or a multi genome project.
	 * @return true if user chose a simple genome project.
	 */
	public boolean isSingleProject () {
		return newProjectPanel.isSingleProject();
	}


	/**
	 * Displays or hides the var panel of the {@link NewProjectPanel}
	 * @param visible set to true to show the var table
	 */
	public void setVarTableVisible(boolean visible) {
		newProjectPanel.setVarTableVisible(visible);
	}


	/**
	 * Reinitializes the list of the recent project files when the ProjectScreens is shown
	 */
	@Override
	public void setVisible(boolean aFlag) {
		if (aFlag) {
			loadProjectPanel .reinitProjectFileList();
		}
		super.setVisible(aFlag);
	}


	/**
	 * This method show the {@link LoadProjectPanel} panel
	 */
	public void toLoadScreenProject () {
		newProjectPanel.setVisible(false);
		loadProjectPanel.setVisible(true);
		confirmPanel.setConfirmButton(LOAD_BUTTON);
		projectTypePanel.getLoadRadio().setSelected(true);
	}


	/**
	 * This method show the {@link NewProjectPanel} panel
	 */
	public void toNewScreenProject () {
		loadProjectPanel.setVisible(false);
		newProjectPanel.setVisible(true);
		confirmPanel.setConfirmButton(CREATE_BUTTON);
		projectTypePanel.getNewRadio().setSelected(true);
	}
}

