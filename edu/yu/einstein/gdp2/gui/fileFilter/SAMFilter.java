package yu.einstein.gdp2.gui.fileFilter;

public class SAMFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -2974583437513007093L; // generated ID
	public static final String[] EXTENSIONS = {"SAM"};
	public static final String DESCRIPTION = "SAM Files (*.sam)";
	
	
	/**
	 * Creates an instance of {@link SAMFilter}
	 */
	public SAMFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}


}
