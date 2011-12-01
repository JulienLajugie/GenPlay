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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.stripesEditing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripesGlobalPanel extends JPanel implements ActionListener, MouseListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 4642065107335630948L;


	private StripesEditingPanel editingPanel;	// the stripe editing panel (left part) 
	private StripesContentPanel contentPanel;	// the content panel (right part)
	private StripeData 			currentData;	// selected current stripe data

	/**
	 * Constructor of {@link StripesGlobalPanel}
	 */
	public StripesGlobalPanel () {
		// Set the layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Get the panels
		editingPanel = new StripesEditingPanel();
		contentPanel = new StripesContentPanel();

		// Set the size of the editing panel
		Dimension editingDimension = new Dimension(200, PropertiesDialog.DIALOG_HEIGHT);
		editingPanel.setPreferredSize(editingDimension);
		editingPanel.addListener(this);

		// Creates the scrollable content pane
		JScrollPane scrollContentPane = new JScrollPane(contentPanel);
		Dimension contentDimension = new Dimension(600, PropertiesDialog.DIALOG_HEIGHT);
		scrollContentPane.setPreferredSize(contentDimension);
		contentPanel.addListener(this);

		// Adds panels
		add(editingPanel, BorderLayout.WEST);
		add(scrollContentPane, BorderLayout.CENTER);
	}


	/**
	 * Controls if a stripe data object is valid.
	 * Any of its information can be null or empty
	 * @param data the stripe data object to control
	 * @return	true if it is valid, false otherwise
	 */
	private boolean controlStripeData (StripeData data) {
		// Initializes the error string
		String errors = "";

		// Controls the genome name (should be never null or empty)
		if (data.getGenome() == null || data.getGenome().equals("")) {
			errors += "Invalid genome selection\n";
		}

		// Controls the variation list (a valid variation list involves a valid correct list)
		if (data.getVariantList() == null || data.getVariantList().size() == 0) {
			errors += "Invalid variation selection\n";
		}

		// Controls the track selection
		if (data.getTrackList() == null || data.getTrackList().length == 0) {
			errors += "Invalid track selection\n";
		}

		// If no error
		if (errors.equals("")) {
			return true; // the stripe data object is valid
		} else {
			System.err.println(errors);
			JOptionPane.showMessageDialog(this, errors, "Stripe settings are not valid", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}


	@Override
	/**
	 * This class listen the click actions made on the 'Add' and 'Apply' buttons of the editing panel.
	 */
	public void actionPerformed(ActionEvent evt) {
		// Get the button that has been clicked
		JButton button = (JButton) evt.getSource();
		
		if (button.getText().equals(StripesEditingPanel.ADD_BUTTON_TEXT)) {				// if the ADD button has been clicked
			StripeData data = editingPanel.getStripeData();								// get the stripe data object
			if (controlStripeData(data)) {												// control if the stripe data object is valid
				contentPanel.addRow(data);												// add a new row
				contentPanel.updateTableSize();											// update the size of the table
			}
		} else if (button.getText().equals(StripesEditingPanel.APPLY_BUTTON_TEXT)) {	// if the APPLY button has been clicked
			StripeData data = editingPanel.getStripeData();								// get the stripe data object
			if (controlStripeData(data)) {												// control if the stripe data object is valid
				currentData.setGenome(data.getGenome());								// set the stripe data object
				currentData.setVariantList(data.getVariantList());
				currentData.setColorList(data.getColorList());
				currentData.setTrackList(data.getTrackList());
				contentPanel.updateTableSize();											// update the size of the table
				contentPanel.getTable().repaint();										// repaint the table
				contentPanel.validate();												// validate the content panel
			}
		} else if (button.equals(contentPanel.getDeleteRowsButton())) {					// if the button to delete rows has been clicked
			editingPanel.getApplyButton().setEnabled(false);							// no row is selected and it is therefore impossible to use the APLLY button
		}
	}


	@Override
	/**
	 * This class listen the mouse actions made on the table of the content panel.
	 */
	public void mouseClicked(MouseEvent e) {
		StripesTable table = contentPanel.getTable();						// get the table
		int column = table.getColumnModel().getColumnIndexAtX(e.getX());	// get the column that has been clicked
		int row = e.getY()/table.getRowHeight();							// get the row that has been clicked

		if (row < table.getRowCount() &&									// if row and column are valid
				row >= 0 &&
				column < table.getColumnCount() &&
				column >= 0) {
			StripeData data = table.getData().get(row);						// get the stripe data object	
			currentData = data;												// set the current data stripe object
			editingPanel.setEditingPanel(currentData);						// set the editing panel with the new strip data object
			editingPanel.getApplyButton().setEnabled(true);					// APPLY button must be activated
		}
		contentPanel.updateEnableButtons();									// update the state of the panel button used for table handling
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
	@Override
	public void mouseReleased(MouseEvent arg0) {}


}
