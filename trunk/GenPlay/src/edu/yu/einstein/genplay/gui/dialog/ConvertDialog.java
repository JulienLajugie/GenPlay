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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import edu.yu.einstein.genplay.core.operation.converter.ConverterFactory;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicListView;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.gui.customComponent.customPanel.OutputLayerPanel;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Images;


/**
 * @author Nicolas Fourel
 */
public class ConvertDialog extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -895612710328946926L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private int	approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not
	private JPanel binPanel;
	private JPanel scwPanel;

	// Input layer elements
	private final Layer<?> 		inputLayer;
	private final LayerType[] 	layerTypes;

	// Simple SCW list elements
	private JComboBox 	jcbSCWCalculMetod;// combo box for the score calculation method of SCW (only if the input layer is a gene list)

	// Bin list elements
	private JSpinner 	jsBinSize;
	private JComboBox 	jcbBinCalculMetod; 	// combo box for the score calculation method of bins

	// Output track elements
	private LayerType 				outputLayerType;	// The output layer type
	private final OutputLayerPanel	outputLayerPanel;	// Panel for the output options


	/**
	 * Constructor of {@link ConvertDialog}
	 * @param inputLayer the layer to convert
	 */
	public ConvertDialog (Layer<?> inputLayer) {
		this.inputLayer = inputLayer;
		if ((GenomicListView<?>) inputLayer.getData() != null) {
			layerTypes = ConverterFactory.getLayerTypes((GenomicListView<?>) inputLayer.getData());
		} else {
			layerTypes = null;
		}

		// Layout settings
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Get the panels
		JPanel inputLayerPanel = getInputLayerPanel();
		JPanel outputTypeLayerPanel = getOutputTypeLayer();
		outputLayerPanel = new OutputLayerPanel("Converted " + inputLayer.getName());
		JPanel validationPanel = getValidationPanel();

		// Add the input layer panel
		add(inputLayerPanel, gbc);

		// Add the output type layer panel
		gbc.gridy++;
		add(outputTypeLayerPanel, gbc);

		// Add the output track panel
		gbc.gridy++;
		add(outputLayerPanel, gbc);

		// Add the validation panel
		gbc.gridy++;
		gbc.weighty = 1;
		add(validationPanel, gbc);

		// Dialog settings
		setTitle("Convert the layer");
		setIconImages(Images.getApplicationImages());
		setResizable(false);
		setVisible(false);
		pack();
	}


	/**
	 * @return the panel for the {@link BinList} options
	 */
	private JPanel getBinPanel () {

		// Create bin size element
		JLabel jlBinSize = new JLabel("Select a bin size:");
		SpinnerNumberModel snm = new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 100);
		jsBinSize = new JSpinner(snm);

		// Create the calculation method elements
		JLabel jlCalculationMethod = new JLabel("Select a calculation method:");
		jcbBinCalculMetod = new JComboBox(ScoreOperation.getPileupOperations());
		jcbBinCalculMetod.setSelectedItem(ScoreOperation.ADDITION);

		// Creates the panel
		JPanel panel = new JPanel();

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weighty = 0;

		// Add the bin size elements
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.insets = new Insets(0, 0, 0, 0);
		panel.add(jlBinSize, gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		panel.add(jsBinSize, gbc);

		// Add the calculation method elements
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		panel.add(jlCalculationMethod, gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 0, 0);
		panel.add(jcbBinCalculMetod, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * @return the selected score calculation method
	 */
	public ScoreOperation getBinScoreCalculationMethod() {
		return (ScoreOperation) jcbBinCalculMetod.getSelectedItem();
	}


	/**
	 * @return the selected binsize
	 */
	public int getBinSize() {
		return (Integer) jsBinSize.getValue();
	}


	/**
	 * Creates the panel describing the input layer
	 * @return	the input layer panel
	 */
	private JPanel getInputLayerPanel () {
		// Creates panel elements
		JLabel jlName = new JLabel("Layer name: " + inputLayer.getName());
		JLabel jlType = new JLabel(inputLayer.getType().toString());

		// Creates the panel
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Input Layer"));

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Add the layer name
		gbc.insets = new Insets(5, 5, 0, 0);
		panel.add(jlName, gbc);

		// Add the layer type
		gbc.gridy++;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 5, 5, 0);
		panel.add(jlType, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * @return the output layer name
	 */
	public String getOutputLayerName() {
		return outputLayerPanel.getLayerName();
	}


	/**
	 * @return the output layer type
	 */
	public LayerType getOutputLayerType () {
		return outputLayerType;
	}


	/**
	 * @return the output track
	 */
	public Track getOutputTrack() {
		return outputLayerPanel.getOutputTrack();
	}


	/**
	 * @return the panel that manages the output layer type option
	 */
	private JPanel getOutputTypeLayer () {
		// Creates the panel
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Convertion Options"));

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Create and add elements
		ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < layerTypes.length; i++) {
			final LayerType currentLayerType = layerTypes[i];
			JRadioButton radio = new JRadioButton(currentLayerType.toString());
			group.add(radio);
			radio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					outputLayerType = currentLayerType;
					updateBinPanel(outputLayerType);
					updateSCWPanel(inputLayer.getType(), outputLayerType);
				}
			});

			if (i == 0) {
				radio.setSelected(true);
				outputLayerType = currentLayerType;
				gbc.insets = new Insets(5, 5, 0, 0);
				gbc.gridy = 0;
			} else {
				gbc.gridy++;
				gbc.insets = new Insets(0, 5, 0, 0);
				if (i == (layerTypes.length - 1)) {
					gbc.weighty = 1;
				}
			}
			panel.add(radio, gbc);
			if (currentLayerType == LayerType.BIN_LAYER) {
				gbc.gridy++;
				gbc.insets = new Insets(0, 25, 0, 0);
				binPanel = getBinPanel();
				updateBinPanel(outputLayerType);
				panel.add(binPanel, gbc);
			} else if (currentLayerType == LayerType.SIMPLE_SCW_LAYER) {
				gbc.gridy++;
				gbc.insets = new Insets(0, 25, 0, 0);
				scwPanel = getScwPanel();
				updateSCWPanel(inputLayer.getType(), outputLayerType);
				panel.add(scwPanel, gbc);
			}
		}
		// Return the panel
		return panel;
	}


	private JPanel getScwPanel() {
		Dimension dimension = new Dimension(100, 20);

		// Create the calculation method elements
		JLabel jlCalculationMethod = new JLabel("Select a calculation method:");
		jcbSCWCalculMetod = new JComboBox(ScoreOperation.getPileupOperations());
		jcbSCWCalculMetod.setSelectedItem(ScoreOperation.ADDITION);
		jcbSCWCalculMetod.setPreferredSize(dimension);

		// Creates the panel
		JPanel panel = new JPanel();

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weighty = 0;

		// Add the bin size elements
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 0, 0, 0);
		panel.add(jlCalculationMethod, gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets(5, 5, 0, 0);
		panel.add(jcbSCWCalculMetod, gbc);

		// Return the panel
		return panel;
	}


	/**
	 * @return the selected score calculation method
	 */
	public ScoreOperation getSCWScoreCalculationMethod() {
		return (ScoreOperation) jcbSCWCalculMetod.getSelectedItem();
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
				approved = APPROVE_OPTION;
				setVisible(false);
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

		// we want the size of the two buttons to be equal
		jbOk.setPreferredSize(jbCancel.getPreferredSize());

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}



	/**
	 * Shows the component.
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		// Sets dialog display options
		setLocationRelativeTo(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);

		return approved;
	}


	/**
	 * Enable/Disable the bin panel according to the output layer type
	 * @param layerType the output layer type
	 */
	private void updateBinPanel (LayerType layerType) {
		if (binPanel != null) {
			boolean enable = false;
			if ((layerType != null) && (layerType == LayerType.BIN_LAYER)) {
				enable = true;
			}
			jsBinSize.setEnabled(enable);
			jcbBinCalculMetod.setEnabled(enable);
		}
	}


	/**
	 * Enable/Disable the bin panel according to the output and input layer types
	 * @param layerType the output layer type
	 */
	private void updateSCWPanel(LayerType inputLayerType, LayerType outputLayerType) {
		if (scwPanel != null) {
			boolean enable = false;
			if ((inputLayerType != null) && (inputLayerType == LayerType.GENE_LAYER) &&
					(outputLayerType != null) && (outputLayerType == LayerType.SIMPLE_SCW_LAYER)) {
				enable = true;
			}
			scwPanel.setEnabled(enable);
			jcbSCWCalculMetod.setEnabled(enable);
		}
	}
}
