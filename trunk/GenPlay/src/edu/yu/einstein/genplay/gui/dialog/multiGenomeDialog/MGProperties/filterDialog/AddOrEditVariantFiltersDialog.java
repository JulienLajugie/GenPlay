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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.dataStructure.enums.VCFColumnName;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.filters.FiltersData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels.EditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels.FileSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels.FilterValuesPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels.IDFilterSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels.LayerSelectionPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels.MultiGenomeEditingPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterDialog.panels.OperatorEditingPanel;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 */
public class AddOrEditVariantFiltersDialog extends JDialog {

	/** Generated serial ID */
	private static final long serialVersionUID = -6130320386508798101L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;

	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private final List<EditingPanel<?>> 		editingPanelList;		// List of editing panel
	private final LayerSelectionPanel 			layerEditingPanel;		// Panel to edit the tracks
	private final FileSelectionPanel 			fileEditingPanel;		// Panel to edit the file
	private final IDFilterSelectionPanel 		IDEditingPanel;			// Panel to edit the ID
	private final MultiGenomeEditingPanel		genomeEditingPanel;		// Panel to edit the genomes
	private final OperatorEditingPanel 			operatorEditingPanel;	// Panel to edit the operator
	private final FilterValuesPanel 			filterValuesPanel;		// Panel to edit the filter

	private FiltersData currentData;						// The current filter data (can be null)
	private int			approved = CANCEL_OPTION;			// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not


	/**
	 * Constructor of {@link AddOrEditVariantFiltersDialog}
	 */
	public AddOrEditVariantFiltersDialog () {
		// Layers editing panel
		layerEditingPanel = new LayerSelectionPanel();

		// File editing panel
		fileEditingPanel = new FileSelectionPanel();

		// ID editing panel
		IDEditingPanel = new IDFilterSelectionPanel();
		fileEditingPanel.addPanelListener(IDEditingPanel);

		// Genomes editing panel
		genomeEditingPanel = new MultiGenomeEditingPanel(true);
		fileEditingPanel.addPanelListener(genomeEditingPanel);
		IDEditingPanel.addPanelListener(genomeEditingPanel);

		// Operator editing panel
		operatorEditingPanel = new OperatorEditingPanel();
		fileEditingPanel.addPanelListener(operatorEditingPanel);
		IDEditingPanel.addPanelListener(operatorEditingPanel);
		genomeEditingPanel.addPanelListener(operatorEditingPanel);

		// Filter editing panel
		filterValuesPanel = new FilterValuesPanel();
		IDEditingPanel.addPanelListener(filterValuesPanel);

		// List of editing panel
		editingPanelList = new ArrayList<EditingPanel<?>>();
		editingPanelList.add(layerEditingPanel);
		editingPanelList.add(fileEditingPanel);
		editingPanelList.add(IDEditingPanel);
		editingPanelList.add(genomeEditingPanel);
		editingPanelList.add(operatorEditingPanel);
		editingPanelList.add(filterValuesPanel);
	}


	/**
	 * @return true is the current selection is valid
	 */
	private boolean approveSelection () {
		String errors = "";
		for (EditingPanel<?> panel: editingPanelList) {
			errors += panel.getErrors();
		}

		if (errors.isEmpty()) {
			return true;
		} else {
			showErrorDialog(errors);
			return false;
		}
	}


	/**
	 * Create the panel containing all the editing panels
	 * @param 	panelList the list of editing panels
	 * @return	the panel containing all the editing panels
	 */
	private JPanel getPanel (List<EditingPanel<?>> panelList) {
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		panel.setLayout(layout);

		for (EditingPanel<?> editingPanel: panelList) {
			panel.add(editingPanel);
		}

		return panel;
	}


	/**
	 * Creates the panel that contains OK and CANCEL buttons
	 * @return the panel
	 */
	private JPanel getValidationPanel () {
		// Creates the ok button
		JButton jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (approveSelection()) {
					approved = APPROVE_OPTION;
					setVisible(false);
				}
			}
		});

		// Creates the cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = CANCEL_OPTION;
				setVisible(false);
			}
		});

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}


	private void initDialog() {
		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Add the panels
		add(getPanel(editingPanelList), BorderLayout.CENTER);
		add(getValidationPanel(), BorderLayout.SOUTH);

		// Dialog settings
		setTitle("Create Filters");
		setIconImages(Images.getApplicationImages());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setVisible(false);
		pack();
	}


	private void initializePanels () {
		fileEditingPanel.update(ProjectManager.getInstance().getMultiGenomeProject().getAllVCFFiles());

		if (currentData != null) {
			fileEditingPanel.initialize(currentData.getReader());

			IDEditingPanel.initialize(((IDFilterInterface) currentData.getFilter()).getHeaderType());

			genomeEditingPanel.initialize(((IDFilterInterface) currentData.getMGFilter().getFilter()).getGenomeNames());

			operatorEditingPanel.initialize(((IDFilterInterface) currentData.getMGFilter().getFilter()).getOperator());

			filterValuesPanel.initialize(currentData.getFilter());

			layerEditingPanel.initialize(currentData.getLayers());
		}
	}


	/**
	 * Retrieves all the information from the panel in order to create/set the filter data object.
	 * If a current filter data has been defined, it will be set and returned.
	 * If no current filter data has been defined, a new one will be created.
	 * @return the {@link FiltersData}
	 */
	private List<FiltersData> retrieveData () {
		VCFFile vcfFile = fileEditingPanel.getSelectedVCFFile();
		IDFilterInterface IDFilter = (IDFilterInterface) filterValuesPanel.getFilter();
		Layer<?>[] layers = layerEditingPanel.getSelectedLayers();

		if ((IDFilter.getHeaderType() != null) && (IDFilter.getHeaderType().getColumnCategory() == VCFColumnName.FORMAT)) {
			List<String> genomeNames = genomeEditingPanel.getSelectedGenomes();
			FormatFilterOperatorType operator = operatorEditingPanel.getSelectedOperator();
			IDFilter.setGenomeNames(genomeNames);
			IDFilter.setOperator(operator);
		} else {
			IDFilter.setGenomeNames(null);
			IDFilter.setOperator(null);
		}

		List<FiltersData> result = new ArrayList<FiltersData>();
		FiltersData data;

		if (currentData != null) {
			((VCFFilter) currentData.getMGFilter()).setVCFFile(vcfFile);
			currentData.getMGFilter().setFilter(IDFilter);
			currentData.setLayers(layers);

			data = currentData;
		} else {
			MGFilter filter = new VCFFilter(IDFilter, vcfFile);
			data = new FiltersData(filter, layers);
		}

		result.add(data);

		return result;
	}


	public void setData(FiltersData data) {
		currentData = data;
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null;
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public List<FiltersData> showDialog(Component parent) {
		initializePanels();
		List<FiltersData> data = null;
		initDialog();
		setLocationRelativeTo(parent);
		setModal(true);
		setVisible(true);
		if (approved == APPROVE_OPTION) {
			data = retrieveData();
		}
		currentData = null;
		return data;
	}


	/**
	 * Shows an error message
	 * @param errors error message
	 */
	private void showErrorDialog (String errors) {
		String message = "Some errors have been found, please check out the following points:\n";
		//String[] errorsArray = errors.split("\n");
		String[] errorsArray = Utils.split(errors, '\n');
		for (int i = 0; i < errorsArray.length; i++) {
			if (!errorsArray[i].isEmpty()) {
				message += i + 1 + ": " + errorsArray[i];
				if (i < (errorsArray.length - 1)) {
					message += "\n";
				}
			}
		}
		JOptionPane.showMessageDialog(this, message, "Settings are not valid", JOptionPane.ERROR_MESSAGE);
	}
}
