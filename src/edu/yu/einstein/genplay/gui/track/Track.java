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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.MultiGenomeStripes;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;



/**
 * A generic track
 * @param <T> type of the data shown in the track
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class Track<T> extends JPanel implements PropertyChangeListener, GenomeWindowListener, GenomeWindowEventsGenerator {

	private static final long serialVersionUID = -8153338844001326776L;	// generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	private static final int 	TRACK_MINIMUM_HEIGHT = 30; 				// minimum height of a track
	private static final int 	TRACK_HEIGHT = 100; 					// height of a track
	/**
	 * Default border of a track
	 */
	public static final Border 	REGULAR_BORDER =
		BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black); 		// regular border of a track
	/**
	 * Border of a track when the track starts being dragged
	 */
	public static final Border 	DRAG_START_BORDER = 
		BorderFactory.createMatteBorder(2, 2, 2, 2, Color.black);		// alternative border when a track is dragged
	/**
	 * Border of a track when the track is being dragged up
	 */
	public static final Border 	DRAG_UP_BORDER = 
		BorderFactory.createMatteBorder(2, 0, 1, 0, Color.black);		// alternative border when a track is dragged up
	/**
	 * Border of a track when the track is being dragged down
	 */
	public static final Border 	DRAG_DOWN_BORDER = 
		BorderFactory.createMatteBorder(0, 0, 2, 0, Color.black);		// alternative border when a track is dragged down
	private List<GenomeWindowListener> listenerList = 
		new ArrayList<GenomeWindowListener>();							// list of GenomeWindowListener
	private int 					defaultHeight = TRACK_HEIGHT;		// default height of a track
	private TrackHandle				trackHandle;						// handle of the track
	protected TrackGraphics<T>		trackGraphics;						// graphics part of the track
	protected String 				genomeName;							// genome on which the track is based (ie aligned on)
	
	
	/**
	 * Constructor
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param trackNumber number of the track
	 * @param data data displayed in the track
	 */
	protected Track(GenomeWindow displayedGenomeWindow, int trackNumber, T data) {
		// create handle
		trackHandle = new TrackHandle(trackNumber);
		trackHandle.addPropertyChangeListener(this);

		// create graphics
		trackGraphics = createsTrackGraphics(displayedGenomeWindow, data);
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
		setPreferredHeight(defaultHeight);
	}


	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.add(genomeWindowListener);
	}
	
	
	/**
	 * Creates the {@link TrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param data data displayed in the track
	 */
	abstract protected TrackGraphics<T> createsTrackGraphics(GenomeWindow displayedGenomeWindow, T data);
	

	/**
	 * Copies the track
	 * @return a copy of the track
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public Track<?> deepClone() throws IOException, ClassNotFoundException {
		// we save in a local variable and then remove the listeners 
		// before cloning the track in order to avoid cloning the listeners
		PropertyChangeListener[] pclSaver = getPropertyChangeListeners(); 
		for (PropertyChangeListener curList: pclSaver)	{
			removePropertyChangeListener(curList);
		}
		GenomeWindowListener[] gwlSaver = getGenomeWindowListeners();
		listenerList.clear();		
		// we remove the listeners of the track graphics as well
		PropertyChangeListener[] trackGraphicsPclSaver = trackGraphics.getPropertyChangeListeners();
		for (PropertyChangeListener curList: trackGraphicsPclSaver)	{
			trackGraphics.removePropertyChangeListener(curList);
		}		
		// we clone the object
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		// we restore the listeners
		for (PropertyChangeListener curList: pclSaver)	{
			addPropertyChangeListener(curList);
		}
		for (GenomeWindowListener curList: gwlSaver) {
			addGenomeWindowListener(curList);
		}
		for (PropertyChangeListener curList: trackGraphicsPclSaver) {
			trackGraphics.addPropertyChangeListener(curList);
		}		
		return (Track<?>) ois.readObject();
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		// we notify the listeners
		for (GenomeWindowListener currentListener: listenerList) {
			currentListener.genomeWindowChanged(evt);
		}		
	}


	/**
	 * @return the data showed in the track
	 */
	public T getData() {
		return trackGraphics.getData();
	}


	/**
	 * @return the genomeName
	 */
	public String getGenomeName() {
		return genomeName;
	}


	/**
	 * @return the displayed {@link GenomeWindow}
	 */
	public GenomeWindow getGenomeWindow() {
		return trackGraphics.getGenomeWindow();
	}


	@Override
	public GenomeWindowListener[] getGenomeWindowListeners() {
		GenomeWindowListener[] genomeWindowListeners = new GenomeWindowListener[listenerList.size()];
		return listenerList.toArray(genomeWindowListeners);
	}


	/**
	 * @return the stripeInformation
	 */
	public MultiGenomeStripes getMultiGenomeStripes() {
		return trackGraphics.getMultiGenomeStripes();
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
	 * @return the preferred height of the track
	 */
	public int getPreferredHeight() {
		return getPreferredSize().height;
	}	


	/**
	 * @return true if the scroll mode is on
	 */
	public boolean getScrollMode() {
		return trackGraphics.getScrollMode();
	}


	/**
	 * @return the stripe list of the track
	 */
	public ChromosomeWindowList getStripes() {
		return trackGraphics.getStripes();
	}


	/**
	 * @return the number of the track
	 */
	public int getTrackNumber() {
		return trackHandle.getTrackNumber();
	}


	/**
	 * @return the verticalLineCount
	 */
	public int getVerticalLineCount() {
		return trackGraphics.getVerticalLineCount();
	}
	

	/**
	 * @return true if the track is selected
	 */
	public boolean isSelected() {
		return trackHandle.isSelected();
	}


	/**
	 * Locks the handle of the track
	 */
	public void lockHandle() {
		trackHandle.lock();	
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
			setPreferredSize(new Dimension(getPreferredSize().width, defaultHeight));
			revalidate();
		} else {
			firePropertyChange(arg0.getPropertyName(), arg0.getOldValue(), arg0.getNewValue());
		}
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		defaultHeight = TRACK_HEIGHT;
		in.readInt();
		listenerList = (List<GenomeWindowListener>) in.readObject();
		trackHandle = (TrackHandle) in.readObject();
		trackGraphics = (TrackGraphics<T>) in.readObject();
		genomeName = (String) in.readObject();
	}


	@Override
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.remove(genomeWindowListener);		
	}


	/**
	 * Save the {@link TrackGraphics} as an image
	 * @param file output file
	 */
	public void saveAsImage(File file) {
		trackGraphics.saveAsImage(file);
	}


	/**
	 * @param genomeName the genomeName to set
	 */
	public void setGenomeName(String genomeName) {
		this.genomeName = genomeName;
		//getStripeInformation().setGenomeName(genomeName);
	}


	/**
	 * Sets the {@link GenomeWindow} displayed by the track
	 * @param newGenomeWindow new {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		trackGraphics.setGenomeWindow(newGenomeWindow);
	}


	/**
	 * @param multiGenomeStripes the stripeInformation to set
	 */
	public void setMultiGenomeStripes(MultiGenomeStripes multiGenomeStripes) {
		trackGraphics.setMultiGenomeStripes(multiGenomeStripes);
		repaint();
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
	 * Sets the preferred Height of the track
	 * @param newPreferredHeight new preferred height
	 */
	public void setPreferredHeight(int newPreferredHeight) {
		setPreferredSize(new Dimension(getPreferredSize().width, newPreferredHeight));
		defaultHeight = newPreferredHeight;
		revalidate();
	}


	/**
	 * Turns the scroll mode on / off
	 * @param scrollMode
	 */
	public void setScrollMode(boolean scrollMode) {
		trackGraphics.setScrollMode(scrollMode);
	}

	
	/**
	 * @param selected the value to set
	 */
	public void setSelected(boolean selected) {
		trackHandle.setSelected(selected);
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
	 * Sets the number of the track
	 * @param trackNumber the number of the track
	 */
	public void setTrackNumber(int trackNumber) {
		trackHandle.setTrackNumber(trackNumber);
	}


	/**
	 * @param verticalLineCount the verticalLineCount to set
	 */
	public void setVerticalLineCount(int verticalLineCount) {
		trackGraphics.setVerticalLineCount(verticalLineCount);
	}


	@Override
	public String toString() {
		if (trackGraphics.getName() != null) {
			return getTrackNumber() + " - " + getName();
		} else {
			return new String("Track #" + trackHandle.getTrackNumber());
		}
	}


	/**
	 * Unlocks the handle of the track
	 */
	public void unlockHandle() {
		trackHandle.unlock();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(listenerList);
		out.writeObject(trackHandle);
		out.writeObject(trackGraphics);
		out.writeObject(genomeName);
	
	}	
}
