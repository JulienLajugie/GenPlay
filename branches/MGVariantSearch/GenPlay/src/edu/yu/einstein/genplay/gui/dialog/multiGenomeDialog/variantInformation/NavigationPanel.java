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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.variantInformation;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.util.Images;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NavigationPanel extends JPanel{

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 793779650948801264L;

	private static final int HEIGHT = 30;	// height of the panel

	private final VariantInformationDialog origin;		// tooltipstripe object to aware it of any changes.
	private final SearchOptionDialog optionDialog;	// the option search dialog
	private final JButton jbOptions;				// button to show the full line
	private final JButton jbPrevious;				// the previous button (move backward)
	private final JButton jbNext;					// the next button (move forward)


	/**
	 * Constructor of {@link NavigationPanel}
	 */
	protected NavigationPanel (VariantInformationDialog origin) {
		this.origin = origin;
		this.optionDialog = new SearchOptionDialog();

		Dimension paneDim = new Dimension(VariantInformationDialog.WIDTH, HEIGHT);
		setSize(paneDim);
		setMinimumSize(paneDim);
		setMaximumSize(paneDim);
		setPreferredSize(paneDim);

		Insets inset = new Insets(0, 0, 0, 0);

		jbOptions = new JButton("Options");
		jbOptions.setToolTipText("Define advanced search options.");
		jbOptions.setMargin(inset);
		jbOptions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int approve = optionDialog.showDialog(getOrigin(), getOrigin().getOptions());
				if (approve == SearchOptionDialog.APPROVE_OPTION) {
					getOrigin().setOptions(optionDialog.getOptions());
				}
			}
		});

		jbNext = new JButton(getIcon(Images.getNextImage()));
		jbNext.setContentAreaFilled(false);
		jbNext.setToolTipText("Next variant on the track");
		jbNext.setMargin(inset);
		jbNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean enable = getOrigin().goToNextVariant();
				jbNext.setEnabled(enable);
			}
		});

		jbPrevious = new JButton(getIcon(Images.getPreviousImage()));
		jbPrevious.setContentAreaFilled(false);
		jbPrevious.setToolTipText("Previous variant on the track");
		jbPrevious.setMargin(inset);
		jbPrevious.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean enable = getOrigin().goToPreviousVariant();
				jbPrevious.setEnabled(enable);
			}
		});

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();


		gbc.insets = inset;
		gbc.weighty = 1;
		gbc.gridy = 0;

		// Add the "previous" button
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.weightx = 0;
		add(jbPrevious, gbc);

		// Add the "details" button
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx++;
		gbc.weightx = 1;
		add(jbOptions, gbc);

		// Add the "next" button
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx++;
		gbc.weightx = 0;
		add(jbNext, gbc);
	}


	/**
	 * @return the {@link VariantInformationDialog} object that requested the {@link NavigationPanel}
	 */
	private VariantInformationDialog getOrigin () {
		return origin;
	}


	/**
	 * Creates a square icon using the given path
	 * @param path	icon path
	 * @param side	size of the side
	 * @return		the icon
	 */
	private ImageIcon getIcon (Image image) {
		Image newImg = image.getScaledInstance(VariantInformationDialog.WIDTH / 4, HEIGHT, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(newImg);
		return icon;
	}


	/**
	 * @return the height of the panel
	 */
	protected static int getPanelHeight () {
		return NavigationPanel.HEIGHT;
	}

}
