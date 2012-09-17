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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantDisplay;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLineDialog.VCFLineDialog;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.drawer.multiGenome.MultiGenomeDrawer;
import edu.yu.einstein.genplay.util.Images;


/**
 * This class shows variant stripe information.
 * It is possible to move forward and backward on the variant list.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ToolTipStripeDialog extends JDialog {

	private static final long serialVersionUID = -4932470485711131874L;


	private static final int WIDTH = 250;	// width of the dialog
	private static final int V_GAP = 5;		// vertical gap between dialog components
	private static final int H_GAP = 5;		// horizontal gap between dialog components

	private final VCFLineDialog 			vcfLineDialog;
	private final List<VariantDisplay> 		variantList;		// a list of displayable variant
	private final MultiGenomeDrawer 		multiGenomeDrawer;

	private VariantDisplay 			currentVariant;		// the current variant object to display
	private VCFLine 				currentLine;		// the current variant object to display

	private final JPanel headerPanel;			// panel containing the global information
	private final JPanel infoPanel;				// panel containing the INFO field information of the VCF
	private final JPanel formatPanel;			// panel containing the FORMAT field information of the VCF
	private final JPanel navigationPanel;		// panel to move forward/backward


	/**
	 * Constructor of {@link ToolTipStripeDialog}
	 * @param multiGenomeDrawer the multi genome drawer requesting the dialog
	 * @param fittedVariantList the full list of displayable variants
	 */
	public ToolTipStripeDialog (MultiGenomeDrawer multiGenomeDrawer, List<VariantDisplay> fittedVariantList) {
		super(MainFrame.getInstance());
		ToolTipStripeHandler.getInstance().addDialog(this);
		this.multiGenomeDrawer = multiGenomeDrawer;
		this.vcfLineDialog = new VCFLineDialog();
		this.variantList = fittedVariantList;
		int trackNumber = MainFrame.getInstance().getTrackList().getTrackNumberFromMGGenomeDrawer(multiGenomeDrawer);
		String title = "Variant properties";
		if (trackNumber > 0) {
			title += " (Track " + trackNumber + ")";
		}
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle(title);

		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, H_GAP, V_GAP);
		setLayout(layout);

		headerPanel = new JPanel();
		infoPanel = new JPanel();
		formatPanel = new JPanel();
		navigationPanel = new JPanel();

		add(headerPanel);
		add(infoPanel);
		add(formatPanel);
		add(navigationPanel);

		int height = GlobalInformationPanel.getPanelHeight() +
				PanelInformation.getPanelHeight() +
				PanelInformation.getPanelHeight() +
				NavigationPanel.getPanelHeight() +
				(V_GAP * 11) + 30;
		Dimension dimension = new Dimension(ToolTipStripeDialog.WIDTH, height);
		setSize(dimension);
	}


	/**
	 * Method for showing the dialog box.
	 * @param variant	variant to show information
	 * @param X			X position on the screen
	 * @param Y			Y position on the screen
	 */
	public void show (VariantDisplay variant, int X, int Y) {
		this.currentVariant = variant;
		this.currentLine = variant.getVCFLine();
		if (currentLine != null) {
			this.currentLine.processForAnalyse();
		}
		initContent();
		setLocation(X, Y);
		setVisible(true);
	}


	/**
	 * Initializes the content of the dialog box according to a variant
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
		VariantDisplay newVariant = getNextVariant();
		return initVariant(newVariant);
	}


	/**
	 * Looks for the previous variant and run the dialog initialization.
	 * @return true if it moves to the previous variant, false otherwise
	 */
	protected boolean goToPreviousVariant () {
		VariantDisplay newVariant = getPreviousVariant();
		return initVariant(newVariant);
	}


	/**
	 * initializes the dialog content and moves the screen onto the related variant.
	 * @param newVariant	the variant to display
	 * @return				if it moves to the previous variant, false otherwise
	 */
	private boolean initVariant (VariantDisplay newVariant) {
		if (newVariant == null) {
			return false;
		}
		this.currentVariant = newVariant;
		this.currentLine = currentVariant.getVCFLine();
		if (currentLine != null) {
			this.currentLine.processForAnalyse();
		}

		initContent();
		int variantStart = currentVariant.getStart();
		GenomeWindow currentGenomeWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		int width = currentGenomeWindow.getSize();
		int startWindow = variantStart - (width / 2);
		int stopWindow = startWindow + width;
		Chromosome chromosome = currentGenomeWindow.getChromosome();
		GenomeWindow genomeWindow = new GenomeWindow(chromosome, startWindow, stopWindow);
		ProjectManager.getInstance().getProjectWindow().setGenomeWindow(genomeWindow);
		return true;
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
	 * @param currentVariant	the current variant
	 * @return the previous variant compare to the current variant
	 */
	private VariantDisplay getPreviousVariant () {
		VariantDisplay result;
		int currentIndex = getVariantIndex(currentVariant);
		int previousIndex = currentIndex - 1;
		if (previousIndex >= 0) {
			result = variantList.get(previousIndex);
			if ((currentVariant.getType() == VariantType.BLANK) || (currentVariant.getType() == VariantType.REFERENCE)) {
				previousIndex = getPreviousValidIndex(previousIndex);
				result = variantList.get(previousIndex);
			}
		} else {
			result = null;
		}
		return result;
	}


	/**
	 * @param currentVariant	the current variant
	 * @return the next variant compare to the current variant
	 */
	private VariantDisplay getNextVariant () {
		VariantDisplay result;
		int currentIndex = getVariantIndex(currentVariant);
		int nextIndex = currentIndex + 1;

		if ((nextIndex >= 0) && (nextIndex < variantList.size())) {
			result = variantList.get(nextIndex);
			if ((currentVariant.getType() == VariantType.BLANK) || (currentVariant.getType() == VariantType.REFERENCE)) {
				nextIndex = getNextValidIndex(nextIndex);
				result = variantList.get(nextIndex);
			}
		} else {
			result = null;
		}
		return result;
	}


	/**
	 * Gets the the next valid index.
	 * A valid index is not an index related to a blank variant where the scan is already on.
	 * The dialog can be on blank, the next button must go to the next variant (that can be a blank but furhter!)
	 * @param index the index after the current index
	 * @return	the valid next index
	 */
	private int getNextValidIndex (int index) {
		boolean found = false;
		int i = index;
		while (!found && (i < variantList.size())) {
			VariantType type = variantList.get(i).getType();
			if ((type == VariantType.BLANK) || (type == VariantType.REFERENCE)) {
				if (	((i + 1) < variantList.size()) &&
						(variantList.get(i + 1).getStart() > variantList.get(i).getStop())) {
					found = true;
				}
				i++;
			} else {
				found = true;
			}
		}
		if (i >= variantList.size()) {
			return index;
		}
		return i;
	}


	/**
	 * See getNextValidIndex description
	 * @param index the index before the current index
	 * @return the previous valid index
	 */
	private int getPreviousValidIndex (int index) {
		boolean found = false;
		int i = index;
		while (!found && (i >= 0)) {
			VariantType type = variantList.get(i).getType();
			if ((type == VariantType.BLANK) || (type == VariantType.REFERENCE)) {
				if (	((i - 1) >= 0) &&
						(variantList.get(i - 1).getStop() < variantList.get(i).getStart())) {
					found = true;
				}
				i--;
			} else {
				found = true;
			}
		}
		if (i < 0) {
			return index;
		}
		return i;
	}


	protected void showVCFLine () {
		vcfLineDialog.show(currentLine);
	}


	/**
	 * @return the multiGenomeDrawer
	 */
	public MultiGenomeDrawer getMultiGenomeDrawer() {
		return multiGenomeDrawer;
	}
}
