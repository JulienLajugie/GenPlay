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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.util.Images;


/**
 * This class is the VCF loader dialog.
 * It displays a table for editing multi-genome VCF settings.
 * Developer can:
 * - show it
 * - close it
 * - set the data
 * - get the data
 * - check settings validity
 * 
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

	private static final 	String 			FONT_NAME			= "ARIAL";										// name of the font
	private static final 	int 			FONT_SIZE 			= 11;											// size of the font
	private static final 	int 			DIALOG_WIDTH 		= 700;											// width of the dialog
	private static final 	int 			VALIDATION_HEIGHT 	= 20;											// height of the validation panel

	private 				int				approved 			= CANCEL_OPTION;								// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	private 				Font 			font 				= new Font(FONT_NAME, Font.ITALIC, FONT_SIZE);	// Font used in the dialog (How to use)
	private 				FontMetrics 	fm					= getFontMetrics(font); 						// FontMetrics to get the size of a string
	private 				VCFLoaderTable 	table;																// the table


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
		JPanel southPanel = getSouthPanel(validationDim);

		// Dialog
		setTitle("VCF Loader");
		setSize(dialogDim);
		setMinimumSize(new Dimension(getMinimumWidth(), getMinimumHeight()));
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		setIconImage(Images.getApplicationImage());
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
	private JPanel getSouthPanel (Dimension validationDimension) {
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		southPanel.add(getInformationPanel(), BorderLayout.CENTER);
		southPanel.add(getValidationPanel(validationDimension), BorderLayout.SOUTH);
		return southPanel;
	}


	/**
	 * Information panel contains label about how to use the table
	 * @return the information panel
	 */
	private JPanel getInformationPanel () {
		JPanel informationPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		informationPanel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel label = new JLabel("Caption:");
		label.setFont(font);
		label.setOpaque(true);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 20, 0, 0);
		informationPanel.add(label, gbc);

		label = new JLabel("Add a new element to the list");
		label.setIcon(getIcon(Images.getAddImage()));
		label.setFont(font);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 40, 0, 0);
		informationPanel.add(label, gbc);

		label = new JLabel("Edit the selected element");
		label.setIcon(getIcon(Images.getEditImage()));
		label.setFont(font);
		gbc.gridx = 0;
		gbc.gridy = 2;
		informationPanel.add(label, gbc);

		label = new JLabel("Delete the selected element");
		label.setIcon(getIcon(Images.getDeleteImage()));
		label.setFont(font);
		gbc.gridx = 0;
		gbc.gridy = 3;
		informationPanel.add(label, gbc);

		label = new JLabel("Add/Delete row(s)");
		label.setIcon(getIcon(Images.getMouseImage()));
		label.setFont(font);
		gbc.gridx = 0;
		gbc.gridy = 4;
		informationPanel.add(label, gbc);

		return informationPanel;
	}


	/**
	 * The validation panel contains ok and cancel buttons
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
				approved = APPROVE_OPTION;
				closeDialog();
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
	 * Creates a square icon using the image
	 * @param image the image
	 * @return		the icon
	 */
	private ImageIcon getIcon (Image image) {
		return new ImageIcon(Images.getSquareImage(image, fm.getHeight()));
		
	}


	/**
	 * The minimum width is according to the longest text label
	 * @return the minimum width of the dialog
	 */
	private int getMinimumWidth () {
		int width = 250;
		return width;
	}


	/**
	 * The minimum height is the height of the caption, the validation panel height plus 100
	 * @return the minimum height of the dialog
	 */
	private int getMinimumHeight () {
		int height = VALIDATION_HEIGHT + fm.getHeight() * 4 + 100;
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
	
	
	/**
	 * @param file
	 * @return the existing VCF file (if exists) or a new VCF File
	 */
	public VCFFile getVCFFile (File file) {
		return table.getVCFFile(file);
	}

}
