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
package edu.yu.einstein.genplay.gui.controlPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.core.multiGenome.utils.ShiftCompute;
import edu.yu.einstein.genplay.core.parser.genomeWindowParser.GenomeWindowInputHandler;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.AlleleType;
import edu.yu.einstein.genplay.dataStructure.enums.CoordinateSystemType;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * The GenomeWindowPanel part of the {@link ControlPanel}
 * @author Julien Lajugie
 * @version 0.1
 */
final class GenomeWindowPanel extends JPanel implements GenomeWindowListener {

	private static final long serialVersionUID = 8279801687428218652L;  // generated ID
	private final JTextField 						jtfGenomeWindow;	// text field for the GenomeWindow
	private final JButton 							jbJump;				// button jump to position
	private final JComboBox							jcbGenomeSelection;
	private final ProjectWindow						projectWindow;		// Instance of the Genome Window Manager


	/**
	 * Creates an instance of {@link GenomeWindowPanel}
	 * @param genomeWindow a {@link GenomeWindow}
	 */
	GenomeWindowPanel() {
		this.projectWindow = ProjectManager.getInstance().getProjectWindow();

		// Create the genome window (positions) text field
		jtfGenomeWindow = new JTextField(20);
		jtfGenomeWindow.setText(projectWindow.getGenomeWindow().toString());
		jtfGenomeWindow.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					updateGenomeWindow();
				}
			}
		});

		// Create the "jump" button
		jbJump = new JButton("jump");
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

		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.weighty = 0;

		// Add the the genome window text field
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(jtfGenomeWindow, gbc);

		// Add the jump button
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridy = 1;
		gbc.weightx = 0;
		add(jbJump, gbc);

		// Add the genome coordinate selector
		if (jcbGenomeSelection != null) {
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = 1;
			gbc.gridy = 0;
			add(jcbGenomeSelection, gbc);
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
			if ((middlePosition < 0) || (middlePosition > newGenomeWindow.getChromosome().getLength())) {
				JOptionPane.showMessageDialog(getRootPane(), "Invalid position", "Error", JOptionPane.WARNING_MESSAGE, null);
			} else {
				projectWindow.setGenomeWindow(newGenomeWindow);
				updateGenomeWindowField(newGenomeWindow);
			}
		}
	}


	/**
	 * @return the newly defined genome window
	 */
	private GenomeWindow getGenomeWindow () {
		GenomeWindowInputHandler handler = new GenomeWindowInputHandler(jtfGenomeWindow.getText());
		GenomeWindow newGenomeWindow = handler.getGenomeWindow();
		if (newGenomeWindow != null) {
			String outputGenome = CoordinateSystemType.METAGENOME.toString();
			String genomeName = FormattedMultiGenomeName.getFullNameWithoutAllele(MGDisplaySettings.SELECTED_GENOME);
			AlleleType inputAlleleType = FormattedMultiGenomeName.getAlleleName(MGDisplaySettings.SELECTED_GENOME);
			int start = ShiftCompute.getPosition(genomeName, inputAlleleType, newGenomeWindow.getStart(), newGenomeWindow.getChromosome(), outputGenome);
			int stop = ShiftCompute.getPosition(genomeName, inputAlleleType, newGenomeWindow.getStop(), newGenomeWindow.getChromosome(), outputGenome);
			newGenomeWindow.setStart(start);
			newGenomeWindow.setStop(stop);
		}
		return newGenomeWindow;
	}


	/**
	 * Locks the genome window panel
	 */
	public void lock() {
		jtfGenomeWindow.setEnabled(false);
	}


	/**
	 * Unlocks the genome window panel
	 */
	public void unlock() {
		jtfGenomeWindow.setEnabled(true);
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		updateGenomeWindowField(evt.getNewWindow());
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
		jtfGenomeWindow.setText(text);
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

}
