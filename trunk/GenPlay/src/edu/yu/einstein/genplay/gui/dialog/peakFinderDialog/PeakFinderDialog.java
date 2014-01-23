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
package edu.yu.einstein.genplay.gui.dialog.peakFinderDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindIslands;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindPeaksDensity;
import edu.yu.einstein.genplay.core.operation.binList.BLOFindPeaksStDev;
import edu.yu.einstein.genplay.dataStructure.enums.PeakFinderType;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.dialog.peakFinderDialog.islandPanel.IslandFinderPanel;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;



/**
 * A dialog window asking the user to choose a peak finder and its parameters
 * @author Julien Lajugie
 * @version 0.1
 */
public class PeakFinderDialog extends JDialog implements TreeSelectionListener {

	private static final long serialVersionUID = 5563029408513103813L;
	private static final Dimension 	PEAK_FINDER_DIALOG_DIMENSION = new Dimension(680, 600); // dimension of this window
	private final JTree 			jt; 						// Tree
	private final JScrollPane 		jspTreeView; 				// Scroll pane containing the tree
	private final JPanel			jpPeakFinder;				// right panel
	private final JButton 			jbOk; 						// Button OK
	private final JButton 			jbCancel; 					// Button cancel
	private final JSplitPane 		jspDivider; 				// Divider between the tree and the panel
	private int 					approved = CANCEL_OPTION;	// indicate if the user canceled or validated
	private static int 				selectionRow = 0;			// save the selected peak finder
	private Operation<BinList[]>	setOperation = null;		// operation set in this dialog


	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 0;


	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 1;


	/**
	 * Creates an instance of {@link PeakFinderDialog}
	 * @param bloDensity {@link BLOFindPeaksDensity} to set
	 * @param bloStdev {@link BLOFindPeaksStDev} to set
	 * @param bloFindIslands {@link BLOFindIslands} to set
	 */
	public PeakFinderDialog(BLOFindPeaksDensity bloDensity, BLOFindPeaksStDev bloStdev, BLOFindIslands bloFindIslands) {
		super();
		// create the tree displayed in the JTree component
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("PeakFinder");
		createNodes(top, bloDensity, bloStdev, bloFindIslands);
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
		// create the scroll pane containing the JTree
		jspTreeView = new JScrollPane(jt);
		jspTreeView.getVerticalScrollBar().setUnitIncrement(Utils.SCROLL_INCREMENT_UNIT);
		// create the right panel
		jpPeakFinder = new JPanel();
		// add the scroll panes to a split pane.
		jspDivider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		jspDivider.setLeftComponent(jspTreeView);
		jspDivider.setRightComponent(jpPeakFinder);
		jspDivider.setContinuousLayout(true);
		Dimension minimumSize = new Dimension(100, 1);
		jspTreeView.setMinimumSize(minimumSize);
		jpPeakFinder.setMinimumSize(minimumSize);
		jspDivider.setDividerLocation(PEAK_FINDER_DIALOG_DIMENSION.width / 4);
		// create the OK button
		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt.getLastSelectedPathComponent();
				PeakFinderPanel peakFinderPanel = (PeakFinderPanel) node.getUserObject();
				// check first if the input is valid
				setOperation = peakFinderPanel.validateInput();
				// close the window if the input is valid
				if (setOperation != null) {
					// save the input so it becomes the default values next time the window is opened
					peakFinderPanel.saveInput();
					// save the filter type selected
					selectionRow = jt.getLeadSelectionRow();
					// close the window
					approved = APPROVE_OPTION;
					setVisible(false);
				}
			}
		});
		// create the cancel button
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		// we want the size of the two buttons to be equal
		jbOk.setPreferredSize(jbCancel.getPreferredSize());

		// add the components
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

		jt.setSelectionRow(selectionRow);
		setTitle("Peak Finder");
		setIconImage(Images.getApplicationImage());
		setSize(PEAK_FINDER_DIALOG_DIMENSION);
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
	private void createNodes(DefaultMutableTreeNode top, BLOFindPeaksDensity bloDensity, BLOFindPeaksStDev bloStdev, BLOFindIslands bloFindIslands) {
		DefaultMutableTreeNode category = null;

		category = new DefaultMutableTreeNode(new StDevFinderPanel(bloStdev));
		top.add(category);

		category = new DefaultMutableTreeNode(new DensityFinderPanel(bloDensity));
		top.add(category);

		category = new DefaultMutableTreeNode(new IslandFinderPanel(bloFindIslands));
		top.add(category);
	}


	/**
	 * @return the {@link Operation} set by the user. Null canceled
	 */
	public Operation<BinList[]> getOperation() {
		return setOperation;
	}


	/**
	 * @return the selected {@link PeakFinderType}
	 */
	public PeakFinderType getPeakFinderType() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt.getLastSelectedPathComponent();
		Object nodeInfo = node.getUserObject();
		if (nodeInfo instanceof DensityFinderPanel) {
			return PeakFinderType.DENSITY;
		} else if (nodeInfo instanceof StDevFinderPanel) {
			return PeakFinderType.STDEV;
		} else if (nodeInfo instanceof IslandFinderPanel) {
			return PeakFinderType.ISLAND;
		} else {
			return null;
		}
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showFilterDialog(Component parent) {
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	/**
	 * Changes the panel displayed when the node of the tree changes.
	 */
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt.getLastSelectedPathComponent();
		jpPeakFinder.removeAll();
		if ((node != null) && (node.isLeaf())) {
			Object nodeInfo = node.getUserObject();
			if (nodeInfo != null) {
				jpPeakFinder.add((JPanel) nodeInfo);
			}
		}
		jpPeakFinder.revalidate();
		jpPeakFinder.repaint();
	}
}
