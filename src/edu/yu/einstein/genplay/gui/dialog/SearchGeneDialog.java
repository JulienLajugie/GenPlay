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
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.core.list.geneList.GeneSearcher;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.util.Images;



/**
 * A dialog to search genes on a GeneListTrack
 * @author Julien Lajugie
 * @version 0.1
 */
public class SearchGeneDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -7154640426239852428L;	// generated ID
	private static final Color 	NOTHING_FOUND_COLOR =
			new Color(230, 150, 115); 							// color of the textfield background when nothing is found
	private static JTextField 	jtfSearchGene;				// text field for the input gene name
	private static JPanel 		jpOption;					// panel containing the check boxes
	private static JCheckBox 	jcbMatchCase;				// check box for the case sensitivity
	private static JCheckBox 	jcbWholeWord;				// check box for searching whole word
	private static JCheckBox 	jcbIncremental;				// check box for searching incrementaly (no need to click "Find")
	private static JCheckBox 	jcbChromosome;				// check box for searching within the current chromosome only
	private static JButton 		jbValidInput;				// valid button
	private static JButton 		jbNextMatch;				// next match button
	private static JButton 		jbPreviousMatch;			// previous match button
	private static JButton 		jbNextGene;					// next gene button
	private static JButton 		jbPreviousGene;				// previous gene button
	private static GeneSearcher geneSearcher;				// object that searches the genes
	private static Color 		textFieldDefaultColor;		// default color of the text field background


	/**
	 * Private constructor. Creates an instance of {@link SearchGeneDialog}
	 * @param parent parent component. Can be null
	 * @param geneSearcher a {@link GeneSearcher} that searches the genes
	 */
	private SearchGeneDialog(Component parent, GeneSearcher geneSearcher) {
		super();
		SearchGeneDialog.geneSearcher = geneSearcher;
		// create the textfield
		jtfSearchGene = new JTextField(geneSearcher.getLastSearchedGeneName());
		// retrieve the default background color of a text field
		textFieldDefaultColor = jtfSearchGene.getBackground();
		jtfSearchGene.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				boolean show = false;
				if (jcbIncremental.isSelected()) {
					show = true;
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					show = true;
				}
				if (show) {
					// search a gene when the user type something in the input box
					Gene geneFound = SearchGeneDialog.geneSearcher.search(jtfSearchGene.getText());
					showGene(geneFound);
				}
			}
		});
		// create the match case check box
		jcbMatchCase = new JCheckBox("Match Case");
		jcbMatchCase.setSelected(geneSearcher.isCaseSensitive());
		jcbMatchCase.addActionListener(this);
		// create the whole word check box
		jcbWholeWord = new JCheckBox("Whole Word");
		jcbWholeWord.setSelected(geneSearcher.isWholeWorld());
		jcbWholeWord.addActionListener(this);
		// create the incremental check box
		jcbIncremental = new JCheckBox("Incremental");
		// create the current chromosome search check box
		jcbChromosome = new JCheckBox("Search within the current chromosome only");
		// create the option panel for the check box
		jpOption = new JPanel();
		jpOption.setBorder(BorderFactory.createTitledBorder("Options"));
		jpOption.setLayout(new BoxLayout(jpOption, BoxLayout.PAGE_AXIS));
		jpOption.add(jcbMatchCase);
		jpOption.add(jcbWholeWord);
		jpOption.add(jcbIncremental);
		jpOption.add(jcbChromosome);
		// create the Find button
		jbValidInput = new JButton("Find");
		jbValidInput.setMargin(new Insets(0, 0, 0, 0));
		jbValidInput.setToolTipText("Move to the first match in the whole genome.");
		Dimension dimension = new Dimension(50, jbValidInput.getPreferredSize().height);
		jbValidInput.setPreferredSize(dimension);
		jbValidInput.addActionListener(this);
		// create the previous match button
		jbPreviousMatch = new JButton("< Prev match");
		jbPreviousMatch.setToolTipText("Move to the previous match.");
		jbPreviousMatch.setPreferredSize(new Dimension(150, jbPreviousMatch.getPreferredSize().height));
		jbPreviousMatch.addActionListener(this);
		// create the next match button
		jbNextMatch = new JButton("Next match     >");
		jbNextMatch.setToolTipText("Move to the next match.");
		jbNextMatch.setPreferredSize(new Dimension(150, jbNextMatch.getPreferredSize().height));
		jbNextMatch.addActionListener(this);
		// create the previous gene button
		jbPreviousGene = new JButton("<  Prev gene");
		jbPreviousGene.setToolTipText("Move to the previous gene.");
		jbPreviousGene.setPreferredSize(new Dimension(150, jbPreviousGene.getPreferredSize().height));
		jbPreviousGene.addActionListener(this);
		// create the next gene button
		jbNextGene = new JButton("Next gene      >");
		jbNextGene.setToolTipText("Move to the next gene.");
		jbNextGene.setPreferredSize(new Dimension(150, jbNextGene.getPreferredSize().height));
		jbNextGene.addActionListener(this);

		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(10, 10, 10, 10);
		add(jtfSearchGene, c);

		c.gridx = 2;
		c.weightx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbValidInput, c);

		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		add(jpOption, c);

		c.gridy++;
		c.gridwidth = 1;
		add(jbPreviousMatch, c);

		c.gridx = 1;
		c.gridwidth = 2;
		add(jbNextMatch, c);

		c.gridy++;
		c.gridx = 0;
		c.gridwidth = 1;
		add(jbPreviousGene, c);

		c.gridx = 1;
		c.gridwidth = 2;
		add(jbNextGene, c);

		getRootPane().setDefaultButton(jbValidInput);
		setTitle("Find Gene");
		setIconImage(Images.getApplicationImage());
		pack();
		setAlwaysOnTop(true);
		setResizable(false);
		setLocationRelativeTo(parent);
	}


	/**
	 * Creates and shows a {@link SearchGeneDialog}
	 * @param parent parent component. Can be null
	 * @param geneSearcher a {@link GeneSearcher}
	 */
	public static void showSearchGeneDialog(Component parent, GeneSearcher geneSearcher) {
		SearchGeneDialog dialog = new SearchGeneDialog(parent, geneSearcher);
		dialog.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent evt) {
		Gene geneFound = null;
		if (evt.getSource() == jcbMatchCase) {
			// case where the match case check box state changes
			geneFound = geneSearcher.setCaseSensitive(jcbMatchCase.isSelected());
		} else if (evt.getSource() == jcbWholeWord) {
			// case where the whole word check box state changes
			geneFound = geneSearcher.setWholeWord(jcbWholeWord.isSelected());
		} else {
			if (evt.getSource() instanceof JButton) {
				JButton source = (JButton) evt.getSource();
				getRootPane().setDefaultButton(source);
				if (source == jbValidInput) {							// case where the "go" button is clicked
					geneFound = SearchGeneDialog.geneSearcher.search(jtfSearchGene.getText());
				} else if (source == jbNextMatch) {						// case where the next match button is clicked
					geneFound = geneSearcher.searchNextMatch();
				} else if (source == jbPreviousMatch) {					// case where the previous match button is clicked
					geneFound = geneSearcher.searchPreviousMatch();
				} else if (source == jbNextGene) {						// case where the next gene button is clicked
					geneFound = geneSearcher.searchNextGene();
				} else if (source == jbPreviousGene) {					// case where the previous gene button is clicked
					geneFound = geneSearcher.searchPreviousGene();
				}
			}
		}
		showGene(geneFound);
	}


	/**
	 * Shows the result of the search on the browser
	 * @param geneFound result of the search
	 */
	private void showGene(Gene geneFound) {
		if (geneFound != null) {
			if (canMove(geneFound)) {
				jbNextMatch.setEnabled(true);
				jbPreviousMatch.setEnabled(true);
				jbNextGene.setEnabled(true);
				jbPreviousGene.setEnabled(true);
				// we want to see larger than the gene found
				int windowStart = geneFound.getStart() - ((geneFound.getStop() - geneFound.getStart()) * 3);
				int minimumDisplayableStart = - geneFound.getChromo().getLength();
				// we don't want the start to be smaller than the minimum displayable position
				windowStart = Math.max(windowStart, minimumDisplayableStart);
				int windowStop = geneFound.getStop() + ((geneFound.getStop() - geneFound.getStart()) * 3);
				int maximumDisplayableStop = geneFound.getChromo().getLength() * 2;
				// we don't want the stop to be greater than the maximum displayable position
				windowStop = Math.min(windowStop, maximumDisplayableStop);
				GenomeWindow genomeWindow = new GenomeWindow(geneFound.getChromo(), windowStart, windowStop);
				ProjectManager.getInstance().getProjectWindow().setGenomeWindow(genomeWindow);
				setEditorColor(true);
			}
		} else {
			setEditorColor(false);
			jbNextMatch.setEnabled(false);
			jbPreviousMatch.setEnabled(false);
			jbNextGene.setEnabled(false);
			jbPreviousGene.setEnabled(false);
		}
	}


	/**
	 * A match found in another chromosome than the current one AND in multi genome project can be annoying.
	 * It may involve many loadings and GenPlay asks if the user really wants to change chromosome.
	 * @param geneFound	a match
	 * @return	true if the user allows (in specific case) to go to found gene, false otherwise
	 */
	private boolean canMove (Gene geneFound) {
		if (jcbChromosome.isSelected()) {
			if (!geneFound.getChromo().equals(ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome())) {
				return false;
			}
		}
		/*if (ProjectManager.getInstance().isMultiGenomeProject() && !geneFound.getChromo().equals(ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome())) {
			Object[] options = {"Yes", "No"};
			int n = JOptionPane.showOptionDialog(this,
					"The following match has been found in another chromosome,\ndo you want to continue?",
					"Chromosome changes",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[1]);
			return n == JOptionPane.YES_OPTION;
		}*/
		return true;
	}


	/**
	 * Colors the background of the input box when nothing is found.
	 * Restores the default color when something is found or when nothing is searched
	 * @param geneFound true if a gene is found, false if not
	 */
	private void setEditorColor(boolean geneFound) {
		if (geneFound) {
			jtfSearchGene.setBackground(textFieldDefaultColor);
		} else {
			if ((jtfSearchGene.getText() == null) || (jtfSearchGene.getText().isEmpty())) {
				jtfSearchGene.setBackground(textFieldDefaultColor);
			} else {
				jtfSearchGene.setBackground(NOTHING_FOUND_COLOR);
			}
		}
	}
}
