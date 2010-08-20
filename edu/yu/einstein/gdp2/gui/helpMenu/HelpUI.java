package yu.einstein.gdp2.gui.helpMenu;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;


public class HelpUI extends JFrame{

	private static final long serialVersionUID = 7153926382952979585L;	
  
	private final JPanel jpTop;
	private final JPanel jpLeft;
	private final JScrollPane jpRight;
		
	private final JLabel jlSearch;
	private final JTextField jtSearch;
	private final JButton jbGo;
	
	private final JPanel jpTopInLeft;
	private final JTree jTreeContents;
	private final JTabbedPane jTabbedPane;
	
	public HelpUI() {
		jpTop = new JPanel();
		jpLeft = new JPanel();
		jpRight = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
		jlSearch = new JLabel("Search: ");
		jtSearch = new JTextField("Enter Search String", 100);
		jbGo = new JButton("GO");
		
		jpTopInLeft = new JPanel();
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Filters");
		createNodes(top);
		jTreeContents = new JTree(top);		
		jTabbedPane = new JTabbedPane(JTabbedPane.SOUTH_EAST);
	}
	
	/**
	 * Creates the data of the tree.
	 * @param top Root DefaultMutableTreeNode of the tree.
	 */
	private void createNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;

		//category = new DefaultMutableTreeNode(new PercentagePanel());
		top.add(category);

		//category = new DefaultMutableTreeNode(new ThresholdPanel());
		top.add(category);

		//category = new DefaultMutableTreeNode(new BandStopPanel());
		top.add(category);
		
		//category = new DefaultMutableTreeNode(new CountPanel());
		top.add(category);
	}
}
