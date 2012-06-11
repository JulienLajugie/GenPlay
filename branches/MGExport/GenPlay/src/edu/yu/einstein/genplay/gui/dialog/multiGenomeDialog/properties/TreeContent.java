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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class TreeContent {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4125853855970664341L;

	private JTree 							tree;	// the tree
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
	}


	/**
	 * Creates the root of the tree
	 */
	private void createNodes () {
		root = new DefaultMutableTreeNode();
		
		nodes = new ArrayList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode currentNode;
		
		// GENERAL node
		currentNode = new DefaultMutableTreeNode(PropertiesDialog.GENERAL);
		root.add(currentNode);
		nodes.add(currentNode);
		
		// SETTINGS node
		currentNode = new DefaultMutableTreeNode(PropertiesDialog.SETTINGS);
		root.add(currentNode);
		nodes.add(currentNode);
		
		// FILES nodes
		DefaultMutableTreeNode vcfFiles = new DefaultMutableTreeNode(PropertiesDialog.FILES);
		List<VCFFile> readerList = ProjectManager.getInstance().getMultiGenomeProject().getAllVCFFiles();
		for (VCFFile reader: readerList) {
			DefaultMutableTreeNode readerNode = new DefaultMutableTreeNode(reader.getFile().getName());
			readerNode.add(new DefaultMutableTreeNode("Information"));
			readerNode.add(new DefaultMutableTreeNode("Statistics"));
			vcfFiles.add(readerNode);
		}
		root.add(vcfFiles);
		
		// FILTERS node
		currentNode = new DefaultMutableTreeNode(PropertiesDialog.FILTERS);
		root.add(currentNode);
		nodes.add(currentNode);
		
		// STRIPES node
		currentNode = new DefaultMutableTreeNode(PropertiesDialog.STRIPES);
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
