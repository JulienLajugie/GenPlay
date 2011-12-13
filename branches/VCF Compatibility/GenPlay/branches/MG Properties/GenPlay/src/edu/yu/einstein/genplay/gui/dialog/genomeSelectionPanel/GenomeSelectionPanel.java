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
package edu.yu.einstein.genplay.gui.dialog.genomeSelectionPanel;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;


/**
 * Panel for the selection of a genome as a reference in a multi-genome project
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenomeSelectionPanel extends JPanel {

	private static final long serialVersionUID = -2863825210102188370L;	// generated ID
	private static final int 				PANEL_WIDTH = 150;	// width of the panel
	private final JComboBox 				jcbGenome; 			// combo box for the score calculation method
	private static int 						defaultGenome = 0;	// default method of calculation
	
	
	/**
	 * Creates an instance of a {@link GenomeSelectionPanel}
	 */
	public GenomeSelectionPanel() {
		super();
		jcbGenome = new JComboBox(ProjectManager.getInstance().getGenomeSynchronizer().getFormattedGenomeArray());
		jcbGenome.setSelectedIndex(defaultGenome);
		add(jcbGenome);
		setBorder(BorderFactory.createTitledBorder("Genome Selection"));
		setPreferredSize(new Dimension(PANEL_WIDTH, getPreferredSize().height));
	}
	
	
	/**
	 * @return the selected score calculation method
	 */
	public int getGenomeIndex() {
		return jcbGenome.getSelectedIndex();
	}
	

	/**
	 * @return the name of the selected genome
	 */
	public String getGenomeName () {
		String name;
		try {
			name = FormattedMultiGenomeName.getRawName((String)jcbGenome.getSelectedItem());
		} catch (Exception e) {
			name = (String)jcbGenome.getSelectedItem();
		}
		return name;
	}
	
	
	/**
	 * Saves the selected method of calculation as default
	 */
	public void saveDefault() {
		defaultGenome = getGenomeIndex();
	}
}
