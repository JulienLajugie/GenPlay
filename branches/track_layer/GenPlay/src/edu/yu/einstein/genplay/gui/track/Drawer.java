package edu.yu.einstein.genplay.gui.track;

import java.awt.Graphics;

/**
 * Interface that can be implemented by classes that draw
 * on a graphics context such as tracks or ruler.
 * @author Julien Lajugie
 */
public interface Drawer {


	/**
	 * Draws on the specific {@link Graphics} context
	 * @param g
	 */
	public void draw(Graphics g);
}
