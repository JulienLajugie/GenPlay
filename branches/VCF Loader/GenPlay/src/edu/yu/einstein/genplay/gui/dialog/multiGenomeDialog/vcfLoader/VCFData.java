/**
 * 
 */
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader;

import java.io.File;

import edu.yu.einstein.genplay.core.enums.VCFType;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFData {


	/** Index used for Group column */
	public static final int GROUP_INDEX		= 0;
	/** Index used for Genome column */
	public static final int GENOME_INDEX 	= 1;
	/** Index used for Raw name column */
	public static final int RAW_INDEX 		= 2;
	/** Index used for VCF file column */
	public static final int FILE_INDEX 		= 3;
	/** Index used for VCF type column */
	public static final int TYPE_INDEX 		= 4;

	/** Name used for Group column */
	public static final String GROUP_NAME	= "Group";
	/** Name used for Genome column */
	public static final String GENOME_NAME 	= "Genome";
	/** Name used for Raw name column */
	public static final String RAW_NAME 	= "Raw name";
	/** Name used for VCF file column */
	public static final String FILE_NAME 	= "File";
	/** Name used for VCF type column */
	public static final String TYPE_NAME 	= "VCF Type";


	private String 	group;	// name of the group
	private String 	genome;	// simplified name of the genome
	private String 	raw;	// raw name of the genome
	private String 	path;	// path of the VCF file
	private VCFType type;	// type of the VCF file


	/**
	 * Constructor of {@link VCFData}
	 */
	public VCFData () {
		this.group = "";
		this.genome = "";
		this.raw = "";
		this.path = "";
		this.type = null;
	}


	/**
	 * Constructor of {@link VCFData}
	 * @param group		name of the group
	 * @param genome	simplified name of the genome
	 * @param raw		raw name of the genome
	 * @param path		path of the VCF file
	 * @param type		type of the VCF file
	 */
	public VCFData (String group, String genome, String raw, String path, VCFType type) {
		this.group = group;
		this.genome = genome;
		this.raw = raw;
		this.path = path;
		this.type = type;
	}


	/**
	 * Checks every attributes and return column(s) where error(s) showed up.
	 * @return the list of error or null if no error
	 */
	public String getErrors () {
		String error = "";

		if (this.group.equals("")) {
			error += GROUP_NAME + "; ";
		}
		if (this.genome.equals("")) {
			error += GENOME_NAME + "; ";
		}
		if (this.raw.equals("")) {
			error += RAW_NAME + "; ";
		}

		File file = new File(path);
		if (!file.isFile()) {
			error += FILE_NAME + "; ";
		}

		if (this.type == null) {
			error += TYPE_NAME + "; ";
		}

		if (error.length() > 0) {
			return error;
		} else {
			return null;
		}
	}


	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}


	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}


	/**
	 * @return the genome
	 */
	public String getGenome() {
		return genome;
	}


	/**
	 * @param genome the genome to set
	 */
	public void setGenome(String genome) {
		this.genome = genome;
	}


	/**
	 * @return the raw
	 */
	public String getRaw() {
		return raw;
	}


	/**
	 * @param raw the raw to set
	 */
	public void setRaw(String raw) {
		this.raw = raw;
	}


	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}


	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}


	/**
	 * @return the type
	 */
	public VCFType getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(VCFType type) {
		this.type = type;
	}


	/**
	 * Prints the content
	 */
	public void show () {
		String info = getGroup();
		info += ", " + getGenome();
		info += ", " + getRaw();
		info += ", " + getPath();
		info += ", " + getType();
		System.out.println(info);
	}

}
