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
package edu.yu.einstein.genplay.core.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import edu.yu.einstein.genplay.core.email.GenPlayEmail;
import edu.yu.einstein.genplay.exception.ExceptionManager;

/**
 * This class represents the cryptography system used in GenPlay.
 * For now, all passwords are encoded and their bytes are written in separated files.
 * This process takes place out of GenPlay.
 * Theses files are never committed on the SVN and will never be shared.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class Crypto {

	private static final String PWD_ALGO = "AES/ECB/PKCS5Padding";

	private SecretKey key;


	/**
	 * Constructor of {@link Crypto}
	 */
	public Crypto () {
		try {
			key = getKey();
		} catch (Exception e) {
			key = null;
			ExceptionManager.getInstance().caughtException(e);
		}
	}


	/**
	 * @param bytes an array of bytes to decode
	 * @param key	the key to use
	 * @return	the decoded string of the array
	 * @throws Exception
	 */
	private String decode (byte[] bytes, SecretKey key) throws Exception {
		Cipher aes = Cipher.getInstance(PWD_ALGO);
		aes.init(Cipher.DECRYPT_MODE, key);
		try {
			new String(aes.doFinal(bytes));
		} catch (Exception e) {
			System.out.println("Crypto.decode() " + bytes.length);
			e.printStackTrace();
		}
		return new String(aes.doFinal(bytes));
	}


	/**
	 * Encodes a text using
	 * @param text	a text to encode
	 * @param key	the key to use
	 * @return	the encoded fragment as an array of bytes
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private byte[] encode (String text, SecretKey key) throws Exception {
		Cipher aes = Cipher.getInstance(PWD_ALGO);
		aes.init(Cipher.ENCRYPT_MODE, key);
		return aes.doFinal(text.getBytes());
	}


	/**
	 * @param is an {@link InputStream}
	 * @return the content of the {@link InputStream} as an array of bytes
	 * @throws IOException
	 */
	private byte[] getBytes (InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		return buffer.toByteArray();
	}


	/**
	 * @param path the internal path of the data to decode
	 * @return the decoded message
	 */
	public String getDecodedInformation (String path) {
		InputStream is = GenPlayEmail.class.getClassLoader().getResourceAsStream(path);
		byte[] bytes = null;
		String message = null;
		try {
			bytes = getBytes(is);
			message = decode(bytes, key);
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
		}
		return message;
	}


	/**
	 * @return the {@link SecretKey} serialized in the key file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private SecretKey getKey () throws IOException, ClassNotFoundException {
		InputStream is = GenPlayEmail.class.getClassLoader().getResourceAsStream(GenPlayEmail.KEY_PATH);
		ObjectInputStream ois = null;
		ois = new ObjectInputStream(is);
		SecretKey key = (SecretKey) ois.readObject();
		return key;
	}


	/**
	 * @return the password
	 */
	public String getPassword () {
		return getDecodedInformation(GenPlayEmail.PASSWORD_PATH);
	}


	/**
	 * @return the username
	 */
	public String getUserName () {
		return getDecodedInformation(GenPlayEmail.USER_PATH);
	}
}
