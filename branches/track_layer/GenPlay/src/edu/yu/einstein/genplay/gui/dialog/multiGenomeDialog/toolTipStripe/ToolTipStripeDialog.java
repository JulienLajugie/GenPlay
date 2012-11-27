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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.toolTipStripe;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantDisplay;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLineDialog.VCFLineDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.old.track.drawer.multiGenome.MultiGenomeDrawer;
import edu.yu.einstein.genplay.util.Images;


/**
 * This class shows variant stripe information.
 * It is possible to move forward and backward on the variant list.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ToolTipStripeDialog extends JDialog {

	private static final long serialVersionUID = -4932470485711131874L;

	/** Dialog width */
	public static final int WIDTH = 250;	// width of the dialog

	private final VCFLineDialog 		vcfLineDialog;
	private List<VariantDisplay> 		variantList;		// a list of displayable variant

	private VariantDisplay 			currentVariant;		// the current variant object to display
	private VCFLine 				currentLine;		// the current variant object to display
	private int						currentIndex;

	private final JPanel headerPanel;			// panel containing the global information
	private final JPanel infoPanel;				// panel containing the INFO field information of the VCF
	private final JPanel formatPanel;			// panel containing the FORMAT field information of the VCF
	private final JPanel navigationPanel;		// panel to move forward/backward


	/**
	 * Constructor of {@link ToolTipStripeDialog}
	 * @param multiGenomeDrawer the multigenome drawer
	 */
	public ToolTipStripeDialog (MultiGenomeDrawer multiGenomeDrawer) {
		super(MainFrame.getInstance());
		this.vcfLineDialog = new VCFLineDialog();
		int trackNumber = MainFrame.getInstance().getTrackList().getTrackNumberFromMGGenomeDrawer(multiGenomeDrawer);
		String title = "Variant properties";
		if (trackNumber > 0) {
			title += " (Track " + trackNumber + ")";
		}
		setTitle(title);
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.gridx = 0;
		gbc.gridy = 0;

		headerPanel = new JPanel();
		infoPanel = new JPanel();
		formatPanel = new JPanel();
		navigationPanel = new JPanel();


		add(headerPanel, gbc);

		gbc.gridy++;
		add(infoPanel, gbc);

		gbc.gridy++;
		add(formatPanel, gbc);

		gbc.gridy++;
		gbc.weighty = 1;
		add(navigationPanel, gbc);
	}


	/**
	 * Method for showing the dialog box.
	 * @param variantList 	the current variant list
	 * @param variant 		the variant to focus on
	 * @param X				X position on the screen
	 * @param Y				Y position on the screen
	 */
	public void show (List<VariantDisplay> variantList, VariantDisplay variant, int X, int Y) {
		this.variantList = variantList;
		this.currentVariant = variant;
		refreshDialog();
		setLocation(X, Y);
		setVisible(true);
	}


	/**
	 * initializes the dialog content and moves the screen onto the related variant.
	 * @param newVariant	the variant to display
	 */
	private void refreshDialog () {
		// Initialize the current variant
		if (currentVariant == null) {
			currentLine = null;
			currentIndex = -1;
		} else {
			this.currentLine = currentVariant.getVCFLine();
			if (currentLine != null) {
				this.currentLine.processForAnalyse();
			}
			this.currentIndex = getVariantIndex(currentVariant);
		}

		// Initialize the content of the dialog
		initContent();

		// Relocate the screen position
		relocateScreenPosition();
	}


	/**
	 * Initializes the content of the dialog box according to a variant.
	 */
	private void initContent () {
		VariantInfo variantInfo;
		VariantFormat variantFormat;

		if (currentLine == null) {
			variantInfo = new VariantInfo(null);
			variantFormat = new VariantFormat(null, null);
		} else {
			variantInfo = new VariantInfo(currentLine);
			variantFormat = new VariantFormat(currentVariant, currentLine);
		}

		updatePanel(headerPanel, new GlobalInformationPanel(currentVariant, currentLine));
		updatePanel(infoPanel, variantInfo.getPane());
		updatePanel(formatPanel, variantFormat.getPane());
		NavigationPanel newNavigationPanel = new NavigationPanel(this);
		if (currentLine == null) {
			newNavigationPanel.setEnableDetail(false);
		} else {
			newNavigationPanel.setEnableDetail(true);
		}
		updatePanel(navigationPanel, newNavigationPanel);

		validate();

		pack();
	}


	/**
	 * Locates the screen position to the start position of the actual variant.
	 */
	private void relocateScreenPosition () {
		int variantStart = currentVariant.getStart();
		GenomeWindow currentGenomeWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		int width = currentGenomeWindow.getSize();
		int startWindow = variantStart - (width / 2);
		int stopWindow = startWindow + width;
		Chromosome chromosome = currentGenomeWindow.getChromosome();
		GenomeWindow genomeWindow = new GenomeWindow(chromosome, startWindow, stopWindow);
		ProjectManager.getInstance().getProjectWindow().setGenomeWindow(genomeWindow);
	}


	/**
	 * @param variant the current variant
	 * @return	the index in the variant list of the variant
	 */
	private int getVariantIndex (VariantDisplay variant) {
		for (int i = 0; i < variantList.size(); i++) {
			if (variantList.get(i).equals(variant)) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * Updates a panel with another one
	 * @param previousPanel	panel to update
	 * @param newPanel		new panel
	 */
	private void updatePanel (JPanel previousPanel, JPanel newPanel) {
		previousPanel.removeAll();
		previousPanel.add(newPanel);
	}


	/**
	 * @return the variant
	 */
	public VariantDisplay getVariant() {
		return currentVariant;
	}


	/**
	 * Looks for the next variant and run the dialog initialization.
	 * @return true if it moves to the next variant, false otherwise
	 */
	protected boolean goToNextVariant () {
		if ((currentIndex + 1) >= variantList.size()) {
			return false;
		}
		currentIndex++;
		currentVariant = variantList.get(currentIndex);
		refreshDialog();
		return true;
	}


	/**
	 * Looks for the previous variant and run the dialog initialization.
	 * @return true if it moves to the previous variant, false otherwise
	 */
	protected boolean goToPreviousVariant () {
		if ((currentIndex - 1) < 0) {
			return false;
		}
		currentIndex--;
		currentVariant = variantList.get(currentIndex);
		refreshDialog();
		return true;
	}


	/**
	 * Shows the vcf line dialog
	 */
	protected void showVCFLine () {
		vcfLineDialog.show(currentLine);
	}

}
