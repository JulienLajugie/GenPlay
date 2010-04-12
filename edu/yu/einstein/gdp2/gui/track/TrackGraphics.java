/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import yu.einstein.gdp2.core.ChromosomeWindow;
import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowListener;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.ZoomManager;


/**
 * Graphics part of a track
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TrackGraphics extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, GenomeWindowEventsGenerator {

	private static final long serialVersionUID = -1930069442535000515L; // Generated ID
	private static final int	 		VERTICAL_LINE_COUNT = 10;		// number of vertical lines to print
	private static final Color			LINE_COLOR = Color.lightGray;	// color of the lines
	private static final Color			MIDDLE_LINE_COLOR = Color.red;	// color of the line in the middle
	private static final Color			STRIPES_COLOR = Color.orange;	// color of the stripes
	protected static final String 		FONT_NAME = "ARIAL";			// name of the font
	protected static final int 			FONT_SIZE = 10;					// size of the font
	protected final FontMetrics 		fm = 
		getFontMetrics(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE)); 	// FontMetrics to get the size of a string
	private final ZoomManager 			zoomManager;					// zoom manager
	private final List<GenomeWindowListener> gwListenerList;			// list of GenomeWindowListener
	private int 						verticalLineCount;				// number of vertical lines to print
	private int 						mouseStartDragX = -1;			// position of the mouse when start dragging
	protected double					xFactor;						// factor between the genomic width and the screen width
	protected GenomeWindow				genomeWindow;					// the genome window displayed by the track
	private boolean 					isScrollMode;					// true if the scroll mode is on
	private int 						scrollModeIntensity = 0;		// Intensity of the scroll.
	transient private ScrollModeThread 	scrollModeThread; 				// Thread executed when the scroll mode is on
	private ChromosomeWindowList		stripeList = null;				// stripes to display on the track


	/**
	 * The ScrollModeThread class is used to scroll the track horizontally 
	 * when the scroll mode is on (ie when the middle button of the mouse is clicked)  
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class ScrollModeThread extends Thread {
		@Override
		public void run() {
			synchronized (this) {
				Thread thisThread = Thread.currentThread();
				while (scrollModeThread == thisThread) {
					GenomeWindow newWindow = new GenomeWindow();
					newWindow.setChromosome(genomeWindow.getChromosome());
					newWindow.setStart(genomeWindow.getStart()- scrollModeIntensity);
					newWindow.setStop(genomeWindow.getStop() - scrollModeIntensity);
					if (newWindow.getMiddlePosition() < 0) {
						newWindow.setStart(-genomeWindow.getSize() / 2);
						newWindow.setStop(newWindow.getStart() + genomeWindow.getSize());
					} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
						newWindow.setStop(newWindow.getChromosome().getLength() + genomeWindow.getSize() / 2);
						newWindow.setStart(newWindow.getStop() - genomeWindow.getSize());
					}
					setGenomeWindow(newWindow);
					yield();
					try {
						if ((scrollModeIntensity == 1) || (scrollModeIntensity == -1)) {
							sleep(100);
						} else {
							sleep(10);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	/**
	 * Creates an instance of {@link TrackGraphics}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow {@link GenomeWindow} currently displayed
	 */
	protected TrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow) {
		super();
		this.zoomManager = zoomManager;
		this.genomeWindow = displayedGenomeWindow;
		this.verticalLineCount = VERTICAL_LINE_COUNT;
		this.gwListenerList = new ArrayList<GenomeWindowListener>();
		setBackground(Color.white);
		setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
		addMouseListener(this);		
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}


	/**
	 * @param verticalLineCount the verticalLineCount to set
	 */
	public void setVerticalLineCount(int verticalLineCount) {
		this.verticalLineCount = verticalLineCount;
		repaint();
	}


	/**
	 * @return the verticalLineCount
	 */
	public int getVerticalLineCount() {
		return verticalLineCount;
	}


	/**
	 * Sets the variable xFactor
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		double newXFactor = (double)getWidth() / (double)(genomeWindow.getStop() - genomeWindow.getStart());
		if (newXFactor != xFactor) {
			xFactor = newXFactor;
			xFactorChanged();
		}
		drawTrack(g);
	}

	
	/**
	 * Called by paintComponent. 
	 * Draws the track
	 * @param g {@link Graphics}
	 */
	abstract protected void drawTrack(Graphics g);
	

	/**
	 * This method is executed when the xFactor changes 
	 */
	protected void xFactorChanged() {
		if (isScrollMode) {
			if (getMousePosition(true) != null) {
				scrollModeIntensity = computeScrollIntensity(getMousePosition().x);
			}
		}
	}

	
	/**
	 * This method is executed when the chromosome changes 
	 */
	protected void chromosomeChanged() {}
	
	
	/**
	 * @param mouseXPosition X position of the mouse on the track
	 * @return the scroll intensity
	 */
	public int computeScrollIntensity(int mouseXPosition) {
		int res = twoScreenPosToGenomeWidth(mouseXPosition, getWidth() / 2);
		if (res > 0) {
			return twoScreenPosToGenomeWidth(mouseXPosition, getWidth() / 2) / 10 + 1;	
		} else {
			return twoScreenPosToGenomeWidth(mouseXPosition, getWidth() / 2) / 10 - 1;
		}		
	}
	

	/**
	 * @return the displayed {@link GenomeWindow}
	 */
	public GenomeWindow getGenomeWindow() {
		return genomeWindow;
	}


	/**
	 * Sets the {@link GenomeWindow} displayed.
	 * @param newGenomeWindow new displayed {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(genomeWindow)) {
			GenomeWindow oldGenomeWindow = genomeWindow;
			genomeWindow = newGenomeWindow;
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, genomeWindow);
			for (GenomeWindowListener currentListener: gwListenerList) {
				currentListener.genomeWindowChanged(evt);
			}
			if (genomeWindow.getChromosome() != oldGenomeWindow.getChromosome()) {
				chromosomeChanged();
			}
			repaint();
		}
	}


	/**
	 * @return the {@link ZoomManager}
	 */
	public ZoomManager getZoomManager() {
		return zoomManager;
	}


	/**
	 * shows stripes on the track
	 * @param stripeList a {@link ChromosomeWindowList}
	 */
	public void setStripes(ChromosomeWindowList stripeList) {
		this.stripeList = stripeList;
		repaint();
	}


	/**
	 * @return the stripe list of the track
	 */
	public ChromosomeWindowList getStripes() {
		return stripeList;		
	}


	/**
	 * @return true if the scroll mode is on
	 */
	public boolean getScrollMode() {
		return isScrollMode;
	}


	/**
	 * Sets the scroll mode
	 * @param scrollMode
	 */
	public void setScrollMode(boolean scrollMode) {
		isScrollMode = scrollMode;
	}


	/**
	 * @param genomePosition a position on the genome
	 * @return the absolute position on the screen (can be > than the screen width)
	 */
	protected int genomePosToScreenPos(int genomePosition) {
		return (int)Math.round((double)(genomePosition - genomeWindow.getStart()) * xFactor);
	}

	
	/**
	 * @param genomePositionStart start position on the genome
	 * @param genomePositionStop stop position on the genome
	 * @return the width on the screen between this tow positions
	 */
	protected int twoGenomePosToScreenWidth(int genomePositionStart, int genomePositionStop) {
		double x1 = ((double)(genomePositionStart - genomeWindow.getStart())) * xFactor;
		double x2 = ((double)(genomePositionStop - genomeWindow.getStart())) * xFactor;
		double distance = Math.abs(x1 - x2);
		return (int)Math.round(distance);
	}


	/**
	 * @param x1 position 1 on the screen
	 * @param x2 position 2 on the screen
	 * @return the distance in base pair between the screen position x1 and x2 
	 */
	protected int twoScreenPosToGenomeWidth(int x1, int x2) {
		double distance = ((double)(x2 - x1) / (double)getWidth() * (double)(genomeWindow.getStop() - genomeWindow.getStart()));
		if ((distance > 0) && (distance < 1)) {
			distance = 1;
		} else if ((distance < 0) && (distance > -1)) {
			distance = -1;
		}	
		return (int)Math.round(distance);
	}


	/**
	 * Draws the vertical lines
	 * @param g2D Graphics2D
	 */
	protected void drawVerticalLines(Graphics g) {
		g.setColor(LINE_COLOR);
		double gap = getWidth() / (double)verticalLineCount;
		int y1 = 0;
		int y2 = getHeight();
		for (int i = 0; i < verticalLineCount; i++) {
			int x = (int)Math.round(i * gap);
			g.drawLine(x, y1, x, y2);
		}
	}

	/**
	 * Draws the main line in the middle of the track
	 * @param g
	 */
	protected void drawMiddleVerticalLine(Graphics g) {
		int y1 = 0;
		int y2 = getHeight();
		g.setColor(MIDDLE_LINE_COLOR);
		int x = (int)Math.round(getWidth() / (double)2);
		g.drawLine(x, y1, x, y2);	
	}

	/**
	 * Draws the name of the track
	 * @param g
	 */
	protected void drawName(Graphics g) {
		if ((getName() != null) && (getName().trim().length() != 0)) {
			int x = getWidth() - fm.stringWidth(getName()) - 4;
			int textWidth = fm.stringWidth(getName()) + 4;
			int textHeight = fm.getHeight();
			g.setColor(getBackground());
			g.fillRect(x, 1, textWidth, textHeight + 2);
			g.setColor(Color.blue);
			g.drawRect(x, 1, textWidth - 1, textHeight + 2);
			g.drawString(getName(), x + 2, textHeight);
		}
	}


	/**
	 * Draws stripes on the track
	 * @param g Graphics
	 */
	protected void drawStripes(Graphics g) {
		if (stripeList != null) {
			int height = getHeight();
			g.setColor(STRIPES_COLOR);
			List<ChromosomeWindow> chromoStripeList = stripeList.getFittedData(genomeWindow, xFactor);//(start, stop);
			if (chromoStripeList != null) {
				for (ChromosomeWindow currentStripe: chromoStripeList) {
					int x = genomePosToScreenPos(currentStripe.getStart()); 
					int widthWindow = genomePosToScreenPos(currentStripe.getStop()) - x;
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					g.fillRect(x, 0, widthWindow, height);
				}
			}
		}
	}


	/**
	 * Calls firePropertyChange with a new {@link GenomeWindow} center where the mouse double clicked.
	 * Starts / stops the scroll mode when the middle button is clicked
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// double left click
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2) && (!isScrollMode)) {
			// Compute the distance from the cursor to the center of the screen
			int distance = twoScreenPosToGenomeWidth(getWidth() / 2, e.getX());
			GenomeWindow newWindow = new GenomeWindow();
			newWindow.setChromosome(genomeWindow.getChromosome());
			newWindow.setStart(genomeWindow.getStart()+ distance);
			newWindow.setStop(genomeWindow.getStop() + distance);
			if ((newWindow.getMiddlePosition()) >= 0 && (newWindow.getMiddlePosition() <= newWindow.getChromosome().getLength())) {
				setGenomeWindow(newWindow);
			}
		} else if (e.getButton() == MouseEvent.BUTTON2) { // click on middle button
			isScrollMode = !isScrollMode;
			if (isScrollMode) {
				setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
				scrollModeIntensity = computeScrollIntensity(getMousePosition().x);
				scrollModeThread = new ScrollModeThread();
				scrollModeThread.start();
				firePropertyChange("scrollMode", false, true);
			} else {
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				scrollModeThread = null;
				firePropertyChange("scrollMode", true, false);
			}
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		if (isScrollMode) {
			setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
			scrollModeThread = new ScrollModeThread();
			scrollModeThread.start();
		} else {
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
	}


	@Override
	public void mouseExited(MouseEvent e) {
		if (isScrollMode) {
			scrollModeThread = null;
		}
	}


	/**
	 * Sets the variable mouseStartDragX when the user press the button 1 of the mouse.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			mouseStartDragX = e.getX();
		}		
	}


	@Override
	public void mouseReleased(MouseEvent e) {}


	/**
	 * Update the {@link GenomeWindow} when the track is dragged horizontally.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			int distance = twoScreenPosToGenomeWidth(e.getX(), mouseStartDragX);
			GenomeWindow newWindow = new GenomeWindow();
			newWindow.setChromosome(genomeWindow.getChromosome());
			newWindow.setStart(genomeWindow.getStart()+ distance);
			newWindow.setStop(genomeWindow.getStop() + distance);
			if (newWindow.getMiddlePosition() < 0) {
				newWindow.setStart(-genomeWindow.getSize() / 2);
				newWindow.setStop(newWindow.getStart() + genomeWindow.getSize());
			} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
				newWindow.setStop(newWindow.getChromosome().getLength() + genomeWindow.getSize() / 2);
				newWindow.setStart(newWindow.getStop() - genomeWindow.getSize());
			}
			setGenomeWindow(newWindow);
			mouseStartDragX = e.getX();
		}		
	}


	@Override
	public void mouseMoved(final MouseEvent e) {
		if ((isScrollMode) && (getMousePosition() != null)) {
			scrollModeIntensity = computeScrollIntensity(getMousePosition().x);
		}
	}


	/**
	 * Update the {@link GenomeWindow} when the mouse wheel is used
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int newZoom = 0;
		int weelRotation = Math.abs(e.getWheelRotation());
		boolean isZoomIn = e.getWheelRotation() > 0;
		for (int i = 0; i < weelRotation; i++) {
			if (isZoomIn) {
				newZoom = zoomManager.getZoomIn(genomeWindow.getSize());
			} else {
				newZoom = zoomManager.getZoomOut(genomeWindow.getSize());
			}
			newZoom = Math.min(genomeWindow.getChromosome().getLength() * 2, newZoom);
		}
		GenomeWindow newWindow = new GenomeWindow();
		newWindow.setChromosome(genomeWindow.getChromosome());
		newWindow.setStart((int)(genomeWindow.getMiddlePosition() - newZoom / 2));
		newWindow.setStop(newWindow.getStart() + newZoom);
		setGenomeWindow(newWindow);
	}


	/**
	 * Save the track as a JPG image.
	 * @param file ouput file
	 */
	public void saveAsImage(File file) {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		paint(g);
		try {         
			ImageIO.write(image, "JPEG", file);
		}catch(Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving the tracks as an image");
		}		
	}


	/**
	 * Copy the current {@link TrackGraphics} to the specified {@link TrackGraphics}
	 * @param trackGraphics specified {@link TrackGraphics}
	 */
	public void copyTo(TrackGraphics trackGraphics) {
		trackGraphics.stripeList = stripeList;
		trackGraphics.setName(getName());
	}

	
	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		gwListenerList.add(genomeWindowListener);		
	}
	
	
	@Override
	public GenomeWindowListener[] getGenomeWindowListeners() {
		GenomeWindowListener[] genomeWindowListeners = new GenomeWindowListener[gwListenerList.size()];
		return gwListenerList.toArray(genomeWindowListeners);
	}
	
	
	@Override
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		gwListenerList.remove(genomeWindowListener);		
	}
}
