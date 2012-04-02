/**
 * 
 */
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.yu.einstein.genplay.util.Images;

/**
 * @author Nicolas Fourel
 * @param <K> class used for the elements of the list of this dialog
 */
public class ListDialog<K> extends JDialog {


	/** Generated serial version ID */
	private static final long serialVersionUID = 1107812535407961768L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private int	approved 		= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private int maximumWidth 	= 300;				// Maximum default width of the dialog 
	private int maximumHeight 	= 500;				// Maximum default height of the dialog 
	private int selectionMode	= ListSelectionModel.SINGLE_SELECTION;

	private Dimension scrollDimension;	// Scroll pane list dimension
	private JScrollPane scrollPane;		// Scroll pane that contains the list
	private JList	jList;				// The list


	/**
	 * Constructor of {@link ListDialog}
	 * @param title title of the dialog
	 */
	public ListDialog (String title) {
		// Dialog settings
		setTitle("Multi-Genome Project Properties");
		setResizable(true);
		setVisible(false);
		setIconImage(Images.getApplicationImage());

		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Creates the list
		jList = new JList();
		jList.setSelectionMode(selectionMode);

		// Creates the scroll pane
		scrollPane = new JScrollPane(jList);

		// Add panels to the dialog
		add(scrollPane, BorderLayout.CENTER);
		add(getValidationPanel(), BorderLayout.SOUTH);

	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @param elements list of elements to show
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent, List<K> elements) {
		// Sets the list
		DefaultListModel model = new DefaultListModel();
		for (K o: elements) {
			model.addElement(o);
		}
		jList.setModel(model);

		// Set dimensions
		if (scrollDimension == null) {
			scrollDimension = getDimension(elements);
		}
		scrollPane.setPreferredSize(scrollDimension);
		scrollPane.setMaximumSize(scrollDimension);

		// Sets dialog display options
		pack();
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	/**
	 * Calculates the dimension
	 * @param list 	list of elements
	 * @return		the dimension
	 */
	private Dimension getDimension (List<K> list) {
		// width
		int width = 0;
		for (K o: list) {
			int currentWidth = getFontMetrics(getFont()).stringWidth(o.toString()) + 30;
			if (currentWidth > width) {
				width = currentWidth;
			}
		}
		if (width > maximumWidth) {
			width = maximumWidth;
		}

		// height
		int height = (getFontMetrics(getFont()).getHeight() + 3) * list.size() + 20;
		if (height > maximumHeight) {
			height = maximumHeight;
		}
		
		// return the dimension
		return new Dimension(width, height);
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
				if (jList.getSelectedValue() == null) {
					JOptionPane.showMessageDialog(getRootPane(), "Please select an item.", "Empty selection", JOptionPane.ERROR_MESSAGE);
				} else {
					scrollDimension = scrollPane.getSize();
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
				scrollDimension = scrollPane.getSize();
				approved = CANCEL_OPTION;
				setVisible(false);				
			}
		});
		
		// Set the default button
		getRootPane().setDefaultButton(jbOk);

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		// Returns the panel
		return panel;
	}


	/**
	 * Set the selection mode of the list (SINGLE_SELECTION by default)
	 * @param selectionMode
	 */
	public void setSelectionMode (int selectionMode) {
		jList.setSelectionMode(selectionMode);
	}


	/**
	 * @return the array of selected elements
	 */
	@SuppressWarnings("unchecked")
	public List<K> getSelectedElements () {
		Object[] objects = jList.getSelectedValues();
		List<Object> selectedElements = new ArrayList<Object>();
		for (Object o: objects) {
			selectedElements.add(o);
		}
		return (List<K>) selectedElements;
	}


	/**
	 * @return the selected element
	 */
	@SuppressWarnings("unchecked")
	public K getSelectedElement () {
		return (K) jList.getSelectedValue();
	}

}
