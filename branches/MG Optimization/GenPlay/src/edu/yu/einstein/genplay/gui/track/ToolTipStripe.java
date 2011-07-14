package edu.yu.einstein.genplay.gui.track;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.multiGenome.stripeManagement.Variant;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

public class ToolTipStripe extends JDialog {

	private static final long serialVersionUID = -4932470485711131874L;

	private final String fields[] = {"Genome:", "Chromosome:", "Type:", "Position:", "First allele:", "Second allele:", "Quality:"};
	private final int LABEL_WIDTH = 110;
	private final int VALUE_WIDTH = 180;
	private final int LINE_HEIGHT = 20;
	private final int LINE_NUMBER = fields.length;
	private final int OFFSET_HEIGHT = 7 * LINE_NUMBER;
	private final int OFFSET_WIDTH = 40;

	private TrackGraphics<?> origin;
	private JLabel label[];
	private JLabel value[];
	private JPanel pane;
	private Variant variant;


	protected ToolTipStripe (TrackGraphics<?> origin) {
		super(MainFrame.getInstance());
		this.origin = origin;
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);

		int height = LINE_NUMBER * LINE_HEIGHT + OFFSET_HEIGHT;
		int width = LABEL_WIDTH + VALUE_WIDTH + OFFSET_WIDTH;
		Dimension dialogDim = new Dimension(width, height);
		setComponentSize(this, dialogDim);
	}


	protected void show (Variant variant, int X, int Y) {
		this.variant = variant;
		initContent(variant);
		setLocation(X, Y);
		setVisible(true);
	}


	private void initContent (Variant variant) {
		// Panel
		/*if (pane != null) {
			pane.removeAll();
		}*/
		pane = new JPanel();

		int height = LINE_NUMBER * LINE_HEIGHT;
		int width = LABEL_WIDTH + VALUE_WIDTH;
		Dimension paneDim = new Dimension(width, height);
		setComponentSize(pane, paneDim);

		// Layout
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		pane.setLayout(layout);

		// Labels
		label = new JLabel[fields.length];
		value = new JLabel[fields.length];
		Dimension labelDim = new Dimension(LABEL_WIDTH, LINE_HEIGHT);
		Dimension valueDim = new Dimension(VALUE_WIDTH, LINE_HEIGHT);
		for (int i = 0; i < fields.length; i++) {
			// initialization
			label[i] = new JLabel(fields[i]);
			value[i] = new JLabel();

			// value text
			value[i].setText(getText(i));

			// size
			setComponentSize(label[i], labelDim);
			setComponentSize(value[i], valueDim);

			// label position
			constraints.gridx = 0;
			constraints.gridy = i;
			constraints.insets = new Insets(0, 0, 0, 0);
			pane.add(label[i], constraints);

			// value position
			constraints.gridx = 1;
			constraints.gridy = i;
			constraints.insets = new Insets(0, 0, 0, 0);
			pane.add(value[i], constraints);
		}

		// Buttons
		Dimension buttonDim = new Dimension(20 * 3, 20);
		Insets inset = new Insets(0, 0, 0, 0);

		// Next variant
		JButton nextVariant = new JButton("next");
		nextVariant.setSize(buttonDim);
		nextVariant.setMinimumSize(buttonDim);
		nextVariant.setMaximumSize(buttonDim);
		nextVariant.setPreferredSize(buttonDim);
		nextVariant.setToolTipText("next");
		nextVariant.setMargin(inset);
		nextVariant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				initVariant();
			}
		});


		// Previous variant
		JButton previousVariant = new JButton("previous");
		previousVariant.setSize(buttonDim);
		previousVariant.setMinimumSize(buttonDim);
		previousVariant.setMaximumSize(buttonDim);
		previousVariant.setPreferredSize(buttonDim);
		previousVariant.setToolTipText("previous");
		previousVariant.setMargin(inset);
		previousVariant.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				initVariant();
			}
		});

		// add buttons
		constraints.gridx = 0;
		constraints.gridy++;
		pane.add(previousVariant, constraints);

		constraints.gridx = 1;
		pane.add(nextVariant, constraints);

		add(pane);
	}



	private void updateContent () {
		for (int i = 0; i < fields.length; i++) {
			value[i].setText(getText(i));
		}
	}


	private String getText (int index) {
		String text = ""; 
		switch (index) {
		case 0:
			text += "NC";
			break;
		case 1:
			text += "NC";
			break;
		case 2:
			text += variant.getType().toString();
			break;
		case 3:
			text += variant.getStart() + " to " + variant.getStop();
			break;
		case 4:
			text += getAllelePresence(variant.isOnFirstAllele());
			break;
		case 5:
			text += getAllelePresence(variant.isOnSecondAllele());
			break;
		case 6:
			text += variant.getQualityScore();
			break;
		default:
			break;
		}
		return text;
	}


	/**
	 * @return the variant
	 */
	public Variant getVariant() {
		return variant;
	}


	private void initVariant () {
		Variant newVariant = origin.getNextVariant(getVariant());
		System.out.println("========== variant");
		variant.show();
		this.variant = newVariant;
		System.out.println("========== new variant");
		variant.show();
		//initContent(newVariant);
		updateContent();
		//pane.revalidate();
		//pane.repaint();
	}


	private void setComponentSize (Component component, Dimension dim) {
		component.setMinimumSize(dim);
		component.setMaximumSize(dim);
		component.setPreferredSize(dim);
		component.setSize(dim);
	}


	private String getAllelePresence (boolean b) {
		String result;
		if (b) {
			result = "present";
		} else {
			result = "absent";
		}
		return result;
	}

}