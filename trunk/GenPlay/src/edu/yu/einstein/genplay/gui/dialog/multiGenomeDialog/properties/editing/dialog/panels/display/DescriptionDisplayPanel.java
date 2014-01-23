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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.display;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.yu.einstein.genplay.core.multiGenome.filter.FilterInterface;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class DescriptionDisplayPanel extends EditingPanel<FilterInterface> implements ListSelectionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;


	/**
	 * Constructor of {@link DescriptionDisplayPanel}
	 */
	public DescriptionDisplayPanel() {
		super("Description");
	}


	@Override
	protected void initializeContentPanel() {}


	@Override
	public void update(Object object) {

		String[] descriptions;;

		if (object instanceof FilterInterface) {
			descriptions = Utils.splitStringWithLength(((FilterInterface) object).getDescription(), MINIMUM_WIDTH - 20, getFontMetrics(getFont()));
		} else {
			descriptions = new String[1];
			descriptions[0] = "nc";
		}

		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;

		for (int i = 0; i < descriptions.length; i++) {
			gbc.gridy = i;
			if (i == (descriptions.length - 1)) {
				gbc.weighty = 1;
			}
			descriptionPanel.add(new JLabel(descriptions[i]), gbc);
		}

		setNewContentPanel(descriptionPanel);
		repaint();
	}


	@Override
	public String getErrors() {
		return "";
	}


	@Override
	public void reset() {
		resetContentPanel();
	}


	@Override
	public void initialize(FilterInterface element) {
		update(element);
	}


	@Override
	public void valueChanged(ListSelectionEvent arg0) {}

}
