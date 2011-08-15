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
package edu.yu.einstein.genplay.gui.dialog.projectScreen;

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
import java.util.concurrent.CountDownLatch;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.loadProject.LoadProjectPanel;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.NewProjectPanel;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * This class manages all the screen project.
 * It defines different sizes, colors, panels...
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ProjectScreenFrame extends JFrame {
	
	private static final long serialVersionUID = -5785973410951935317L;

	private final static 	String 	ICON_PATH = "edu/yu/einstein/genplay/resource/icon.png"; 	// path of the icon of the application
	private 	 			Image 	iconImage; 													// icon of the application

	//Ratio
	private static final Double BANNER_RATIO 			= 0.15;
	private static final Double TYPE_RATIO 				= 0.08;
	private static final Double CONTENT_RATIO 			= 0.7;
	private static final Double NAME_RATIO 				= 0.1;
	private static final Double ASSEMBLY_RATIO 			= 0.25;
	private static final Double GENOME_RATIO 			= 0.15;
	private static final Double VCF_RATIO 				= 0.5;
	private static final Double LINE_RATIO 				= 0.1;
	private static final Double PROJECT_CHOOSER_RATIO 	= 0.05;
	private static final Double CONFIRM_RATIO 			= 0.07;

	//Size
	private static final int SCREEN_WIDTH 				= 450;
	private static final int SCREEN_HEIGHT 				= 700;
	private static final int BANNER_HEIGHT 				= (int)Math.round(SCREEN_HEIGHT*BANNER_RATIO);
	private static final int TYPE_HEIGHT 				= (int)Math.round(SCREEN_HEIGHT*TYPE_RATIO);
	private static final int CONTENT_HEIGHT 			= (int)Math.round(SCREEN_HEIGHT*CONTENT_RATIO);
	private static final int NAME_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*NAME_RATIO);
	private static final int ASSEMBLY_HEIGHT 			= (int)Math.round(CONTENT_HEIGHT*ASSEMBLY_RATIO);
	private static final int GENOME_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*GENOME_RATIO);
	private static final int VCF_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*VCF_RATIO);
	private static final int LINE_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*LINE_RATIO);
	private static final int PROJECT_CHOOSER_HEIGHT 	= (int)Math.round(CONTENT_HEIGHT*PROJECT_CHOOSER_RATIO);
	private static final int CONFIRM_HEIGHT = (int)Math.round(SCREEN_HEIGHT*CONFIRM_RATIO);

	// Banner panel dimensions
	protected static final Dimension BANNER_DIM 	= new Dimension (SCREEN_WIDTH, BANNER_HEIGHT);
	
	// Type panel dimensions
	protected static final Dimension TYPE_DIM 		= new Dimension (SCREEN_WIDTH, TYPE_HEIGHT);

	// New project panel dimensions
	protected static final Dimension NEW_DIM 		= new Dimension (SCREEN_WIDTH, CONTENT_HEIGHT);
	protected static final Dimension NAME_DIM 		= new Dimension (SCREEN_WIDTH, NAME_HEIGHT);
	protected static final Dimension ASSEMBLY_DIM 	= new Dimension (SCREEN_WIDTH, ASSEMBLY_HEIGHT);
	protected static final Dimension GENOME_DIM 	= new Dimension (SCREEN_WIDTH, GENOME_HEIGHT);
	protected static final Dimension VCF_DIM 		= new Dimension (SCREEN_WIDTH, VCF_HEIGHT);

	// Load project panel dimensions
	protected static final Dimension LOAD_DIM 				= new Dimension (SCREEN_WIDTH, CONTENT_HEIGHT);
	protected static final Dimension LINE_DIM 				= new Dimension (SCREEN_WIDTH, LINE_HEIGHT);
	protected static final Dimension PROJECT_CHOOSER_DIM 	= new Dimension (SCREEN_WIDTH-70, PROJECT_CHOOSER_HEIGHT);
	protected static final Dimension CONFIRM_DIM 			= new Dimension (SCREEN_WIDTH, CONFIRM_HEIGHT);

	//Tool tip text
	protected static final String ADD_VCF_FILE 		= "Add vcf file";
	protected static final String DEL_VCF_FILES 	= "Delete selection";
	protected static final String CONFIRM_FILES		= "Confirm selection";
	protected static final String CANCEL_FILES		= "Cancel modification";
	protected static final String SELECT_FILES 		= "Enable selection";
	protected static final String UNSELECT_FILES 	= "Disable selection";
	protected static final String MOVE_UP_FILES 	= "Move up selection";
	protected static final String MOVE_DOWN_FILES 	= "Move down selection";
	protected static final String SELECT_BASIC_CHR 	= "Select basics chromosome";

	//Real colors
	protected static final Color COLOR 			= Color.white;
	protected static final Color BANNER_COLOR 	= COLOR;
	protected static final Color TYPE_COLOR 	= COLOR;
	protected static final Color NEW_COLOR 		= COLOR;
	protected static final Color NAME_COLOR 			= COLOR;
	protected static final Color ASSEMBLY_COLOR 		= COLOR;
	protected static final Color GENOME_COLOR 			= COLOR;
	protected static final Color VCF_COLOR 				= COLOR;
	protected static final Color TABLE_PANEL_COLOR 		= COLOR;
	protected static final Color TABLE_BUTTON_COLOR 	= COLOR;
	protected static final Color LOAD_COLOR 	= COLOR;
	protected static final Color CONFIRM_COLOR 	= COLOR;

	private static final 	String 					CREATE_BUTTON = "Create";	// Text of the button if you choose a new project
	private static final 	String 					LOAD_BUTTON = "Load";		// Text of the button if you choose to load a project
	private static 			ProjectScreenFrame 	instance = null;			// The instance of the class
	private 				BannerPanel 			bannerPanel;				// The banner
	private 				ProjectTypePanel 			projectTypePanel;				// The type of the project (new/load)
	private static 			NewProjectPanel 				newProjectPanel;					// Panel for a new project
	private static 			LoadProjectPanel 			loadProjectPanel;				// Panel for loading a project
	private 				ConfirmPanel 			confirmPanel;				// Panel to confirm the user choice
	private 				GridBagConstraints 		gbc;						// Constraints for the GriBagLayout
	private					CountDownLatch 			projectSignal;

	private	String		name;		// Name of the project
	private	String		clade;		// Name of the selected clade
	private	String		genome;		// Name of the selected genome
	private	String		assembly;	// Name of the selected assembly


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
	private ProjectScreenFrame() throws HeadlessException {
		super();
	}


	/**
	 * @return the instance of the singleton {@link ProjectScreenFrame}.
	 */
	public static ProjectScreenFrame getInstance () {
		if (instance == null) {
			instance = new ProjectScreenFrame();
		}
		return instance;
	}


	/**
	 * Main method of the class.
	 * It initializes frame and panels.
	 */
	public void initScreen () {

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
	protected static void confirmCreate () {
		Boolean valid = true;

		//Name
		getInstance().name = newProjectPanel.getProjectName();

		//Clade
		getInstance().clade = newProjectPanel.getCladeName();
		getInstance().genome = newProjectPanel.getGenomeName();
		getInstance().assembly = newProjectPanel.getAssemblyName();

		if (getInstance().name == null) {
			valid = false;
		}

		//VCF files
		if (!newProjectPanel.isSimpleProject()) {
			if (!newProjectPanel.isValidMultigenomeProject()) {
				valid = false;
			}
		}

		//Decrement countDown object
		if (valid) {
			getInstance().projectSignal.countDown();
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
	protected static void confirmLoading () {
		if (loadProjectPanel.getProject() != null) {
			getInstance().projectSignal.countDown();
		}
	}


	/**
	 * @param projectSignal the projectSignal to set
	 */
	public void setProjectSignal(CountDownLatch projectSignal) {
		this.projectSignal = projectSignal;
	}


	/**
	 * @return selected project
	 */
	public File getProject () {
		return loadProjectPanel.getProject();
	}


	/**
	 * @return 	yes if the user wants to load an existing project
	 * 			no if the user creates a new project
	 */
	public boolean isLoadingEvent () {
		return loadProjectPanel.isVisible();
	}


	/**
	 * @return the selected chromosome list
	 */
	public Map<String, Chromosome> getNewChromosomeList() {
		return newProjectPanel.getNewChromosomeList();
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the clade
	 */
	public String getClade() {
		return clade;
	}


	/**
	 * @return the genome
	 */
	public String getGenome() {
		return genome;
	}


	/**
	 * @return the assembly
	 */
	public String getAssembly() {
		return assembly;
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
	 * @return the screenWidth
	 */
	public static int getScreenWidth() {
		return SCREEN_WIDTH;
	}


	/**
	 * @return the screenHeight
	 */
	public static int getScreenHeight() {
		return SCREEN_HEIGHT;
	}


	/**
	 * @return the bannerDim
	 */
	public static Dimension getBannerDim() {
		return BANNER_DIM;
	}


	/**
	 * @return the typeDim
	 */
	public static Dimension getTypeDim() {
		return TYPE_DIM;
	}


	/**
	 * @return the newDim
	 */
	public static Dimension getNewDim() {
		return NEW_DIM;
	}


	/**
	 * @return the nameDim
	 */
	public static Dimension getNameDim() {
		return NAME_DIM;
	}


	/**
	 * @return the assemblyDim
	 */
	public static Dimension getAssemblyDim() {
		return ASSEMBLY_DIM;
	}


	/**
	 * @return the genomeDim
	 */
	public static Dimension getGenomeDim() {
		return GENOME_DIM;
	}


	/**
	 * @return the varDim
	 */
	public static Dimension getVCFDim() {
		return VCF_DIM;
	}


	/**
	 * @return the loadDim
	 */
	public static Dimension getLoadDim() {
		return LOAD_DIM;
	}


	/**
	 * @return the lineDim
	 */
	public static Dimension getLineDim() {
		return LINE_DIM;
	}


	/**
	 * @return the projectChooserDim
	 */
	public static Dimension getProjectChooserDim() {
		return PROJECT_CHOOSER_DIM;
	}


	/**
	 * @return the confirmDim
	 */
	public static Dimension getConfirmDim() {
		return CONFIRM_DIM;
	}


	/**
	 * @return the addVarFiles
	 */
	public static String getAddVarFiles() {
		return ADD_VCF_FILE;
	}


	/**
	 * @return the delVarFiles
	 */
	public static String getDelVarFiles() {
		return DEL_VCF_FILES;
	}


	/**
	 * @return the confirmFiles
	 */
	public static String getConfirmFiles() {
		return CONFIRM_FILES;
	}


	/**
	 * @return the cancelFiles
	 */
	public static String getCancelFiles() {
		return CANCEL_FILES;
	}


	/**
	 * @return the selectFiles
	 */
	public static String getSelectFiles() {
		return SELECT_FILES;
	}


	/**
	 * @return the unselectFiles
	 */
	public static String getUnselectFiles() {
		return UNSELECT_FILES;
	}


	/**
	 * @return the moveUpFiles
	 */
	public static String getMoveUpFiles() {
		return MOVE_UP_FILES;
	}


	/**
	 * @return the moveDownFiles
	 */
	public static String getMoveDownFiles() {
		return MOVE_DOWN_FILES;
	}


	/**
	 * @return the selectBasicChr
	 */
	public static String getSelectBasicChr() {
		return SELECT_BASIC_CHR;
	}


	/**
	 * @return the bannerColor
	 */
	public static Color getBannerColor() {
		return BANNER_COLOR;
	}


	/**
	 * @return the typeColor
	 */
	public static Color getTypeColor() {
		return TYPE_COLOR;
	}


	/**
	 * @return the newColor
	 */
	public static Color getNewColor() {
		return NEW_COLOR;
	}


	/**
	 * @return the nameColor
	 */
	public static Color getNameColor() {
		return NAME_COLOR;
	}


	/**
	 * @return the assemblyColor
	 */
	public static Color getAssemblyColor() {
		return ASSEMBLY_COLOR;
	}


	/**
	 * @return the genomeColor
	 */
	public static Color getGenomeColor() {
		return GENOME_COLOR;
	}


	/**
	 * @return the varColor
	 */
	public static Color getVCFColor() {
		return VCF_COLOR;
	}


	/**
	 * @return the tablePanelColor
	 */
	public static Color getTablePanelColor() {
		return TABLE_PANEL_COLOR;
	}


	/**
	 * @return the tableButtonColor
	 */
	public static Color getTableButtonColor() {
		return TABLE_BUTTON_COLOR;
	}


	/**
	 * @return the loadColor
	 */
	public static Color getLoadColor() {
		return LOAD_COLOR;
	}


	/**
	 * @return the confirmColor
	 */
	public static Color getConfirmColor() {
		return CONFIRM_COLOR;
	}


	/**
	 * @return the createButton
	 */
	public static String getCreateButton() {
		return CREATE_BUTTON;
	}


	/**
	 * @return the loadButton
	 */
	public static String getLoadButton() {
		return LOAD_BUTTON;
	}

}