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
package edu.yu.einstein.genplay.gui.dialog.optionDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import edu.yu.einstein.genplay.core.DAS.DASServerListWriter;
import edu.yu.einstein.genplay.core.manager.project.ProjectConfiguration;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.recording.RecordingManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Dialog allowing to change the configuration of the program.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class OptionDialog extends JDialog implements TreeSelectionListener, PropertyChangeListener {

	private static final long serialVersionUID = 4050757943368845382L; // Generated ID
	private static final Dimension OPTION_DIALOG_DIMENSION = new Dimension(600, 400); // dimension of this window
	private final ProjectConfiguration 	cm; 				// A ConfigurationManager
	private final JTree 				jt; 				// Tree
	private final JScrollPane 			jspTreeView; 		// Scroll pane containing the tree
	private final JPanel 				jpOption; 			// Panel containing the different panel of configuration
	private final JButton 				jbOk; 				// Button OK
	private final JButton 				jbCancel; 			// Button cancel
	private final JSplitPane 			jspDivider; 		// Divider between the tree and the panel
	private final String 				defaultDirectory; 	// default directory
	private final String 				lookAndFeel; 		// look and feel
	private final String 				dasServerListFile; 	// DAS Server List File
	private final int 					trackCount; 		// track count
	private final int 					trackHeight; 		// track height
	private final int 					undoCount; 			// undo count
	private final boolean				resetTrack;			// reset track feature
	private final boolean				legend;				// show legend (multi genome)
	private MemoryOptionPanel memoryOptionPanel = null;		// panel for the install version of GenPlay on OSX to modify the max memory
	private int 			approved = CANCEL_OPTION; 		// Equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not


	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 0;


	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 1;


	/**
	 * Creates an instance of {@link OptionDialog}
	 */
	public OptionDialog() {
		super();
		cm = ProjectManager.getInstance().getProjectConfiguration();
		dasServerListFile = cm.getDASServerListFile();
		defaultDirectory = cm.getDefaultDirectory();
		lookAndFeel = cm.getLookAndFeel();
		trackCount = cm.getTrackCount();
		trackHeight = cm.getTrackHeight();
		undoCount = cm.getUndoCount();
		resetTrack = cm.isResetTrack();
		legend = cm.isLegend();

		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Options");
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
		jspTreeView.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		jpOption = new JPanel();

		// Add the scroll panes to a split pane.
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
				approved = APPROVE_OPTION;
				setVisible(false);
				try {
					RecordingManager.getInstance().getApplicationRecording().writeConfigurationFile();
					if (DASOptionPanel.tableChangedFlag == true) {
						cm.setDASServerListFile(cm.getDASServerListFile());
						DASServerListWriter dasServerListWriter = new DASServerListWriter();
						dasServerListWriter.write(DASOptionPanel.tableData, cm.getDASServerListFile());
					}
					// if the memory option panel exist we write changes if needed
					if (memoryOptionPanel != null) {
						memoryOptionPanel.writeNewPList();
					}
				} catch (IOException er) {
					JOptionPane.showMessageDialog(getRootPane(), "Error while saving the configuration", "Error", JOptionPane.ERROR_MESSAGE);
					ExceptionManager.getInstance().caughtException(er);
				}
			}
		});
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		// we want the size of the two buttons to be equal
		jbOk.setPreferredSize(jbCancel.getPreferredSize());

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
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				// if the modification had been canceled we restore
				// the cm the way it was when the window was opened
				if (approved == CANCEL_OPTION) {
					cm.setDASServerListFile(dasServerListFile);
					cm.setDefaultDirectory(defaultDirectory);
					cm.setLookAndFeel(lookAndFeel);
					cm.setTrackCount(trackCount);
					cm.setTrackHeight(trackHeight);
					cm.setUndoCount(undoCount);
				}
			}
		});
		setTitle("Option");
		setSize(OPTION_DIALOG_DIMENSION);
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
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

		category = new DefaultMutableTreeNode(new GeneralOptionPanel());
		top.add(category);


		category = new DefaultMutableTreeNode(new TrackOptionPanel());
		top.add(category);

		category = new DefaultMutableTreeNode(new DASOptionPanel());
		top.add(category);

		if(Utils.isMacInstall()) {
			try {
				memoryOptionPanel = new MemoryOptionPanel();
				if (memoryOptionPanel != null) {
					category = new DefaultMutableTreeNode(memoryOptionPanel);
					top.add(category);
				}
			} catch (IOException e) {
				// do nothing
			}
		}

		category = new DefaultMutableTreeNode(new RestoreOptionPanel());
		top.add(category);
	}


	/**
	 * @return true if dasServerListFile changed
	 */
	public boolean dasServerListFileChanged() {
		return !dasServerListFile.equals(cm.getDASServerListFile());
	}


	/**
	 * @return true if defaultDirectory changed
	 */
	public boolean defaultDirectoryChanged() {
		return !defaultDirectory.equals(cm.getDefaultDirectory());
	}


	/**
	 * @return true if the legend parameter changed
	 */
	public boolean legendChanged() {
		return legend != cm.isLegend();
	}


	/**
	 * @return true if lookAndFeel changed
	 */
	public boolean lookAndFeelChanged() {
		return !lookAndFeel.equals(cm.getLookAndFeel());
	}


	/**
	 * Restores the data and regenerate the tree when the option restore
	 * configuration is clicked.
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
	 * @return true if the reset track parameter changed
	 */
	public boolean resetTrackChanged() {
		return resetTrack != cm.isResetTrack();
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


	/**
	 * @return true if the undo count changed
	 */
	public boolean undoCountChanged() {
		return undoCount != cm.getUndoCount();
	}


	/**
	 * Changes the panel displayed when the node of the tree changes.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt.getLastSelectedPathComponent();
		jpOption.removeAll();
		if ((node != null) && (node.isLeaf())) {
			Object nodeInfo = node.getUserObject();
			if (nodeInfo != null) {
				jpOption.add((JPanel) nodeInfo);
				((JPanel) nodeInfo).addPropertyChangeListener(this);
			}
		}
		jpOption.revalidate();
		jpOption.repaint();
	}
}
