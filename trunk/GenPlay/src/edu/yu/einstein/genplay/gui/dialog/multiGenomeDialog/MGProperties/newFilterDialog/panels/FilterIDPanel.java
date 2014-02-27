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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.panels;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderInfoType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.AddOrEditVariantFiltersDialog;


/**
 * Panel containing a list of VCF fields.
 * This panel let the user choose a VCF field.
 * @author Julien Lajugie
 */
public class FilterIDPanel extends JPanel implements ListSelectionListener, ItemListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 5470522161551881606L;

	/** File change property name */
	public static final String FILTER_ID_PROPERTY_NAME = "Filter ID Change";

	private List<VCFHeaderType> headerList;

	/** jList displaying the different filters */
	private final JList jList;

	/** check box to hide the info fields*/
	private final JCheckBox hideInfoFields;


	/**
	 * Creates an instance of {@link FilterIDPanel}
	 * @param headerList list of the VCF fields displayed in this panel
	 * @param selectedHeader VCF already selected, can be null if none
	 */
	public FilterIDPanel(List<VCFHeaderType> headerList, VCFHeaderType selectedHeader) {
		super();
		this.headerList = headerList;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createTitledBorder("Select field to filter"));

		jList = new JList();
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setModel(new DefaultListModel());
		jList.getSelectionModel().addListSelectionListener(this);
		JScrollPane jsp = new JScrollPane(jList);

		hideInfoFields = new JCheckBox("Hide INFO field");
		hideInfoFields.addItemListener(this);

		add(jsp);
		add(hideInfoFields);

		setFilters(headerList);
		jList.setSelectedValue(selectedHeader, true);
		setPreferredSize(new Dimension(AddOrEditVariantFiltersDialog.LARGE_PANELS_PREFERRED_WIDTH, getPreferredSize().height));
	}


	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == hideInfoFields ) {
			updateListContent();
		}
	}


	/**
	 * Sets the list of VCF fields to display in this panel
	 * @param headerList
	 */
	public void setFilters(List<VCFHeaderType> headerList) {
		this.headerList = headerList;
		jList.clearSelection();
		updateListContent();
	}


	/**
	 * Updates the content of the list showing the fields of the VCF file
	 */
	private void updateListContent() {
		Object selectedValue = jList.getSelectedValue();
		DefaultListModel model = (DefaultListModel) jList.getModel();
		boolean isInfoFieldFound = false;
		model.removeAllElements();
		if (headerList != null) {
			for (VCFHeaderType currentHeader: headerList) {
				if (currentHeader instanceof VCFHeaderInfoType) {
					isInfoFieldFound = true;
					if (!hideInfoFields.isSelected()) {
						model.addElement(currentHeader);
					}
				} else {
					model.addElement(currentHeader);
				}
			}
		}
		hideInfoFields.setVisible(isInfoFieldFound);
		if ((selectedValue == null) || (selectedValue instanceof VCFHeaderInfoType)) {
			jList.setSelectedIndex(0);
		} else {
			jList.setSelectedValue(selectedValue, false);
		}
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (jList.getSelectedValue() != null) {
			firePropertyChange(FILTER_ID_PROPERTY_NAME, null, jList.getSelectedValue());
		}
	}
}
