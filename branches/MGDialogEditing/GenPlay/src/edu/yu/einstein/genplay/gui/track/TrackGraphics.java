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
package edu.yu.einstein.genplay.gui.track;

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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.ChromosomeWindow;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectZoom;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.track.drawer.MultiGenomeDrawer;
import edu.yu.einstein.genplay.gui.track.drawer.TrackHeaderDrawer;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Graphics part of a track
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 * @param <T> type of data
 */
public abstract class TrackGraphics<T> extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, GenomeWindowListener {


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
					newWindow.setChromosome(projectWindow.getGenomeWindow().getChromosome());
					newWindow.setStart(projectWindow.getGenomeWindow().getStart()- scrollModeIntensity);
					newWindow.setStop(projectWindow.getGenomeWindow().getStop() - scrollModeIntensity);
					if (newWindow.getMiddlePosition() < 0) {
						newWindow.setStart(-projectWindow.getGenomeWindow().getSize() / 2);
						newWindow.setStop(newWindow.getStart() + projectWindow.getGenomeWindow().getSize());
					} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
						newWindow.setStop(newWindow.getChromosome().getLength() + projectWindow.getGenomeWindow().getSize() / 2);
						newWindow.setStart(newWindow.getStop() - projectWindow.getGenomeWindow().getSize());
					}
					projectWindow.setGenomeWindow(newWindow);
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

	
	private static final long serialVersionUID = -1930069442535000515L; 	// Generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;				// saved format version
	private static final int	 			VERTICAL_LINE_COUNT = 10;		// number of vertical lines to print
	private static final Color				LINE_COLOR = Colors.LIGHT_GREY;	// color of the lines
	private static final Color				MIDDLE_LINE_COLOR = Colors.RED;	// color of the line in the middle
	private static final Color				STRIPES_COLOR = Colors.GREY;	// color of the stripes
	private static final int				STRIPES_TRANSPARENCY = 150;		// transparency of the stripes
	protected static final String 			FONT_NAME = "ARIAL";			// name of the font
	protected static final int 				FONT_SIZE = 10;					// size of the font
	private static int						trackGraphicsWidth = 0;			// width of the track graphics
	protected FontMetrics 		fm = 
		getFontMetrics(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE)); 		// FontMetrics to get the size of a string
	private int 							verticalLineCount;				// number of vertical lines to print
	transient private int					mouseStartDragX = -1;			// position of the mouse when start dragging
	transient private boolean				isScrollMode = false;			// true if the scroll mode is on
	transient private int					scrollModeIntensity = 0;		// Intensity of the scroll.
	transient private ScrollModeThread 		scrollModeThread; 				// Thread executed when the scroll mode is on
	private ChromosomeWindowList			stripeList = null;				// stripes to display on the track
	protected T 							data;							// data showed in the track
	private String 							genomeName;						// genome on which the track is based (ie aligned on)
	private TrackHeaderDrawer				trackHeaderDrawer;				// the track header drawer
	protected ProjectWindow					projectWindow;					// instance of the genome window manager
	private MultiGenomeDrawer<T>			multiGenomeDrawer = null;		// the multi genome drawer manages all MG graphics


	/**
	 * Creates an instance of {@link TrackGraphics}
	 * @param displayedGenomeWindow {@link GenomeWindow} currently displayed
	 * @param data data showed in the track
	 */
	protected TrackGraphics(T data) {
		super();
		this.data = data;
		this.verticalLineCount = VERTICAL_LINE_COUNT;
		this.projectWindow = ProjectManager.getInstance().getProjectWindow();
		this.trackHeaderDrawer = new TrackHeaderDrawer();
		TrackGraphics.trackGraphicsWidth = getWidth();
		setBackground(Colors.WHITE);
		setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		multiGenomeInitializing();
	}
	
	
	public void setName (String name) {
		super.setName(name);
	}


	/**
	 * @param mouseXPosition X position of the mouse on the track
	 * @return the scroll intensity
	 */
	public int computeScrollIntensity(int mouseXPosition) {
		double res = projectWindow.twoScreenPosToGenomeWidth(trackGraphicsWidth, mouseXPosition, getWidth() / 2);
		if (res > 0) {
			return (int) (res / 10d) + 1;	
		} else {
			return (int) (res / 10d) - 1;
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


	//////////////////////////////////////////////////////////////////////////////// Multi Genome


	/**
	 * Initializes the genome drawer
	 */
	public void multiGenomeInitializing() {
		if (multiGenomeDrawer == null && ProjectManager.getInstance().isMultiGenomeProject()) {
			multiGenomeDrawer = new MultiGenomeDrawer<T>();
		}
	}


	/**
	 * Draws stripes showing information for multi genome
	 * @param g
	 */
	public void drawMultiGenomeInformation(Graphics g) {
		if (multiGenomeDrawer != null) {
			multiGenomeDrawer.drawMultiGenomeInformation(g, projectWindow.getGenomeWindow(), projectWindow.getXFactor());
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Draws the header of the track.
	 * The header of the track is composed of:
	 * - the name
	 * - the stripe legend (for multi-genome)
	 * It allows a fine width distribution between these 2 texts.
	 * @param g
	 */
	public void drawHeaderTrack (Graphics g) {
		if (g == null) {
			g = getGraphics(); 
		}
		trackHeaderDrawer.drawHeaderTrack(g, multiGenomeDrawer, getFontMetrics(getFont()), getName(), getWidth(), getBackground());
	}


	/**
	 * Draws stripes on the track
	 * @param g Graphics
	 */
	protected void drawStripes(Graphics g) {
		if (stripeList != null) {
			int height = getHeight();
			// create a transparent color for the stripes
			Color color = new Color(STRIPES_COLOR.getRed(), STRIPES_COLOR.getGreen(), STRIPES_COLOR.getBlue(), STRIPES_TRANSPARENCY);
			g.setColor(color);
			List<ChromosomeWindow> chromoStripeList = stripeList.getFittedData(projectWindow.getGenomeWindow(), projectWindow.getXFactor());//(start, stop);
			if (chromoStripeList != null) {
				for (ChromosomeWindow currentStripe: chromoStripeList) {
					int x = projectWindow.genomePosToScreenXPos(currentStripe.getStart()); 
					//int widthWindow = projectWindow.genomePosToScreenXPos(currentStripe.getStop()) - x;
					int widthWindow = projectWindow.twoGenomePosToScreenWidth(currentStripe.getStart(), currentStripe.getStop());
					if (widthWindow < 1) {
						widthWindow = 1;
					}
					g.fillRect(x, 0, widthWindow, height);
				}
			}
		}
	}


	/**
	 * Called by paintComponent. 
	 * Draws the track
	 * @param g {@link Graphics}
	 */
	abstract protected void drawTrack(Graphics g);


	/**
	 * Draws the vertical lines
	 * @param g {@link Graphics}
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
	 * @return the data showed in the track
	 */
	public T getData() {
		return data;
	}


	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @return true if the scroll mode is on
	 */
	public boolean getScrollMode() {
		return isScrollMode;
	}


	/**
	 * @return the stripe list of the track
	 */
	public ChromosomeWindowList getStripes() {
		return stripeList;		
	}


	/**
	 * @return the verticalLineCount
	 */
	public int getVerticalLineCount() {
		return verticalLineCount;
	}


	/**
	 * Calls firePropertyChange with a new {@link GenomeWindow} center where the mouse double clicked.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// double left click
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2) && (!isScrollMode)) {
			// Compute the distance from the cursor to the center of the screen
			double distance = projectWindow.twoScreenPosToGenomeWidth(trackGraphicsWidth, getWidth() / 2, e.getX());
			distance = Math.floor(distance);
			GenomeWindow newWindow = new GenomeWindow();
			newWindow.setChromosome(projectWindow.getGenomeWindow().getChromosome());
			newWindow.setStart(projectWindow.getGenomeWindow().getStart()+ (int) distance);
			newWindow.setStop(projectWindow.getGenomeWindow().getStop() + (int) distance);
			if ((newWindow.getMiddlePosition()) >= 0 && (newWindow.getMiddlePosition() <= newWindow.getChromosome().getLength())) {
				projectWindow.setGenomeWindow(newWindow);
			}
		}
		if (e.getButton() == MouseEvent.BUTTON3 && ProjectManager.getInstance().isMultiGenomeProject()) {
			multiGenomeDrawer.toolTipStripe(getHeight(), e);
		}
	}


	/**
	 * Update the {@link GenomeWindow} when the track is dragged horizontally.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			double distance = projectWindow.twoScreenPosToGenomeWidth(trackGraphicsWidth, e.getX(), mouseStartDragX);
			if ((distance > 1) || (distance < -1)) {
				GenomeWindow newWindow = new GenomeWindow();
				newWindow.setChromosome(projectWindow.getGenomeWindow().getChromosome());
				newWindow.setStart(projectWindow.getGenomeWindow().getStart()+ (int) distance);
				newWindow.setStop(projectWindow.getGenomeWindow().getStop() + (int) distance);
				if (newWindow.getMiddlePosition() < 0) {
					newWindow.setStart(-projectWindow.getGenomeWindow().getSize() / 2);
					newWindow.setStop(newWindow.getStart() + projectWindow.getGenomeWindow().getSize());
				} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
					newWindow.setStop(newWindow.getChromosome().getLength() + projectWindow.getGenomeWindow().getSize() / 2);
					newWindow.setStart(newWindow.getStop() - projectWindow.getGenomeWindow().getSize());
				}
				projectWindow.setGenomeWindow(newWindow);
				mouseStartDragX = e.getX();
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
		} else if (multiGenomeDrawer != null && multiGenomeDrawer.hasToBeRepaintAfterExit()) {
			repaint();
		}
	}


	@Override
	public void mouseMoved(final MouseEvent e) {
		if ((isScrollMode) && (getMousePosition() != null)) {
			scrollModeIntensity = computeScrollIntensity(getMousePosition().x);
		} else if (multiGenomeDrawer != null && multiGenomeDrawer.isOverVariant(getHeight(), e)) {
			repaint();
		}
	}


	/**
	 * Sets the variable mouseStartDragX when the user press the button 1 of the mouse.
	 * Activates/deactivates the scroll mode when the middle button of the mouse is released
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
			mouseStartDragX = e.getX();
		}
		// Activates/deactivates scroll mode
		if (e.getButton() == MouseEvent.BUTTON2) { // click on middle button
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
	public void mouseReleased(MouseEvent e) {}


	/**
	 * Update the {@link GenomeWindow} when the mouse wheel is used
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int newZoom = 0;
		int weelRotation = Math.abs(e.getWheelRotation());
		boolean isZoomIn = e.getWheelRotation() > 0;
		for (int i = 0; i < weelRotation; i++) {
			// retrieve the only instance of the singleton ZoomManager
			ProjectZoom projectZoom = ProjectManager.getInstance().getProjectZoom();
			if (isZoomIn) {
				newZoom = projectZoom.getZoomIn(projectWindow.getGenomeWindow().getSize());
			} else {
				newZoom = projectZoom.getZoomOut(projectWindow.getGenomeWindow().getSize());
			}
			newZoom = Math.min(projectWindow.getGenomeWindow().getChromosome().getLength() * 2, newZoom);
		}
		GenomeWindow newWindow = new GenomeWindow();
		newWindow.setChromosome(projectWindow.getGenomeWindow().getChromosome());
		newWindow.setStart((int)(projectWindow.getGenomeWindow().getMiddlePosition() - newZoom / 2));
		newWindow.setStop(newWindow.getStart() + newZoom);
		projectWindow.setGenomeWindow(newWindow);
	}


	/**
	 * Sets the variable xFactor
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		TrackGraphics.trackGraphicsWidth = getWidth();
		double newXFactor = projectWindow.getXFactor(getWidth());
		if (newXFactor != projectWindow.getXFactor()) {
			projectWindow.setXFactor(newXFactor);
			xFactorChanged();
		}
		drawTrack(g);
	}


	/**
	 * Save the track as a PNG image.
	 * @param file output file
	 */
	public void saveAsImage(File file) {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		paint(g);
		try {         
			ImageIO.write(image, "PNG", file);
		}catch(Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving the tracks as an image");
		}		
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
	}


	/**
	 * Sets the scroll mode
	 * @param scrollMode
	 */
	public void setScrollMode(boolean scrollMode) {
		isScrollMode = scrollMode;
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
	 * @param verticalLineCount the verticalLineCount to set
	 */
	public void setVerticalLineCount(int verticalLineCount) {
		this.verticalLineCount = verticalLineCount;
		repaint();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(verticalLineCount);
		out.writeObject(stripeList);
		out.writeObject(data);
		out.writeObject(genomeName);
		out.writeObject(trackHeaderDrawer);
		out.writeObject(multiGenomeDrawer);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		verticalLineCount = in.readInt();
		stripeList = (ChromosomeWindowList) in.readObject();
		data = (T) in.readObject();
		genomeName = (String) in.readObject();
		trackHeaderDrawer = (TrackHeaderDrawer) in.readObject();
		multiGenomeDrawer = (MultiGenomeDrawer<T>) in.readObject();
		
		projectWindow = ProjectManager.getInstance().getProjectWindow();
		fm = getFontMetrics(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE)); 
	}

	
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
	 * This function is called when the track is deleted.
	 * Removes all the listeners.  Can be overridden.
	 */
	protected void delete() {
		for (PropertyChangeListener curList: getPropertyChangeListeners())	{
			removePropertyChangeListener(curList);
		}	
	}


	/**
	 * @return the genomeDrawer
	 */
	public MultiGenomeDrawer<T> getMultiGenomeDrawer() {
		return multiGenomeDrawer;
	}

	

	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		if (evt.chromosomeChanged()) {
			chromosomeChanged();
		}
		repaint();
	}
	
	/**
	 * This method is executed when the chromosome changes 
	 */
	protected void chromosomeChanged() {}


	/**
	 * @return the trackGraphicsWidth
	 */
	public static int getTrackGraphicsWidth() {
		return trackGraphicsWidth;
	}
}
