/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.newCurveTrackDialog;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * Panel for the track name input of a {@link NewCurveTrackDialog}
 * @author Julien Lajugie
 * @version 0.1
 */
class TrackNamePanel extends JPanel {	
	
	private static final long serialVersionUID = -5969101278574088008L;	// generated ID
	private final JTextField jtfTrackName;	// text field for the track name
	

	/**
	 * Creates an instance of a {@link TrackNamePanel}
	 * @param trackName default name of a track
	 */
	TrackNamePanel(String trackName) {
		super();
		jtfTrackName = new JTextField(trackName);
		jtfTrackName.setColumns(15);
		add(jtfTrackName);
		setBorder(BorderFactory.createTitledBorder("Track Name"));
	}
	

	/**
	 * @return the name inside the input box
	 */
	String getTrackName() {
		return jtfTrackName.getText();
	}
}
