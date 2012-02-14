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
import edu.yu.einstein.genplay.core.multiGenome.display.variant.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


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

	private List<VariantInterface> 	variantList;		// a list of displayable variant
	private MGPosition 				variantInformation;	// the current variant object to display
	private VariantInterface 		variant;			// the current variant object to display

	private JPanel headerPanel;			// panel containing the global information
	private JPanel infoPanel;			// panel containing the INFO field information of the VCF
	private JPanel formatPanel;			// panel containing the FORMAT field information of the VCF
	private JPanel navigationPanel;		// panel to move forward/backward
	private boolean first;


	/**
	 * Constructor of {@link ToolTipStripeDialog}
	 * @param fittedVariantList the full list of displayable variants
	 */
	public ToolTipStripeDialog (List<VariantInterface> fittedVariantList) {
		super(MainFrame.getInstance());
		this.variantList = fittedVariantList;
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle("Variant properties");
		first = true;
	}


	/**
	 * Method for showing the dialog box.
	 * @param variantInformation	variant to show information
	 * @param X						X position on the screen
	 * @param Y						Y position on the screen
	 */
	public void show (MGPosition variantInformation, int X, int Y) {
		this.variantInformation = variantInformation;
		if (variantInformation != null) {
			this.variant = variantInformation.getVariant();
		} else {
			this.variant = null;
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

		if (variant == null || variant.getType() == VariantType.MIX) {
			variantInfo = new VariantInfo(null);
			variantFormat = new VariantFormat(null);
		} else {
			variantInfo = new VariantInfo(variantInformation);
			variantFormat = new VariantFormat(variantInformation);
		}

		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, H_GAP, V_GAP);
		setLayout(layout);

		if (first) {
			headerPanel = new JPanel();
			infoPanel = new JPanel();
			formatPanel = new JPanel();
			navigationPanel = new JPanel();

			add(headerPanel);
			add(infoPanel);
			add(formatPanel);
			add(navigationPanel);
			first = false;
		}
		updatePanel(headerPanel, new GlobalInformationPanel(variantInformation));
		updatePanel(infoPanel, variantInfo.getPane());
		updatePanel(formatPanel, variantFormat.getPane());
		updatePanel(navigationPanel, new NavigationPanel(this));

		int height = GlobalInformationPanel.getPanelHeight() +
		PanelInformation.getPanelHeight() +
		PanelInformation.getPanelHeight() +
		NavigationPanel.getPanelHeight() +
		(V_GAP * 11) + 40;
		Dimension dimension = new Dimension(ToolTipStripeDialog.WIDTH, height);
		setSize(dimension);

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
	public VariantInterface getVariant() {
		return variant;
	}


	/**
	 * Looks for the next variant and run the dialog initialization.
	 * @return true if it moves to the next variant, false otherwise
	 */
	protected boolean goToNextVariant () {
		VariantInterface newVariant = getNextVariant();
		return initVariant(newVariant);
	}


	/**
	 * Looks for the previous variant and run the dialog initialization.
	 * @return true if it moves to the previous variant, false otherwise
	 */
	protected boolean goToPreviousVariant () {
		VariantInterface newVariant = getPreviousVariant();
		return initVariant(newVariant);
	}


	/**
	 * initializes the dialog content and moves the screen onto the related variant.
	 * @param newVariant	the variant to display
	 * @return				if it moves to the previous variant, false otherwise 
	 */
	private boolean initVariant (VariantInterface newVariant) {
		if (newVariant == null) {
			return false;
		}
		this.variant = newVariant;
		this.variantInformation = variant.getFullVariantInformation();
		initContent();
		int variantStart = variant.getStart();
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
	private int getVariantIndex (VariantInterface variant) {
		for (int i = 0; i < variantList.size(); i++) {
			if (variantList.get(i).equals(variant)) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * @param variant	the current variant
	 * @return the previous variant compare to the current variant
	 */
	private VariantInterface getPreviousVariant () {
		VariantInterface result;
		int currentIndex = getVariantIndex(variant);
		int previousIndex = currentIndex - 1;
		if (previousIndex >= 0) {
			result = variantList.get(previousIndex);
			if (variant.getType() == VariantType.BLANK) {
				previousIndex = getPreviousValidIndex(previousIndex);
				result = variantList.get(previousIndex);
			}
		} else {
			result = variant;
		}
		return result;
	}


	/**
	 * @param variant	the current variant
	 * @return the next variant compare to the current variant
	 */
	private VariantInterface getNextVariant () {
		VariantInterface result;
		int currentIndex = getVariantIndex(variant);
		int nextIndex = currentIndex + 1;

		if (nextIndex >= 0 && nextIndex < variantList.size()) {
			result = variantList.get(nextIndex);
			if (variant.getType() == VariantType.BLANK) {
				nextIndex = getNextValidIndex(nextIndex);
				result = variantList.get(nextIndex);
			}
		} else {
			result = variant;
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
		while (!found && i < variantList.size()) {
			if (variantList.get(i).getType() == VariantType.BLANK) {
				if (	(i + 1) < variantList.size() &&
						variantList.get(i + 1).getStart() > variantList.get(i).getStop()) {
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
		while (!found && i >= 0) {
			if (variantList.get(i).getType() == VariantType.BLANK) {
				if (	(i - 1) >= 0 &&
						variantList.get(i - 1).getStop() < variantList.get(i).getStart()) {
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

}
