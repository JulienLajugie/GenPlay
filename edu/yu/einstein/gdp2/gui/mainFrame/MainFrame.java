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
import yu.einstein.gdp2.gui.action.project.AboutAction;
import yu.einstein.gdp2.gui.action.project.ExitAction;
import yu.einstein.gdp2.gui.action.project.FullScreenAction;
import yu.einstein.gdp2.gui.action.project.HelpAction;
import yu.einstein.gdp2.gui.action.project.LoadProjectAction;
import yu.einstein.gdp2.gui.action.project.MoveLeftAction;
import yu.einstein.gdp2.gui.action.project.MoveRightAction;
import yu.einstein.gdp2.gui.action.project.OptionAction;
import yu.einstein.gdp2.gui.action.project.SaveProjectAction;
import yu.einstein.gdp2.gui.action.project.ZoomInAction;
import yu.einstein.gdp2.gui.action.project.ZoomOutAction;
import yu.einstein.gdp2.gui.controlPanel.ControlPanel;
import yu.einstein.gdp2.gui.dialog.optionDialog.OptionDialog;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowListener;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionEvent;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionListener;
import yu.einstein.gdp2.gui.popupMenu.MainMenu;
import yu.einstein.gdp2.gui.statusBar.StatusBar;
import yu.einstein.gdp2.gui.track.Ruler;
import yu.einstein.gdp2.gui.trackList.TrackList;

/**
 * Main Frame of the application.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class MainFrame extends JFrame implements PropertyChangeListener, GenomeWindowListener, TrackListActionListener, ActionListener {

	private static final long serialVersionUID = -4637394760647080396L; // generated ID

	private static final int VERSION_NUMBER = 96; // GenPlay version
	/**
	 * Title of the application
	 */
	public static final String APPLICATION_TITLE = " - GenPlay, Einstein Genome Analyser (v"+ VERSION_NUMBER + ") -";
	private static final String DEFAULT_PROJECT_NAME = "New Project";
	private final static String 		ICON_PATH = 
		"yu/einstein/gdp2/resource/icon.png";					// path of the icon of the application
	private static MainFrame			instance = null;		// instance of the singleton MainFrame
	private final Image					iconImage;				// icon of the application
	private final Ruler 				ruler;					// Ruler component
	private final TrackList 			trackList;				// TrackList component
	private final ControlPanel 			controlPanel;			// ControlPanel component
	private final StatusBar				statusBar;				// Statut bar component
	private final ConfigurationManager 	configurationManager;	// ConfigurationManager
	private final ChromosomeManager 	chromosomeManager;		// ChromosomeManager
	private final ZoomManager 			zoomManager;			// ZoomManager
	private Rectangle 					screenBounds;			// position and dimension of this frame



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
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainFrame.getInstance().setVisible(true);
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
		trackList.addTrackListActionListener(this);

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


	@Override
	public void actionEnds(TrackListActionEvent evt) {
		statusBar.actionEnds(evt);
	}


	/**
	 * Shows the main menu when the button in the ruler is clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		new MainMenu(getRootPane().getActionMap()).show(this, getMousePosition().x, getMousePosition().y);		
	}


	@Override
	public void actionStarts(TrackListActionEvent evt) {
		statusBar.actionStarts(evt);
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
					trackList.setScrollMode((Boolean)evt.getNewValue());
				} else if (evt.getSource() == trackList) {
					ruler.setScrollMode((Boolean)evt.getNewValue());
				}
			}
		}
	}


	/**
	 * Sets the action map of the main frame. This actions are associated with the main menu.
	 */
	private void setActionMap() {		
		getRootPane().getActionMap().put(AboutAction.ACTION_KEY, new AboutAction(getRootPane()));
		getRootPane().getActionMap().put(ExitAction.ACTION_KEY, new ExitAction(this));
		getRootPane().getActionMap().put(FullScreenAction.ACTION_KEY, new FullScreenAction(this));
		getRootPane().getActionMap().put(HelpAction.ACTION_KEY, new HelpAction(getRootPane()));
		getRootPane().getActionMap().put(LoadProjectAction.ACTION_KEY, new LoadProjectAction(trackList));
		getRootPane().getActionMap().put(OptionAction.ACTION_KEY, new OptionAction(this));
		getRootPane().getActionMap().put(SaveProjectAction.ACTION_KEY, new SaveProjectAction(trackList));
		getRootPane().getActionMap().put(MoveLeftAction.ACTION_KEY, new MoveLeftAction());
		getRootPane().getActionMap().put(MoveRightAction.ACTION_KEY, new MoveRightAction());		
		getRootPane().getActionMap().put(ZoomInAction.ACTION_KEY, new ZoomInAction());
		getRootPane().getActionMap().put(ZoomOutAction.ACTION_KEY, new ZoomOutAction());
	}


	/**
	 * Asks the user to confirm that he wants to close the application before exiting
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
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ExitAction.ACCELERATOR, ExitAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FullScreenAction.ACCELERATOR, FullScreenAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(HelpAction.ACCELERATOR, HelpAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(HelpAction.ACCELERATOR, HelpAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(MoveLeftAction.ACCELERATOR, MoveLeftAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(MoveRightAction.ACCELERATOR, MoveRightAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ZoomInAction.ACCELERATOR, ZoomInAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ZoomOutAction.ACCELERATOR, ZoomOutAction.ACTION_KEY);
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
		OptionDialog optionDialog = new OptionDialog(configurationManager);
		optionDialog.showConfigurationDialog(getRootPane());
		if (optionDialog.lookAndFeelChanged()) {
			setLookAndFeel();
		}
		if (optionDialog.trackHeightChanged()) {
			trackList.trackHeightChanged();
		}
		if (optionDialog.trackCountChanged()) {
			trackList.trackCountChanged();
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
