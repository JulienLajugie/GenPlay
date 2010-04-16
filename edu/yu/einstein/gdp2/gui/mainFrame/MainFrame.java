/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.mainFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
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
import yu.einstein.gdp2.gui.action.project.AboutAction;
import yu.einstein.gdp2.gui.action.project.ExitAction;
import yu.einstein.gdp2.gui.action.project.FullScreenAction;
import yu.einstein.gdp2.gui.action.project.HelpAction;
import yu.einstein.gdp2.gui.action.project.LoadProjectAction;
import yu.einstein.gdp2.gui.action.project.OptionAction;
import yu.einstein.gdp2.gui.action.project.SaveProjectAction;
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
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ConfigurationManager;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.ZoomManager;

/**
 * Main Frame of the application.
 * @author Julien Lajugie
 * @version 0.1
 */
public class MainFrame extends JFrame implements PropertyChangeListener, GenomeWindowListener, TrackListActionListener {


	/**
	 * Starts the application
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainFrame();
			}
		});
	}

	private static final int VERSION_NUMBER = 86; // GenPlay version
	
	/**
	 * Title of the application
	 */
	public static final String APPLICATION_TITLE = " - Einstein Browser: GenPlay (v"+ VERSION_NUMBER + ") -";
	

	
	private static final String DEFAULT_PROJECT_NAME = "New Project";
	private static final long serialVersionUID = -4637394760647080396L; // generated ID

	private final static String 		ICON_PATH = 
		"yu/einstein/gdp2/resource/icon.png";					// path of the icon of the application
	private final Ruler 				ruler;					// Ruler component
	private final TrackList 			trackList;				// TrackList component
	private final ControlPanel 			controlPanel;			// ControlPanel component
	private final StatusBar				statusBar;				// Statut bar component
	
	private final ConfigurationManager 	configurationManager;	// ConfigurationManager
	private final ChromosomeManager 	chromosomeManager;		// ChromosomeManager
	private final ZoomManager 			zoomManager;			// ZoomManager
	private Rectangle 					screenBounds;			// position and dimension of this frame


	/**
	 * Creates an instance of {@link MainFrame}
	 */
	public MainFrame() {
		super(DEFAULT_PROJECT_NAME + APPLICATION_TITLE, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
		ClassLoader cl = this.getClass().getClassLoader();
		setIconImage(Toolkit.getDefaultToolkit().getImage(cl.getResource(ICON_PATH)));
		configurationManager = ConfigurationManager.getInstance();
		chromosomeManager = ChromosomeManager.getInstance();
		zoomManager = ZoomManager.getInstance();
		try {
			configurationManager.loadConfigurationFile();
		} catch (Exception e) {
			//ExceptionManager.handleException(getRootPane(), e, "Configuration file not found.");
		} 
		try {
			if (configurationManager.getChromosomeFile() != "") {
				chromosomeManager.loadConfigurationFile(new File(configurationManager.getChromosomeFile()));
			}
		} catch (IOException e) {
			ExceptionManager.handleException(getRootPane(), e, "Chromosome file not found.");
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Chromosome file corrupted");
		}
		try {
			if (configurationManager.getZoomFile() != "") {
				zoomManager.loadConfigurationFile(new File(configurationManager.getZoomFile()));
			}
		} catch (IOException e) {
			ExceptionManager.handleException(getRootPane(), e, "Zoom file not found.");
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Zoom file corrupted");
		}

		Chromosome chromosome = chromosomeManager.getChromosome((short)0);
		GenomeWindow genomeWindow = new GenomeWindow(chromosome, 0, chromosome.getLength());
		ruler = new Ruler(zoomManager, genomeWindow);
		ruler.addPropertyChangeListener(this);
		ruler.addGenomeWindowListener(this);
		
		trackList = new TrackList(configurationManager, chromosomeManager, zoomManager, genomeWindow);
		trackList.addPropertyChangeListener(this);
		trackList.addGenomeWindowListener(this);
		trackList.addTrackListActionListener(this);
		
		controlPanel = new ControlPanel(chromosomeManager, zoomManager, genomeWindow);
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
		getRootPane().getActionMap().put(AboutAction.ACTION_KEY, new AboutAction(getRootPane()));
		getRootPane().getActionMap().put(ExitAction.ACTION_KEY, new ExitAction(this));
		getRootPane().getActionMap().put(FullScreenAction.ACTION_KEY, new FullScreenAction(this));
		getRootPane().getActionMap().put(HelpAction.ACTION_KEY, new HelpAction(getRootPane()));
		getRootPane().getActionMap().put(LoadProjectAction.ACTION_KEY, new LoadProjectAction(trackList));
		getRootPane().getActionMap().put(OptionAction.ACTION_KEY, new OptionAction(this));
		getRootPane().getActionMap().put(SaveProjectAction.ACTION_KEY, new SaveProjectAction(trackList));
		
		// add shortcuts
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ExitAction.ACCELERATOR, ExitAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(FullScreenAction.ACCELERATOR, FullScreenAction.ACTION_KEY);
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(HelpAction.ACCELERATOR, HelpAction.ACTION_KEY);

		// change the colors for nimbus look and feel
		UIManager.put("nimbusBase", new Color(41, 96, 150));
		UIManager.put("nimbusBlueGrey", new Color(187, 196, 209));
		UIManager.put("control", new Color(228, 236, 247));
		
		// set the look and feel
		setLookAndFeel();
		
		// ask if the user want to save the project before closing the application
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int res = JOptionPane.showConfirmDialog(getRootPane(), "Do you want to save the project before exiting?", APPLICATION_TITLE, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null);
				if (res == JOptionPane.YES_OPTION) {
					getRootPane().getActionMap().get(SaveProjectAction.ACTION_KEY).actionPerformed(null);
					dispose();
					System.exit(0);
				} else if (res == JOptionPane.NO_OPTION) {
					dispose();
					System.exit(0);
				}
			}
		});
		
		setMinimumSize(new Dimension(200, 150));
		setPreferredSize(new Dimension(800, 600));
		pack();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationByPlatform(true);
		setVisible(true);
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
			} else if (evt.getPropertyName() == "rulerButtonClicked") {
				new MainMenu(getRootPane().getActionMap()).show(this, getMousePosition().x, getMousePosition().y);
			}
		}
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


	@Override
	public void actionEnds(TrackListActionEvent evt) {
		statusBar.actionEnds(evt);
	}


	@Override
	public void actionStarts(TrackListActionEvent evt) {
		statusBar.actionStarts(evt);
	}
}
