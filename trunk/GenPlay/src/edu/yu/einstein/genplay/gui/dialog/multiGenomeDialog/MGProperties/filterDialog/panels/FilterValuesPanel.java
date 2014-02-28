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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderAdvancedType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderBasicType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderElementRecord;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderFilterType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.GenotypeIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.idEditors.IDEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.idEditors.IDFlagEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.idEditors.IDGTEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.idEditors.IDNumberEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.idEditors.IDStringEditor;

/**
 * Panels to select values for a filter
 * @author Nicolas Fourel
 * @author Julien Lajugie
 */
public class FilterValuesPanel extends JPanel implements ActionListener, PropertyChangeListener {

	/** Name of the property that is true when the selection in this panel is valid, false otherwise */
	public static final String IS_SELECTION_VALID_PROPERTY_NAME = "Is selection valid";

	/** Generated serial version ID */
	private static final long serialVersionUID = -5350638693635564694L;

	/** Preferred height of this panel */
	private final int PREFERRED_HEIGHT = 180;

	private IDEditor 		filterEditor;			// regular editor
	private IDEditor 		specialFilterEditor;	// editor for GT field
	private JRadioButton 	regularRadioBox;		// box to choose the regular editor to edit a GT field
	private JRadioButton 	specialRadioBox;		// box to choose the special editor to edit a GT fied
	private boolean 		isSelectionValid;		// return true if the current selection is valid, false otherwise


	/**
	 * Constructor of {@link FilterValuesPanel}
	 */
	public FilterValuesPanel(VCFHeaderType headerType, FilterInterface filterInterface) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Select filter values"));
		setHeaderType(headerType);
		if (filterInterface != null) {
			initialize(filterInterface);
		}
		setPreferredSize(new Dimension(getPreferredSize().width, PREFERRED_HEIGHT));
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() instanceof JRadioButton) {
			JRadioButton radio = (JRadioButton) arg0.getSource();
			if (radio.equals(regularRadioBox)) {
				specialFilterEditor.setVisible(false);
				filterEditor.setVisible(true);
			} else {
				filterEditor.setVisible(false);
				specialFilterEditor.setVisible(true);
			}
			checkIfSelectionIsValid();
		}
	}


	/**
	 * Checks if the current selection made by the user is valid and update the
	 * {@link #isSelectionValid} property. Fire a property change event if the
	 * property changes.
	 */
	private final void checkIfSelectionIsValid() {
		boolean newIsSelectionValid = false;
		if ((regularRadioBox == null) || !regularRadioBox.isVisible() || regularRadioBox.isSelected()) {
			newIsSelectionValid = filterEditor.isSelectionValid();
		} else if (specialFilterEditor != null) {
			newIsSelectionValid = specialFilterEditor.isSelectionValid();
		}
		if (newIsSelectionValid != isSelectionValid) {
			boolean oldIsSelectionValid = isSelectionValid;
			isSelectionValid = newIsSelectionValid;
			firePropertyChange(IS_SELECTION_VALID_PROPERTY_NAME, oldIsSelectionValid, newIsSelectionValid);
		}
	}


	/**
	 * Creates the panel containing the editor.
	 * If a special editor has been created (as for the GT field), it will be put on the top.
	 * @return the panel containing the editor(s)
	 */
	private JPanel getCustomPanel () {
		JPanel panel = null;
		JPanel regularPanel = filterEditor.updatePanel();
		regularPanel.addPropertyChangeListener(IDEditor.IS_SELECTION_VALID_PROPERTY_NAME, this);

		if (specialFilterEditor != null) {
			panel = new JPanel();

			JPanel specialPanel = specialFilterEditor.updatePanel();
			specialPanel.addPropertyChangeListener(IDEditor.IS_SELECTION_VALID_PROPERTY_NAME, this);

			regularRadioBox = new JRadioButton("<html><i>Regular Editor:</i><html>");
			regularRadioBox.addActionListener(this);
			specialRadioBox = new JRadioButton("<html><i>Special Editor:</i><html>");
			specialRadioBox.addActionListener(this);
			specialRadioBox.setSelected(true);

			// Creates the group
			ButtonGroup group = new ButtonGroup();
			group.add(regularRadioBox);
			group.add(specialRadioBox);

			// Layout settings
			GridBagLayout layout = new GridBagLayout();
			panel.setLayout(layout);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.weighty = 1;
			panel.add(specialRadioBox, gbc);

			gbc.gridy++;
			gbc.insets = new Insets(0, 10, 0, 0);
			panel.add(specialPanel, gbc);
			specialFilterEditor.setVisible(true);

			gbc.gridy++;
			gbc.insets = new Insets(10, 0, 0, 0);
			panel.add(regularRadioBox, gbc);

			gbc.gridy++;
			gbc.insets = new Insets(0, 10, 0, 0);
			panel.add(regularPanel, gbc);
			filterEditor.setVisible(false);
		} else {
			panel = regularPanel;
		}
		return panel;
	}


	/**
	 * @return the ID filter
	 */
	public FilterInterface getFilter () {
		if (filterEditor.isVisible()) {
			return filterEditor.getFilter();
		}
		return specialFilterEditor.getFilter();
	}


	/**
	 * Initializes the editors. Select editors adapted to the VCF
	 * field to edit.
	 * @param headerType
	 */
	private void initEditors(VCFHeaderType headerType) {
		filterEditor = null;
		specialFilterEditor = null;

		if (headerType instanceof VCFHeaderBasicType) {
			VCFHeaderBasicType header = (VCFHeaderBasicType) headerType;
			VCFColumnName column = header.getColumnCategory();
			if (column == VCFColumnName.ALT) {
				filterEditor = new IDStringEditor();
			} else if (column == VCFColumnName.QUAL) {
				filterEditor = new IDNumberEditor();
			} else if (column == VCFColumnName.FILTER) {
				filterEditor = new IDStringEditor();
				if (header instanceof VCFHeaderElementRecord) {
					List<String> elements = new ArrayList<String>();
					List<Object> elementsList = ((VCFHeaderElementRecord)header).getElements();
					for (Object o: elementsList) {
						if ((elementsList != null) && (elementsList.size() > 0)) {
							if (o instanceof String) {
								elements.add(o.toString());
							} else if (o instanceof VCFHeaderBasicType) {
								elements.add(((VCFHeaderBasicType)o).getId());
							}
						}
					}
					((IDStringEditor)filterEditor).setDefaultElements(elements);
				}
			}
		} else if (headerType instanceof VCFHeaderAdvancedType) {
			VCFHeaderAdvancedType advancedHeader = (VCFHeaderAdvancedType) headerType;
			if ((advancedHeader.getType() == Integer.class) || (advancedHeader.getType() == Float.class)) {
				filterEditor = new IDNumberEditor();
			} else if (advancedHeader.getType() == Boolean.class){
				filterEditor = new IDFlagEditor();
			} else if (advancedHeader.getType() == String.class){
				filterEditor = new IDStringEditor();
				if (advancedHeader instanceof VCFHeaderElementRecord) {
					List<Object> elementsList = ((VCFHeaderElementRecord)advancedHeader).getElements();
					if ((elementsList != null) && (elementsList.size() > 0)) {
						List<String> elements = new ArrayList<String>();
						for (Object o: elementsList) {
							elements.add(o.toString());
						}
						((IDStringEditor)filterEditor).setDefaultElements(elements);
					}
				}
			}

			if (advancedHeader.getId().equals("GT")) {
				specialFilterEditor = new IDGTEditor();
			}
		} else if (headerType instanceof VCFHeaderType) {
			VCFHeaderType header = headerType;
			if (header.getColumnCategory() == VCFColumnName.ALT) {
				filterEditor = new IDStringEditor();
			} else if (headerType instanceof VCFHeaderFilterType) {
				filterEditor = new IDFlagEditor();
			}
		}
		if (filterEditor != null) {
			filterEditor.setHeaderType(headerType);
		}
		if (specialFilterEditor != null) {
			specialFilterEditor.setHeaderType(headerType);
		}
	}


	/**
	 * First initialization when the component is created.
	 * Restores the values of a filter if this filter is edited
	 * @param element
	 */
	private void initialize(FilterInterface element) {
		if( (filterEditor != null) && (element != null)) {
			if (element instanceof IDFilterInterface) {
				IDFilterInterface filter = (IDFilterInterface) element;
				if (element instanceof GenotypeIDFilter) {
					specialFilterEditor.initializesPanel(filter);
					showSpecialPanel();
				} else {
					filterEditor.initializesPanel(filter);
					showRegularPanel();
				}
			}
		}
		checkIfSelectionIsValid();
	}


	/**
	 * @return true if the current values selected by the user are valid, false otherwise
	 */
	public boolean isSelectionValid() {
		return isSelectionValid;
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		checkIfSelectionIsValid();
	}


	/**
	 * Sets the VCF field being edited and adapt
	 * the panel to the type of this field.
	 * @param headerType
	 */
	public void setHeaderType(VCFHeaderType headerType) {
		initEditors(headerType);
		JPanel panel;
		if (filterEditor != null) {
			panel = getCustomPanel();
		} else {
			panel = new JPanel();
		}
		removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(panel, gbc);
		revalidate();
		repaint();
		checkIfSelectionIsValid();
	}


	/**
	 * Shows the panel for regular filters
	 */
	private void showRegularPanel () {
		if (specialFilterEditor != null) {
			specialFilterEditor.setVisible(false);
		}
		if (regularRadioBox != null) {
			regularRadioBox.setSelected(true);
		}
		filterEditor.setVisible(true);
	}


	/**
	 * Shows the panel for special filters
	 */
	private void showSpecialPanel () {
		filterEditor.setVisible(false);
		if (specialRadioBox != null) {
			specialRadioBox.setSelected(true);
		}
		if (specialFilterEditor != null) {
			specialFilterEditor.setVisible(true);
		}
	}
}
