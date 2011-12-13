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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filters.IDEditors;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.AltFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.FilterFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.StringIDFilter;
import edu.yu.einstein.genplay.core.multiGenome.VCF.filtering.StringIDFilterInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class IDStringEditor implements IDEditor {

	private final static String PRESENT 	= "must contains";
	private final static String ABSENT 		= "must not contains";


	private VCFHeaderType 	id;					// Header ID
	private String			category;			// category of the filter
	private List<String>	defaultElements;
	private JComboBox		jcOption;			// Combo box for selecting wether the value must be present or not
	private JComboBox		jcValue;			// Editable combo box for selecting the value


	/**
	 * Constructor of {@link IDStringEditor}
	 */
	public IDStringEditor () {
		defaultElements = new ArrayList<String>();
	}


	@Override
	public void updatePanel(JPanel panel) {
		// Back up the size of the panel
		Dimension previousDimension = panel.getPreferredSize();

		// Remove everything from the panel
		panel.removeAll();

		// Gets boxes
		jcOption = getOptionBox();
		jcValue = getValueBox();

		// Layout
		GridLayout layout = new GridLayout(2, 1);
		layout.setVgap(5);
		layout.setHgap(5);
		panel.setLayout(layout);

		// Create a new dimension based on the previous one
		Dimension dimension = new Dimension((int)previousDimension.getWidth(), 50);

		// Restore size to former value
		panel.setPreferredSize(dimension);
		panel.setMinimumSize(dimension);

		// Add components to the panel
		panel.add(jcOption);
		panel.add(jcValue);
	}


	@Override
	public IDFilterInterface getFilter() {
		StringIDFilterInterface filter = null;
		
		if (category.equals("ALT") && id == null) {
			filter = new AltFilter();
		} else if (category.equals("FILTER") && id == null) {
			filter = new FilterFilter();
		} else {
			filter = new StringIDFilter();
			filter.setID(id);
			filter.setCategory(category);
		}
		
		if (jcValue.getSelectedItem() != null) {
			filter.setValue(jcValue.getSelectedItem().toString());
		} else {
			filter.setValue(null);
		}
		if (jcOption.getSelectedItem().toString().equals(PRESENT)) {
			filter.setRequired(true);
		} else {
			filter.setRequired(false);
		}
		
		return filter;
	}


	@Override
	public void setID(VCFHeaderType id) {
		this.id = id;
	}
	
	
	@Override
	public VCFHeaderType getID () {
		return id;
	}


	/**
	 * Creates the option box and return it
	 * @return the option box
	 */
	private JComboBox getOptionBox () {
		JComboBox box = new JComboBox();
		DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();
		model.addElement(PRESENT);
		model.addElement(ABSENT);
		return box;
	}


	/**
	 * Creates the value box and return it
	 * @return the value box
	 */
	private JComboBox getValueBox () {
		JComboBox box = new JComboBox();
		DefaultComboBoxModel model = (DefaultComboBoxModel) box.getModel();
		for (String s: defaultElements) {
			model.addElement(s);
		}
		box.setEditable(true);
		return box;
	}


	@Override
	public void initializesPanel(IDFilterInterface filter) {
		boolean isRequired = false;
		String value = null;
		if (filter instanceof StringIDFilter) {
			StringIDFilter castFilter = (StringIDFilter) filter;
			isRequired = castFilter.isRequired();
			value= castFilter.getValue();
		} else if (filter instanceof AltFilter) {
			AltFilter castFilter = (AltFilter) filter;
			isRequired = castFilter.isRequired();
			value= castFilter.getValue();
		} else if (filter instanceof FilterFilter) {
			FilterFilter castFilter = (FilterFilter) filter;
			isRequired = castFilter.isRequired();
			value= castFilter.getValue();
		} else {
			System.err.println("initializesPanel");
		}
		
		if (isRequired) {
			jcOption.setSelectedItem(PRESENT);
		} else {
			jcOption.setSelectedItem(ABSENT);
		}
		((DefaultComboBoxModel)jcValue.getModel()).addElement(value);
		jcValue.setSelectedItem(value);
	}


	@Override
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	@Override
	public String getCategory() {
		return category;
	}


	/**
	 * @param defaultElements the defaultElements to set
	 */
	public void setDefaultElements(List<String> defaultElements) {
		this.defaultElements = defaultElements;
	}

}
