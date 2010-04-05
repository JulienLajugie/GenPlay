/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.optionDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import yu.einstein.gdp2.util.ConfigurationManager;


/**
 * Dialog allowing to change the configuration of the program.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class OptionDialog extends JDialog implements TreeSelectionListener, PropertyChangeListener {

	private static final long 		serialVersionUID = 4050757943368845382L; // Generated serial number

	private static final Dimension 	
	OPTION_DIALOG_DIMENSION = new Dimension(600, 400); 	// dimension of this window
	private final ConfigurationManager 	cm;				// A ConfigurationManager
	private final JTree 		jt;					// Tree
	private final JScrollPane	jspTreeView; 		// Scroll pane containing the tree
	private final JPanel 		jpOption;			// Panel containing the different panel of configuration
	private final JButton		jbOk;				// Button OK
	private final JButton		jbCancel;			// Button cancel
	private final JSplitPane	jspDivider;			// Divider between the tree and the panel
	private final String 		zoomFile;			// zoom config file
	private final String 		chromosomeFile;		// chromosome config file
	private final String 		logFile;			// log file
	private final String 		defaultDirectory;	// default directory
	private final String 		lookAndFeel;		// look and feel
	private final int 			trackCount;			// track count
	private final int 			trackHeight;		// track height
	private int					approved = CANCEL_OPTION;	// Equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not	
	
	
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	
	
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;
	
	
	/**
	 * Creates an instance of {@link OptionDialog}
	 * @param configurationManager a {@link ConfigurationManager}
	 */
	public OptionDialog(ConfigurationManager configurationManager) {
		super();
		cm = configurationManager;		
		zoomFile = cm.getZoomFile();
		chromosomeFile = cm.getChromosomeFile();
		logFile = cm.getLogFile();
		defaultDirectory = cm.getDefaultDirectory();
		lookAndFeel = cm.getLogFile();
		trackCount = cm.getTrackCount();
		trackHeight = cm.getTrackHeight();
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Options");;
		createNodes(top);
		jt = new JTree(top);
		// hide the root node
		jt.setRootVisible(false);
		// hide the lines
		jt.setShowsRootHandles(true);
		// Remove the icon from the tree
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		jt.setCellRenderer(renderer);
		jt.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jt.addTreeSelectionListener(this);
				
		jspTreeView = new JScrollPane(jt);
		jpOption = new JPanel();
		
		//Add the scroll panes to a split pane.
		jspDivider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jspDivider.setLeftComponent(jspTreeView);
		jspDivider.setBottomComponent(jpOption);
		jspDivider.setContinuousLayout(true);

        Dimension minimumSize = new Dimension(100, 1);
        jspTreeView.setMinimumSize(minimumSize);
        
        jpOption.setMinimumSize(minimumSize);
        
        jspDivider.setDividerLocation(OPTION_DIALOG_DIMENSION.width / 3); 

        jbOk = new JButton("OK");
        jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// save the data when okay has been pressed
				try {
					cm.writeConfigurationFile();
				} catch (IOException er) {
					JOptionPane.showMessageDialog(getRootPane(), "Error while saving the configuration", "Error", JOptionPane.ERROR_MESSAGE);
					er.printStackTrace();
				}	
				approved = APPROVE_OPTION;
				setVisible(false);			
			}
		});
        jbCancel = new JButton("Cancel");
        jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cm.setZoomFile(zoomFile);
				cm.setChromosomeFile(chromosomeFile);
				cm.setLogFile(logFile);
				cm.setDefaultDirectory(defaultDirectory);
				cm.setLookAndFeel(lookAndFeel);
				cm.setTrackCount(trackCount);
				cm.setTrackHeight(trackHeight);
				setVisible(false);
			}
		});
        
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 0.99;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		add(jspDivider, c);

		c.fill = GridBagConstraints.VERTICAL;
		c.weightx = 0.99;
		c.weighty = 0.01;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbOk, c);
		
		c.weightx = 0.01;
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, c);	
		
		jt.setSelectionRow(0);
		setTitle("Option");
		setSize(OPTION_DIALOG_DIMENSION);
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(getRootPane());
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(jbOk);
	}



	/**
	 * Creates the data of the tree.
	 * @param top Root DefaultMutableTreeNode of the tree.
	 */
	private void createNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;

		category = new DefaultMutableTreeNode(new GeneralOptionPanel(cm));
		top.add(category);

		category = new DefaultMutableTreeNode(new ConfigFileOptionPanel(cm));
		top.add(category);

		category = new DefaultMutableTreeNode(new TrackOptionPanel(cm));
		top.add(category);
		
		category = new DefaultMutableTreeNode(new RestoreOptionPanel(cm));
		top.add(category);
	}


	/**
	 * Changes the panel displayed when the node of the tree changes.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		jt.getLastSelectedPathComponent();
		jpOption.removeAll();
		if ((node != null) && (node.isLeaf())) {
			Object nodeInfo = node.getUserObject();
			if (nodeInfo != null) {				
				jpOption.add((JPanel)nodeInfo);
				((JPanel)nodeInfo).addPropertyChangeListener(this);
			}
		}
		jpOption.revalidate();
		jpOption.repaint();
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showConfigurationDialog(Component parent) {
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
	
	
	/**
	 * Restores the data and regenerate the tree when the option 
	 * restore configuration is clicked.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("reset")) {
			DefaultMutableTreeNode top = new DefaultMutableTreeNode("Options");
			createNodes(top);
			jt.setModel(new DefaultTreeModel(top));
			jt.revalidate();
			jt.setSelectionRow(0);
		}		
	}
	
	
	/**
	 * @return true if zoomFile changed
	 */
	public boolean zoomFileChanged() {
		return !zoomFile.equals(cm.getZoomFile());
	}
	
	
	/**
	 * @return true if chromosomeFile changed
	 */
	public boolean chromosomeFileChanged() {
		return !chromosomeFile.equals(cm.getChromosomeFile());
	}
	
	
	/**
	 * @return true if logFile changed
	 */
	public boolean logFileChanged() {
		return !logFile.equals(cm.getLogFile());
	}
	
	
	/**
	 * @return true if defaultDirectory changed
	 */
	public boolean defaultDirectoryChanged() {
		return !defaultDirectory.equals(cm.getDefaultDirectory());
	}
	
	
	/**
	 * @return true if lookAndFeel changed
	 */
	public boolean lookAndFeelChanged() {
		return !lookAndFeel.equals(cm.getLookAndFeel());
	}
	
	
	/**
	 * @return true if trackCount changed
	 */
	public boolean trackCountChanged() {
		return trackCount != cm.getTrackCount();
	}
	
	
	/**
	 * @return true if trackHeight changed
	 */
	public boolean trackHeightChanged() {
		return trackHeight != cm.getTrackHeight();
	}
}
