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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFID.IDFilterInterface;
import edu.yu.einstein.genplay.core.multiGenome.filter.utils.FormatFilterOperatorType;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.FiltersData;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserPanel;
import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserTableModel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.panels.FilePanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.panels.FilterIDPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.panels.FilterValuesPanel;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.newFilterDialog.panels.MultiGenomePanel;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;


public class AddOrEditVariantFiltersDialog extends JDialog implements PropertyChangeListener {

	/** Generated serial ID */
	private static final long serialVersionUID = -8357422979931946414L;

	/** Return value when OK has been clicked. */
	public static final int APPROVE_OPTION = 0;

	/** Return value when Cancel has been clicked. */
	public static final int CANCEL_OPTION = 1;

	/** Preferred height of the layer chooser */
	private static final int LAYER_PANEL_PREFERRED_HEIGHT = 200;

	public static final int LARGE_PANELS_PREFERRED_WIDTH = 700;

	public static FiltersData showAddDialog(Component parentComponent) {
		AddOrEditVariantFiltersDialog dialog = new AddOrEditVariantFiltersDialog(null);
		dialog.setTitle("Create Filter");
		dialog.setLocationRelativeTo(parentComponent);
		dialog.setVisible(true);
		if (dialog.approved == APPROVE_OPTION) {
			return dialog.filtersData;
		} else {
			return null;
		}
	}


	public static FiltersData showEditDialog(Component parentComponent, FiltersData data) {
		AddOrEditVariantFiltersDialog dialog = new AddOrEditVariantFiltersDialog(data);
		dialog.setTitle("Edit Filter");
		dialog.setLocationRelativeTo(parentComponent);
		dialog.setVisible(true);
		if (dialog.approved == APPROVE_OPTION) {
			return dialog.filtersData;
		} else {
			return null;
		}
	}


	private final FilePanel 			filePanel;
	private final LayerChooserPanel 	layerPanel;
	private final FilterIDPanel 		idPanel;
	private final FilterValuesPanel 	valuePanel;
	private final MultiGenomePanel 		genomePanel;
	private final JButton 				jbOk;
	private final FiltersData 			filtersData;						// The current filter data (can be null)


	private int	approved = CANCEL_OPTION;			// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not


	private AddOrEditVariantFiltersDialog(FiltersData filtersData) {
		super();

		List<VCFFile> fileList = ProjectManager.getInstance().getMultiGenomeProject().getAllVCFFiles();
		VCFFile selectedFile;
		List<VCFHeaderType> headerList;
		VCFHeaderType selectedHeader;
		List<String> selectedGenomes = null;
		FormatFilterOperatorType selectedOperator = null;

		if(filtersData == null) {
			selectedFile = fileList.get(0);
			headerList = selectedFile.getHeader().getAllSortedHeader();
			selectedHeader = headerList.get(0);
			this.filtersData = new FiltersData();
			this.filtersData.setMGFilter(new VCFFilter());
			((VCFFilter) this.filtersData.getMGFilter()).setVCFFile(selectedFile);
		} else {
			this.filtersData  = filtersData;
			selectedFile = filtersData.getReader();
			headerList = selectedFile.getHeader().getAllSortedHeader();
			selectedHeader = ((IDFilterInterface) filtersData.getFilter()).getHeaderType();
			selectedGenomes = ((IDFilterInterface) filtersData.getMGFilter().getFilter()).getGenomeNames();
			selectedOperator = ((IDFilterInterface) filtersData.getMGFilter().getFilter()).getOperator();
		}

		filePanel = new FilePanel(fileList, selectedFile);
		layerPanel = createLayerChooser();
		idPanel = new FilterIDPanel(headerList, selectedHeader);
		valuePanel = new FilterValuesPanel(selectedHeader, this.filtersData.getFilter());
		genomePanel = new MultiGenomePanel(selectedFile.getHeader().getGenomeNames(), selectedHeader, selectedGenomes, selectedOperator);
		jbOk = new JButton("Ok");
		JPanel validationPanel = createValidationPanel();

		registerListeners();

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		add(filePanel, gbc);

		gbc.gridy = 1;
		add(layerPanel, gbc);

		gbc.gridy = 2;
		add(idPanel, gbc);

		gbc.gridwidth = 1;
		gbc.gridy = 3;
		add(valuePanel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 0.9;
		add(genomePanel, gbc);

		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.gridy = 4;
		gbc.gridx = 0;
		add(validationPanel, gbc);

		getContentPane().setBackground(validationPanel.getBackground());
		setIconImages(Images.getApplicationImages());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		pack();
	}


	private LayerChooserPanel createLayerChooser() {
		List<Layer<?>> availableLayers = MainFrame.getInstance().getTrackListPanel().getModel().getAllLayers();
		availableLayers = Utils.getLayers(availableLayers, new LayerType[] {LayerType.VARIANT_LAYER});
		List<Layer<?>> selectedLayers = null;
		if (filtersData.getLayers() != null) {
			selectedLayers = Arrays.asList(filtersData.getLayers());
		}
		LayerChooserPanel chooser = new LayerChooserPanel(availableLayers, selectedLayers, new LayerType[] {LayerType.VARIANT_LAYER}, true);
		chooser.setBorder(new TitledBorder("Select layer(s) to filter"));
		TableColumnModel tcm = chooser.getTable().getColumnModel();
		TableColumn layerTypeColumn = tcm.getColumn(LayerChooserTableModel.LAYER_TYPE_INDEX);
		tcm.removeColumn(layerTypeColumn);
		chooser.setPreferredSize(new Dimension(LARGE_PANELS_PREFERRED_WIDTH, LAYER_PANEL_PREFERRED_HEIGHT));
		chooser.setMinimumSize(chooser.getPreferredSize());
		return chooser;
	}


	/**
	 * Creates the panel that contains OK and CANCEL buttons
	 * @return the panel
	 */
	private JPanel createValidationPanel() {
		// Creates the ok button
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		// Creates the cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		jbOk.setPreferredSize(jbCancel.getPreferredSize());
		jbOk.setEnabled(isFilterValid());

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}


	private boolean isFilterValid() {
		if ((layerPanel.getSelectedLayers() == null) || layerPanel.getSelectedLayers().isEmpty()) {
			return false;
		}
		if ((genomePanel != null) && genomePanel.isVisible() && !genomePanel.isSelectionValid()) {
			return false;
		}
		return true;
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(FilePanel.FILE_CHANGE_PROPERTY_NAME)) {
			VCFFile vcfFile = (VCFFile) evt.getNewValue();
			genomePanel.setGenomeList(vcfFile.getHeader().getGenomeNames());
			idPanel.setFilters(vcfFile.getHeader().getAllSortedHeader());
		} else if (evt.getPropertyName().equals(LayerChooserPanel.SELECTED_LAYERS_PROPERTY_NAME)) {

		} else if (evt.getPropertyName().equals(FilterIDPanel.FILTER_ID_PROPERTY_NAME)) {
			valuePanel.setHeaderType((VCFHeaderType) evt.getNewValue());
			genomePanel.setHeaderType((VCFHeaderType) evt.getNewValue());
		}
		jbOk.setEnabled(isFilterValid());
	}


	private void registerListeners() {
		filePanel.addPropertyChangeListener(FilePanel.FILE_CHANGE_PROPERTY_NAME, this);
		layerPanel.addPropertyChangeListener(LayerChooserPanel.SELECTED_LAYERS_PROPERTY_NAME, this);
		idPanel.addPropertyChangeListener(FilterIDPanel.FILTER_ID_PROPERTY_NAME, this);
		genomePanel.addPropertyChangeListener(MultiGenomePanel.IS_SELECTION_VALID_PROPERTY_NAME, this);
	}
}

