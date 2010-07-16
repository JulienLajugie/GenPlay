package yu.einstein.gdp2.core.enums;

public enum PeakFinderType {

	DENSITY ("Density Finder"),
	STDEV ("Stdev Finder"),
	ISLAND ("Island Finder");
	
	private final String name; // name of the peak finder 
	
	/**
	 * Private constructor. Creates an instance of {@link PeakFinderType}
	 * @param name
	 */
	private PeakFinderType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
