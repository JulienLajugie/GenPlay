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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.genotype;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class GenomeMappingPanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = -541059954559358366L;

	private static final String NO_PROCESS_OPTION = "Do not process";

	private final JLabel titleLabel;
	private List<String> srcGenomeNames;
	private List<String> destGenomeNames;
	private List<JComboBox> boxList;


	/**
	 * Constructor of {@link GenomeMappingPanel}
	 */
	protected GenomeMappingPanel () {
		initialize(null, null);
		titleLabel = new JLabel("Please precise the genome names mapping:");
	}


	protected void initialize (List<String> srcGenomeNames, List<String> destGenomeNames) {
		removeAll();

		this.srcGenomeNames = srcGenomeNames;
		this.destGenomeNames = destGenomeNames;

		if ((srcGenomeNames != null) && (destGenomeNames != null)) {
			if ((srcGenomeNames.size() > 0) && (destGenomeNames.size() > 0)) {

				// Create the layout
				GridBagLayout layout = new GridBagLayout();
				setLayout(layout);
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.anchor = GridBagConstraints.FIRST_LINE_START;
				gbc.insets = new Insets(0, 0, 0, 0);
				gbc.gridy = 0;
				gbc.weightx = 1;
				gbc.weighty = 0;

				// Add the title
				gbc.gridwidth = 2;
				add(titleLabel, gbc);

				// Set constraints after the title
				gbc.insets = new Insets(5, 10, 0, 0);
				gbc.gridwidth = 1;
				gbc.anchor = GridBagConstraints.LINE_START;

				// Create and add labels and boxes
				boxList = new ArrayList<JComboBox>();
				for (int i = 0; i < destGenomeNames.size(); i++) {
					String destGenomeName = destGenomeNames.get(i);
					JLabel currentLabel = new JLabel(destGenomeName + ":");
					JComboBox currentBox = getComboBox();
					boxList.add(currentBox);

					if (i == (destGenomeNames.size() - 1)) {
						gbc.weighty = 1;
					}

					gbc.gridy++;
					gbc.gridx = 0;
					add(currentLabel, gbc);
					gbc.gridx = 1;
					add(currentBox, gbc);
				}
			}
		}
	}


	private JComboBox getComboBox () {
		Object[] o = new Object[srcGenomeNames.size() + 1];
		int index = 0;
		o[index] = NO_PROCESS_OPTION;
		for (String name: srcGenomeNames) {
			index++;
			o[index] = name;
		}
		return new JComboBox(o);
	}


	protected Map<String, String> getGenomeMap () {
		Map<String, String> map = new HashMap<String, String>();

		if (destGenomeNames != null) {
			for (int i = 0; i < destGenomeNames.size(); i++) {
				String currentSrcName = boxList.get(i).getSelectedItem().toString();
				if (!currentSrcName.equals(NO_PROCESS_OPTION)) {
					map.put(destGenomeNames.get(i), currentSrcName);
				}
			}
		}

		return map;
	}


	protected boolean hasDuplicate () {
		Map<String, Integer> map = new HashMap<String, Integer>();

		if (boxList != null) {
			for (JComboBox box: boxList) {
				String currentGenome = box.getSelectedItem().toString();
				if (!currentGenome.equals(NO_PROCESS_OPTION)) {
					int count = 0;
					if (map.containsKey(currentGenome)) {
						count = map.get(currentGenome);
					}
					count++;

					map.put(currentGenome, count);
				}
			}
		}

		for (Integer count: map.values()) {
			if (count > 1) {
				return true;
			}
		}

		return false;

	}

}
