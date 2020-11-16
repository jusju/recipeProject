package com.example.recipeProject.web;


import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class Sahkoposti {
	public void lahetaSahkoposti(String lahettajanGoogleEmail, String lahettajanGoogleSalasana, String vastaanottajanEmail, String otsikko, String emailinSisalto ) {

               String from = lahettajanGoogleEmail;
                String pass = lahettajanGoogleSalasana;
                pass = "Armtv1990!3l33th4x";
                from = "jtjuslin";
                System.out.println(pass);
                String host = "mail.cs.hut.fi";


		Properties properties = System.getProperties();
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.user", from);
		properties.put("mail.smtp.from", "jtjuslin@cs.hut.fi");
		properties.put("mail.smtp.password", pass);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");

		// Haetaan oletus session olio
		Session session = Session.getDefaultInstance(properties);

		try {
			// Luo oletus MimeMessage olio
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));

			// Viestin vastaanottajan asettaminen
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					vastaanottajanEmail));

			// Viestin otsikko eli subject
			message.setSubject(otsikko);

			message.setText(emailinSisalto);

			Transport transport = session.getTransport("smtp");
			transport.connect(host, from, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
}
