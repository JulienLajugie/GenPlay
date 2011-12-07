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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.ContentTable;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.GlobalPanel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class StripesGlobalPanel extends GlobalPanel<StripesEditingPanel, StripesContentPanel> {

	/** Generated serial version ID */
	private static final long serialVersionUID = 4642065107335630948L;

	private StripesData 	currentData;	// selected current stripe data


	/**
	 * Constructor of {@link StripesGlobalPanel}
	 */
	public StripesGlobalPanel () {
		createPanels();
		addListeners();
		initializes();
	}


	@Override
	protected void createPanels() {
		// Get the panels
		editingPanel = new StripesEditingPanel();
		contentPanel = new StripesContentPanel();
	}


	@Override
	protected void addListeners() {
		getEditingPanel().addListener(this);
		getContentPanel().addListener(this);
	}
	
	
	/**
	 * Set the stripe panel with specific values
	 * @param list list of data
	 */
	public void setSettings (List<StripesData> list) {
		((StripesContentPanel) contentPanel).setSettings(list);
	}
	
	
	/**
	 * @return the filters list
	 */
	public List<StripesData> getStripesData () {
		return ((StripesContentPanel) contentPanel).getStripesData();
	}
	

	/**
	 * Controls if a stripe data object is valid.
	 * Any of its information can be null or empty
	 * @param data the stripe data object to control
	 * @return	true if it is valid, false otherwise
	 */
	private boolean controlStripesData (StripesData data) {
		// Initializes the error string
		String errors = "";

		// Controls the genome name (should be never null or empty)
		if (data.getGenome() == null || data.getGenome().equals("")) {
			errors += "Invalid genome selection\n";
		}

		// Controls the variation list (a valid variation list involves a valid correct list)
		if (data.getVariationTypeList() == null || data.getVariationTypeList().size() == 0) {
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
			StripesData data = getEditingPanel().getElement();							// get the stripe data object
			if (controlStripesData(data)) {												// control if the stripe data object is valid
				getContentPanel().addRow(data);											// add a new row
				getContentPanel().updateTableSize();									// update the size of the table
			}
		} else if (button.getText().equals(StripesEditingPanel.APPLY_BUTTON_TEXT)) {	// if the APPLY button has been clicked
			StripesData data = getEditingPanel().getElement();							// get the stripe data object
			if (controlStripesData(data)) {												// control if the stripe data object is valid
				currentData.setGenome(data.getGenome());								// set the stripe data object
				currentData.setVariationTypeList(data.getVariationTypeList());
				currentData.setColorList(data.getColorList());
				currentData.setTrackList(data.getTrackList());
				getContentPanel().updateTableSize();									// update the size of the table
				getContentPanel().getTable().repaint();									// repaint the table
				getContentPanel().validate();											// validate the content panel
			}
		} else if (button.equals(getContentPanel().getDeleteRowsButton())) {			// if the button to delete rows has been clicked
			getEditingPanel().getApplyButton().setEnabled(false);						// no row is selected and it is therefore impossible to use the APLLY button
		}
	}


	@Override
	/**
	 * This class listen the mouse actions made on the table of the content panel.
	 * It also listens the content panel in order to unselect rows when click is not on the table
	 */
	public void mouseClicked(MouseEvent e) {
		ContentTable<StripesData> table = getContentPanel().getTable();		// get the table
		int column = table.getColumnModel().getColumnIndexAtX(e.getX());	// get the column that has been clicked
		int row = e.getY()/table.getRowHeight();							// get the row that has been clicked

		boolean isIn = false;
		if (row < table.getRowCount() &&									// if row and column are valid
				row >= 0 &&
				column < table.getColumnCount() &&
				column >= 0) {
			isIn = true;
		}

		if (isIn) {
			if (table.getSelectedRows().length == 1) {
				StripesData data = table.getData().get(row);						// get the stripe data object	
				currentData = data;												// set the current data stripe object
				getEditingPanel().setEditingPanel(currentData);					// set the editing panel with the new strip data object
				getEditingPanel().getApplyButton().setEnabled(true);			// APPLY button must be activated
			} else if (table.getSelectedRows().length > 1) {
				// clear edit panel
				currentData = null;
				clearSelection();
			}
		} else {															// if the click is not in the table
			table.clearSelection();											// unselect all rows and columns
		}
		getContentPanel().updateEnableButtons();							// update the state of the panel button used for table handling
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
