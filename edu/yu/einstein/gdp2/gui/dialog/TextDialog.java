/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/**
 * A {@link JDialog} with a {@link JEditorPane}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TextDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4149933399246523843L; // generated ID

	private static final Dimension DIALOG_DIMENSION = new Dimension(600, 500); // dimension of the dialog
	private final JEditorPane 	jepText;	// display the text
	private final JScrollPane 	jspText;	// scroll pane for the text
	private final JButton 		jbOk;		// button ok


	/**
	 * Private constructor.
	 * Creates an instance of {@link TextDialog}
	 * @param fileURL url of the file to display
	 * @param title title of the dialog
	 * @throws IOException
	 */
	private TextDialog(String fileURL, String title) throws IOException {
		jepText = new JEditorPane(fileURL);
		jepText.setEditable(false);
		// add  hyperlink gestion
		jepText.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent evt) {
				if (Desktop.isDesktopSupported()) {
					if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						try {
							Desktop.getDesktop().browse(evt.getURL().toURI());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});

		jspText = new JScrollPane(jepText);

		jbOk = new JButton("Ok");
		jbOk.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		add(jspText, c);

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		add(jbOk, c);

		setTitle(title);
		getRootPane().setDefaultButton(jbOk);
		pack();
		setSize(DIALOG_DIMENSION);
		setResizable(false);
		setModal(true);
		setLocationRelativeTo(getRootPane());		
	}


	/**
	 * Closes the window
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		dispose();		
	}


	/**
	 * Shows the dialog
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param fileURL url of the file to display
	 * @param title title of the dialog
	 * @throws IOException
	 */
	public static void showDialog(Component parent, String fileURL, String title) throws IOException {
		TextDialog textDialog = new TextDialog(fileURL, title);
		textDialog.setLocationRelativeTo(parent);
		textDialog.setVisible(true);
	}
}
