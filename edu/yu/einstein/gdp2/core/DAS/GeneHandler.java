/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DAS;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.enums.Strand;


/**
 * Parse a DNS XML file and extract the list of {@link Gene}
 * <br/>See <a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class GeneHandler extends DefaultHandler {

	private final	List<Gene>	geneList;				// list of genes
	private String 	currentMarkup = null;	// current XML markup
	private	Gene	currentGene = null;		// current gene

	private String previousGroupID = null;

	private	String 	groupID;	// a group define a gene for the different exons
	private	int 	start;
	private	int 	end;
	private	double 	score;
	private Strand 	orientation;
	private String  name;
	private final Chromosome chromosome;
	

	/**
	 * Creates an instance of {@link GeneHandler}
	 */
	public GeneHandler(Chromosome chromosome) {
		super();
		geneList = new ArrayList<Gene>();
		this.chromosome = chromosome;
	}


	/**
	 * @return the List of {@link Gene}
	 */
	public final List<Gene> getGeneList() {
		return geneList;
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("FEATURE")) {
			if(attributes.getLength() > 0) {
				name = attributes.getValue("label");
			}
		} else if (qName.equalsIgnoreCase("START")) {
			currentMarkup = "START";
		} else 	if (qName.equalsIgnoreCase("END")) {
			currentMarkup = "END";
		} else 	if (qName.equalsIgnoreCase("SCORE")) {
			currentMarkup = "SCORE";
		} else 	if (qName.equalsIgnoreCase("ORIENTATION")) {
			currentMarkup = "ORIENTATION";
		} else 	if (qName.equalsIgnoreCase("GROUP")) {
			currentMarkup = "GROUP";
			if(attributes.getLength() > 0) {
				groupID = attributes.getValue("id");
			}
		} else {
			currentMarkup = null;
		}
	}


	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equalsIgnoreCase("FEATURE")) {
			// case where it's the first gene of the document
			if (previousGroupID == null) {
				currentGene = new Gene();
				previousGroupID = groupID;
			} else if (!groupID.equalsIgnoreCase(previousGroupID)) {	// if we have a new group we add the previous gene to the list
				// set the gene start
				currentGene.setTxStart(currentGene.getExonStarts()[0]);
				// set the gene stop
				currentGene.setTxStop(currentGene.getExonStops()[currentGene.getExonStops().length - 1]);
				currentGene.setName(name);
				currentGene.setStrand(orientation);
				currentGene.setChromo(chromosome);
				geneList.add(currentGene);
				previousGroupID = groupID;
				currentGene = new Gene();
			} 
			currentGene.addExon(start, end, score);			
		}
	}


	@Override
	public void characters(char[] ch, int start, int length) {
		if (currentMarkup != null) {
			String elementValue = new String(ch, start, length);
			if (currentMarkup.equals("START")) {
				this.start = Integer.parseInt(elementValue);
			} else if (currentMarkup.equals("END")) {
				end = Integer.parseInt(elementValue);
			} else if (currentMarkup.equals("ORIENTATION")) {
				orientation = Strand.get(elementValue.charAt(0));				
			} else if (currentMarkup.equals("SCORE")) {
				// if the score is not specified we set a 0 score value
				if (elementValue.trim().equals("-")) {
					score = 0;
				} else {
					score = Double.parseDouble(elementValue);
				}
			}
		}
	}
}
