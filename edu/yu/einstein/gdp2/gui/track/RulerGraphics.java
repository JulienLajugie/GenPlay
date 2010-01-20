/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.util.ZoomManager;

/**
 * The graphics part of the ruler
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RulerGraphics extends TrackGraphics {

	private static final long serialVersionUID = 1612257945809961448L; // Generated ID
	private static final int 			LINE_COUNT = 10;						// Number of line to print (must be an even number)
	private static final Color			LINE_COLOR = Color.lightGray;			// color of the lines
	private static final Color			TEXT_COLOR = Color.black;				// color of the text
	private static final Color			MIDDLE_LINE_COLOR = Color.red;			// color of the line in the middle
	private static final int 			MAJOR_TEXT_HEIGHT = 11;					// height of the absolute position text
	private static final int 			MINOR_TEXT_HEIGHT = 2;					// height of the relative position text
	private static final DecimalFormat 	DF = new DecimalFormat("###,###,###");	// decimal format


	/**
	 * Creates an instance of {@link RulerGraphics}
	 * @param zoomManager a {@link ZoomManager}
	 * @param genomeWindow displayed {@link GenomeWindow}
	 */
	public RulerGraphics(ZoomManager zoomManager, GenomeWindow genomeWindow) {
		super(zoomManager, genomeWindow);
		setVisible(true);
	}

	
	@Override
	protected void drawTrack(Graphics g) {
		drawRelativeUnits(g);
		drawAbsoluteUnits(g);		
	}
	

	/**
	 * Draws the absolute units. 
	 * @param g2D
	 */
	private void drawAbsoluteUnits(Graphics g) {
		int width = getWidth();
		int halfWidth = (int)Math.round(width / 2d);
		int height = getHeight();
		int positionStart = genomeWindow.getStart();
		int positionStop = genomeWindow.getStop();

		g.setColor(MIDDLE_LINE_COLOR);
		int yText = height - MAJOR_TEXT_HEIGHT;
		String stringToPrint = DF.format(positionStart); 
		g.drawString(stringToPrint, 2, yText);
		stringToPrint = DF.format((positionStart + positionStop) / 2); 
		g.drawString(stringToPrint, halfWidth + 3, yText);
		stringToPrint = DF.format(positionStop); 
		g.drawString(stringToPrint, width - fm.stringWidth(stringToPrint) - 1, yText);
	}


	/**
	 * Draws the relative units.
	 * @param g2D
	 */
	private void drawRelativeUnits(Graphics g) {
		int height = getHeight();
		int positionStart = genomeWindow.getStart();
		int positionStop = genomeWindow.getStop();
		int y = height - MINOR_TEXT_HEIGHT;
		int lastTextStopPos = 0;
		double gap = getWidth() / (double)LINE_COUNT;
		for (int i = 0; i < LINE_COUNT; i++) {
			int x1 = (int)Math.round(i * gap);
			int x2 = (int)Math.round((2 * i + 1) * gap / 2d);
			int distanceFromMiddle = Math.abs(i - LINE_COUNT / 2) * (positionStop - positionStart) / LINE_COUNT;
			String stringToPrint = DF.format(distanceFromMiddle);
			if (x1 >= lastTextStopPos) {
				g.setColor(TEXT_COLOR);
				g.drawString(stringToPrint, x1 + 2, y);
				lastTextStopPos = x1 + fm.stringWidth(stringToPrint) + 2;
			} else {
				g.setColor(LINE_COLOR);
				g.drawLine(x1, y, x1, height);
			}
			g.setColor(LINE_COLOR);
			g.drawLine(x2, y, x2, height);	
		}	
	}
}