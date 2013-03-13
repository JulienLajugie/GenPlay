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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors.idEditors.IDEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors.idEditors.IDFlagEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors.idEditors.IDGTEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors.idEditors.IDNumberEditor;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editors.idEditors.IDStringEditor;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class FilterEditingPanel extends EditingPanel<FilterInterface> implements ActionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;
	private IDEditor filterEditor;
	private IDEditor specialFilterEditor;
	private JRadioButton regularRadioBox;
	private JRadioButton specialRadioBox;


	/**
	 * Constructor of {@link FilterEditingPanel}
	 */
	public FilterEditingPanel() {
		super("Filter");
	}


	@Override
	protected void initializeContentPanel() {}


	@Override
	public void update(Object object) {
		initEditors(object);

		JPanel panel;
		if (filterEditor != null) {
			panel = getCustomPanel();
		} else {
			panel = new JPanel();
		}

		setNewContentPanel(panel);
		initializeContentPanelSize(MINIMUM_WIDTH, panel.getPreferredSize().height + 10);
		repaint();
	}



	private void initEditors (Object object) {
		filterEditor = null;
		specialFilterEditor = null;

		if (object instanceof VCFHeaderBasicType) {
			VCFHeaderBasicType header = (VCFHeaderBasicType) object;
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
		} else if (object instanceof VCFHeaderAdvancedType) {
			VCFHeaderAdvancedType advancedHeader = (VCFHeaderAdvancedType) object;
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
		} else if (object instanceof VCFHeaderType) {
			VCFHeaderType header = (VCFHeaderType) object;
			if (header.getColumnCategory() == VCFColumnName.ALT) {
				filterEditor = new IDStringEditor();
			} else if (object instanceof VCFHeaderFilterType) {
				filterEditor = new IDFlagEditor();
			}
		}

		if (filterEditor != null) {
			filterEditor.setHeaderType((VCFHeaderType) object);
		}

		if (specialFilterEditor != null) {
			specialFilterEditor.setHeaderType((VCFHeaderType) object);
		}
	}


	/**
	 * Creates the panel containing the editor.
	 * If a special editor has been created (as for the GT field), it will be put on the top.
	 * @return the panel containing the editor(s)
	 */
	private JPanel getCustomPanel () {
		JPanel panel = null;
		if (specialFilterEditor != null) {
			panel = new JPanel();

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
			gbc.weightx = 1;
			gbc.weighty = 0;

			gbc.gridx = 0;
			gbc.gridy = 0;
			panel.add(specialRadioBox, gbc);

			gbc.gridy++;
			panel.add(specialFilterEditor.updatePanel(), gbc);
			specialFilterEditor.setEnabled(true);

			gbc.gridy++;
			gbc.insets = new Insets(10, 0, 0, 0);
			panel.add(regularRadioBox, gbc);

			gbc.gridy++;
			gbc.weighty = 1;
			panel.add(filterEditor.updatePanel(), gbc);
			filterEditor.setEnabled(false);
		} else {
			panel = filterEditor.updatePanel();
		}

		return panel;
	}


	/**
	 * @return the ID filter
	 */
	public FilterInterface getFilter () {
		if (filterEditor.isEnabled()) {
			return filterEditor.getFilter();
		}
		return specialFilterEditor.getFilter();
	}


	@Override
	public String getErrors() {
		String errors = "";

		if (filterEditor == null) {
			errors += "Filter selection\n";
		} else if (filterEditor.isEnabled()) {
			errors += filterEditor.getErrors();
		} else if (specialFilterEditor.isEnabled()) {
			errors += specialFilterEditor.getErrors();
		}

		return errors;
	}


	@Override
	public void reset() {
		resetContentPanel();
		element = null;
		filterEditor = null;
	}


	@Override
	public void initialize(FilterInterface element) {
		if (filterEditor != null) {
			if (element instanceof IDFilterInterface) {
				IDFilterInterface filter = (IDFilterInterface) element;
				if (element instanceof GenotypeIDFilter) {
					specialFilterEditor.initializesPanel(filter);
					enableSpecialPanel();
				} else {
					filterEditor.initializesPanel(filter);
					enableRegularPanel();
				}
			}
		}
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() instanceof JRadioButton) {
			JRadioButton radio = (JRadioButton) arg0.getSource();
			if (radio.equals(regularRadioBox)) {
				specialFilterEditor.setEnabled(false);
				filterEditor.setEnabled(true);
			} else {
				filterEditor.setEnabled(false);
				specialFilterEditor.setEnabled(true);
			}
		}
	}


	/**
	 * Enables the panel for regular filters
	 */
	private void enableRegularPanel () {
		if (specialFilterEditor != null) {
			specialFilterEditor.setEnabled(false);
		}
		if (regularRadioBox != null) {
			regularRadioBox.setSelected(true);
		}
		filterEditor.setEnabled(true);
	}


	/**
	 * Enables the panel for special filters
	 */
	private void enableSpecialPanel () {
		filterEditor.setEnabled(false);
		if (specialRadioBox != null) {
			specialRadioBox.setSelected(true);
		}
		if (specialFilterEditor != null) {
			specialFilterEditor.setEnabled(true);
		}
	}

}
