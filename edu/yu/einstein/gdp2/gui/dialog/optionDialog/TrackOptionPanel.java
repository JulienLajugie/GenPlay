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

/**
 * Panel of the {@link OptionDialog} that allows to configure the tracks * 
 * @author Julien Lajugie
 * @version 0.1
 */
final class TrackOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 1941311091566384114L; 	// generated ID
	private static final int MIN_TRACK_COUNT = 1; 						// minimum number of tracks
	private static final int MAX_TRACK_COUNT = 1024; 					// maximum number of tracks
	private static final int MIN_TRACK_HEIGHT = 30; 					// minimum height of the tracks
	private static final int MAX_TRACK_HEIGHT = 2000; 					// maximum height of the tracks
	private final JLabel 				jlTrackCount; 		// label track count
	private final JFormattedTextField 	jftfTrackCount; 	// text field track count
	private final JLabel 				jlTrackHeight; 		// label track height
	private final JFormattedTextField 	jftfTrackHeight;	// text field track count
	private final JLabel 				jlUndoCount; 		// label undo count
	private final JFormattedTextField 	jftfUndoCount; 		// label undo count

	
	/**
	 * Creates an instance of {@link TrackOptionPanel}
	 */
	public TrackOptionPanel() {
		super("Track Option");

		jlTrackCount = new JLabel("Number of Tracks:");

		jftfTrackCount = new JFormattedTextField(new DecimalFormat("#"));
		((NumberFormatter) jftfTrackCount.getFormatter()).setMinimum(MIN_TRACK_COUNT);
		((NumberFormatter) jftfTrackCount.getFormatter()).setMaximum(MAX_TRACK_COUNT);
		jftfTrackCount.setColumns(5);
		jftfTrackCount.setValue(configurationManager.getTrackCount());
		jftfTrackCount.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				configurationManager.setTrackCount(((Number) jftfTrackCount.getValue()).intValue());
			}
		});

		jlTrackHeight = new JLabel("Default Track Height:");

		jftfTrackHeight = new JFormattedTextField(new DecimalFormat("#"));
		((NumberFormatter) jftfTrackHeight.getFormatter()).setMinimum(MIN_TRACK_HEIGHT);
		((NumberFormatter) jftfTrackHeight.getFormatter()).setMaximum(MAX_TRACK_HEIGHT);
		jftfTrackHeight.setColumns(5);
		jftfTrackHeight.setValue(configurationManager.getTrackHeight());
		jftfTrackHeight.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				configurationManager.setTrackHeight(((Number) jftfTrackHeight
						.getValue()).intValue());
			}
		});

		jlUndoCount = new JLabel("Undo Count:");
		jftfUndoCount = new JFormattedTextField(new DecimalFormat("#"));
		((NumberFormatter) jftfUndoCount.getFormatter()).setMinimum(0);
		((NumberFormatter) jftfUndoCount.getFormatter()).setMaximum(Integer.MAX_VALUE);
		jftfUndoCount.setColumns(5);
		jftfUndoCount.setValue(configurationManager.getUndoCount());
		jftfUndoCount.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				configurationManager.setUndoCount(((Number) jftfUndoCount.getValue()).intValue());
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
		c.insets = new Insets(10, 0, 10, 10);
		add(jlTrackHeight, c);

		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jftfTrackHeight, c);

		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 10, 10);
		add(jlUndoCount, c);

		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_END;
		c.insets = new Insets(10, 10, 10, 0);
		add(jftfUndoCount, c);
	}
}
