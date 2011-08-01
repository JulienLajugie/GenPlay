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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.projectScreen;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JPanel;

/**
 * This class load the banner on the project screen top.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BannerPanel extends JPanel {
	
	private static final long serialVersionUID = 3269872506089576106L;
	
	private final static 	String 	BANNER_PATH = "edu/yu/einstein/genplay/resource/genplay_banner_447x91.png";	// Path of the banner
	private 				Image 	banner = null;	// The banner
	
	
	/**
	 * Constructor of {@link BannerPanel}
	 */
	protected BannerPanel () {
		//Size Panel
		setSize(ProjectScreen.getBannerDim());
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setMaximumSize(getSize());

		//Background color
		setBackground(ProjectScreen.getBannerColor());
		
		//Load the image
		banner = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(BANNER_PATH));
	}
	
	/**
	 * The picture loaded in the banner panel is automatically resize to fit well.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g); //paint background
		if (banner != null) { //there is a picture: draw it
			int height = this.getSize().height;
			int width = this.getSize().width;
			g.drawImage(banner, 0, 0, width, height, this); //resize automatically
		}
	}
	
}