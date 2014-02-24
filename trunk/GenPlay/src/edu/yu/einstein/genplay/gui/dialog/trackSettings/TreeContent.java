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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class TreeContent {

	/** preferred width of the tree */
	private final static int TREE_PREFERRED_WIDTH = 100;

	private final JTree 					tree;	// the tree
	private DefaultMutableTreeNode 			root;	// root of the tree
	private List<DefaultMutableTreeNode>	nodes;	// nodes record


	/**
	 * Constructor of {@link TreeContent}
	 * Initializes the root of the tree.
	 */
	protected TreeContent () {
		// Creates nodes
		createNodes();

		// Creates tree using the nodes
		tree = new JTree(root);

		// Root node is not visible
		tree.setRootVisible(false);

		// Shows the lines
		tree.setShowsRootHandles(true);

		// Sets the tree renderer (without icons)
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		tree.setCellRenderer(renderer);

		// Single selection
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// set the new tree preferred width
		tree.setPreferredSize(new Dimension(TREE_PREFERRED_WIDTH, tree.getPreferredSize().height));
	}


	/**
	 * Creates the root of the tree
	 */
	private void createNodes () {
		root = new DefaultMutableTreeNode();

		nodes = new ArrayList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode currentNode;

		// TRACK node
		currentNode = new DefaultMutableTreeNode(TrackSettingsDialog.TRACK);
		root.add(currentNode);
		nodes.add(currentNode);

		// LAYER node
		currentNode = new DefaultMutableTreeNode(TrackSettingsDialog.LAYER);
		root.add(currentNode);
		nodes.add(currentNode);
	}


	/**
	 * Creates the tree
	 * @return the tree
	 */
	protected JTree getTree () {
		return tree;
	}


	protected TreePath getTreePath (String node) {
		for (DefaultMutableTreeNode current: nodes) {
			if (current.getUserObject().toString().equals(node)) {
				return new TreePath(current.getPath());
			}
		}
		return null;
	}

}
