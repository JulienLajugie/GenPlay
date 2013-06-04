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
package edu.yu.einstein.genplay.util;

import java.awt.Image;
import java.awt.Toolkit;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class Images {

	private static final ClassLoader cl = MainFrame.class.getClassLoader();

	//	private static final String APPLICATION_IMAGE_PATH 	= "edu/yu/einstein/genplay/resource/images/icon.png";						// Path of the application image
	private static final String APPLICATION_IMAGE_PATH 	= "edu/yu/einstein/genplay/resource/images/GenPlay.png";					// Path of the application image
	private static final String BANNER_IMAGE_PATH		= "edu/yu/einstein/genplay/resource/images/genplay_banner_447x91.png";		// Path of the banner image
	private static final String TOOLS_IMAGE_PATH 		= "edu/yu/einstein/genplay/resource/images/tools.png";						// Path of the tools image
	private static final String ADD_IMAGE_PATH 			= "edu/yu/einstein/genplay/resource/images/add_entry_50x50.png"; 			// Path of the add entry image
	private static final String EDIT_IMAGE_PATH 		= "edu/yu/einstein/genplay/resource/images/edit_entry_50x50.png"; 			// Path of the edit entry image
	private static final String DELETE_IMAGE_PATH 		= "edu/yu/einstein/genplay/resource/images/delete_entry_50x50.png"; 		// Path of the delete entry image
	private static final String MOUSE_IMAGE_PATH		= "edu/yu/einstein/genplay/resource/images/mouse_right_click_50x50.png"; 	// Path of the mouse image
	private static final String NEXT_IMAGE_PATH 		= "edu/yu/einstein/genplay/resource/images/next_icon_256x256.png"; 			// Path of the next image
	private static final String PREVIOUS_IMAGE_PATH 	= "edu/yu/einstein/genplay/resource/images/previous_icon_256x256.png"; 		// Path of the previous image
	private static final String ACTUALIZE_IMAGE_PATH 	= "edu/yu/einstein/genplay/resource/images/actualize15x15.png";				// Path of the actualize image
	private static final String SUPERIOR_IMAGE_PATH 	= "edu/yu/einstein/genplay/resource/images/superior8x6.png";				// Path of the superior sign
	private static final String INFERIOR_IMAGE_PATH 	= "edu/yu/einstein/genplay/resource/images/inferior8x6.png";				// Path of the inferior sign
	private static final String DNA_IMAGE_PATH 			= "edu/yu/einstein/genplay/resource/images/dna_25x17.png";					// Path of the DNA sign
	private static final String LOADING_IMAGE_PATH 		= "edu/yu/einstein/genplay/resource/images/loading_anim.gif";				// Path of the application image
	private static Image applicationImage 	= null;		// Application image
	private static Image bannerImage		= null;		// Banner image
	private static Image toolsImage 		= null;		// Tools image
	private static Image addImage 			= null;		// Add image
	private static Image editImage			= null;		// Edit image
	private static Image deleteImage		= null;		// Delete image
	private static Image mouseImage 		= null;		// Mouse image
	private static Image nextImage 			= null;		// Next image
	private static Image previousImage 		= null;		// Previous image
	private static Image actualizeImage 	= null;		// Actualize image
	private static Image superiorImage 		= null;		// Superior image
	private static Image inferiorImage 		= null;		// Inferior image
	private static Image dnaImage 			= null;		// DNA image
	private static Image loadingImage		= null;		// image showed while tracks are loading


	/**
	 * @return the image of the Actualize icon (15x15)
	 */
	public static Image getActualizeImage () {
		if (actualizeImage == null) {
			actualizeImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(ACTUALIZE_IMAGE_PATH));
		}
		return actualizeImage;
	}


	/**
	 * @return the image of the Add icon (50x50)
	 */
	public static Image getAddImage () {
		if (addImage == null) {
			addImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(ADD_IMAGE_PATH));
		}
		return addImage;
	}


	/**
	 * @return the image of the GenPlay icon
	 */
	public static Image getApplicationImage () {
		if (applicationImage == null) {
			applicationImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(APPLICATION_IMAGE_PATH));
		}
		return applicationImage;
	}


	/**
	 * @return the image of the banner icon (447x91)
	 */
	public static Image getBannerImage () {
		if (bannerImage == null) {
			bannerImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(BANNER_IMAGE_PATH));
		}
		return bannerImage;
	}


	/**
	 * @return the image of the Delete icon (50x50)
	 */
	public static Image getDeleteImage () {
		if (deleteImage == null) {
			deleteImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(DELETE_IMAGE_PATH));
		}
		return deleteImage;
	}


	/**
	 * @return the image of the DNA icon (25x17)
	 */
	public static Image getDNAImage () {
		if (dnaImage == null) {
			dnaImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(DNA_IMAGE_PATH));
		}
		return dnaImage;
	}


	/**
	 * @return the image of the Edit icon (50x50)
	 */
	public static Image getEditImage () {
		if (editImage == null) {
			editImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(EDIT_IMAGE_PATH));
		}
		return editImage;
	}


	/**
	 * @return the image of the Inferior icon (8x6)
	 */
	public static Image getInferiorImage () {
		if (inferiorImage == null) {
			inferiorImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(INFERIOR_IMAGE_PATH));
		}
		return inferiorImage;
	}


	/**
	 * @return the image showed while tracks are loading
	 */
	public static Image getLoadingImage () {
		if (loadingImage == null) {
			loadingImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(LOADING_IMAGE_PATH));
		}
		return loadingImage;
	}


	/**
	 * @return the image of the Mouse icon (50x50)
	 */
	public static Image getMouseImage () {
		if (mouseImage == null) {
			mouseImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(MOUSE_IMAGE_PATH));
		}
		return mouseImage;
	}


	/**
	 * @return the image of the Next icon (256x256)
	 */
	public static Image getNextImage () {
		if (nextImage == null) {
			nextImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(NEXT_IMAGE_PATH));
		}
		return nextImage;
	}


	/**
	 * @return the image of the Previous icon (256x256)
	 */
	public static Image getPreviousImage () {
		if (previousImage == null) {
			previousImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(PREVIOUS_IMAGE_PATH));
		}
		return previousImage;
	}


	/**
	 * Creates a new resized image.
	 * @param image		the image
	 * @param width 	width of the new image
	 * @param height 	height of the new image
	 * @return			the resized image
	 */
	public static Image getResizedImage (Image image, int width, int height) {
		Image newImg = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return newImg;
	}


	/**
	 * Creates a new squared image.
	 * @param image	the image
	 * @param side	size of the side
	 * @return		the resized
	 */
	public static Image getSquareImage (Image image, int side) {
		return getResizedImage(image, side, side);
	}


	/**
	 * @return the image of the Superior icon (8x6)
	 */
	public static Image getSuperiorImage () {
		if (superiorImage == null) {
			superiorImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(SUPERIOR_IMAGE_PATH));
		}
		return superiorImage;
	}


	/**
	 * @return the image of the Tools icon
	 */
	public static Image getToolsImage () {
		if (toolsImage == null) {
			toolsImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(TOOLS_IMAGE_PATH));
		}
		return toolsImage;
	}
}
