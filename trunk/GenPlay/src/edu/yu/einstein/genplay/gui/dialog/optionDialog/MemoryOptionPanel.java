package edu.yu.einstein.genplay.gui.dialog.optionDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel to select the memory to allocate to GenPlay.
 * Only available for the OSX installed version of GenPlay
 * @author Julien Lajugie
 */
class MemoryOptionPanel extends OptionPanel  {

	/** Generated serial ID */
	private static final long serialVersionUID = 8758894281496945808L;

	/** Path to the app bundle info file */
	private final static String PLIST_PATH = "GenPlay.app/Contents/Info.plist";

	/** Number of bytes in a MB */
	private final static int BYTES_PER_MB = 1048576;

	/** Max memory that can be allocated to GenPlay as a ratio to the RAM available */
	private final static double MAX_MEMORY_USAGE_RATIO = 0.9;

	/** Min memory that can be allocated to GenPlay in MB*/
	private final static int MIN_MEMORY = 100;

	/** If the RAM cannot be determined (because of the JVM) we assume the following (in MB) */
	private static final int DEFAULT_RAM_ASSUMED = 50000;

	private final JLabel 	jlMemory;			// label memory
	private final JSlider 	jsMemorySelect;		// slider to select the memory to allocate
	private final JLabel 	jlMemorySelected;	// label showing the memory amount selected
	private final JLabel 	jlRestartNeeded;	// label telling that a restart is needed

	private final String 	xmxParameter;	// String of the xmx parameter
	private final int 		xmxMemory;		// value of the xmx parameter in MB
	private final int 		computerRam;	// RAM of the computer in MB


	/**
	 * Creates an instance of {@link MemoryOptionPanel}
	 * @throws IOException if the Plist file cannot be found
	 */
	MemoryOptionPanel() throws IOException {
		super("Memory");
		xmxParameter = retrieveXmxParameter();
		computerRam = getComputerRAM();
		int maxMemory = (int) (computerRam * MAX_MEMORY_USAGE_RATIO);
		xmxMemory = Math.min(extractXmxMemory(), maxMemory);
		jlMemory = new JLabel("Select the maximum amount of memory that can be allocated to GenPlay:");
		jsMemorySelect = new JSlider(JSlider.HORIZONTAL, MIN_MEMORY, maxMemory, xmxMemory);
		jsMemorySelect.setMajorTickSpacing(1000);
		jsMemorySelect.setMinorTickSpacing(100);
		jsMemorySelect.setPaintTicks(true);
		jsMemorySelect.setPaintLabels(false);
		final NumberFormat format = DecimalFormat.getInstance();
		format.setMaximumFractionDigits(1);
		String currentMemoryValue = format.format(jsMemorySelect.getValue() / 1000d);
		jlMemorySelected = new JLabel("Memory allocated to GenPlay: " + currentMemoryValue + "GB");
		jsMemorySelect.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				String currentMemoryValue = format.format(jsMemorySelect.getValue() / 1000d);
				jlMemorySelected.setText("Memory allocated to GenPlay: " + currentMemoryValue + "GB");
			}
		});

		jlRestartNeeded = new JLabel("Changes will take effect when you restart GenPlay");
		// add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 0, 5, 0);
		add(jlMemory, c);
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5, 0, 5, 0);
		add(jsMemorySelect, c);
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5, 0, 5, 0);
		add(jlMemorySelected, c);
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(5, 0, 5, 0);
		add(jlRestartNeeded, c);
	}


	/**
	 * Extracts the memory value from the Xmx parameter
	 * @return the memory set in the Xmx parameter in MB
	 */
	private int extractXmxMemory() {
		if (xmxParameter == null) {
			return -1;
		}
		String memoryString = xmxParameter.substring(4, xmxParameter.length() - 1);
		int mem = Integer.parseInt(memoryString);
		// multiply by 1000 if the parameter is in GB
		if (xmxParameter.toLowerCase().charAt(xmxParameter.length() - 1) == 'g') {
			mem *= 1000;
		}
		return mem;
	}


	/**
	 * @return the RAM memory in MB
	 */
	private int getComputerRAM() {
		long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
		// if the RAM cannot be retrieved
		if (memorySize == 0) {
			return DEFAULT_RAM_ASSUMED;
		}
		return (int) (memorySize / (double) BYTES_PER_MB);
	}


	/**
	 * @return the Xmx jvm parameter from the plist file
	 * @throws IOException
	 */
	private String retrieveXmxParameter() throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(PLIST_PATH));
			String xmxParam = null;
			String line;
			while (((line = reader.readLine()) != null) && (xmxParam == null)) {
				String lowerCaseline = line.toLowerCase();
				int xmxStartIndex = lowerCaseline.indexOf("-xmx");
				if (xmxStartIndex != -1) {
					int xmxStopIndex = lowerCaseline.indexOf('m', xmxStartIndex + 4);
					if (xmxStopIndex == -1) {
						xmxStopIndex = lowerCaseline.indexOf('g', xmxStartIndex + 4);
					}
					if (xmxStopIndex != -1) {
						xmxParam = line.substring(xmxStartIndex, xmxStopIndex + 1);
					}
				}
			}
			return xmxParam;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}


	/**
	 * Replaces the value of the Xmx parameter in the plist file
	 * if it changed
	 * @throws IOException
	 */
	void writeNewPList() throws IOException {
		int selectedMemory = jsMemorySelect.getValue();
		if (selectedMemory != xmxMemory) {
			String newXmxString = "-Xmx" + selectedMemory + "M";
			BufferedReader reader = null;
			FileOutputStream writer = null;
			try {
				reader = new BufferedReader(new FileReader(PLIST_PATH));
				String line;
				String input = "";
				while ((line = reader.readLine()) != null) {
					line = line.replace(xmxParameter, newXmxString);
					input += line + '\n';
				}
				reader.close();
				writer = new FileOutputStream(PLIST_PATH);
				writer.write(input.getBytes());
			} finally {
				if (reader != null){
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			}
		}
	}
}
