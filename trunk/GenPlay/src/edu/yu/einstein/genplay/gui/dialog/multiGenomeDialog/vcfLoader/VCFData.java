/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader;

import java.io.File;


/**
 * This class represents a line of the VCf loader table with all object that it must implements.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFData {

	/** Index used for VCF file column */
	public static final int FILE_INDEX 		= 0;
	/** Index used for Raw name column */
	public static final int RAW_INDEX 		= 1;
	/** Index used for Genome column */
	public static final int NICKNAME_INDEX 	= 2;
	/** Index used for Group column */
	public static final int GROUP_INDEX		= 3;

	/** Name used for VCF file column */
	public static final String FILE_NAME 	= "File";
	/** Name used for Raw name column */
	public static final String RAW_NAME 	= "Raw name";
	/** Name used for Nickname column */
	public static final String NICKNAME 	= "Nickname";
	/** Name used for Group column */
	public static final String GROUP_NAME	= "Group";


	private String 	group;		// name of the group
	private String 	nickname;	// simplified name of the genome
	private String 	raw;		// raw name of the genome
	private File 	file;		// path of the VCF file


	/**
	 * Constructor of {@link VCFData}
	 */
	public VCFData () {
		group = "";
		nickname = "";
		file = null;
		raw = "";
	}


	/**
	 * Constructor of {@link VCFData}
	 * @param group		name of the group
	 * @param nickname	simplified name of the genome
	 * @param file		path of the VCF file
	 * @param raw		raw name of the genome
	 */
	public VCFData (String group, String nickname, File file, String raw) {
		this.group = group;
		this.nickname = nickname;
		this.file = file;
		this.raw = raw;
	}


	/**
	 * Checks every attributes and return column(s) where error(s) showed up.
	 * @return the list of error or null if no error
	 */
	public String getErrors () {
		String error = "";

		if (group.equals("")) {
			error += GROUP_NAME + "; ";
		}
		if (nickname.equals("")) {
			error += NICKNAME + "; ";
		}
		if ((file == null) || !file.isFile()) {
			error += FILE_NAME + "; ";
		}
		if (raw.equals("")) {
			error += RAW_NAME + "; ";
		}

		if (error.length() > 0) {
			return error;
		} else {
			return null;
		}
	}


	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}


	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}


	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}


	/**
	 * @return the raw
	 */
	public String getRaw() {
		return raw;
	}


	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}


	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	/**
	 * @param raw the raw to set
	 */
	public void setRaw(String raw) {
		this.raw = raw;
	}


	/**
	 * Prints the content
	 */
	public void show () {
		String info = getGroup();
		info += ", " + getNickname();
		info += ", " + getRaw();
		info += ", " + getFile();
		System.out.println(info);
	}

}
