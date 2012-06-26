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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.editingDialog.editingPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.comparator.ListComparator;
import edu.yu.einstein.genplay.core.enums.VCFColumnName;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenomeEditingPanel extends EditingPanel<List<String>> implements ActionListener {

	/** Generated serial version ID */
	private static final long serialVersionUID = -4060807866730514644L;
	private JCheckBox[] boxes;

	private VCFFile file;
	private VCFHeaderType header;
	private boolean formatHeaderDependant;


	/**
	 * Constructor of {@link GenomeEditingPanel}
	 * @param formatHeaderDependant true if the panel must react only to a FORMAT header, false otherwise
	 */
	public GenomeEditingPanel(boolean formatHeaderDependant) {
		super("Genome(s)");
		boxes = null;
		file = null;
		header = null;
		this.formatHeaderDependant = formatHeaderDependant;
	}


	@Override
	protected void initializeContentPanel() {}


	@SuppressWarnings("unchecked")
	@Override
	public void update(Object object) {
		if (object instanceof List<?>) {
			if (((List<?>)object).size() > 0 && ((List<?>)object).get(0) instanceof String) {
				createPanel((List<String>)object);
			}
		} else if (object instanceof VCFFile) {
			VCFFile file = getVCFFile(object);
			if (file == null) {
				boxes = null;
				resetContentPanel();
			} else {
				if (!file.equals(this.file)) {
					this.file = file;
					createPanel(file.getHeader().getGenomeNames());
				}
			}
		} else if (boxes != null && object instanceof VCFHeaderType) {
			VCFHeaderType header = (VCFHeaderType) object;
			if (header.getColumnCategory() == VCFColumnName.FORMAT) {
				for (JCheckBox box: boxes) {
					box.setEnabled(true);
				}
			} else {
				for (JCheckBox box: boxes) {
					box.setEnabled(false);
				}
			}
			this.header = header;
		}
	}


	/**
	 * Creates the boxes panel
	 */
	private void createPanel (List<String> genomeNames) {
		boxes = new JCheckBox[genomeNames.size()];
		String[] paths = new String[genomeNames.size()];

		boolean enable = mustBeDefaultEnabled();

		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;

		for (int i = 0; i < genomeNames.size(); i++) {
			paths[i] = genomeNames.get(i);
			JCheckBox checkBox = new JCheckBox(genomeNames.get(i));
			checkBox.setEnabled(enable);
			boxes[i] = checkBox;
			boxes[i].addActionListener(this);
			if (i == (genomeNames.size() - 1)) {
				gbc.weighty = 1;
			}
			content.add(boxes[i], gbc);
			gbc.gridy++;
		}
		int width = getMaxStringLength(paths);
		int height = getStringHeight() * genomeNames.size();

		setNewContentPanel(content);

		initializeContentPanelSize(width, height);

		repaint();
	}


	/**
	 * If FORMAT header dependant and the header IS FORMAT, then the boxes must be enabled by default, disabled otherwise.
	 * If not FORMAT header dependant, boxes are enabled.
	 * @return true/false if the boxes must enable by default
	 */
	private boolean mustBeDefaultEnabled () {
		if (formatHeaderDependant) {
			if (header != null && header.getColumnCategory() == VCFColumnName.FORMAT) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}


	/**
	 * Tries to cast an object to a {@link VCFFile}
	 * @param object the object to cast
	 * @return	the casted object or null
	 */
	private VCFFile getVCFFile (Object object) {
		if (object instanceof VCFFile) {
			return (VCFFile) object;
		}
		return null;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<String> selectedGenomes = getSelectedGenomes();
		List<String> previousGenomes = null;

		if (element != null) {
			previousGenomes = (List<String>) element;
		}

		ListComparator<String> comparator = new ListComparator<String>();
		if (comparator.areDifferent(previousGenomes, selectedGenomes)) {
			setElement(selectedGenomes);
		}
	}


	/**
	 * @return the list of selected genomes
	 */
	public List<String> getSelectedGenomes () {
		List<String> selectedGenomes = new ArrayList<String>();
		if (boxes != null) {
			for (JCheckBox box: boxes) {
				if (box.isEnabled() && box.isSelected()) {
					selectedGenomes.add(box.getText());
				}
			}
		}
		return selectedGenomes;
	}


	@Override
	public String getErrors() {
		String errors = "";
		if (formatHeaderDependant) {
			if (header != null && header.getColumnCategory() == VCFColumnName.FORMAT) {
				if (getSelectedGenomes().size() == 0) {
					errors += "Genome(s) selection\n";
				}
			}
		} else {
			if (getSelectedGenomes().size() == 0) {
				errors += "Genome(s) selection\n";
			}
		}
		return errors;
	}


	@Override
	public void reset() {
		resetContentPanel();
		element = null;
		file = null;
		header = null;
	}


	@Override
	public void initialize(List<String> element) {
		if (element != null) {
			for (JCheckBox box: boxes) {
				if (element.contains(box.getText())) {
					box.setSelected(true);
				} else {
					box.setSelected(false);
				}
			}
			setElement(element);
		}
	}
}
