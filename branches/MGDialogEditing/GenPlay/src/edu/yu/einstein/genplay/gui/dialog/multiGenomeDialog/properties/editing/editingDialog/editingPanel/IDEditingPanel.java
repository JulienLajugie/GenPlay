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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class IDEditingPanel extends EditingPanel<VCFHeaderType> implements ListSelectionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;
	private JList jList;
	private DefaultListModel model;


	/**
	 * Constructor of {@link IDEditingPanel}
	 */
	public IDEditingPanel() {
		super("ID");
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
	}


	@Override
	public void update(Object object) {
		VCFFile file = getVCFFile(object);
		if (file != null) {
			List<VCFHeaderType> headers = file.getHeader().getAllHeader();
			String[] paths = new String[headers.size()];
			model.clear();
			for (int i = 0; i < headers.size(); i++) {
				model.addElement(headers.get(i));
				paths[i] = headers.get(i).toString();
			}
			int width = getMaxStringLength(paths);
			int height = getStringHeight() * headers.size();

			Dimension newDimension = initializeContentPanelSize(width, height);
			jList.setPreferredSize(newDimension);
			
			// Creates the content panel
			contentPanel.add(jList);
			
			repaint();
		}
	}


	/**
	 * Tries to cast an object to a {@link VCFFile}
	 * @param object the object to cast
	 * @return	the casted object or null
	 */
	private VCFFile getVCFFile (Object object) {
		if (object instanceof VCFFile) {
			return (VCFFile) object;
		}
		return null;
	}


	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getValueIsAdjusting() == false) {
			if (jList.getSelectedIndex() != -1) {
				VCFHeaderType selectedElement = (VCFHeaderType) jList.getSelectedValue();
				if (!selectedElement.equals(element)) {
					setElement(selectedElement);
				}
			}
		}
	}


	/**
	 * @return the selected ID
	 */
	public VCFHeaderType getID () {
		return element;
	}


	@Override
	public String getErrors() {
		String errors = "";
		if (getID() == null) {
			errors += "ID selection\n";
		}
		return errors;
	}


	@Override
	public void reset() {
		model.clear();
		jList.clearSelection();
		element = null;

		Dimension newDimension = new Dimension(0, 0);
		jList.setPreferredSize(newDimension);
		
		setNewContentPanel(new JPanel());
		((FlowLayout) contentPanel.getLayout()).setHgap(0);
		((FlowLayout) contentPanel.getLayout()).setVgap(0);
	}


	@Override
	public void initialize(VCFHeaderType element) {
		int index = -1;
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i).equals(element)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			jList.setSelectedIndex(index);
			setElement(element);
		}
	}

}
