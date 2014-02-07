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
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.IO.dataReader.GeneReader;
import edu.yu.einstein.genplay.core.IO.dataReader.NucleotideReader;
import edu.yu.einstein.genplay.core.IO.dataReader.RepeatReader;
import edu.yu.einstein.genplay.core.IO.dataReader.SCWReader;
import edu.yu.einstein.genplay.core.IO.extractor.Extractor;
import edu.yu.einstein.genplay.core.IO.extractor.ExtractorFactory;
import edu.yu.einstein.genplay.core.IO.extractor.TransferableTrackExtractor;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;


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
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TAAddLayer.class.getName();

	private static final String ACTION_NAME = "Add Layer(s)"; 										// action name
	private static final String DESCRIPTION = "Add one or multiple layers to the selected track";	// tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_A; 											// mnemonic key

	private File fileToLoad; // the file to load


	/**
	 * Loads one or multiple layers to a specified track
	 */
	public TAAddLayer() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	/**
	 * Loads layer(s) extracted from the specified file to a specified track
	 */
	public TAAddLayer(File fileToLoad) {
		super();
		this.fileToLoad = fileToLoad;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@Override
	public void actionPerformed(ActionEvent evt) {
		try {
			Track selectedTrack = getTrackListPanel().getSelectedTrack();
			if (selectedTrack != null) {
				if (fileToLoad == null) {
					fileToLoad = Utils.chooseFileToLoad(getRootPane(), "Choose File to Load", Utils.getReadableLayerFileFilters(), true);
				}
				if (fileToLoad != null) {
					Extractor extractor = ExtractorFactory.getExtractor(fileToLoad);
					if (extractor instanceof TransferableTrackExtractor) {
						new TAAddGenPlayTrack(extractor).actionPerformed(evt);
					} else {
						LayerType[] layerTypes = getLayerTypes(extractor);
						LayerType selectedLayerType = null;
						if (layerTypes.length == 0) {
							throw new InvalidFileTypeException();
						} else if (layerTypes.length == 1) {
							selectedLayerType = layerTypes[0];
						} else {
							selectedLayerType = (LayerType)JOptionPane.showInputDialog(
									getRootPane(),
									"Please select the type of layer to add",
									"Layer Type",
									JOptionPane.QUESTION_MESSAGE,
									null,
									layerTypes,
									layerTypes[0]);
						}
						if (selectedLayerType != null) {
							switch (selectedLayerType) {
							case SCW_LAYER:
								new TAAddSCWLayer(extractor).actionPerformed(evt);
								break;
							case GENE_LAYER:
								new TAAddGeneLayer(extractor).actionPerformed(evt);
								break;
							case REPEAT_FAMILY_LAYER:
								new TAAddRepeatLayer(extractor).actionPerformed(evt);
								break;
							case NUCLEOTIDE_LAYER:
								new TAAddNucleotideLayer(extractor).actionPerformed(evt);
								break;
							case MASK_LAYER:
								new TAAddMaskLayer(extractor).actionPerformed(evt);
								break;
							default:
								// do nothing
								break;
							}
						}
					}
				}
			}
		} catch (InvalidFileTypeException e) {
			JOptionPane.showMessageDialog(getRootPane(), "The specified file type cannot be detected.", "Invalid File Type", JOptionPane.WARNING_MESSAGE, null);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(getRootPane(), "The specified file cannot be loaded.", "Invalid File", JOptionPane.WARNING_MESSAGE, null);
		}
		// reset the file to load
		fileToLoad = null;
	}


	/**
	 * @param extractor an extractor
	 * @return an array of layer types that can be extracted by the specified extractor
	 */
	private LayerType[] getLayerTypes(Extractor extractor) {
		List<LayerType> typeList = new ArrayList<LayerType>();
		if (extractor instanceof SCWReader) {
			typeList.add(LayerType.SCW_LAYER);
			typeList.add(LayerType.MASK_LAYER);
		}
		if (extractor instanceof GeneReader) {
			typeList.add(LayerType.GENE_LAYER);
		}
		if (extractor instanceof RepeatReader) {
			typeList.add(LayerType.REPEAT_FAMILY_LAYER);
		}
		if (extractor instanceof NucleotideReader) {
			typeList.add(LayerType.NUCLEOTIDE_LAYER);
		}
		return typeList.toArray(new LayerType[0]);
	}
}
