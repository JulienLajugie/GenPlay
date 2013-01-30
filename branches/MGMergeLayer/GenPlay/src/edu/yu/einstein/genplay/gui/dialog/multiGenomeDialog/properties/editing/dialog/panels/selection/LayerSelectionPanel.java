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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.selection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class LayerSelectionPanel extends EditingPanel<Layer<?>[]> implements ActionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;

	private JList jlElements;
	private JButton jbLayers;


	/**
	 * Constructor of {@link LayerSelectionPanel}
	 */
	public LayerSelectionPanel() {
		super("Layer(s)");
	}


	@Override
	public String getErrors() {
		String errors = "";
		if ((getSelectedLayers() == null) || (getSelectedLayers().length == 0)) {
			errors += "Layer(s) selection\n";
		}
		return errors;
	}


	/**
	 * @return the selected tracks
	 */
	public Layer<?>[] getSelectedLayers () {
		Layer<?>[] layers = new Layer<?>[jlElements.getModel().getSize()];
		for (int i = 0 ; i < layers.length; i++) {
			layers[i] = (Layer<?>) jlElements.getModel().getElementAt(i);
		}
		return layers;
	}


	private List<Layer<?>> getLayers () {
		List<Layer<?>> layers = new ArrayList<Layer<?>>();
		for (Layer<?> currentLayer: getSelectedLayers()) {
			layers.add(currentLayer);
		}
		return layers;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void initialize(Layer[] element) {
		if (element != null) {
			jlElements.setListData(element);
		}
	}


	@Override
	protected void initializeContentPanel() {
		reset();

		contentPanel = new JPanel();

		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(jlElements, BorderLayout.CENTER);
		contentPanel.add(jbLayers, BorderLayout.SOUTH);

		Dimension panelDimension = contentPanel.getSize();

		int width = panelDimension.width;
		int height = panelDimension.height;

		if (width <= EditingPanel.MINIMUM_WIDTH) {
			width = EditingPanel.MINIMUM_WIDTH;
		}
		if (height <= EditingPanel.CONTENT_HEIGHT) {
			height = EditingPanel.CONTENT_HEIGHT;
		}

		Dimension newDimension = new Dimension(width, height);
		contentPanel.setSize(newDimension);
		contentPanel.setPreferredSize(newDimension);
	}


	@Override
	public void reset() {
		jlElements = new JList();
		jbLayers = new JButton("Select Layer(s)");
		jbLayers.addActionListener(this);
	}


	@Override
	public void update(Object object) {}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(jbLayers)) {
			LayerChooserDialog dialog = new LayerChooserDialog();
			dialog.setLayers(MainFrame.getInstance().getTrackListPanel().getAllVariantLayers());
			dialog.setSelectedLayers(getLayers());
			if (dialog.showDialog(this, "Select Variant Layers") == LayerChooserDialog.APPROVE_OPTION) {
				jlElements.setListData(dialog.getSelectedLayers().toArray());
			}
		}
	}

}
