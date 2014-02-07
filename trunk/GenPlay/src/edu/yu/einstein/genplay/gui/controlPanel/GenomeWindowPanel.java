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
package edu.yu.einstein.genplay.gui.controlPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.core.parser.genomeWindowParser.GenomeWindowInputHandler;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.dataStructure.gwBookmark.GWBookmark;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.project.PABookmarkCurrentPosition;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.util.Images;


/**
 * The GenomeWindowPanel part of the {@link ControlPanel}
 * @author Julien Lajugie
 * @version 0.1
 */
final class GenomeWindowPanel extends JPanel implements GenomeWindowListener {

	private static final long serialVersionUID = 8279801687428218652L;  // generated ID
	static final String DELETE_BOOKMARK_ACTION_KEY = "delete selected bookmark";
	private final JComboBox 						jcbGenomeWindow;	// combobox the GenomeWindow
	private final JButton 							jbJump;				// button jump to position
	private final JComboBox							jcbGenomeSelection;	// combobox to select a genome in multi-genome project
	private final JButton							jbBookmark;			// button to add current position to bookmark
	private final ProjectWindow						projectWindow;		// Instance of the Genome Window Manager


	/**
	 * Creates an instance of {@link GenomeWindowPanel}
	 * @param genomeWindow a {@link GenomeWindow}
	 */
	GenomeWindowPanel() {
		projectWindow = ProjectManager.getInstance().getProjectWindow();

		// Create the genome window (positions) text field
		jcbGenomeWindow = new JComboBox();
		initGenomeWindowCombo();

		// Create the "jump" button
		jbJump = new JButton();
		jbJump.setIcon(new ImageIcon(Images.getJumpImage()));
		jbJump.setRolloverIcon(new ImageIcon(Images.getJumpRolledOverImage()));
		jbJump.setDisabledIcon(new ImageIcon(Images.getJumpDisabledImage()));
		jbJump.setBorderPainted(false);
		jbJump.setFocusPainted(false);
		jbJump.setMargin(new Insets(0, 0, 0, 0));
		jbJump.setContentAreaFilled(false);
		//jbJump.set

		jbJump.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGenomeWindow();
			}
		});

		// Create the genome coordinate selector
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			Object[] genomes = new Object[2];
			genomes[0] = CoordinateSystemType.METAGENOME.toString();
			genomes[1] = CoordinateSystemType.REFERENCE.toString();
			jcbGenomeSelection = new JComboBox(genomes);
			jcbGenomeSelection.setSelectedIndex(0);
			jcbGenomeSelection.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Object object = ((JComboBox) arg0.getSource()).getSelectedItem();
					if (object != null) {
						MainFrame.getInstance().setNewGenomeCoordinate(object.toString());
					}
				}
			});
		} else {
			jcbGenomeSelection = null;
		}

		jbBookmark = new JButton();
		// update the combobox with the bookmarks when a bookmark is added
		jbBookmark.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateBookmarkDropdownList();
			}
		});
		jbBookmark.addActionListener(new PABookmarkCurrentPosition(getRootPane()));
		jbBookmark.setBorderPainted(false);
		jbBookmark.setMargin(new Insets(0, 0, 0, 0));
		jbBookmark.setContentAreaFilled(false);
		jbBookmark.setFocusPainted(false);
		jbBookmark.setHideActionText(true);
		jbBookmark.setIcon(new ImageIcon(Images.getBookmarkImage()));
		jbBookmark.setRolloverIcon(new ImageIcon(Images.getBookmarkRolledOverImage()));
		jbBookmark.setDisabledIcon(new ImageIcon(Images.getBookmarkDisabledImage()));


		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Add the the genome window text field
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(jcbGenomeWindow, gbc);

		// Add the jump button
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0;
		add(jbJump, gbc);

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 2;
		gbc.gridy = 0;
		add(jbBookmark, gbc);

		// Add the genome coordinate selector
		if (jcbGenomeSelection != null) {
			gbc.fill = GridBagConstraints.NONE;
			gbc.gridx = 0;
			gbc.gridy = 1;
			add(jcbGenomeSelection, gbc);
		}
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		updateGenomeWindowField(evt.getNewWindow());
	}


	/**
	 * @return the newly defined genome window
	 */
	private GenomeWindow getGenomeWindow () {
		GenomeWindowInputHandler handler;
		if (jcbGenomeWindow.getSelectedItem() instanceof GWBookmark) {
			return ((GWBookmark) jcbGenomeWindow.getSelectedItem()).getGenomeWindow();
		} else {
			handler = new GenomeWindowInputHandler((String) jcbGenomeWindow.getSelectedItem());
			GenomeWindow newGenomeWindow = handler.getGenomeWindow();
			if (newGenomeWindow != null) {
				String outputGenome = CoordinateSystemType.METAGENOME.toString();
				String genomeName = FormattedMultiGenomeName.getFullNameWithoutAllele(MGDisplaySettings.SELECTED_GENOME);
				AlleleType inputAlleleType = FormattedMultiGenomeName.getAlleleName(MGDisplaySettings.SELECTED_GENOME);
				int start = ShiftCompute.getPosition(genomeName, inputAlleleType, newGenomeWindow.getStart(), newGenomeWindow.getChromosome(), outputGenome);
				int stop = ShiftCompute.getPosition(genomeName, inputAlleleType, newGenomeWindow.getStop(), newGenomeWindow.getChromosome(), outputGenome);
				newGenomeWindow = new SimpleGenomeWindow(newGenomeWindow.getChromosome(), start, stop);
			}
			return newGenomeWindow;
		}
	}


	/**
	 * @return the button to jump on a genomic position
	 */
	public JButton getJumpButton() {
		return jbJump;
	}


	/**
	 * Initialized the combo box for the genome window address
	 */
	public void initGenomeWindowCombo() {
		jcbGenomeWindow.setEditable(true);
		jcbGenomeWindow.setPrototypeDisplayValue(new GWBookmark(projectWindow.getGenomeWindow().toString(), projectWindow.getGenomeWindow()));
		jcbGenomeWindow.setSelectedItem(projectWindow.getGenomeWindow().toString());

		jcbGenomeWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jcbGenomeWindow.getSelectedItem() instanceof GWBookmark) {
					GWBookmark selectedBookmark = (GWBookmark) jcbGenomeWindow.getSelectedItem();
					if ((e.getModifiers() & ActionEvent.ALT_MASK) != 0) {
						// remove bookmark
						ProjectManager.getInstance().getProjectBookmarks().remove(selectedBookmark);
						updateBookmarkDropdownList();
					} else {
						// goto bookmark
						jcbGenomeWindow.setSelectedItem(selectedBookmark.getGenomeWindow().toString());
						if (jcbGenomeSelection != null) {
							setSelectedGenomeName(selectedBookmark.getGenomeName());
						}
						updateGenomeWindow();
					}

				}
			}
		});
	}


	/**
	 * Locks the genome window panel
	 */
	public void lock() {
		jcbGenomeWindow.setEnabled(false);
		jbJump.setEnabled(false);
		jbBookmark.setEnabled(false);
	}


	/**
	 * @param genomeNames genome names to add to the genome selector (multi genome only)
	 */
	public void resetGenomeNames (List<String> genomeNames) {
		jcbGenomeSelection.removeAllItems();
		jcbGenomeSelection.addItem(CoordinateSystemType.METAGENOME.toString());
		jcbGenomeSelection.addItem(CoordinateSystemType.REFERENCE.toString());
		int width = getFontMetrics(getFont()).stringWidth(CoordinateSystemType.REFERENCE.toString());
		if (genomeNames != null) {
			for (String genomeName: genomeNames) {
				String name01 = FormattedMultiGenomeName.getFullNameWithAllele(genomeName, AlleleType.ALLELE01);
				String name02 = FormattedMultiGenomeName.getFullNameWithAllele(genomeName, AlleleType.ALLELE02);
				width = Math.max(width, getFontMetrics(getFont()).stringWidth(name01));
				jcbGenomeSelection.addItem(name01);
				jcbGenomeSelection.addItem(name02);
			}
		}
		width  *= 1.2;
		Dimension dimension = new Dimension(width, 19);
		jcbGenomeSelection.setSize(dimension);
		jcbGenomeSelection.setPreferredSize(dimension);
	}


	/**
	 * @param genomeName genome name to select in the genome selector (multi genome only)
	 */
	public void setSelectedGenomeName (String genomeName) {
		jcbGenomeSelection.setSelectedItem(genomeName);
		updateGenomeWindowField(ProjectManager.getInstance().getProjectWindow().getGenomeWindow());
	}


	/**
	 * Unlocks the genome window panel
	 */
	public void unlock() {
		jcbGenomeWindow.setEnabled(true);
		jbJump.setEnabled(true);
		jbBookmark.setEnabled(true);
	}


	private void updateBookmarkDropdownList() {
		jcbGenomeWindow.removeAllItems();
		jcbGenomeWindow.setSelectedItem(projectWindow.getGenomeWindow().toString());
		List<GWBookmark> bookmarks = ProjectManager.getInstance().getProjectBookmarks();
		for (GWBookmark currentBookmark: bookmarks) {
			jcbGenomeWindow.addItem(currentBookmark);
		}
	}


	/**
	 * Called when the current {@link GenomeWindow} changes
	 */
	void updateGenomeWindow() {
		GenomeWindow newGenomeWindow = getGenomeWindow();
		if (newGenomeWindow == null) {
			JOptionPane.showMessageDialog(getRootPane(), "Invalid position", "Error", JOptionPane.WARNING_MESSAGE, null);
		} else if (!newGenomeWindow.equals(projectWindow.getGenomeWindow())) {
			int middlePosition = (int)newGenomeWindow.getMiddlePosition();
			if ((middlePosition < 1) || (middlePosition > newGenomeWindow.getChromosome().getLength())) {
				JOptionPane.showMessageDialog(getRootPane(), "Invalid position", "Error", JOptionPane.WARNING_MESSAGE, null);
			} else {
				projectWindow.setGenomeWindow(newGenomeWindow);
				updateGenomeWindowField(newGenomeWindow);
			}
		}
	}


	/**
	 * Actual method when the genome window has changed.
	 * @param genomeWindow the new genome window
	 */
	private void updateGenomeWindowField(GenomeWindow genomeWindow) {
		String text = genomeWindow.toString();
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			Chromosome currentChromosome = projectWindow.getGenomeWindow().getChromosome();
			String genomeName = FormattedMultiGenomeName.getFullNameWithoutAllele(MGDisplaySettings.SELECTED_GENOME);
			AlleleType inputAlleleType = FormattedMultiGenomeName.getAlleleName(MGDisplaySettings.SELECTED_GENOME);
			int positionStart = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, genomeWindow.getStart(), currentChromosome, genomeName);
			int positionStop = ShiftCompute.getPosition(FormattedMultiGenomeName.META_GENOME_NAME, inputAlleleType, genomeWindow.getStop(), currentChromosome, genomeName);
			text = currentChromosome.getName() + ":" + positionStart + "-" + positionStop;
		}
		jcbGenomeWindow.setSelectedItem(text);
	}
}
