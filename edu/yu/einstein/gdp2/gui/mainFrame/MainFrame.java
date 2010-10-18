/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.mainFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.core.manager.ZoomManager;
import yu.einstein.gdp2.gui.action.project.PAAbout;
import yu.einstein.gdp2.gui.action.project.PAExit;
import yu.einstein.gdp2.gui.action.project.PAFullScreen;
import yu.einstein.gdp2.gui.action.project.PAHelp;
import yu.einstein.gdp2.gui.action.project.PALoadProject;
import yu.einstein.gdp2.gui.action.project.PAMoveLeft;
import yu.einstein.gdp2.gui.action.project.PAMoveRight;
import yu.einstein.gdp2.gui.action.project.PAOption;
import yu.einstein.gdp2.gui.action.project.PARNAPosToDNAPos;
import yu.einstein.gdp2.gui.action.project.PASaveProject;
import yu.einstein.gdp2.gui.action.project.PAZoomIn;
import yu.einstein.gdp2.gui.action.project.PAZoomOut;
import yu.einstein.gdp2.gui.controlPanel.ControlPanel;
import yu.einstein.gdp2.gui.dialog.optionDialog.OptionDialog;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowListener;
import yu.einstein.gdp2.gui.popupMenu.MainMenu;
import yu.einstein.gdp2.gui.statusBar.StatusBar;
import yu.einstein.gdp2.gui.track.Ruler;
import yu.einstein.gdp2.gui.trackList.TrackList;

/**
 * Main Frame of the application.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class MainFrame extends JFrame implements PropertyChangeListener, GenomeWindowListener, ActionListener {

	private static final long serialVersionUID = -4637394760647080396L; // generated ID
	private static final int VERSION_NUMBER = 274; 						// GenPlay version
	/**
	 * Title of the application
	 */
	public static final String APPLICATION_TITLE = " - GenPlay, Einstein Genome Analyzer (v" + VERSION_NUMBER + ") -";
	private static final String DEFAULT_PROJECT_NAME = "New Project";
	private final static String ICON_PATH = "yu/einstein/gdp2/resource/icon.png"; // path of the icon of the application
	
	private static MainFrame 			instance = null; 		// instance of the singleton MainFrame
	private final Image 				iconImage; 				// icon of the application
	private final Ruler 				ruler; 					// Ruler component
	private final TrackList 			trackList; 				// TrackList component
	private final ControlPanel 			controlPanel; 			// ControlPanel component
	private final StatusBar 			statusBar; 				// Statut bar component
	private final ConfigurationManager 	configurationManager; 	// ConfigurationManager
	private final ChromosomeManager 	chromosomeManager; 		// ChromosomeManager
	private final ZoomManager 			zoomManager; 			// ZoomManager
	private Rectangle 					screenBounds; 			// position and dimension of this frame

	
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
	 * Starts the application
	 * @param args
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame mainFrame = MainFrame.getInstance();
				mainFrame.setVisible(true);
				if (args.length == 1) {
					try {
						mainFrame.getTrackList().loadProject(new File(args[0]));
						// unlock the tracks
						mainFrame.getTrackList().actionEnds();
					} catch (Exception e) {
						ExceptionManager.handleException(mainFrame.getRootPane(), e, "Error while loading the project.");
					}
				}
			}
		});
	}

	
	/**
	 * Private constructor. Creates an instance of singleton {@link MainFrame}
	 */
	private MainFrame() {
		super(DEFAULT_PROJECT_NAME + APPLICATION_TITLE, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		ClassLoader cl = this.getClass().getClassLoader();
		iconImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(ICON_PATH));
		setIconImage(iconImage);
		configurationManager = ConfigurationManager.getInstance();
		chromosomeManager = ChromosomeManager.getInstance();
		zoomManager = ZoomManager.getInstance();
		// load the managers from the configuration files
		loadManagers();

		Chromosome chromosome = chromosomeManager.get(0);
		GenomeWindow genomeWindow = new GenomeWindow(chromosome, 0, chromosome.getLength());
		ruler = new Ruler(genomeWindow);
		ruler.getOptionButton().addActionListener(this);
		ruler.addGenomeWindowListener(this);

		trackList = new TrackList(genomeWindow);
		trackList.addPropertyChangeListener(this);
		trackList.addGenomeWindowListener(this);

		controlPanel = new ControlPanel(genomeWindow);
		controlPanel.addGenomeWindowListener(this);

		statusBar = new StatusBar();

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(ruler, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(trackList, gbc);

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

		// create actions
		setActionMap();
		// add shortcuts
		setInputMap();
		// customise the look and feel
		customizeLookAndFeel();
		// set the look and feel
		setLookAndFeel();
		// set the application behavior when closed
		setDefaultCloseOperation();

		setMinimumSize(new Dimension(200, 150));
		setPreferredSize(new Dimension(800, 600));
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationByPlatform(true);
	}

	
	/**
	 * Shows the main menu when the button in the ruler is clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new MainMenu(getRootPane().getActionMap()).show(this, getMousePosition().x, getMousePosition().y);
	}

	
	/**
	 * Customizes the look and feel
	 */
	private void customizeLookAndFeel() {
		// change the colors for nimbus look and feel
		UIManager.put("nimbusBase", new Color(41, 96, 150));
		UIManager.put("nimbusBlueGrey", new Color(187, 196, 209));
		UIManager.put("control", new Color(228, 236, 247));
	}

	
	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		if (evt.getSource() != ruler) {
			ruler.setGenomeWindow(evt.getNewWindow());
		}
		if (evt.getSource() != trackList) {
			trackList.setGenomeWindow(evt.getNewWindow());
		}
		if (evt.getSource() != controlPanel) {
			controlPanel.setGenomeWindow(evt.getNewWindow());
		}
	}

	
	/**
	 * @return the controlPanel
	 */
	public final ControlPanel getControlPanel() {
		return controlPanel;
	}

	
	/**
	 * @return the icon of the application
	 */
	@Override
	public Image getIconImage() {
		return iconImage;
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
	 * @return the trackList
	 */
	public final TrackList getTrackList() {
		return trackList;
	}

	
	/**
	 * Loads the managers with the configuration files
	 */
	private void loadManagers() {
		// load configuration manager
		try {
			configurationManager.loadConfigurationFile();
		} catch (Exception e) {
			// do nothing if the configuration file is not found
		}
		// load chromosome manager
		try {
			if (configurationManager.getChromosomeFile() != "") {
				chromosomeManager.loadConfigurationFile(new File(configurationManager.getChromosomeFile()));
			}
		} catch (IOException e) {
			ExceptionManager.handleException(getRootPane(), e, "Chromosome file not found.");
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Chromosome file corrupted");
		}
		// load the zoom manager
		try {
			if (configurationManager.getZoomFile() != "") {
				zoomManager.loadConfigurationFile(new File(configurationManager.getZoomFile()));
			}
		} catch (IOException e) {
			ExceptionManager.handleException(getRootPane(), e, "Zoom file not found.");
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Zoom file corrupted");
		}
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (isEnabled()) {
			if (evt.getPropertyName() == "scrollMode") {
				if (evt.getSource() == ruler) {
					trackList.setScrollMode((Boolean) evt.getNewValue());
				} else if (evt.getSource() == trackList) {
					ruler.setScrollMode((Boolean) evt.getNewValue());
				}
			}
		}
	}
	

	/**
	 * Sets the action map of the main frame. This actions are associated with
	 * the main menu.
	 */
	private void setActionMap() {
		getRootPane().getActionMap().put(PAAbout.ACTION_KEY, new PAAbout(getRootPane()));
		getRootPane().getActionMap().put(PAExit.ACTION_KEY, new PAExit(this));
		getRootPane().getActionMap().put(PAFullScreen.ACTION_KEY, new PAFullScreen(this));
		getRootPane().getActionMap().put(PAHelp.ACTION_KEY, new PAHelp(getRootPane()));
		getRootPane().getActionMap().put(PALoadProject.ACTION_KEY, new PALoadProject(trackList));
		getRootPane().getActionMap().put(PAOption.ACTION_KEY, new PAOption(this));
		getRootPane().getActionMap().put(PASaveProject.ACTION_KEY, new PASaveProject(trackList));
		getRootPane().getActionMap().put(PAMoveLeft.ACTION_KEY, new PAMoveLeft());
		getRootPane().getActionMap().put(PAMoveRight.ACTION_KEY, new PAMoveRight());
		getRootPane().getActionMap().put(PAZoomIn.ACTION_KEY, new PAZoomIn());
		getRootPane().getActionMap().put(PAZoomOut.ACTION_KEY, new PAZoomOut());
		getRootPane().getActionMap().put(PARNAPosToDNAPos.ACTION_KEY, new PARNAPosToDNAPos(this));
	}
	

	/**
	 * Asks the user to confirm that he wants to close the application before
	 * exiting
	 */
	private void setDefaultCloseOperation() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAExit.ACCELERATOR, PAExit.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAFullScreen.ACCELERATOR, PAFullScreen.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAHelp.ACCELERATOR, PAHelp.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAHelp.ACCELERATOR, PAHelp.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveLeft.ACCELERATOR, PAMoveLeft.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAMoveRight.ACCELERATOR, PAMoveRight.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAZoomIn.ACCELERATOR, PAZoomIn.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(PAZoomOut.ACCELERATOR, PAZoomOut.ACTION_KEY);
	}

	
	/**
	 * Changes the look and feel of the application
	 */
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(configurationManager.getLookAndFeel());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while loading the look and feel specified in the config file");
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception e1) {
				ExceptionManager.handleException(getRootPane(), e1, "Error while loading the default look and feel");
				e1.printStackTrace();
			}
		}
	}
	

	/**
	 * Shows the option screen
	 */
	public void showOption() {
		OptionDialog optionDialog = new OptionDialog();
		if (optionDialog.showConfigurationDialog(getRootPane()) == OptionDialog.APPROVE_OPTION) {
			if (optionDialog.lookAndFeelChanged()) {
				setLookAndFeel();
			}
			if (optionDialog.trackHeightChanged()) {
				trackList.trackHeightChanged();
			}
			if (optionDialog.trackCountChanged()) {
				trackList.trackCountChanged();
			}
			if (optionDialog.undoCountChanged()) {
				trackList.undoCountChanged();
			}
		}
		optionDialog.dispose();
	}

	
	/**
	 * Toggles the full screen mode
	 */
	public void toggleFullScreenMode() {
		if (!isUndecorated()) {
			setVisible(false);
			dispose();
			setUndecorated(true);
			controlPanel.setVisible(false);
			statusBar.setVisible(false);
			screenBounds = getBounds();
			setExtendedState(JFrame.MAXIMIZED_BOTH);
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
