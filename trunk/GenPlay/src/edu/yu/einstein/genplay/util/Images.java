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
package edu.yu.einstein.genplay.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class Images {

	private static final ClassLoader cl = MainFrame.class.getClassLoader();

	// Path of the application images at different resolution
	private static final String[] APPLICATION_IMAGE_PATHS 		=
		{
		"edu/yu/einstein/genplay/resource/images/GenPlay_16x16.png",
		"edu/yu/einstein/genplay/resource/images/GenPlay_24x24.png",
		"edu/yu/einstein/genplay/resource/images/GenPlay_32x32.png",
		"edu/yu/einstein/genplay/resource/images/GenPlay_48x48.png",
		"edu/yu/einstein/genplay/resource/images/GenPlay_64x64.png",
		"edu/yu/einstein/genplay/resource/images/GenPlay_96x96.png",
		"edu/yu/einstein/genplay/resource/images/GenPlay_128x128.png",
		"edu/yu/einstein/genplay/resource/images/GenPlay_256x256.png",
		"edu/yu/einstein/genplay/resource/images/GenPlay_512x512.png",
		};

	private static final String BANNER_IMAGE_PATH				= "edu/yu/einstein/genplay/resource/images/genplay_banner_447x91.png";		// Path of the banner image
	private static final String TOOLS_IMAGE_PATH 				= "edu/yu/einstein/genplay/resource/images/tools_16x16.png";				// Path of the tools image
	private static final String ADD_IMAGE_PATH 					= "edu/yu/einstein/genplay/resource/images/add_entry_50x50.png"; 			// Path of the add entry image
	private static final String EDIT_IMAGE_PATH 				= "edu/yu/einstein/genplay/resource/images/edit_entry_50x50.png"; 			// Path of the edit entry image
	private static final String DELETE_IMAGE_PATH 				= "edu/yu/einstein/genplay/resource/images/delete_entry_50x50.png"; 		// Path of the delete entry image
	private static final String MOUSE_IMAGE_PATH				= "edu/yu/einstein/genplay/resource/images/mouse_right_click_50x50.png"; 	// Path of the mouse image
	private static final String NEXT_IMAGE_PATH 				= "edu/yu/einstein/genplay/resource/images/next_icon_32x32.png"; 			// Path of the next image
	private static final String PREVIOUS_IMAGE_PATH 			= "edu/yu/einstein/genplay/resource/images/previous_icon_32x32.png"; 		// Path of the previous image
	private static final String ACTUALIZE_IMAGE_PATH 			= "edu/yu/einstein/genplay/resource/images/actualize_15x15.png";			// Path of the actualize image
	private static final String SUPERIOR_IMAGE_PATH 			= "edu/yu/einstein/genplay/resource/images/superior_8x6.png";				// Path of the superior sign
	private static final String INFERIOR_IMAGE_PATH 			= "edu/yu/einstein/genplay/resource/images/inferior_8x6.png";				// Path of the inferior sign
	private static final String DNA_IMAGE_PATH 					= "edu/yu/einstein/genplay/resource/images/dna_25x17.png";					// Path of the DNA sign
	private static final String LOADING_IMAGE_PATH 				= "edu/yu/einstein/genplay/resource/images/loading_anim_32x32.gif";			// Path of the loading animated GIF
	private static final String HELP_IMAGE_PATH 				= "edu/yu/einstein/genplay/resource/images/help_16x16.png";					// Path of the help image
	private static final String BOOKMARK_IMAGE_PATH 			= "edu/yu/einstein/genplay/resource/images/bookmark_24x24.png";				// Path of the bookmark image
	private static final String BOOKMARK_ROLLEDOVER_IMAGE_PATH 	= "edu/yu/einstein/genplay/resource/images/bookmark_rolled_over_24x24.png";	// Path of the bookmark rolled over image
	private static final String BOOKMARK_DISABLED_PATH 			= "edu/yu/einstein/genplay/resource/images/bookmark_disabled_24x24.png";	// Path of the bookmark disabled image
	private static final String JUMP_IMAGE_PATH					= "edu/yu/einstein/genplay/resource/images/jump_24x24.png";					// Path of the jump image
	private static final String JUMP_ROLLEDOVER_IMAGE_PATH		= "edu/yu/einstein/genplay/resource/images/jump_rolled_over_24x24.png";		// Path of the jump rolled over image
	private static final String JUMP_DISABLED_IMAGE_PATH		= "edu/yu/einstein/genplay/resource/images/jump_disabled_24x24.png";		// Path of the jump disabled
	private static final String STOP_IMAGE_PATH					= "edu/yu/einstein/genplay/resource/images/stop_16x16.png";					// Path of the stop image
	private static final String STOP_ROLLEDOVER_IMAGE_PATH		= "edu/yu/einstein/genplay/resource/images/stop_rolled_over_16x16.png";		// Path of the stop rolled over image
	private static final String STOP_DISABLED_IMAGE_PATH		= "edu/yu/einstein/genplay/resource/images/stop_disabled_16x16.png";		// Path of the stop disabled
	private static final String RECYCLE_IMAGE_PATH				= "edu/yu/einstein/genplay/resource/images/recycle_16x16.png";				// Path of the recycle image
	private static final String RECYCLEROLLEDOVER_IMAGE_PATH	= "edu/yu/einstein/genplay/resource/images/recycle_rolled_over_16x16.png";	// Path of the recycle rolled over image
	private static final String PLUS_IMAGE_PATH					= "edu/yu/einstein/genplay/resource/images/plus_24x24.png";					// Path of the plus image
	private static final String PLUS_ROLLEDOVER_IMAGE_PATH		= "edu/yu/einstein/genplay/resource/images/plus_rolled_over_24x24.png";		// Path of the plus rolled over image
	private static final String MINUS_IMAGE_PATH				= "edu/yu/einstein/genplay/resource/images/minus_24x24.png";				// Path of the minus image
	private static final String MINUS_ROLLEDOVER_IMAGE_PATH		= "edu/yu/einstein/genplay/resource/images/minus_rolled_over_24x24.png";	// Path of the minus rolled over image


	private static List<Image> applicationImages	= null;		// list with the application images at different resolution
	private static Image bannerImage				= null;		// Banner image
	private static Image toolsImage 				= null;		// Tools image
	private static Image addImage 					= null;		// Add image
	private static Image editImage					= null;		// Edit image
	private static Image deleteImage				= null;		// Delete image
	private static Image mouseImage 				= null;		// Mouse image
	private static Image nextImage 					= null;		// Next image
	private static Image previousImage 				= null;		// Previous image
	private static Image actualizeImage 			= null;		// Actualize image
	private static Image superiorImage 				= null;		// Superior image
	private static Image inferiorImage 				= null;		// Inferior image
	private static Image dnaImage 					= null;		// DNA image
	private static Image loadingImage				= null;		// image showed while tracks are loading
	private static Image helpImage					= null;		// help image
	private static Image bookmarkImage				= null;		// bookmark image
	private static Image bookmarkRolledOverImage	= null;		// bookmark rolled over image
	private static Image bookmarkDisabledImage		= null;		// bookmark disabled image
	private static Image jumpImage					= null;		// jump image
	private static Image jumpRolledOverImage		= null;		// jump rolled over image
	private static Image jumpDisabledImage			= null;		// jump disabled image
	private static Image stopImage					= null;		// stop image
	private static Image stopRolledOverImage		= null;		// stop rolled over image
	private static Image stopDisabledImage			= null;		// stop disabled image
	private static Image recycleImage 				= null;		// recycle image
	private static Image recycleRolledOverImage 	= null;		// recycle rolled over
	private static Image plusImage 					= null;		// plus image
	private static Image plusRolledOverImage 		= null;		// plus rolled over
	private static Image minusImage					= null;		// minus image
	private static Image minusRolledOverImage 		= null;		// minus rolled over image


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
	 * @return a list containing images of the GenPlay icon at different resolutions
	 */
	public static List<Image> getApplicationImages () {
		if (applicationImages == null) {
			applicationImages = new ArrayList<Image>();
			for (String path: APPLICATION_IMAGE_PATHS) {
				applicationImages.add(Toolkit.getDefaultToolkit().getImage(cl.getResource(path)));
			}
		}
		return applicationImages;
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
	 * @return the bookmark disabled image
	 */
	public static Image getBookmarkDisabledImage () {
		if (bookmarkDisabledImage == null) {
			bookmarkDisabledImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(BOOKMARK_DISABLED_PATH));
		}
		return bookmarkDisabledImage;
	}


	/**
	 * @return the bookmark image
	 */
	public static Image getBookmarkImage () {
		if (bookmarkImage == null) {
			bookmarkImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(BOOKMARK_IMAGE_PATH));
		}
		return bookmarkImage;
	}


	/**
	 * @return the bookmark rolled over image
	 */
	public static Image getBookmarkRolledOverImage () {
		if (bookmarkRolledOverImage == null) {
			bookmarkRolledOverImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(BOOKMARK_ROLLEDOVER_IMAGE_PATH));
		}
		return bookmarkRolledOverImage;
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
	 * @return the help image
	 */
	public static Image getHelpImage () {
		if (helpImage == null) {
			helpImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(HELP_IMAGE_PATH));
		}
		return helpImage;
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
	 * @return the jump disabled image
	 */
	public static Image getJumpDisabledImage () {
		if (jumpDisabledImage == null) {
			jumpDisabledImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(JUMP_DISABLED_IMAGE_PATH));
		}
		return jumpDisabledImage;
	}


	/**
	 * @return the jump image
	 */
	public static Image getJumpImage () {
		if (jumpImage == null) {
			jumpImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(JUMP_IMAGE_PATH));
		}
		return jumpImage;
	}


	/**
	 * @return the jump rolled over image
	 */
	public static Image getJumpRolledOverImage () {
		if (jumpRolledOverImage == null) {
			jumpRolledOverImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(JUMP_ROLLEDOVER_IMAGE_PATH));
		}
		return jumpRolledOverImage;
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
	 * @return the minus image
	 */
	public static Image getMinusImage () {
		if (minusImage == null) {
			minusImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(MINUS_IMAGE_PATH));
		}
		return minusImage;
	}


	/**
	 * @return the minus rolled over image
	 */
	public static Image getMinusRolledOverImage () {
		if (minusRolledOverImage == null) {
			minusRolledOverImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(MINUS_ROLLEDOVER_IMAGE_PATH));
		}
		return minusRolledOverImage;
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
	 * @return the image of the Next icon
	 */
	public static Image getNextImage () {
		if (nextImage == null) {
			nextImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(NEXT_IMAGE_PATH));
		}
		return nextImage;
	}


	/**
	 * @return the plus image
	 */
	public static Image getPlusImage () {
		if (plusImage == null) {
			plusImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(PLUS_IMAGE_PATH));
		}
		return plusImage;
	}


	/**
	 * @return the plus rolled over image
	 */
	public static Image getPlusRolledOverImage () {
		if (plusRolledOverImage == null) {
			plusRolledOverImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(PLUS_ROLLEDOVER_IMAGE_PATH));
		}
		return plusRolledOverImage;
	}


	/**
	 * @return the image of the Previous icon
	 */
	public static Image getPreviousImage () {
		if (previousImage == null) {
			previousImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(PREVIOUS_IMAGE_PATH));
		}
		return previousImage;
	}


	/**
	 * @return the recycle image
	 */
	public static Image getRecycleImage () {
		if (recycleImage == null) {
			recycleImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(RECYCLE_IMAGE_PATH));
		}
		return recycleImage;
	}


	/**
	 * @return the plus recycle over image
	 */
	public static Image getRecycleRolledOverImage () {
		if (recycleRolledOverImage == null) {
			recycleRolledOverImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(RECYCLEROLLEDOVER_IMAGE_PATH));
		}
		return recycleRolledOverImage;
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
	 * @return the stop disabled image
	 */
	public static Image getStopDisabledImage () {
		if (stopDisabledImage == null) {
			stopDisabledImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(STOP_DISABLED_IMAGE_PATH));
		}
		return stopDisabledImage;
	}


	/**
	 * @return the stop image
	 */
	public static Image getStopImage () {
		if (stopImage == null) {
			stopImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(STOP_IMAGE_PATH));
		}
		return stopImage;
	}


	/**
	 * @return the stop rolled over image
	 */
	public static Image getStopRolledOverImage () {
		if (stopRolledOverImage == null) {
			stopRolledOverImage = Toolkit.getDefaultToolkit().getImage(cl.getResource(STOP_ROLLEDOVER_IMAGE_PATH));
		}
		return stopRolledOverImage;
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
