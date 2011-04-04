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
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.swing.JFrame;
import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.loadProject.LoadProject;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.NewProject;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * This class manages all the screen project.
 * It defines different sizes, colors, panels...
 * @author Nicolas Fourel
 */
public class ProjectScreenManager extends JFrame {
	
	private static final long serialVersionUID = -5785973410951935317L;
	
	//Ratio
	private static final Double BANNER_RATIO 	= 0.15;
	private static final Double TYPE_RATIO 		= 0.08;
	private static final Double CONTENT_RATIO 	= 0.7;
		private static final Double NAME_RATIO 				= 0.1;
		private static final Double ASSEMBLY_RATIO 			= 0.25;
		private static final Double GENOME_RATIO 			= 0.15;
		private static final Double VAR_RATIO 				= 0.5;
		private static final Double LINE_RATIO 				= 0.1;
		private static final Double PROJECT_CHOOSER_RATIO 	= 0.05;
	private static final Double CONFIRM_RATIO 	= 0.07;
	
	//Size
	private static final int SCREEN_WIDTH 	= 450;
	private static final int SCREEN_HEIGHT 	= 700;
	private static final int BANNER_HEIGHT 	= (int)Math.round(SCREEN_HEIGHT*BANNER_RATIO);
	private static final int TYPE_HEIGHT 	= (int)Math.round(SCREEN_HEIGHT*TYPE_RATIO);
	private static final int CONTENT_HEIGHT = (int)Math.round(SCREEN_HEIGHT*CONTENT_RATIO);
		private static final int NAME_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*NAME_RATIO);
		private static final int ASSEMBLY_HEIGHT 			= (int)Math.round(CONTENT_HEIGHT*ASSEMBLY_RATIO);
		private static final int GENOME_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*GENOME_RATIO);
		private static final int VAR_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*VAR_RATIO);
		private static final int LINE_HEIGHT 				= (int)Math.round(CONTENT_HEIGHT*LINE_RATIO);
		private static final int PROJECT_CHOOSER_HEIGHT 	= (int)Math.round(CONTENT_HEIGHT*PROJECT_CHOOSER_RATIO);
	private static final int CONFIRM_HEIGHT = (int)Math.round(SCREEN_HEIGHT*CONFIRM_RATIO);
	
	//Dimensions
	protected static final Dimension BANNER_DIM 	= new Dimension (SCREEN_WIDTH, BANNER_HEIGHT);
	protected static final Dimension TYPE_DIM 		= new Dimension (SCREEN_WIDTH, TYPE_HEIGHT);
	protected static final Dimension NEW_DIM 		= new Dimension (SCREEN_WIDTH, CONTENT_HEIGHT);
		protected static final Dimension NAME_DIM 				= new Dimension (SCREEN_WIDTH, NAME_HEIGHT);
		protected static final Dimension ASSEMBLY_DIM 			= new Dimension (SCREEN_WIDTH, ASSEMBLY_HEIGHT);
		protected static final Dimension GENOME_DIM 			= new Dimension (SCREEN_WIDTH, GENOME_HEIGHT);
		protected static final Dimension VAR_DIM 				= new Dimension (SCREEN_WIDTH, VAR_HEIGHT);
	protected static final Dimension LOAD_DIM 		= new Dimension (SCREEN_WIDTH, CONTENT_HEIGHT);
		protected static final Dimension LINE_DIM 				= new Dimension (SCREEN_WIDTH, LINE_HEIGHT);
		protected static final Dimension PROJECT_CHOOSER_DIM 	= new Dimension (SCREEN_WIDTH-70, PROJECT_CHOOSER_HEIGHT);
	protected static final Dimension CONFIRM_DIM 	= new Dimension (SCREEN_WIDTH, CONFIRM_HEIGHT);
	
	//Tool tip text
	protected static final String ADD_VAR_FILES 	= "Add var file(s)";
	protected static final String DEL_VAR_FILES 	= "Delete selection";
	protected static final String CONFIRM_FILES		= "Confirm selection";
	protected static final String CANCEL_FILES		= "Cancel modification";
	protected static final String SELECT_FILES 		= "Enable selection";
	protected static final String UNSELECT_FILES 	= "Disable selection";
	protected static final String MOVE_UP_FILES 	= "Move up selection";
	protected static final String MOVE_DOWN_FILES 	= "Move down selection";
	protected static final String SELECT_BASIC_CHR 	= "Select basics chromosome";
	
	//Colors use for development
	/*protected static final Color BANNER_COLOR = Color.yellow;
	protected static final Color TYPE_COLOR = Color.green;
	protected static final Color NEW_COLOR = Color.black;
		protected static final Color NAME_COLOR = Color.magenta;
		protected static final Color ASSEMBLY_COLOR = Color.cyan;
		protected static final Color GENOME_COLOR = Color.lightGray;
		protected static final Color VAR_COLOR = Color.darkGray;
		protected static final Color TABLE_PANEL_COLOR = Color.blue;
		protected static final Color TABLE_BUTTON_COLOR = Color.yellow;
	protected static final Color LOAD_COLOR = Color.red;
	protected static final Color CONFIRM_COLOR = Color.orange;*/
	
	//Real colors
	protected static final Color COLOR 			= Color.white;
	protected static final Color BANNER_COLOR 	= COLOR;
	protected static final Color TYPE_COLOR 	= COLOR;
	protected static final Color NEW_COLOR 		= COLOR;
		protected static final Color NAME_COLOR 			= COLOR;
		protected static final Color ASSEMBLY_COLOR 		= COLOR;
		protected static final Color GENOME_COLOR 			= COLOR;
		protected static final Color VAR_COLOR 				= COLOR;
		protected static final Color TABLE_PANEL_COLOR 		= COLOR;
		protected static final Color TABLE_BUTTON_COLOR 	= COLOR;
	protected static final Color LOAD_COLOR 	= COLOR;
	protected static final Color CONFIRM_COLOR 	= COLOR;
	
	private static final 	String 					CREATE_BUTTON = "Create";	// Text of the button if you choose a new project
	private static final 	String 					LOAD_BUTTON = "Load";		// Text of the button if you choose to load a project
	private static 			ProjectScreenManager 	instance = null;			// The instance of the class
	private 				BannerPanel 			bannerPanel;				// The banner
	private 				ProjectType 			projectType;				// The type of the project (new/load)
	private static 			NewProject 				newProject;					// Panel for a new project
	private static 			LoadProject 			loadProject;				// Panel for loading a project
	private 				ConfirmPanel 			confirmPanel;				// Panel to confirm the user choice
	private 				GridBagConstraints 		gbc;						// Constraints for the GriBagLayout
	private					CountDownLatch 			projectSignal;
	
	private	String		name;		// Name of the project
	private	String		clade;		// Name of the selected clade
	private	String		genome;		// Name of the selected genome
	private	String		assembly;	// Name of the selected assembly
	private List<File>	varFiles;	// Var files list
	
	
	/**
	 * Private constructor.
	 * Creates an instance of singleton {@link MainFrame}
	 * @throws HeadlessException
	 */
	private ProjectScreenManager() throws HeadlessException {
		super();
	}
	
	
	/**
	 * @return the instance of the singleton {@link ProjectScreenManager}.
	 */
	public static ProjectScreenManager getInstance () {
		if (instance == null) {
			instance = new ProjectScreenManager();
		}
		return instance;
	}
	
	/**
	 * Private constructor.
	 * Creates an instance of singleton {@link MainFrame}
	 * @throws HeadlessException
	 *//*
	private ProjectScreenManager(CountDownLatch projectSignal) throws HeadlessException {
		super();
		this.projectSignal = projectSignal;
	}*/
	
	
	/**
	 * @return the instance of the singleton {@link ProjectScreenManager}.
	 *//*
	public static ProjectScreenManager getInstance (CountDownLatch loginSignal) {
		if (instance == null) {
			instance = new ProjectScreenManager(loginSignal);
		}
		return instance;
	}*/
	
	
	/**
	 * Main method of the class.
	 * It initializes frame and panels.
	 */
	public void initScreen () {
		//Init panels
		bannerPanel = new BannerPanel();
		projectType = new ProjectType(this);
		newProject = new NewProject();
		loadProject = new LoadProject(ConfigurationManager.getInstance().getProjects());
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
		add(projectType, gbc);
		
		//newProject
		gbc.gridy = 2;
		add(newProject, gbc);
		
		//loadProject
		add(loadProject, gbc);
		
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
	 * This method show the {@link LoadProject} panel
	 */
	protected void toLoadScreenProject () {
		newProject.setVisible(false);
		loadProject.setVisible(true);
		confirmPanel.setConfirmButton(LOAD_BUTTON);
	}
	
	
	/**
	 * This method show the {@link NewProject} panel
	 */
	protected void toNewScreenProject () {
		loadProject.setVisible(false);
		newProject.setVisible(true);
		confirmPanel.setConfirmButton(CREATE_BUTTON);
	}

	
	/**
	 * This method gather new project information. 
	 */
	protected static void confirmCreate () {
		//Name
		getInstance().name = newProject.getProjectName();
		
		//Clade
		getInstance().clade = newProject.getCladeName();
		getInstance().genome = newProject.getGenomeName();
		getInstance().assembly = newProject.getAssemblyName();
		
		//Var files
		if (!newProject.isSimpleProject()) {
			getInstance().varFiles = newProject.getFiles();
		} else {
			getInstance().varFiles = null;
		}
		
		//Decrement countDown object
		Boolean valid = false;
		if (getInstance().name != null) {
			if (newProject.isSimpleProject()) {
				valid = true;
			} else {
				if (getInstance().varFiles.size() > 0 ) {
					valid = true;
				}
			}
		}
		if (valid) {
			getInstance().projectSignal.countDown();
		}
	}
	
	
	/**
	 * This method gather loading project information. 
	 */
	protected static void confirmLoading () {
		if (loadProject.getProject() != null) {
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
		return loadProject.getProject();
	}
	
	
	/**
	 * @return 	yes if the user wants to load an existing project
	 * 			no if the user creates a new project
	 */
	public boolean isLoadingEvent () {
		return loadProject.isVisible();
	}
	
	
	/**
	 * @return the selected chromosome list
	 */
	public Map<String, Chromosome> getNewChromosomeList() {
		return newProject.getNewChromosomeList();
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
	 * @return the varFiles
	 */
	public List<File> getVarFiles() {
		return varFiles;
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
	public static Dimension getVarDim() {
		return VAR_DIM;
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
		return ADD_VAR_FILES;
	}


	/**
	 * @return the delVarFiles
	 */
	public static String getDelVarFiles() {
		return DEL_VAR_FILES;
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
	public static Color getVarColor() {
		return VAR_COLOR;
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