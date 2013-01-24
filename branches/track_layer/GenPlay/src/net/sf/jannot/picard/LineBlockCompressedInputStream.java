/**
 * This file is part of JAnnot
 * 
 * Copyright (C) 2007-2011 Thomas Abeel
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
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Project: http://genomeview.sourceforge.net/
 */
package net.sf.jannot.picard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.samtools.seekablestream.SeekableBufferedStream;
import net.sf.samtools.seekablestream.SeekableStream;
import net.sf.samtools.util.BlockCompressedInputStream;

/**
 * @author Thomas Abeel
 * 
 */
public class LineBlockCompressedInputStream extends BlockCompressedInputStream {

	private BufferedReader br=null;
	/**
	 * Reads a line from the inputstream until a \n or \n\r is encountered. The
	 * file pointer will be positioned at the beginning of the next line after
	 * this read.
	 * 
	 * @return a string with the read characters. Null when no more characters
	 *         are being read.
	 * @throws IOException
	 */
	@Override
	public String readLine() throws IOException {
		return br.readLine();
	}

	@Override
	public void seek(long place){
		try {
			super.seek(place);
			br=new BufferedReader(new InputStreamReader(this));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param strm
	 */
	public LineBlockCompressedInputStream(SeekableStream strm) {
		super(new SeekableBufferedStream(strm));
		br=new BufferedReader(new InputStreamReader(this));
	}



}
