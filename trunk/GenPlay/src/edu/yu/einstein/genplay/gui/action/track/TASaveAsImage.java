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
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.FileChooser;


/**
 * Saves the selected track as a PNG image
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TASaveAsImage extends TrackListAction {

	private static final long serialVersionUID = -4363481310731795005L; 				// generated ID
	private static final String ACTION_NAME = "Save as Image"; 							// action name
	private static final String DESCRIPTION = "Save the selected track as a PNG image"; // tooltip
	private static final int 	MNEMONIC = KeyEvent.VK_V; 								// mnemonic key


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = TASaveAsImage.class.getName();


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);


	/**
	 * @param selectedTrack selected track in the track panel
	 * @return the screen shot of the selected track with the ruler on top to show the positions
	 */
	public static BufferedImage createImage(Track selectedTrack) {
		// retrieve the image of the ruler
		BufferedImage rulerImage = MainFrame.getInstance().getRuler().getImage();
		// retrieve the image of the selected track
		BufferedImage trackImage = selectedTrack.getImage();
		int imageWidth = trackImage.getWidth();
		int trackHeight = trackImage.getHeight();
		int rulerHeight = rulerImage.getHeight();
		int imageHeight = trackHeight + rulerHeight;
		// we create an image that will contains both the ruler and the selected track
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR); // used to be TYPE_INT_ARGB but it was producing an error on Linux with Open JDK
		Graphics g = image.getGraphics();
		// we draw the image containing the ruler and the track
		g.drawImage(rulerImage, 0, 0, null);
		g.drawImage(trackImage, 0, rulerHeight, null);
		// we draw a horizontal line between the ruler and the track
		g.setColor(Color.BLACK);
		g.drawLine(0, rulerHeight, imageWidth, rulerHeight);
		return image;
	}


	/**
	 * Creates an instance of {@link TASaveAsImage}
	 */
	public TASaveAsImage() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	public void actionPerformed(ActionEvent evt) {
		Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			FileFilter[] filters = {new FileNameExtensionFilter("PNG file (*.PNG)", "png")};
			File f = new File(selectedTrack.getName() + ".png");
			File selectedFile = FileChooser.chooseFile(getRootPane(), FileChooser.SAVE_FILE_MODE, "Save Image As", filters, false, f);
			if(selectedFile != null) {
				MainFrame.getInstance().getStatusBar().actionStart("Saving Track as Image", 0, null);
				try {
					ImageIO.write(createImage(selectedTrack), "PNG", selectedFile);
				}catch(Exception e) {
					ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error while saving the tracks as an image");
				}
				MainFrame.getInstance().getStatusBar().actionStop("Operation Done");
			}
		}
	}
}
