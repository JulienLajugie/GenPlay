/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import yu.einstein.gdp2.gui.track.Track;


/**
 * A dialog box used to choose a track. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackChooser extends JDialog {

	private static final long 	serialVersionUID = 2840205300507226959L;	// Generated ID
	private static JLabel 		jlText;										// label 
	private static JComboBox 	jcbTrack;									// comboBox to choose the track
	private static JButton 		jbOk;										// OK button
	private static JButton 		jbCancel;									// cancel button
	private static Track<?>[] 	options;									// list of available tracks 
	private static String		textLabel;									// text of the label
	private static boolean 		validated;									// true if OK has been pressed
	
	
	/**
	 * Private constructor. Used internally to create a TrackChooser dialog. 
	 * @param parent The {@link Component} from which the dialog is displayed.
	 * @param title Title of the dialog.
	 * @param text Text of the dialog.
	 * @param tracks List of {@link Track}.
	 */
	private TrackChooser(Component parent, String title, String text, Track<?>[] tracks) {
		super();
		options = tracks;
		validated = false;
		textLabel = text;
		setModal(true);
		setTitle(title);
		initComponent();	
		
		setPreferredSize(new Dimension(300, 175));
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	
	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jlText = new JLabel(textLabel);
		jcbTrack = new JComboBox(options);

		jbOk = new JButton("OK");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();				
			}
		});
		
		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();				
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER ;
		c.weightx = 0.5;
		c.weighty = 0.20;
		c.anchor = GridBagConstraints.CENTER;
		add(jlText, c);

		c.gridy = 1;
		add(jcbTrack, c);

		
		c.fill = GridBagConstraints.NONE;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(jbOk, c);

		c.gridx = 1;
		add(jbCancel, c);		
	}

	
	/**
	 * Closes the dialog. No action are performed.
	 */
	private void jbCancelActionPerformed() {
		this.dispose();
	}

	
	/**
	 * Closes the dialog. Sets validated to true so the main function can return the two selected tracks.
	 */
	private void jbOkActionPerformed() {
		validated = true;
		this.dispose();		
	}


	/**
	 * Only public function. Displays a TrackChooser dialog, and returns a track
	 * @param parent The {@link Component} from which the dialog is displayed.
	 * @param title Title of the dialog.
	 * @param text Text of the dialog.
	 * @param tracks List of {@link Track}.
	 * @return The selected track
	 */
	public static Track<?> getTracks(Component parent, String title, String text, Track<?>[] tracks) {
		TrackChooser tc = new TrackChooser(parent, title, text, tracks);
		tc.setVisible(true);			
		if(validated) {
			return (Track<?>)jcbTrack.getSelectedItem();
		}
		else
			return null;
	}
}
