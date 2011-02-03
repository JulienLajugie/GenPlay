/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;

import java.awt.Color;


/**
 * This enumeration contains a single static method that returns
 * a color for a track. The color is selected amid all 
 * the elements of the enumeration {@link TrackColor}. When all 
 * the colors of the enumeration have been selected the algorithm
 * start from the first color again. 
 * @author Julien Lajugie
 * @version 0.1
 */
public enum TrackColor {

	RED (Color.RED),
	DARK_BLUE (new Color(0x0000CD)),
	LIGHT_GREEN(new Color(0x7FFF00)),
	GRAY (new Color(0x696969)),
	PINK (new Color(0xFF69B4)),
	BROWN (new Color(0x8B4513)),
	LIGHT_CYAN (new Color(0x00FFFF)),
	DARK_GREEN (new Color(0x006400)),
	ORANGE (new Color(0xFF4500)),
	PURPLE (new Color(0x800080)),
	LIGHT_BLUE (new Color(0xADD8E6)),
	BLACK (Color.BLACK),
	YELLOW (new Color(0xFFD700)),
	DARK_CYAN (new Color(0x008B8B));

	private static int currentColorInt = 0; // static field that saves the next selected color index
	private final Color color; 				// color field of the TrackColorEnum


	/**
	 * Private constructor. Creates an instance of a {@link TrackColorEnum}
	 * @param color color of the enum element
	 */
	private TrackColor(Color color){
		this.color = color;
	}


	/**
	 * @return a color for the track. The color is selected amid all 
	 * the elements of the enumeration {@link TrackColor}. When all 
	 * the colors of the enumeration have been selected the algorithm
	 * start from the first color again. 
	 */
	public static Color getTrackColor() {
		TrackColor[] trackColors = TrackColor.values();
		Color currentColor = trackColors[currentColorInt].color;
		currentColorInt++;
		// we want the value to be in the array length range
		currentColorInt = currentColorInt % trackColors.length;
		return currentColor;		
	}
}

