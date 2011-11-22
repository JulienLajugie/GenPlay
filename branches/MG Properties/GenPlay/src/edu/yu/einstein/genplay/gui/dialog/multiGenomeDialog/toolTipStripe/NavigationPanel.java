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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class NavigationPanel extends JPanel{

	
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 793779650948801264L;
	
	private static final	String 			NEXT_ICON_PATH 			= "edu/yu/einstein/genplay/resource/next_icon_256x256.png"; 	// path of the icon of the application
	private static final	String 			PREVIOUS_ICON_PATH 		= "edu/yu/einstein/genplay/resource/previous_icon_256x256.png"; // path of the icon of the application
	
	private static final int WIDTH = 230;	// width of the panel
	private static final int HEIGHT = 30;	// height of the panel
	
	private ToolTipStripe origin;			// tooltipstripe object to aware it of any changes.
	private JButton previous;				// the previous button (move backward)
	private JButton next;					// the next button (move forward)
	
	
	/**
	 * Constructor of {@link NavigationPanel}
	 */
	protected NavigationPanel (ToolTipStripe origin) {
		this.origin = origin;
		
		Dimension paneDim = new Dimension(WIDTH, HEIGHT);
		setSize(paneDim);
		setMinimumSize(paneDim);
		setMaximumSize(paneDim);
		setPreferredSize(paneDim);
		
		setLayout(new GridLayout(1, 2, 0, 0));
		Insets inset = new Insets(0, 0, 0, 0);

		next = new JButton(getIcon(NEXT_ICON_PATH));
		next.setContentAreaFilled(false);
		next.setToolTipText("Next variant on the track");
		next.setMargin(inset);
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean enable = getOrigin().goToNextVariant();
				next.setEnabled(enable);
			}
		});

		previous = new JButton(getIcon(PREVIOUS_ICON_PATH));
		previous.setContentAreaFilled(false);
		previous.setToolTipText("Previous variant on the track");
		previous.setMargin(inset);
		previous.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean enable = getOrigin().goToPreviousVariant();
				previous.setEnabled(enable);
			}
		});
	
		add(previous);
		add(new JLabel());	// glue label
		add(new JLabel());	// glue label
		add(next);
		
	}

	
	/**
	 * @return the {@link ToolTipStripe} object that requested the {@link NavigationPanel}
	 */
	private ToolTipStripe getOrigin () {
		return origin;
	}
	
	
	/**
	 * Creates a square icon using the given path 
	 * @param path	icon path
	 * @param side	size of the side
	 * @return		the icon
	 */
	private ImageIcon getIcon (String path) {
		ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource(path)));
		Image img = icon.getImage();
		Image newImg = img.getScaledInstance(WIDTH / 4, HEIGHT, Image.SCALE_SMOOTH);
		icon = new ImageIcon(newImg);
		return icon;
	}
	
	
	/**
	 * @return the height of the panel
	 */
	protected int getPanelHeight () {
		return NavigationPanel.HEIGHT;
	}

}
