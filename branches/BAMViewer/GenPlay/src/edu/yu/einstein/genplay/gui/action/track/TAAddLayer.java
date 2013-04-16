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
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;


/**
 * Adds a layer to a specified track
 * @author Julien Lajugie
 */
public class TAAddLayer extends TrackListAction {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = -6765104407932350110L;


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddLayer.class.getName();


	private static final String ACTION_NAME = "Add Layer"; 						// action name
	private static final String DESCRIPTION = "Add a layer to selected track";	// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_A; 						// mnemonic key


	/**
	 * Adds a layer to a specified track
	 */
	public TAAddLayer() {
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	public void actionPerformed(ActionEvent evt) {
		Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			LayerType[] layerTypes = getLayerTypes();
			LayerType selectedLayerType = (LayerType)JOptionPane.showInputDialog(
					getRootPane(),
					"Please select the type of layer to add",
					"Layer Type",
					JOptionPane.QUESTION_MESSAGE,
					null,
					layerTypes,
					layerTypes[0]);
			if (selectedLayerType != null) {
				switch (selectedLayerType) {
				case BIN_LAYER:
					new TAAddBinLayer().actionPerformed(evt);
					break;
				case SCW_LAYER:
					new TAAddSCWLayer().actionPerformed(evt);
					break;
				case GENE_LAYER:
					new TAAddGeneLayer().actionPerformed(evt);
					break;
				case BAM_LAYER:
					new TAAddBAMLayer().actionPerformed(evt);
					break;
				case REPEAT_FAMILY_LAYER:
					new TAAddRepeatLayer().actionPerformed(evt);
					break;
				case NUCLEOTIDE_LAYER:
					new TAAddNucleotideLayer().actionPerformed(evt);
					break;
				case VARIANT_LAYER:
					new TAAddVariantLayer().actionPerformed(evt);
					break;
				case MASK_LAYER:
					new TAAddMask().actionPerformed(evt);
					break;
				default:
					// do nothing
					break;
				}
			}
		}
	}


	/**
	 * @return the {@link LayerType} that can be added
	 */
	private LayerType[] getLayerTypes () {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			LayerType[] layerTypes = {LayerType.BIN_LAYER, LayerType.SCW_LAYER, LayerType.GENE_LAYER, LayerType.REPEAT_FAMILY_LAYER, LayerType.NUCLEOTIDE_LAYER, LayerType.VARIANT_LAYER, LayerType.MASK_LAYER};
			return layerTypes;
		}
		LayerType[] layerTypes = {LayerType.BIN_LAYER, LayerType.SCW_LAYER, LayerType.GENE_LAYER, LayerType.REPEAT_FAMILY_LAYER, LayerType.NUCLEOTIDE_LAYER, LayerType.MASK_LAYER};
		return layerTypes;
	}
}
