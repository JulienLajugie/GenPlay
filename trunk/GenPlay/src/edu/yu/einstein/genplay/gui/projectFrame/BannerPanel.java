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
package edu.yu.einstein.genplay.gui.projectFrame;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.util.Images;

/**
 * This class load the banner on the project screen top.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BannerPanel extends JPanel {

	private static final long serialVersionUID = 3269872506089576106L; //generated ID

	private 				Image 	banner = null;	// The banner


	/**
	 * Constructor of {@link BannerPanel}
	 */
	protected BannerPanel () {
		//Size Panel
		setSize(ProjectFrame.BANNER_DIM);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());

		//Background color
		setBackground(ProjectFrame.BANNER_COLOR);

		//Load the image
		banner = Images.getBannerImage();
	}

	/**
	 * The picture loaded in the banner panel is automatically resize to fit well.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); //paint background
		if (banner != null) { //there is a picture: draw it
			int height = this.getSize().height;
			int width = this.getSize().width;
			g.drawImage(banner, 0, 0, width, height, this); //resize automatically
		}
	}

}
