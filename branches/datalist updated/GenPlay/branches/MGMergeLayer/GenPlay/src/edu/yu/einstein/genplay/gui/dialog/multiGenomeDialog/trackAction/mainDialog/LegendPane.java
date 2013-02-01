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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.yu.einstein.genplay.gui.dialog.layerChooser.LayerChooserDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class LegendPane extends JPanel implements ActionListener {

	/** Generated default serial version ID */
	private static final long serialVersionUID = -5372729762581187391L;
	private static final String SELECT_TEXT = "Select Layer(s)";

	private final MultiGenomeTrackActionDialog dialog;
	private JButton jbLayers;
	private List<Layer<?>> selectedLayers;

	/**
	 * Constructor of {@link LegendPane}
	 * @param layer the selected {@link Layer}
	 */
	protected LegendPane (MultiGenomeTrackActionDialog dialog, Layer<?> layer) {
		this.dialog = dialog;
		List<Layer<?>> layers = new ArrayList<Layer<?>>();
		layers.add(layer);
		initialize(layers);
	}


	public void initialize (List<Layer<?>> layers) {
		this.selectedLayers = layers;
		removeAll();

		// Create the field set effect
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Layer(s)");
		setBorder(titledBorder);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 15, 0, 10);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Genomes values
		for (int i = 0; i < layers.size(); i++) {
			/*if (i == (layers.size() - 1)) {
				gbc.insets = lastValueInset;
			}*/
			gbc.gridy++;
			add(new JLabel(layers.get(i).getName()), gbc);
		}

		jbLayers = new JButton(SELECT_TEXT);
		jbLayers.addActionListener(this);
		jbLayers.setEnabled(false);

		gbc.gridy++;
		gbc.insets = new Insets(10, 15, 5, 0);
		add(jbLayers, gbc);

		dialog.revalidate();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// I compare the text of the button instead of the button itself since the button can be recreated on the way and does not match the source of the event.
		if (e.getSource() instanceof JButton) {
			String text = ((JButton)e.getSource()).getText();
			if (text.equals(SELECT_TEXT)) {
				LayerType[] filter = {LayerType.VARIANT_LAYER};
				List<Layer<?>> allLayers = MainFrame.getInstance().getTrackListPanel().getModel().getAllLayers();
				List<Layer<?>> layers = Utils.getLayers(allLayers, filter);

				LayerChooserDialog dialog = new LayerChooserDialog();
				dialog.setMultiselectable(true);
				dialog.setLayers(layers);
				dialog.setSelectedLayers(selectedLayers);
				if (dialog.showDialog(this, "Select Variant Layers") == LayerChooserDialog.APPROVE_OPTION) {
					initialize(dialog.getSelectedLayers());
				}
			}
		}
	}
}
