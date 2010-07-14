/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.peakFinderDialog;

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

import yu.einstein.gdp2.core.enums.PeakFinderType;


/**
 * A dialog window asking the user to choose a peak finder and its parameters
 * @author Julien Lajugie
 * @version 0.1
 */
public class PeakFinderDialog extends JDialog implements TreeSelectionListener {

	private static final long serialVersionUID = 5563029408513103813L;
	private static final Dimension 	PEAK_FINDER_DIALOG_DIMENSION = new Dimension(700, 400); // dimension of this window
	private final JTree 			jt; 						// Tree
	private final JScrollPane 		jspTreeView; 				// Scroll pane containing the tree
	private final JPanel			jpPeakFinder;				// right panel
	private final JButton 			jbOk; 						// Button OK
	private final JButton 			jbCancel; 					// Button cancel
	private final JSplitPane 		jspDivider; 				// Divider between the tree and the panel
	private int 					approved = CANCEL_OPTION;	// indicate if the user canceled or validated 
	private static int 				selectionRow = 0;			// save the selected peak finder

	
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 0;


	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 1;

	
	/**
	 * Main method for the tests
	 * @param args
	 */
	public static void main(String[] args) {
		PeakFinderDialog fd = new PeakFinderDialog();
		if (fd.showFilterDialog(null) == PeakFinderDialog.APPROVE_OPTION) {
			System.out.println("okay");
		} else {
			System.out.println("cancel");
		}
		fd.dispose();
		PeakFinderDialog fd2 = new PeakFinderDialog();
		fd2.showFilterDialog(null);
		fd2.dispose();
	}


	/**
	 * Creates an instance of {@link PeakFinderDialog}
	 */
	public PeakFinderDialog() {
		super();
		// create the tree displayed in the JTree component
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("PeakFinder");
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
		// create the scroll pane containing the JTree
		jspTreeView = new JScrollPane(jt);
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
				boolean isInputValid = peakFinderPanel.isInputValid();
				// close the window if the input is valid
				if (isInputValid) {
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
		setSize(PEAK_FINDER_DIALOG_DIMENSION);
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

		category = new DefaultMutableTreeNode(new StDevFinderPanel());
		top.add(category);

		category = new DefaultMutableTreeNode(new DensityFinderPanel());
		top.add(category);

//		category = new DefaultMutableTreeNode(new IslandFinderPanel());
//		top.add(category);
	}


	/**
	 * @return the selected panel. This Object can be casted to retrieve the input
	 */
	public JPanel getPeakFinderPanel() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt.getLastSelectedPathComponent();
		Object nodeInfo = node.getUserObject();
		return (JPanel) nodeInfo;
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
