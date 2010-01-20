/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.gui.event.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.GenomeWindowListener;
import yu.einstein.gdp2.gui.event.GenomeWindowModifier;
import yu.einstein.gdp2.util.ZoomManager;

/**
 * A ruler
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Ruler extends JPanel implements GenomeWindowListener, GenomeWindowModifier {

	private static final long serialVersionUID = -5243446035761988387L; // Generated ID
	private static final int 	HANDLE_WIDTH = 50;				// Width of the track handle
	private static final int 	TRACKS_SCROLL_WIDTH = 17;		// Width of the scroll bar
	private static final int 	RULER_HEIGHT = 20;				// Height of the ruler 
	private static final String ICON_FILE_NAME = "tools.png"; 	// name of the menu icon 
	private final RulerGraphics	rulerGraphics;					// Graphics part
	private final JButton 		rulerButton;					// button of the ruler				 
	private final ArrayList<GenomeWindowListener> listenerList;	// list of GenomeWindowListener


	/**
	 * Creates an instance of {@link Ruler}
	 * @param zoomManager a {@link ZoomManager}
	 * @param genomeWindow displayed {@link GenomeWindow}
	 */
	public Ruler(ZoomManager zoomManager, GenomeWindow genomeWindow) {
		listenerList = new ArrayList<GenomeWindowListener>();
		rulerGraphics = new RulerGraphics(zoomManager, genomeWindow);
		rulerGraphics.addGenomeWindowListener(this);
		rulerButton = new JButton();
		initButton();		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		add(rulerButton, gbc);			
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, TRACKS_SCROLL_WIDTH);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(rulerGraphics, gbc);
		setMinimumSize(new Dimension(getPreferredSize().width, RULER_HEIGHT));
	}


	/**
	 * Initializes the button of the ruler
	 */
	private void initButton() {
		rulerButton.setBackground(Color.white);
		rulerButton.setMargin(new Insets(0, 0, 0, 0));
		rulerButton.setFocusPainted(false);
		rulerButton.setIcon(new ImageIcon(ICON_FILE_NAME));
		rulerButton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.lightGray));	
		rulerButton.setPreferredSize(new Dimension(HANDLE_WIDTH + 1, RULER_HEIGHT));
		rulerButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				firePropertyChange("rulerButtonClicked", false, true);
			}
		});
		rulerButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				rulerButton.setBackground(Color.gray);
				super.mouseEntered(e);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				rulerButton.setBackground(Color.white);
				super.mouseExited(e);
			}
		});
	}


	/**
	 * Sets the {@link GenomeWindow} displayed by the ruler
	 * @param newGenomeWindow {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		rulerGraphics.setGenomeWindow(newGenomeWindow);
	}


	/**
	 * Turns the scroll mode on / off
	 * @param scrollMode
	 */
	public void setScrollMode(boolean scrollMode) {
		rulerGraphics.setScrollMode(scrollMode);
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
