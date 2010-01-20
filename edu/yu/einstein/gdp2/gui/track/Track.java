/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.gui.event.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.GenomeWindowListener;
import yu.einstein.gdp2.gui.event.GenomeWindowModifier;
import yu.einstein.gdp2.util.ZoomManager;

/**
 * A generic track
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class Track extends JPanel implements Serializable, PropertyChangeListener, GenomeWindowListener, GenomeWindowModifier {

	private static final long serialVersionUID = -8153338844001326776L;	// generated ID
	private static final int 	TRACK_HEIGHT = 100;					// height of a track
	private static final int 	TRACK_MINIMUM_HEIGHT = 30; 			// minimum height of a track
	public static final Border 	REGULAR_BORDER =
		BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black); 	// regular border of a track
	public static final Border 	ALT_BORDER = 
		BorderFactory.createLineBorder(Color.black, 2);				// alternative border of a track
	private final ArrayList<GenomeWindowListener> listenerList = 
		new ArrayList<GenomeWindowListener>();						// list of GenomeWindowListener
	protected TrackHandle 		trackHandle;						// handle of the track
	protected TrackGraphics 	trackGraphics;						// graphics part of the track
	
	
	/**
	 * initializes the {@link TrackGraphics}
	 * @param zoomManager {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 */
	abstract protected void initTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow);

	
	/**
	 * Copies the track
	 * @return a copy of the track
	 */
	abstract public Track copy();
	

	/**
	 * Initializes the component and the subcomponents
	 * @param zoomManager {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 */
	protected final void initComponent(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, int trackNumber) {
		// create handle
		initTrackHandle(trackNumber);
		trackHandle.addPropertyChangeListener(this);
		
		// create graphics
		initTrackGraphics(zoomManager, displayedGenomeWindow);
		trackGraphics.addPropertyChangeListener(this);
		trackGraphics.addGenomeWindowListener(this);
		
		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		add(trackHandle, gbc);
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(trackGraphics, gbc);

		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		setPreferredHeight(TRACK_HEIGHT);
	}
	
	
	public String toString() {
		return getName();
	}
	
	
	/**
	 * Initializes the {@link TrackHandle}
	 * @param trackNumber number of the track
	 * @return a new {@link TrackHandle}
	 */
	private void initTrackHandle(int trackNumber){
			trackHandle = new TrackHandle(trackNumber);
	}
	
	
	/**
	 * Sets the {@link GenomeWindow} displayed by the track
	 * @param newGenomeWindow new {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		trackGraphics.setGenomeWindow(newGenomeWindow);
	}
	
	
	/**
	 * @return the displayed {@link GenomeWindow}
	 */
	public GenomeWindow getGenomeWindow() {
		return trackGraphics.getGenomeWindow();
	}

	
	/**
	 * @return the number of the track
	 */
	public int getTrackNumber() {
		return trackHandle.getTrackNumber();
	}
	
	
	/**
	 * Sets the number of the track
	 * @param trackNumber the number of the track
	 */
	public void setTrackNumber(int trackNumber) {
		trackHandle.setTrackNumber(trackNumber);
	}
	
	
	/**
	 * @return the preferred height of the track
	 */
	public int getPreferredHeight() {
		return getPreferredSize().height;
	}	
	
	
	/**
	 * Sets the preferred Height of the track
	 * @param newPreferredHeight new preferred height
	 */
	public void setPreferredHeight(int newPreferredHeight) {
		setPreferredSize(new Dimension(getPreferredSize().width, newPreferredHeight));
		revalidate();
	}

	
	/**
	 * Renames the track
	 * @param newName a new name for the track
	 */
	@Override
	public void setName(String newName) {
		trackGraphics.setName(newName);
	}
	

	/**
	 * @return the name of the track
	 */
	@Override
	public String getName() {
		if ((trackGraphics == null) || (trackHandle == null)){
			return super.getName();
		}
		if (trackGraphics.getName() != null) {
			return trackGraphics.getName();
		} else {
			return new String("Track #" + trackHandle.getTrackNumber());
		}
	}
	
	
	/**
	 * @return true if the scroll mode is on
	 */
	public boolean getScrollMode() {
		return trackGraphics.getScrollMode();
	}
	
	
	/**
	 * Turns the scroll mode on / off
	 * @param scrollMode
	 */
	public void setScrollMode(boolean scrollMode) {
		trackGraphics.setScrollMode(scrollMode);
	}
	
	
	/**
	 * shows stripes on the track
	 * @param stripeList a {@link ChromosomeWindowList}
	 */
	public void setStripes(ChromosomeWindowList stripeList) {
		trackGraphics.setStripes(stripeList);
		repaint();
	}


	/**
	 * @return the stripe list of the track
	 */
	public ChromosomeWindowList getStripes() {
		return trackGraphics.getStripes();		
	}
	
	
	/**
	 * @param verticalLineCount the verticalLineCount to set
	 */
	public void setVerticalLineCount(int verticalLineCount) {
		trackGraphics.setVerticalLineCount(verticalLineCount);
	}


	/**
	 * @return the verticalLineCount
	 */
	public int getVerticalLineCount() {
		return trackGraphics.getVerticalLineCount();
	}
	
	
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName() == "resize") {
			int newHeight = getPreferredSize().height + (Integer)arg0.getNewValue();
			// we don't want the new height to be smaller than TRACK_MINIMUM_HEIGHT	
			newHeight = Math.max(TRACK_MINIMUM_HEIGHT, newHeight);
			setPreferredSize(new Dimension(getPreferredSize().width, newHeight));
			revalidate();
		} else if (arg0.getPropertyName() == "defaultSize") {
			setPreferredSize(new Dimension(getPreferredSize().width, TRACK_HEIGHT));
			revalidate();
		} else {
			firePropertyChange(arg0.getPropertyName(), arg0.getOldValue(), arg0.getNewValue());
		}
	}
	

	/**
	 * Locks the handle of the track
	 */
	public void lockHandle() {
		trackHandle.lock();	
	}


	/**
	 * Unlocks the handle of the track
	 */
	public void unlockHandle() {
		trackHandle.unlock();
	}
	

	/**
	 * Save the {@link TrackGraphics} as an image
	 * @param file output file
	 */
	public void saveAsImage(File file) {
		trackGraphics.saveAsImage(file);
	}
	
	
	/**
	 * @param selected the value to set
	 */
	public void setSelected(boolean selected) {
		trackHandle.setSelected(selected);
	}


	/**
	 * @return true if the track is selected
	 */
	public boolean isSelected() {
		return trackHandle.isSelected();
	}
	
	
	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.add(genomeWindowListener);		
	}
	
	
	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		// we notify the listeners
		for (GenomeWindowListener currentListener: listenerList) {
			currentListener.genomeWindowChanged(evt);
		}		
	}
}
