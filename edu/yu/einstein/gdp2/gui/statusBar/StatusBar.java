/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import yu.einstein.gdp2.gui.event.TrackListActionEvent;
import yu.einstein.gdp2.gui.event.TrackListActionListener;


/**
 * @author Julien Lajugie
 * @version 0.1
 */
public class StatusBar extends JPanel implements TrackListActionListener{

	private static final long serialVersionUID = 6145997500187047785L;
	private static final int PANEL_HEIGHT = 10;
	private final MemoryPanel memoryPanel;
	private final JProgressBar jpbProgress;
	private final JLabel jlAction;
	
	
	
	public StatusBar() {		
		jpbProgress = new JProgressBar();
		jpbProgress.setBackground(Color.white);
		jpbProgress.setMinimumSize(jpbProgress.getPreferredSize());
		jpbProgress.setSize(jpbProgress.getPreferredSize());
		jlAction = new JLabel();
		
		memoryPanel = new MemoryPanel();
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 10, 0, 0);
		add(jpbProgress, gbc);
		
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.gridx = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(jlAction, gbc);
		
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 2;		
		add(memoryPanel, gbc);
		
		setPreferredSize(new Dimension(getPreferredSize().width, PANEL_HEIGHT));
		setBorder(BorderFactory.createEtchedBorder());
	}
	

	@Override
	public void actionEnds(TrackListActionEvent evt) {
		jpbProgress.setIndeterminate(false);
		jlAction.setText(evt.getActionDescription());		
	}


	@Override
	public void actionStarts(TrackListActionEvent evt) {
		jpbProgress.setIndeterminate(true);
		jlAction.setText(evt.getActionDescription());
	}
}
