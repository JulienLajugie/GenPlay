/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.crypto.Crypto;
import edu.yu.einstein.genplay.exception.ExceptionManager;

/**
 * The {@link GenPlayMail} offers the possibility to send an email to the official GenPlay Mailbox.
 * There is only one method: send, which has a subject and a message.
 * This is anonymous, the sender is not the user.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenPlayMail {

	/**
	 * Send an anonymous email with the name of Nicolas Fourel as GenPlay Mailbox admin to the GenPlay Mailbox.
	 * @param subject the subject of the email
	 * @param content the content of the email
	 * @return true if the sending operation was successful, false otherwise
	 */
	public static boolean send (String subject, String content) {
		boolean hasBeenSent = false;
		boolean canSend = true;

		// Test if the internet connection is working
		if (!isInternetReachable()) {
			canSend = false;
			JOptionPane.showMessageDialog(null, "No internet connection has been found.", "Could not send the email", JOptionPane.INFORMATION_MESSAGE);
		}

		// Test if the mail connection can be used (and if the internet is working testing canSend)
		if (canSend && !isMailEnabled()) {
			canSend = false;
			JOptionPane.showMessageDialog(null, "The email feature is not available on this current version.", "Could not send the email", JOptionPane.INFORMATION_MESSAGE);
		}

		// Start the sending process if everything seems ok
		if (canSend) {
			try {
				Properties properties = getProperties();
				Authenticator authenticator = getAuthenticator();
				Session session = Session.getDefaultInstance(properties, authenticator);
				Message message = getMessage(session, subject, content);
				Transport.send(message);
				hasBeenSent = true;
			} catch (Exception e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}

		return hasBeenSent;
	}


	/**
	 * @return true if the email feature is available, false otherwise
	 */
	private static boolean isMailEnabled () {
		InputStream isKey = GenPlayMail.class.getClassLoader().getResourceAsStream("edu/yu/einstein/genplay/resource/files/key");
		InputStream isUser = GenPlayMail.class.getClassLoader().getResourceAsStream("edu/yu/einstein/genplay/resource/files/usr");
		InputStream isPassword = GenPlayMail.class.getClassLoader().getResourceAsStream("edu/yu/einstein/genplay/resource/files/pwd");
		if ((isKey != null) && (isUser != null) && (isPassword != null)) {
			return true;
		}
		return false;
	}


	/**
	 * @return true if GenPlay can connect to the GenPlay website, false otherwise
	 */
	private static boolean isInternetReachable() {
		try {
			// Make a URL to a known source
			URL url = new URL("http://www.genplay.net");

			// Open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

			// Trying to retrieve data from the source. If there is no connection, this line will fail.
			@SuppressWarnings("unused") // only for the connection test
			Object objData = urlConnect.getContent();
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(e);
			return false;
		}
		return true;
	}


	/**
	 * @return the {@link Properties} of the email
	 */
	private static Properties getProperties () {
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "owa.yu.edu");
		properties.setProperty("mail.smtp.port", "" + 587);
		properties.put("mail.smtp.auth", true);
		return properties;
	}


	/**
	 * @return the {@link Authenticator} of the sender
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Authenticator getAuthenticator () throws IOException, ClassNotFoundException {
		Crypto crypto = new Crypto();
		final String username = crypto.getUserName();
		final String password = crypto.getPassword();
		if ((username != null) && (password != null)) {
			Authenticator authenticator = new Authenticator() {
				private final PasswordAuthentication pa = new PasswordAuthentication(username, password);
				@Override
				public PasswordAuthentication getPasswordAuthentication() {
					return pa;
				}
			};
			return authenticator;
		}
		return null;
	}


	/**
	 * @param session the {@link Session} to send the email
	 * @param subject the subject of the email
	 * @param message the content of the email
	 * @return the {@link Message} of the email
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	private static Message getMessage (Session session, String subject, String message) throws UnsupportedEncodingException, MessagingException {
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress("nicolas.fourel@einstein.yu.edu", "GenPlay Mailbox Admin"));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress("genplay@einstein.yu.edu", "GenPlay Mailbox"));
		msg.setSubject(subject);
		msg.setText(message);
		return msg;
	}

}
