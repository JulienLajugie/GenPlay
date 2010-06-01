/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JPanel;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.manager.ZoomManager;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowListener;


/**
 * A ControlPanel component
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ControlPanel extends JPanel implements GenomeWindowListener, GenomeWindowEventsGenerator {

	private static final long serialVersionUID = -8254420324898563978L; // generated ID
	private static final int INCREMENT_FACTOR = 100; 					// the length of a left or right move is the length of the   
																		// displayed chromosome window divided by this constant 
	private final PositionScrollPanel 				positionScrollPanel;// PositionScrollPanel part
	private final ZoomPanel 						zoomPanel;			// ZoomPanel part
	private final ChromosomePanel 					chromosomePanel;	// ChromosomePanel part
	private final GenomeWindowPanel 				genomeWindowPanel;	// GenomeWindowPanel part
	private final ArrayList<GenomeWindowListener> 	listenerList;		// list of GenomeWindowListener
	private GenomeWindow 							currentGenomeWindow;// current GenomeWindow


	/**
	 * Creates an instance of {@link ControlPanel}
	 * @param currentGenomeWindow current {@link GenomeWindow}
	 */
	public ControlPanel(GenomeWindow currentGenomeWindow) { 
		this.currentGenomeWindow = currentGenomeWindow;
		this.listenerList = new ArrayList<GenomeWindowListener>();

		positionScrollPanel = new PositionScrollPanel(currentGenomeWindow);
		positionScrollPanel.addGenomeWindowListener(this);

		zoomPanel = new ZoomPanel(currentGenomeWindow);
		zoomPanel.addGenomeWindowListener(this);

		chromosomePanel = new ChromosomePanel(currentGenomeWindow);
		chromosomePanel.addGenomeWindowListener(this);

		genomeWindowPanel = new GenomeWindowPanel(currentGenomeWindow);
		genomeWindowPanel.addGenomeWindowListener(this);

		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(positionScrollPanel, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(zoomPanel, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(chromosomePanel, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(genomeWindowPanel, gbc);
	}


	/**
	 * Sets the current {@link GenomeWindow}
	 * @param newGenomeWindow new {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(currentGenomeWindow)) {			
			GenomeWindow oldGenomeWindow = currentGenomeWindow;
			currentGenomeWindow = newGenomeWindow;
			// we notify the gui
			positionScrollPanel.setGenomeWindow(newGenomeWindow);
			zoomPanel.setGenomeWindow(newGenomeWindow);
			chromosomePanel.setGenomeWindow(newGenomeWindow);
			genomeWindowPanel.setGenomeWindow(newGenomeWindow);
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, currentGenomeWindow);
			for (GenomeWindowListener currentListener: listenerList) {
				currentListener.genomeWindowChanged(evt);
			}
		}
	}


	/**
	 * Decrements the start and the stop positions of the {@link GenomeWindow} 
	 */
	public void moveLeft() {
		int moveGap = (int) (currentGenomeWindow.getSize() / (double) INCREMENT_FACTOR);
		// we want to move from at least 1 nucleotide
		moveGap = Math.max(moveGap, 1);
		GenomeWindow newGenomeWindow = new GenomeWindow(currentGenomeWindow.getChromosome(), currentGenomeWindow.getStart() - moveGap, currentGenomeWindow.getStop() - moveGap);
		if (newGenomeWindow.getMiddlePosition() < 0) {
			int size = newGenomeWindow.getSize();
			newGenomeWindow.setStart(-size / 2);
			newGenomeWindow.setStop(newGenomeWindow.getStart() + size);
		}
		setGenomeWindow(newGenomeWindow);		
	}


	/**
	 * Increments the start and the stop positions of the {@link GenomeWindow}
	 */
	public void moveRight() {
		int moveGap = (int) (currentGenomeWindow.getSize() / (double) INCREMENT_FACTOR);
		// we want to move from at least 1 nucleotide
		moveGap = Math.max(moveGap, 1);
		GenomeWindow newGenomeWindow = new GenomeWindow(currentGenomeWindow.getChromosome(), currentGenomeWindow.getStart() + moveGap, currentGenomeWindow.getStop() + moveGap);
		if (newGenomeWindow.getMiddlePosition() > currentGenomeWindow.getChromosome().getLength()) {
			int size = newGenomeWindow.getSize();
			newGenomeWindow.setStart(currentGenomeWindow.getChromosome().getLength() - size / 2);
			newGenomeWindow.setStop(newGenomeWindow.getStart() + size);
		}
		setGenomeWindow(newGenomeWindow);		
	}		


	/**
	 * Zooms in
	 */
	public void zoomIn() {
		int newZoom = ZoomManager.getInstance().getZoomIn(currentGenomeWindow.getSize());
		zoomPanel.zoomChanged(newZoom);
	}


	/**
	 * Zooms out
	 */
	public void zoomOut() {
		int newZoom = ZoomManager.getInstance().getZoomOut(currentGenomeWindow.getSize());
		zoomPanel.zoomChanged(newZoom);
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		setGenomeWindow(evt.getNewWindow());
	}


	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.add(genomeWindowListener);			
	}


	@Override
	public GenomeWindowListener[] getGenomeWindowListeners() {
		GenomeWindowListener[] genomeWindowListeners = new GenomeWindowListener[listenerList.size()];
		return listenerList.toArray(genomeWindowListeners);
	}


	@Override
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.remove(genomeWindowListener);		
	}
}
