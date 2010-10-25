package yu.einstein.gdp2.duplicateReadFilter;

public class ReadsData {
	private String readName;
	private String strand;
	private String chromosome;
	private int start;
//	private String geneSequence;
//	private String unusedCodeField;
//	private int unusedUnknownField;
//	private String mismatches;
	
	public ReadsData(String readName, String strand, String chromosome, int start) {//, String geneSequence, String unusedCodeField, int unusedUnknownField, String mismatches) {
		this.setReadName(readName);
		this.setStrand(strand);
		this.setChromosome(chromosome);
		this.setStart(start);
//		this.geneSequence = geneSequence;
//		this.unusedCodeField = unusedCodeField;
//		this.unusedUnknownField = unusedUnknownField;
//		this.mismatches = mismatches;
	}

	/**
	 * @param readName the readName to set
	 */
	public void setReadName(String readName) {
		this.readName = readName;
	}

	/**
	 * @return the readName
	 */
	public String getReadName() {
		return readName;
	}

	/**
	 * @param strand the strand to set
	 */
	public void setStrand(String strand) {
		this.strand = strand;
	}

	/**
	 * @return the strand
	 */
	public String getStrand() {
		return strand;
	}

	/**
	 * @param chromosome the chromosome to set
	 */
	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	/**
	 * @return the chromosome
	 */
	public String getChromosome() {
		return chromosome;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}
}
