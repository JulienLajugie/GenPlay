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
package edu.yu.einstein.genplay.gui.dialog.layerChooser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Images;

/**
 * Dialog that prompt the user to choose layers.
 * A list of selectable layer types can be specified.
 * A list of layers already selected can be specified.
 * @author Julien Lajugie
 */
public class LayerChooserDialog extends JDialog {

	/** Return value when OK has been clicked. */
	public static final int APPROVE_OPTION = 0;

	/** Return value when Cancel has been clicked. */
	public static final int CANCEL_OPTION = 1;

	/** generated serial ID */
	private static final long serialVersionUID = 1667557444873634190L;

	/** Window size */
	private static final Dimension DIALOG_SIZE = new Dimension(600, 600);

	private int	approved = CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	private  List<Layer<?>>		layers;						// List of layers displayed in the table
	private  List<Layer<?>>		selectedLayers;				// List of layers selected
	private  LayerType[]		selectableLayerTypes;		// type of layer types that can be selected.  Any type can be selected if null
	private	 boolean			isMultiselectable;			// true if more than one layer can be selected


	/**
	 * Constructor of {@link LayerChooserDialog}
	 */
	public LayerChooserDialog () {
		super();
		layers = new ArrayList<Layer<?>>();
		selectedLayers = new ArrayList<Layer<?>>();
		isMultiselectable = false;
	}


	/**
	 * @return the first element of the list of selected layers.
	 * This method return the only layer selected if the property
	 * isMultiselectable is set to false.
	 */
	public Layer<?> getSelectedLayer() {
		if ((selectedLayers == null) || selectedLayers.isEmpty()) {
			return null;
		} else {
			return selectedLayers.get(0);
		}
	}


	/**
	 * @return the list of selected layers
	 */
	public List<Layer<?>> getSelectedLayers() {
		return selectedLayers;
	}


	/**
	 * Initializes dialog components
	 */
	private void init() {
		// panel to select the layers
		final LayerChooserPanel layerChooserPanel =
				new LayerChooserPanel(layers, selectedLayers, selectableLayerTypes, isMultiselectable);

		// Confirm button
		JButton jbConfirm = new JButton("Ok");
		jbConfirm.setToolTipText(ProjectFrame.CONFIRM_FILES);
		jbConfirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedLayers = layerChooserPanel.getSelectedLayers();
				approved = APPROVE_OPTION;
				dispose();
			}
		});
		getRootPane().setDefaultButton(jbConfirm);

		// Cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.setToolTipText(ProjectFrame.CANCEL_FILES);
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				approved = CANCEL_OPTION;
				dispose();
			}
		});

		// we want the size of the two buttons to be equal
		jbConfirm.setPreferredSize(jbCancel.getPreferredSize());

		//BotPane
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(jbConfirm);
		buttonPanel.add(jbCancel);

		//Add panels
		BorderLayout border = new BorderLayout();
		setLayout(border);
		add(layerChooserPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		//JDialog information
		setSize(DIALOG_SIZE);
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
	}


	/**
	 * Sets the list of the layers displayed in the chooser dialog
	 * @param layers list of layer
	 */
	public void setLayers(List<Layer<?>> layers) {
		this.layers = layers;
	}


	/**
	 * @param isMultiselectable set to true so more than one layer can be selected
	 */
	public void setMultiselectable(boolean isMultiselectable) {
		this.isMultiselectable = isMultiselectable;
	}


	/**
	 * Sets the layer types that can be selected
	 * @param selectableLayerTypes list of layer type that can be selected
	 */
	public void setSelectableLayerTypes(LayerType[] selectableLayerTypes) {
		this.selectableLayerTypes = selectableLayerTypes;
	}


	/**
	 * Sets the list of selected layers
	 * @param selectedLayers list of layer
	 */
	public void setSelectedLayers(List<Layer<?>> selectedLayers) {
		this.selectedLayers = selectedLayers;
	}


	/**
	 * Displays the layer chooser dialog
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @param title		title of the window
	 * @return 			APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent, String title) {
		init();
		setLocationRelativeTo(parent);
		setTitle(title);
		setIconImages(Images.getApplicationImages());
		setVisible(true);
		return approved;
	}
}
