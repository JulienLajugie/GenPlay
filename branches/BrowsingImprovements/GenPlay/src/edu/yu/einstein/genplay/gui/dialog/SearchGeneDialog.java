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

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.list.geneList.GeneSearcher;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;



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
	private static JButton 		jbNext;						// next button
	private static JButton 		jbPrevious;					// previous button
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
				// search a gene when the user type something in the input box
				Gene geneFound = SearchGeneDialog.geneSearcher.search(jtfSearchGene.getText());
				showGene(geneFound);
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
		// create the option panel for the check box
		jpOption = new JPanel();
		jpOption.setBorder(BorderFactory.createTitledBorder("Option"));
		jpOption.setLayout(new BoxLayout(jpOption, BoxLayout.PAGE_AXIS));
		jpOption.add(jcbMatchCase);
		jpOption.add(jcbWholeWord);
		// create the previous button
		jbPrevious = new JButton("<   Previous");
		jbPrevious.setPreferredSize(new Dimension(100, jbPrevious.getPreferredSize().height));
		jbPrevious.addActionListener(this);
		// create the next button
		jbNext = new JButton("Next           >");
		jbNext.setPreferredSize(new Dimension(100, jbNext.getPreferredSize().height));
		jbNext.addActionListener(this);

		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		add(jtfSearchGene, c);

		c.gridy = 1;
		add(jpOption, c);

		c.gridy = 2;
		c.gridwidth = 1;
		add(jbPrevious, c);

		c.gridx = 1;
		add(jbNext, c);

		getRootPane().setDefaultButton(jbNext);
		setModal(true);
		setTitle("Find Gene");
		pack();
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
		dialog.dispose();
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
			if (evt.getSource() == jbNext) {
				// case where the next button is clicked
				getRootPane().setDefaultButton(jbNext);
				geneFound = geneSearcher.searchNext();
			} else if (evt.getSource() == jbPrevious) {
				// case where the previous button is clicked
				getRootPane().setDefaultButton(jbPrevious);
				geneFound = geneSearcher.searchPrevious();
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
			jbNext.setEnabled(true);
			jbPrevious.setEnabled(true);
			// we want to see larger than the gene found
			int windowStart = geneFound.getStart() - (geneFound.getStop() - geneFound.getStart()) * 3;
			int minimumDisplayableStart = - geneFound.getChromo().getLength();
			// we don't want the start to be smaller than the minimum displayable position
			windowStart = Math.max(windowStart, minimumDisplayableStart);
			int windowStop = geneFound.getStop() + (geneFound.getStop() - geneFound.getStart()) * 3;
			int maximumDisplayableStop = geneFound.getChromo().getLength() * 2;
			// we don't want the stop to be greater than the maximum displayable position
			windowStop = Math.min(windowStop, maximumDisplayableStop);
			GenomeWindow genomeWindow = new GenomeWindow(geneFound.getChromo(), windowStart, windowStop);
			ProjectManager.getInstance().getProjectWindow().setGenomeWindow(genomeWindow);
			setEditorColor(true);
		} else {
			setEditorColor(false);
			jbNext.setEnabled(false);
			jbPrevious.setEnabled(false);
		}
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
