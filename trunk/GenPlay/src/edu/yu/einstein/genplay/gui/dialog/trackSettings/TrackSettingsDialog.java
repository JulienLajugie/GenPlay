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
package edu.yu.einstein.genplay.gui.dialog.trackSettings;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import edu.yu.einstein.genplay.gui.dialog.layerSettings.LayerSettingsPanel;
import edu.yu.einstein.genplay.gui.dialog.layerSettings.LayerSettingsRow;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.util.Images;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TrackSettingsDialog extends JDialog implements TreeSelectionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 3713110227164397033L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;
	/** Height of the dialog */
	public static final	int					DIALOG_HEIGHT 		= 400;
	/** Text for GENERAL tree node */
	public static final		String			TRACK 				= "General";
	/** Text for SETTINGS tree node */
	public static final		String			LAYER 				= "Layer(s)";


	private int				approved 			= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	private final Dimension contentDimension = new Dimension(700, DIALOG_HEIGHT);

	private final TreeContent 						treeContent;			// the tree manager
	private final JTree 							tree;					// the tree of the dialog
	private final JPanel 							contentPane;			// right part of the dialog
	private final TrackSettingsPanel				trackPanel;
	private final LayerSettingsPanel				layerPanel;


	/**
	 * Constructor of {@link TrackSettingsDialog}
	 */
	public TrackSettingsDialog () {
		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Tree (left part of the dialog)
		treeContent = new TreeContent();
		tree = treeContent.getTree();
		tree.addTreeSelectionListener(this);
		JScrollPane treeScroll = new JScrollPane(tree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScroll.getVerticalScrollBar().setUnitIncrement(edu.yu.einstein.genplay.util.Utils.SCROLL_INCREMENT_UNIT);
		Dimension scrollDimension = new Dimension(100, DIALOG_HEIGHT);
		treeScroll.setPreferredSize(scrollDimension);
		treeScroll.setMinimumSize(scrollDimension);

		// Content panel (right part of the dialog)
		contentPane = new JPanel();
		contentPane.setPreferredSize(contentDimension);

		// Create track panel
		trackPanel = new TrackSettingsPanel();

		// Create layer panel
		layerPanel = new LayerSettingsPanel();

		// Adds panels
		add(treeScroll, BorderLayout.WEST);
		add(contentPane, BorderLayout.CENTER);
		add(getValidationPanel(), BorderLayout.SOUTH);

		// Dialog settings
		setTitle("Track Settings");
		setIconImage(Images.getApplicationImage());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setVisible(false);
		pack();
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param track the selected track
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent, Track track) {
		trackPanel.setOptions(track);

		Layer<?>[] trackLayers = track.getLayers().getLayers();
		LayerSettingsRow[] layerSettings = null;
		if ((trackLayers != null) && (trackLayers.length > 0)) {
			layerSettings = new LayerSettingsRow[trackLayers.length];
			for (int i = 0; i < trackLayers.length; i++) {
				layerSettings[i] = new LayerSettingsRow(trackLayers[i]);
				layerSettings[i].setLayerActive(track.getActiveLayer() == trackLayers[i]);
			}
		}

		layerPanel.initialize(layerSettings);

		String accessor = TRACK;
		return showDialog(parent, accessor);
	}


	/**
	 * Shows the component.
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @param accessor 	get into a specific node of the properties dialog
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	private int showDialog(Component parent, String accessor) {
		// Sets the content panel
		if (accessor.equals(TRACK)) {
			setScrollableCenterPanel(trackPanel);
		} else if (accessor.equals(LAYER)) {
			setScrollableCenterPanel(layerPanel);
		}

		// Gets the tree path if exists and select it
		TreePath treePath = treeContent.getTreePath(accessor);
		if (treePath != null) {
			tree.setSelectionPath(treePath);
			tree.scrollPathToVisible(treePath);
		}

		// Sets dialog display options
		setLocationRelativeTo(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);

		return approved;
	}


	/**
	 * Sets the panel at the center of the dialog with the one given as parameter
	 * It first includes the panel in a scroll panel.
	 * @param panel the panel to show at the center of the dialog
	 */
	protected void setScrollableCenterPanel (JPanel panel) {
		// Set the panel to the right dimension
		JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(edu.yu.einstein.genplay.util.Utils.SCROLL_INCREMENT_UNIT);
		scrollPane.setPreferredSize(contentDimension);

		// Removes all content of the contentPane
		contentPane.removeAll();

		// Set the redular dimension for the content panel
		contentPane.setPreferredSize(contentDimension);

		// Set the panel gaps to zero
		((FlowLayout)(contentPane.getLayout())).setHgap(0);
		((FlowLayout)(contentPane.getLayout())).setVgap(0);

		// Add the panel to the content panel
		contentPane.add(scrollPane);
		contentPane.repaint();
		validate();

		pack();

	}


	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		Object nodeInfo = node.getUserObject();
		if (nodeInfo.equals(TRACK)) {
			setScrollableCenterPanel(trackPanel);
		} else if (nodeInfo.equals(LAYER)) {
			setScrollableCenterPanel(layerPanel);
		}
	}


	/**
	 * Creates the panel that contains OK and CANCEL buttons
	 * @return the panel
	 */
	private JPanel getValidationPanel () {
		// Creates the ok button
		JButton jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (layerPanel.confirmLayerDeletion()) {
					approved = APPROVE_OPTION;
					setVisible(false);
				}
			}
		});

		// Creates the cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = CANCEL_OPTION;
				setVisible(false);
			}
		});

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}


	/**
	 * @return the track options
	 */
	public TrackSettingsPanel getTrackOptions() {
		return trackPanel;
	}


	/**
	 * @return the layer options
	 */
	public LayerSettingsRow[] getLayerOptions() {
		return layerPanel.getData();
	}

}
