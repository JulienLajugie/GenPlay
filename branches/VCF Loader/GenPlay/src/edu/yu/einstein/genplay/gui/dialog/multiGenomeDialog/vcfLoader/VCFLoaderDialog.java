/**
 * 
 */
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.gui.customComponent.customComboBox.CustomComboBox;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFLoaderDialog extends JDialog {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = -6703399045694111551L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private static final	String 			ICON_PATH 			= "edu/yu/einstein/genplay/resource/icon.png"; 	// path of the icon of the application
	private static final 	String 			FONT_NAME			= "ARIAL";										// name of the font
	private static final 	String 			EDITING_INFO 		= "   Right click for adding/deleting row(s)";	// part of "how to use" labels (used for width)
	private static final 	int 			FONT_SIZE 			= 11;											// size of the font
	private static final 	int 			DIALOG_WIDTH 		= 700;											// width of the dialog
	private static final 	int 			VALIDATION_HEIGHT 	= 20;											// height of the validation panel
	private static			Image 			iconImage; 															// icon of the application

	private 				int				approved 			= CANCEL_OPTION;								// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	
	private 				Font 			font 				= new Font(FONT_NAME, Font.ITALIC, FONT_SIZE);	// Font used in the dialog (How to use)
	private 				FontMetrics 	fm					= getFontMetrics(font); 						// FontMetrics to get the size of a string
	
	private 				VCFLoaderTable 	table;				// the table
	private 				JPanel 			southPanel;


	/**
	 * Constructor of {@link VCFLoaderDialog}
	 */
	public VCFLoaderDialog () {
		// Dimensions
		Dimension dialogDim = new Dimension(DIALOG_WIDTH, 400);
		Dimension validationDim = new Dimension(DIALOG_WIDTH, VALIDATION_HEIGHT);

		// Table
		VCFLoaderModel model = new VCFLoaderModel();
		table = new VCFLoaderTable(model);
		initializesPopUpMenu();

		// Center
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		// South
		initializesSouthPanel(validationDim);

		// Dialog
		setTitle("VCF Loader");
		setSize(dialogDim);
		setMinimumSize(new Dimension(getMinimumWidth(), getMinimumHeight()));
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		iconImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource(ICON_PATH));
		setIconImage(iconImage);
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		if (table.getData().size() == 0) {
			table.addEmptyRow();
		}
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	/**
	 * Closes the VCF loader dialog
	 */
	public void closeDialog () {
		setVisible(false);
	}
	
	
	/**
	 * Initializes the south panel.
	 * South panel contains "how to use" panel and Ok/Cancel button panel.
	 * @param validationDimension
	 */
	private void initializesSouthPanel (Dimension validationDimension) {
		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(getInformationPanel(), BorderLayout.CENTER);
		southPanel.add(getValidationPanel(validationDimension), BorderLayout.SOUTH);
	}
	
	
	/**
	 * Information panel contains label about how to use the table
	 * @return the information panel
	 */
	private JPanel getInformationPanel () {
		JPanel informationPanel = new JPanel();
		informationPanel.setLayout(new GridLayout(5, 1));

		Dimension dimension = new Dimension(getMinimumWidth(), fm.getHeight());
		
		JLabel label = new JLabel("   Editing cell:");
		label.setFont(font);
		label.setSize(dimension);
		informationPanel.add(label);
		
		label = new JLabel("     - Adding: select '" + CustomComboBox.ADD_TEXT + "' option first and type over it");
		label.setFont(font);
		label.setSize(dimension);
		informationPanel.add(label);
		
		label = new JLabel("     - Replacing: select desired entry and type over it");
		label.setFont(font);
		label.setSize(dimension);
		informationPanel.add(label);
		
		label = new JLabel("     - Removing: select desired entry and empty the cell");
		label.setFont(font);
		label.setSize(dimension);
		informationPanel.add(label);
		
		label = new JLabel(VCFLoaderDialog.EDITING_INFO);
		label.setFont(font);
		label.setSize(dimension);
		informationPanel.add(label);
		
		return informationPanel;
	}
	

	/**
	 * The validation panel contain ok and cancel buttons
	 * @param dimension dimension of the panel
	 * @return			the panel
	 */
	private JPanel getValidationPanel (Dimension dimension) {
		Dimension buttonDim = new Dimension(60, 30);
		Insets inset = new Insets(0, 0, 0, 0);

		JButton confirm = new JButton("Ok");
		confirm.setPreferredSize(buttonDim);
		confirm.setToolTipText("Ok");
		confirm.setMargin(inset);
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("confirm");
				if (areValidSettings()) {
					approved = APPROVE_OPTION;
					closeDialog();
				}
			}
		});

		JButton cancel = new JButton("Cancel");
		cancel.setPreferredSize(buttonDim);
		cancel.setToolTipText("Cancel");
		cancel.setMargin(inset);
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = CANCEL_OPTION;
				closeDialog();
			}
		});

		JPanel validationPanel = new JPanel();
		validationPanel.add(confirm);
		validationPanel.add(cancel);
		validationPanel.setSize(dimension);

		return validationPanel;
	}


	/**
	 * Initializes the popup menu for the table.
	 */
	private void initializesPopUpMenu () {

		MouseListener popupListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JPopupMenu popup = new JPopupMenu();
					JMenuItem menuItem;
					menuItem = new JMenuItem("Add row");
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							table.addEmptyRow();
						}
					});
					popup.add(menuItem);
					menuItem = new JMenuItem("Delete selected row(s)");
					if (table.getSelectedRowCount() == 0) {
						menuItem.setEnabled(false);
					} else {
						menuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								int[] selectedRows = table.getSelectedRows();
								int index = selectedRows.length;
								int[] reverse = new int[index];
								for (int i: selectedRows) {
									index--;
									reverse[index] = i;
								}
								for (int i: reverse) {
									((VCFLoaderModel) table.getModel()).deleteRow(i);
								}
							}
						});
					}
					popup.add(menuItem);
					popup.show(e.getComponent(),
							e.getX(), e.getY());
				}
			}
		};

		table.addMouseListener(popupListener);
	}

	
	/**
	 * The minimum width is according to the longest text label
	 * @return the minimum width of the dialog
	 */
	private int getMinimumWidth () {
		int width = fm.stringWidth(EDITING_INFO) * 2;
		return width;
	}
	
	
	/**
	 * The minimum height is the height of both labels, the validation panel height plus 100
	 * @return the minimum height of the dialog
	 */
	private int getMinimumHeight () {
		int height = VALIDATION_HEIGHT + fm.getHeight() * 2 + 100;
		return height;
	}


	/**
	 * Checks if row are valid.
	 * @return true if settings are valid, false otherwise
	 */
	public boolean areValidSettings () {
		List<VCFData> data = getData();
		String errors = "";
		if (data == null || data.size() == 0) {
			errors = "No settings found";
		} else {
			for (int i = 0; i < data.size(); i++) {
				String error = data.get(i).getErrors();
				if (error != null) {
					errors += "row " + i + ": " + error +"\n";
				}
			}
		}
		if (errors.length() > 0) {
			System.err.println(errors);
			JOptionPane.showMessageDialog(this, errors, "Settings are not valid", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;

	}


	/**
	 * @return the data
	 */
	public List<VCFData> getData() {
		if (table != null) {
			return table.getData();
		}
		return null;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(List<VCFData> data) {
		table.setData(data);
	}

}
