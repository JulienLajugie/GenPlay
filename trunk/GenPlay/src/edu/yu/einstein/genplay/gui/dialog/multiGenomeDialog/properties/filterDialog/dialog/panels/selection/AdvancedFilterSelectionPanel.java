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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filterDialog.dialog.panels.selection;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.filterDialog.dialog.panels.EditingPanel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class AdvancedFilterSelectionPanel extends EditingPanel<FilterInterface> implements ListSelectionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;
	private JList jList;
	private DefaultListModel model;


	/**
	 * Constructor of {@link AdvancedFilterSelectionPanel}
	 */
	public AdvancedFilterSelectionPanel() {
		super("Advanced Filters");
	}


	@Override
	protected void initializeContentPanel() {
		((FlowLayout) contentPanel.getLayout()).setHgap(0);
		((FlowLayout) contentPanel.getLayout()).setVgap(0);

		// Creates the list
		model = new DefaultListModel();
		jList = new JList();
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setModel(model);
		jList.addListSelectionListener(this);

		// Creates the content panel
		contentPanel.add(jList);
	}


	@Override
	public void update(Object object) {
		List<FilterInterface> list = getFilterList(object);
		if (list != null) {
			String[] names = new String[list.size()];
			model.clear();
			for (int i = 0; i < list.size(); i++) {
				model.addElement(list.get(i));
				names[i] = list.get(i).toString();
			}
			int width = getMaxStringLength(names);
			int height = getStringHeight() * list.size();

			Dimension newDimension = initializeContentPanelSize(width, height);
			jList.setPreferredSize(newDimension);

			repaint();
		}
	}


	/**
	 * Tries to cast an object to a list of {@link VCFFile}
	 * @param object the object to cast
	 * @return	the casted object or null
	 */
	@SuppressWarnings("unchecked")
	private List<FilterInterface> getFilterList (Object object) {
		if ((object instanceof List<?>) && (((List<?>)object).size() > 0) && (((List<?>)object).get(0) instanceof FilterInterface)) {
			return (List<FilterInterface>) object;
		}
		return null;
	}


	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getValueIsAdjusting() == false) {
			if (jList.getSelectedIndex() != -1) {
				FilterInterface selectedElement = (FilterInterface) jList.getSelectedValue();
				if (!selectedElement.equals(element)) {
					setElement(selectedElement);
				}
			}
		}
	}


	/**
	 * @return the selected VCF File
	 */
	public FilterInterface getSelectedFilter () {
		return (FilterInterface) jList.getSelectedValue();
	}


	@Override
	public String getErrors() {
		String errors = "";
		if (getSelectedFilter() == null) {
			errors += "Filter selection\n";
		}
		return errors;
	}


	@Override
	public void reset() {
		model = new DefaultListModel();
		jList.clearSelection();
		element = null;
	}


	@Override
	public void initialize(FilterInterface element) {
		int index = 0;
		while (!((FilterInterface)model.getElementAt(index)).getName().equals(element.getName())) {
			index++;
		}
		jList.setSelectedIndex(index);
		setElement(element);
	}

}
