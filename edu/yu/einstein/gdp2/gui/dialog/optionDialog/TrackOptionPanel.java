/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.optionDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.text.NumberFormatter;

import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ConfigurationManager;


/**
 * Panel of the {@link OptionDialog} that allows to configure the tracks 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 1941311091566384114L; // generated ID

	private static final int MIN_TRACK_COUNT = 1;		// minimum number of tracks
	private static final int MAX_TRACK_COUNT = 1024;	// maximum number of tracks
	private static final int MIN_TRACK_HEIGHT = 30;		// minimum height of the tracks 
	private static final int MAX_TRACK_HEIGHT = 2000;		// maximum height of the tracks
	
	private final JLabel 				jlTrackCount;	// label track count
	private final JFormattedTextField 	jftfTrackCount;	// text field track count
	private final JLabel 				jlTrackHeight;	// label track height
	private final JFormattedTextField 	jftfTrackHeight;// text field track count

	
	/**
	 * Creates an instance of {@link TrackOptionPanel}
	 * @param configMmanager a {@link ChromosomeManager}
	 */
	public TrackOptionPanel(ConfigurationManager configMmanager) {
		super("Track Option", configMmanager);
		
		jlTrackCount = new JLabel("Number of track:");
		
		jftfTrackCount = new JFormattedTextField(new DecimalFormat("#"));
		((NumberFormatter)jftfTrackCount.getFormatter()).setMinimum(MIN_TRACK_COUNT);
		((NumberFormatter)jftfTrackCount.getFormatter()).setMaximum(MAX_TRACK_COUNT);
		jftfTrackCount.setColumns(5);
		jftfTrackCount.setValue(cm.getTrackCount());
		jftfTrackCount.addPropertyChangeListener(new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				cm.setTrackCount(((Number)jftfTrackCount.getValue()).intValue());
			}
		});
		
		jlTrackHeight = new JLabel("Default track Height:");
		
		jftfTrackHeight = new JFormattedTextField(new DecimalFormat("#"));
		((NumberFormatter)jftfTrackHeight.getFormatter()).setMinimum(MIN_TRACK_HEIGHT);
		((NumberFormatter)jftfTrackHeight.getFormatter()).setMaximum(MAX_TRACK_HEIGHT);
		jftfTrackHeight.setColumns(5);
		jftfTrackHeight.setValue(cm.getTrackHeight());
		jftfTrackHeight.addPropertyChangeListener(new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				cm.setTrackHeight(((Number)jftfTrackHeight.getValue()).intValue());
			}
		});
		
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jlTrackCount, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jftfTrackCount, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 10);
		add(jlTrackHeight, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 0, 0);
		add(jftfTrackHeight, c);
	}
}
