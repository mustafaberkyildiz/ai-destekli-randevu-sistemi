package com.mustafaberk.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	private final JavaMailSender mailSender;
	
	public MailService(JavaMailSender mailSender) {
	       this.mailSender = mailSender;
	}
	
	public void sendTestMail(String to) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);// hangi maile gonderilecegi burada bu disaridan gelir 

        message.setSubject("Test Mail");// baslik
        message.setText("Bu bir test mailidir.");// icerik

        mailSender.send(message);
    }
	
	public void sendMailtoCode(String to,String code) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Dogrulama Kodu");
		message.setText("Dogrulama Kodu : " + code);
		
		mailSender.send(message);
	}
	
	public void sendAppointmentCancelledMailByDoctor(String to , String name , String surname , LocalDate date , LocalTime time ) {
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	    
	    String formattedDate = date.format(dateFormatter);
	    String formattedTime = time.format(timeFormatter);
				
		 String text = "Sayın " + name + " " + surname + ",\n\n"
		            + formattedDate + " tarihli ve " + formattedTime + " saatli randevunuz doktorun istegi uzerine iptal edilmiştir.\n\n"
		            + "Anlayışınız için teşekkür ederiz.";
		
		 SimpleMailMessage message = new SimpleMailMessage();
		 message.setTo(to);
		 message.setSubject("Randevu İptali");
		 message.setText(text);

		 mailSender.send(message);
	}
		
	public void sendAppointmentCancelledMailByPatient(String to , String name , String surname , LocalDate date , LocalTime time ) { 
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	    
	    String formattedDate = date.format(dateFormatter);
	    String formattedTime = time.format(timeFormatter);
				
		 String text = "Sayın " + name + " " + surname + ",\n\n"
		            + formattedDate + " tarihli ve " + formattedTime + " saatli randevunuz hastanin istegi uzerine iptal edilmiştir.\n\n"
		            + "Anlayışınız için teşekkür ederiz.";
		 
		 SimpleMailMessage message = new SimpleMailMessage();
		 message.setTo(to);
		 message.setSubject("Randevu İptali");
		 message.setText(text);

		 mailSender.send(message);
	}
	
	public void sendAppointmentCancelledMailByAdmin(String to , String name , String surname , LocalDate date , LocalTime time ) {
				
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		
		String formattedDate = date.format(dateFormatter);
		String formattedTime = time.format(timeFormatter);
		
		String text = "Sayın " + name + " " + surname + ",\n\n"
	            + formattedDate + " tarihli ve " + formattedTime + " saatli randevunuz sistem tarafından iptal edilmiştir.\n\n"
	            + "Anlayışınız için teşekkür ederiz.";
		
		SimpleMailMessage message = new SimpleMailMessage();
		 
		message.setTo(to);
		message.setSubject("Randevu İptali");
		message.setText(text);
		
		mailSender.send(message);
		
	}
	
	public void sendAppointmentConfirmedMailToPatient(String to , String patientName , String patientSurname , 
													  String doctorName	, String doctorSurname , String departmentName ,
													  LocalDate date , LocalTime time) {
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	    
	    String formattedDate = date.format(dateFormatter);
	    String formattedTime = time.format(timeFormatter);
				
		 String text =  "Sayın " + patientName + " " + patientSurname + ",\n\n" +
			            "Randevunuz başarıyla oluşturulmuştur.\n\n" +
	
			            "Tarih: " + formattedDate + "\n" +
			            "Saat: " + formattedTime + "\n" +
			            "Bölüm: " + departmentName + "\n" +
			            "Doktor: " + doctorName + " " + doctorSurname + "\n\n" +
	
			            "Lütfen randevu saatinden 10 dakika önce hastanede hazır bulununuz.\n\n" +
	
			            "Randevunuzu iptal etmek veya görüntülemek için sistemimize giriş yapabilirsiniz.\n\n" +
	
			            "Sağlıklı günler dileriz.";
		 
		 SimpleMailMessage message = new SimpleMailMessage();
		 message.setTo(to);
		 message.setSubject("Randevunuz Oluşturuldu");
		 message.setText(text);

		 mailSender.send(message);
	}
	
}
