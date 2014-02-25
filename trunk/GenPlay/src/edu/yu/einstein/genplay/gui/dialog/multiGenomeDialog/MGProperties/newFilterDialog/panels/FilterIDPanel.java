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


/**
 * @author Julien Lajugie
 */
public class FilterIDPanel extends JPanel implements ListSelectionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = 5470522161551881606L;

	/** File change property name */
	public static final String FILTER_ID_PROPERTY_NAME = "Filter ID Change";

	/** jList displaying the different filters */
	private final JList jList;

	/** check box to hide the info fields*/
	private final JCheckBox hideInfoFields;


	public FilterIDPanel(List<VCFHeaderType> headerList, VCFHeaderType selectedHeader) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createTitledBorder("Select field to filter"));

		jList = new JList();
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setModel(new DefaultListModel());
		jList.getSelectionModel().addListSelectionListener(this);
		JScrollPane jsp = new JScrollPane(jList);

		hideInfoFields = new JCheckBox("Hide INFO field");

		add(jsp);
		add(hideInfoFields);

		setFilters(headerList);
		jList.setSelectedValue(selectedHeader, true);
	}


	public void setFilters(List<VCFHeaderType> headerList) {
		boolean isInfoFieldFound = false;
		DefaultListModel model = (DefaultListModel) jList.getModel();
		model.removeAllElements();
		if (headerList != null) {
			for (VCFHeaderType currentHeader: headerList) {
				model.addElement(currentHeader);
				if (currentHeader instanceof VCFHeaderInfoType) {
					isInfoFieldFound = true;
				}
			}
		}
		hideInfoFields.setVisible(isInfoFieldFound);
		jList.setSelectedIndex(0);
		revalidate();
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		firePropertyChange(FILTER_ID_PROPERTY_NAME, null, jList.getSelectedValue());
	}
}
