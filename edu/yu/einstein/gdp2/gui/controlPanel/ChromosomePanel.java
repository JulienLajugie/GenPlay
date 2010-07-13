/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowListener;


/**
 * The ChromosomePanel part of the {@link ControlPanel} 
 * @author Julien Lajugie
 * @version 0.1
 */
final class ChromosomePanel extends JPanel implements MouseWheelListener, ItemListener, GenomeWindowEventsGenerator {

	private static final long serialVersionUID = -7749788921295566004L;	// generated ID
	private final JLabel 							jlChromosome;		// label chromosome
	private final JComboBox 						jcbChromosome;		// combo box chromosome
	private final ArrayList<GenomeWindowListener> 	listenerList;		// list of GenomeWindowListener
	private GenomeWindow 							currentGenomeWindow;// current GenomeWindow
	
	
	/**
	 * Creates an instance of {@link ChromosomePanel}
	 * @param genomeWindow a {@link GenomeWindow}
	 */
	ChromosomePanel(GenomeWindow genomeWindow) {
		this.currentGenomeWindow = genomeWindow;
		this.listenerList = new ArrayList<GenomeWindowListener>();
		jlChromosome = new JLabel(" Chromosome ");
		// Create ComboBox for the chromosome selection
		jcbChromosome = new JComboBox(ChromosomeManager.getInstance().toArray());
		// select the first item case currentChromosome is not in the list
		jcbChromosome.setSelectedIndex(0);
		jcbChromosome.setSelectedItem(currentGenomeWindow.getChromosome());
		jcbChromosome.addItemListener(this);
		
		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jlChromosome, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jcbChromosome, gbc);

		addMouseWheelListener(this);
	}

	
	/**
	 * Sets the current {@link GenomeWindow}
	 * @param newGenomeWindow new {@link GenomeWindow}
	 */
	void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(currentGenomeWindow)) {
			GenomeWindow oldGenomeWindow = currentGenomeWindow;
			currentGenomeWindow = newGenomeWindow;
			// we notify the gui
			jcbChromosome.setSelectedItem(currentGenomeWindow.getChromosome());
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, currentGenomeWindow);
			for (GenomeWindowListener currentListener: listenerList) {
				currentListener.genomeWindowChanged(evt);
			}
		}
	}

	
	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		if (mwe.getWheelRotation() + jcbChromosome.getSelectedIndex() < 0) {
			jcbChromosome.setSelectedIndex(0);
		} else if (mwe.getWheelRotation() + jcbChromosome.getSelectedIndex() > jcbChromosome.getItemCount() - 1) {
			jcbChromosome.setSelectedIndex(jcbChromosome.getItemCount() - 1);
		} else {
			jcbChromosome.setSelectedIndex(mwe.getWheelRotation() + jcbChromosome.getSelectedIndex());
		}		
	}


	@Override
	public void itemStateChanged(ItemEvent arg0) {
		Chromosome newChromosome = (Chromosome)jcbChromosome.getSelectedItem();
		if (!newChromosome.equals(currentGenomeWindow.getChromosome())) {
			GenomeWindow newGenomeWindow = new GenomeWindow(newChromosome, 0, newChromosome.getLength());
			setGenomeWindow(newGenomeWindow);
		}		
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
