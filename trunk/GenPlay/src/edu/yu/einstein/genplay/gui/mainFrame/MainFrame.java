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
package edu.yu.einstein.genplay.gui.mainFrame;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.apple.eawt.Application;
import com.apple.eawt.FullScreenUtilities;

import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.OSIntegration.OSXHandler;
import edu.yu.einstein.genplay.gui.action.multiGenome.properties.MGARefresh;
import edu.yu.einstein.genplay.gui.action.project.PAAbout;
import edu.yu.einstein.genplay.gui.action.project.PABookmarkCurrentPosition;
import edu.yu.einstein.genplay.gui.action.project.PACheckForUpdates;
import edu.yu.einstein.genplay.gui.action.project.PACopyCurrentPosition;
import edu.yu.einstein.genplay.gui.action.project.PAExit;
import edu.yu.einstein.genplay.gui.action.project.PAFullScreen;
import edu.yu.einstein.genplay.gui.action.project.PAHelp;
import edu.yu.einstein.genplay.gui.action.project.PALoadProject;
import edu.yu.einstein.genplay.gui.action.project.PAMoveFarLeft;
import edu.yu.einstein.genplay.gui.action.project.PAMoveFarRight;
import edu.yu.einstein.genplay.gui.action.project.PAMoveLeft;
import edu.yu.einstein.genplay.gui.action.project.PAMoveRight;
import edu.yu.einstein.genplay.gui.action.project.PANewProject;
import edu.yu.einstein.genplay.gui.action.project.PAOption;
import edu.yu.einstein.genplay.gui.action.project.PARNAPosToDNAPos;
import edu.yu.einstein.genplay.gui.action.project.PASaveProject;
import edu.yu.einstein.genplay.gui.action.project.PASaveProjectAs;
import edu.yu.einstein.genplay.gui.action.project.PAShowErrorReport;
import edu.yu.einstein.genplay.gui.action.project.PAShowWarningReport;
import edu.yu.einstein.genplay.gui.action.project.PASortFile;
import edu.yu.einstein.genplay.gui.action.project.PAZoomIn;
import edu.yu.einstein.genplay.gui.action.project.PAZoomOut;
import edu.yu.einstein.genplay.gui.action.track.TAAddLayer;
import edu.yu.einstein.genplay.gui.action.track.TAAddLayerFromDAS;
import edu.yu.einstein.genplay.gui.action.track.TAAddVariantLayer;
import edu.yu.einstein.genplay.gui.action.track.TACopy;
import edu.yu.einstein.genplay.gui.action.track.TACut;
import edu.yu.einstein.genplay.gui.action.track.TADelete;
import edu.yu.einstein.genplay.gui.action.track.TAInsert;
import edu.yu.einstein.genplay.gui.action.track.TAPasteOrDrop;
import edu.yu.einstein.genplay.gui.action.track.TASaveAsImage;
import edu.yu.einstein.genplay.gui.action.track.TASaveTrack;
import edu.yu.einstein.genplay.gui.action.track.TATrackSettings;
import edu.yu.einstein.genplay.gui.controlPanel.ControlPanel;
import edu.yu.einstein.genplay.gui.dialog.optionDialog.OptionDialog;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.menu.MainMenu;
import edu.yu.einstein.genplay.gui.menu.MenuBar;
import edu.yu.einstein.genplay.gui.statusBar.StatusBar;
import edu.yu.einstein.genplay.gui.track.ruler.Ruler;
import edu.yu.einstein.genplay.gui.trackList.TrackListModel;
import edu.yu.einstein.genplay.gui.trackList.TrackListPanel;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.LookAndFeels;
import edu.yu.einstein.genplay.util.Utils;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Main Frame of the application.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public final class MainFrame extends JFrame implements GenomeWindowListener, ActionListener {

	/** Generated ID */
	private static final long serialVersionUID = -4637394760647080396L;

	/** Major version of GenPlay */
	private static final int VERSION_MAJOR = 1;

	/** Minor version of GenPlay */
	private static final int VERSION_MINOR = 0;

	/** Build version of GenPlay */
	private static final int VERSION_BUILD = 3;

	/** GenPlay version */
	public static final String GENPLAY_VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_BUILD;

	/** Title of the application */
	public static final String APPLICATION_TITLE = "GenPlay, Einstein Genome Analyzer (v" + GENPLAY_VERSION + ")";

	/** Default size of the application */
	private final static Dimension WINDOW_DEFAULT_SIZE = new Dimension(800, 600);

	/** Minimum size of the application */
	private final static Dimension WINDOW_MINIMUM_SIZE = new Dimension(200, 150);

	/** Instance of the singleton MainFrame */
	private static 	MainFrame instance = null;


	/**
	 * @return the instance of the singleton MainFrame
	 */
	public static MainFrame getInstance() {
		if (instance == null) {
			synchronized (MainFrame.class) {
				if (instance == null) {
					instance = new MainFrame();
				}
			}
		}
		return instance;
	}


	/**
	 * @return true if the main frame has been initialized, false otherwise
	 */
	public static boolean isInitialized() {
		return instance != null;
	}


	/**
	 * Reinit the {@link ProjectChromosomes} and the chromosome panel of the {@link ControlPanel} if needed
	 */
	public static void reinit() {
		// if instance is null the mainframe has never been initialized
		// so there is no need to do a reinit
		if (instance != null) {
			ProjectManager.getInstance().updateChromosomeList();
			instance.getControlPanel().reinitChromosomePanel();
			// creates a new model and register the tracks to the project window manager
			TrackListModel trackListModel = new TrackListModel();
			instance.getTrackListPanel().setModel(trackListModel);
			ProjectManager.getInstance().getProjectWindow().removeAllListeners();
			instance.setTitle();
			instance.getStatusBar().reinit();
		}
	}


	private final 	Ruler 				ruler; 				// Ruler component
	private final 	TrackListPanel 		trackListPanel; 	// TrackList component
	private final 	ControlPanel		controlPanel; 		// ControlPanel component
	private final 	StatusBar 			statusBar; 			// Status bar component
	private 		Rectangle			screenBounds; 		// position and dimension of this frame
	private 		boolean 			isLocked;			// true if the main frame is locked

	/**
	 * Private constructor. Creates an instance of singleton {@link MainFrame}
	 */
	private MainFrame() {
		super(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		setIconImages(Images.getApplicationImages());

		setTitle();

		ruler = new Ruler();
		ruler.getOptionButton().addActionListener(this);

		TrackListModel trackListModel = new TrackListModel();
		trackListPanel = new TrackListPanel(trackListModel);

		controlPanel = new ControlPanel();

		statusBar = new StatusBar();

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(ruler.getRulerPanel(), gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(trackListPanel, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(controlPanel, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.gridy = 3;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(statusBar, gbc);

		// register to the genome window manager so it can be notified when the project window changes
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		projectWindow.addGenomeWindowListener(this);

		// create actions
		setActionMap();
		// add shortcuts
		setInputMap();

		// add menu bar
		setJMenuBar(new MenuBar(getRootPane().getActionMap()));

		if (Utils.isMacOS()) {
			// add a menu bar for OSX
			Application macApplication = Application.getApplication();
			try {
				macApplication.setDefaultMenuBar(getJMenuBar());
			} catch (IllegalStateException e) {}// case where the menu bar is not suported by the look and feel
			macApplication.setAboutHandler(OSXHandler.getInstance());
			macApplication.setPreferencesHandler(OSXHandler.getInstance());
			macApplication.setQuitHandler(OSXHandler.getInstance());
			FullScreenUtilities.setWindowCanFullScreen(this, true);
		}

		showMenuBar();

		// customise the look and feel
		LookAndFeels.customizeLookAndFeel();
		// set the look and feel
		LookAndFeels.setLookAndFeel(getRootPane());
		if ((trackListPanel != null) && (trackListPanel.getTrackMenu() != null)) {
			LookAndFeels.setLookAndFeel(getTrackListPanel().getTrackMenu());
		}
		// set the application behavior when closed
		setDefaultCloseOperation();

		getContentPane().setBackground(Colors.MAIN_GUI_BACKGROUND);
		setMinimumSize(WINDOW_MINIMUM_SIZE);
		setPreferredSize(WINDOW_DEFAULT_SIZE);
		pack();
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setLocationByPlatform(true);

		if (RecordingManager.getInstance().getProjectRecording().isLoadingEvent()) {
			PALoadProject loader = new PALoadProject();
			loader.setSkipFileSelection(true);
			loader.actionPerformed(null);
		}

		// set the jump button as the default button pressed when enter is pressed
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				getRootPane().setDefaultButton(controlPanel.getJumpButton());
				super.windowActivated(e);
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				if ((trackListPanel != null) && (trackListPanel.getTrackMenu() != null) && trackListPanel.getTrackMenu().isVisible()) {
					trackListPanel.getTrackMenu().setVisible(false);
				}
				super.windowDeactivated(e);
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				if ((trackListPanel != null) && (trackListPanel.getTrackMenu() != null) && trackListPanel.getTrackMenu().isVisible()) {
					trackListPanel.getTrackMenu().setVisible(false);
				}
				super.windowLostFocus(e);
			}
		});
	}


	/**
	 * Shows the main menu when the button in the ruler is clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new MainMenu(getRootPane().getActionMap()).show(this, getMousePosition().x, getMousePosition().y);
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		// if the chromosome changed we reinitialize the multigenome data
		if (evt.chromosomeChanged() && ProjectManager.getInstance().isMultiGenomeProject()) {
			ProjectManager.getInstance().getMultiGenomeProject().getFileContentManager().updateCurrentVariants();
			MGARefresh tracksUpdate = new MGARefresh();
			tracksUpdate.actionPerformed(null);
		}
	}


	/**
	 * @return the controlPanel
	 */
	public final ControlPanel getControlPanel() {
		return controlPanel;
	}


	/**
	 * @return the ruler
	 */
	public final Ruler getRuler() {
		return ruler;
	}


	/**
	 * @return the statusBar
	 */
	public final StatusBar getStatusBar() {
		return statusBar;
	}


	/**
	 * @return the track list panel
	 */
	public final TrackListPanel getTrackListPanel(){
		return trackListPanel;
	}


	/**
	 * Initializes the status bar when starting a new project
	 */
	public void initStatusBarForFirstUse () {
		statusBar.initDescriptionForFirstUse();
	}


	/**
	 * @return true if the main frame is locked
	 */
	public boolean isLocked() {
		synchronized (MainFrame.class) {
			return isLocked;
		}
	}


	/**
	 * Locks the main frame:
	 * - the action button (top left button)
	 * - the track handles
	 * - the chromosome selection box
	 * - the chromosome position text field
	 */
	public void lock() {
		synchronized (MainFrame.class) {
			ruler.lock();
			trackListPanel.lockTrackHandles();
			controlPanel.setEnabled(false);
			isLocked = true;
		}
	}


	/**
	 * Sets the action map of the main frame. This actions are associated with
	 * the main menu.
	 */
	private void setActionMap() {
		// Add project actions to action map
		getRootPane().getActionMap().put(PASortFile.ACTION_KEY, new PASortFile());
		getRootPane().getActionMap().put(PACheckForUpdates.ACTION_KEY, new PACheckForUpdates());
		getRootPane().getActionMap().put(PAAbout.ACTION_KEY, new PAAbout(getRootPane()));
		getRootPane().getActionMap().put(PAExit.ACTION_KEY, new PAExit(this));
		getRootPane().getActionMap().put(PAFullScreen.ACTION_KEY, new PAFullScreen(this));
		getRootPane().getActionMap().put(PAHelp.ACTION_KEY, new PAHelp(getRootPane()));
		getRootPane().getActionMap().put(PALoadProject.ACTION_KEY, new PALoadProject());
		getRootPane().getActionMap().put(PANewProject.ACTION_KEY, new PANewProject());
		getRootPane().getActionMap().put(PAOption.ACTION_KEY, new PAOption(this));
		getRootPane().getActionMap().put(PASaveProject.ACTION_KEY, new PASaveProject());
		getRootPane().getActionMap().put(PASaveProjectAs.ACTION_KEY, new PASaveProjectAs());
		getRootPane().getActionMap().put(PAMoveLeft.ACTION_KEY, new PAMoveLeft());
		getRootPane().getActionMap().put(PAMoveFarLeft.ACTION_KEY, new PAMoveFarLeft());
		getRootPane().getActionMap().put(PAMoveRight.ACTION_KEY, new PAMoveRight());
		getRootPane().getActionMap().put(PAMoveFarRight.ACTION_KEY, new PAMoveFarRight());
		getRootPane().getActionMap().put(PAZoomIn.ACTION_KEY, new PAZoomIn());
		getRootPane().getActionMap().put(PAZoomOut.ACTION_KEY, new PAZoomOut());
		getRootPane().getActionMap().put(PARNAPosToDNAPos.ACTION_KEY, new PARNAPosToDNAPos(this));
		getRootPane().getActionMap().put(PAShowWarningReport.ACTION_KEY, new PAShowWarningReport());
		getRootPane().getActionMap().put(PAShowErrorReport.ACTION_KEY, new PAShowErrorReport());
		getRootPane().getActionMap().put(PACopyCurrentPosition.ACTION_KEY, new PACopyCurrentPosition());
		getRootPane().getActionMap().put(PABookmarkCurrentPosition.ACTION_KEY, new PACopyCurrentPosition());
		// Add track actions to action map
		getRootPane().getActionMap().put(TAAddLayer.ACTION_KEY, new TAAddLayer());
		getRootPane().getActionMap().put(TAAddVariantLayer.ACTION_KEY, new TAAddVariantLayer());
		getRootPane().getActionMap().put(TAAddLayerFromDAS.ACTION_KEY, new TAAddLayerFromDAS());
		getRootPane().getActionMap().put(TACopy.ACTION_KEY, new TACopy());
		getRootPane().getActionMap().put(TACut.ACTION_KEY, new TACut());
		getRootPane().getActionMap().put(TAPasteOrDrop.ACTION_KEY, new TAPasteOrDrop());
		getRootPane().getActionMap().put(TAInsert.ACTION_KEY, new TAInsert());
		getRootPane().getActionMap().put(TADelete.ACTION_KEY, new TADelete());
		getRootPane().getActionMap().put(TASaveTrack.ACTION_KEY, new TASaveTrack());
		getRootPane().getActionMap().put(TASaveAsImage.ACTION_KEY, new TASaveAsImage());
		getRootPane().getActionMap().put(TATrackSettings.ACTION_KEY, new TATrackSettings());
	}


	/**
	 * Asks the user to confirm that he wants to close the application before
	 * exiting
	 */
	private void setDefaultCloseOperation() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int res = JOptionPane.showConfirmDialog(getRootPane(), "Exit GenPlay?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
				if (res == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});
	}


	/**
	 * Sets the input map. This map contain the short cuts of the applications.
	 */
	private void setInputMap() {
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PASortFile.ACCELERATOR,PASortFile.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PACheckForUpdates.ACCELERATOR,PACheckForUpdates.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAExit.ACCELERATOR, PAExit.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAFullScreen.ACCELERATOR, PAFullScreen.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAHelp.ACCELERATOR, PAHelp.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PASaveProject.ACCELERATOR, PASaveProject.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PASaveProjectAs.ACCELERATOR, PASaveProjectAs.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PALoadProject.ACCELERATOR, PALoadProject.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PANewProject.ACCELERATOR, PANewProject.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveLeft.ACCELERATOR, PAMoveLeft.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveFarLeft.ACCELERATOR, PAMoveFarLeft.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveRight.ACCELERATOR, PAMoveRight.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveFarRight.ACCELERATOR, PAMoveFarRight.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAZoomIn.ACCELERATOR, PAZoomIn.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAZoomOut.ACCELERATOR, PAZoomOut.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PACopyCurrentPosition.ACCELERATOR, PACopyCurrentPosition.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PABookmarkCurrentPosition.ACCELERATOR, PABookmarkCurrentPosition.ACTION_KEY);
	}


	/**
	 * Set the current selected genome as well as repaint the frame
	 * @param genomeName the new selected genome name to use for the coordinate system
	 */
	public void setNewGenomeCoordinate (String genomeName) {
		MGDisplaySettings.SELECTED_GENOME = genomeName;
		controlPanel.setSelectedGenomeName(genomeName);
		repaint();
	}


	/**
	 * Sets the main frame title.
	 * Application title - Project name - Genome name - Assembly name.
	 */
	public void setTitle () {
		setTitle(	MainFrame.APPLICATION_TITLE
				+ " - " +
				ProjectManager.getInstance().getProjectName()
				+ " - (" +
				ProjectManager.getInstance().getGenomeName()
				+ ", " +
				ProjectManager.getInstance().getAssembly().getName()
				+ ")");
	}


	/**
	 * Sets the main menu bar
	 */
	private void showMenuBar() {
		ConfigurationManager cm = ConfigurationManager.getInstance();
		if (cm.isMenuBarShown()) {
			getJMenuBar().setVisible(true);
		} else {
			getJMenuBar().setVisible(false);
		}
		validate();
	}


	/**
	 * Shows the option screen
	 */
	public void showOption() {
		OptionDialog optionDialog = new OptionDialog();
		if (optionDialog.showConfigurationDialog(getRootPane()) == OptionDialog.APPROVE_OPTION) {
			if (optionDialog.lookAndFeelChanged()) {
				LookAndFeels.setLookAndFeel(getRootPane());
				LookAndFeels.setLookAndFeel(getTrackListPanel().getTrackMenu());
			}
			if (optionDialog.showMenuBarChanged()) {
				showMenuBar();
			}
			if (optionDialog.trackHeightChanged()) {
				trackListPanel.trackHeightChanged();
			}
			if (optionDialog.trackCountChanged()) {
				trackListPanel.trackCountChanged();
			}
			if (optionDialog.undoCountChanged()) {
				trackListPanel.undoCountChanged();
			}
			if (optionDialog.resetTrackChanged()) {
				trackListPanel.resetLayerChanged();
			}
			if (optionDialog.legendChanged()) {
				trackListPanel.legendChanged();
			}
		}
		optionDialog.dispose();
	}


	/**
	 * Toggles the full screen mode
	 */
	public void toggleFullScreenMode() {
		if (Utils.isMacOS()) {
			// full screen for integration in OSX
			Application.getApplication().requestToggleFullScreen(this);
		} else {
			// full screen on linux and windows
			if (!isUndecorated()) {
				setVisible(false);
				dispose();
				setUndecorated(true);
				controlPanel.setVisible(false);
				statusBar.setVisible(false);
				screenBounds = getBounds();
				setExtendedState(Frame.MAXIMIZED_BOTH);
				setVisible(true);
			} else {
				setVisible(false);
				dispose();
				setUndecorated(false);
				controlPanel.setVisible(true);
				statusBar.setVisible(true);
				setVisible(true);
				setBounds(screenBounds);
			}
		}
	}


	/**
	 * Unlocks the main frame
	 */
	public void unlock() {
		synchronized (MainFrame.class) {
			ruler.unlock();
			trackListPanel.unlockTrackHandles();
			controlPanel.setEnabled(true);
			isLocked = false;
		}
	}
}
