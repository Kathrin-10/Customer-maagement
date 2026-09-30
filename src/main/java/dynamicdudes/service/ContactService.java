package dynamicdudes.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dynamicdudes.dto.ContactRequest;
import dynamicdudes.model.Contact;
import dynamicdudes.repository.ContactRepository;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final JavaMailSender mailSender;

    public ContactService(
            ContactRepository contactRepository,
            JavaMailSender mailSender) {

        this.contactRepository = contactRepository;
        this.mailSender = mailSender;
    }

    public void saveContact(ContactRequest request) {
        System.out.println("CHECK 5 - ContactService reached");
        // 1. Create Contact entity
        Contact contact = new Contact();

        // 2. Copy form data into entity
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setSubject(request.getSubject());
        contact.setMessage(request.getMessage());

        // 3. Save contact into database
        contactRepository.save(contact);
        System.out.println(
            "CHECK 6 - Contact saved. ID = " + contact.getId()
        );
        // 4. Send email notification (best-effort — don't fail the request if mail fails)
        try {
            sendEmail(contact);
        } catch (Exception e) {
            System.err.println("CHECK 7b - Email send failed (contact still saved): " + e.getMessage());
        }
    }

    private void sendEmail(Contact contact) {
        System.out.println("CHECK 7 - Email sending started");
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo("sharinjoea@gmail.com");

        mail.setSubject("New Contact Message: " + contact.getSubject());

        mail.setText(
                "You received a new message from the website.\n\n" +
                "Name: " + contact.getName() + "\n" +
                "Email: " + contact.getEmail() + "\n" +
                "Subject: " + contact.getSubject() + "\n\n" +
                "Message:\n" +
                contact.getMessage()
        );

        mailSender.send(mail);

        System.out.println("CHECK 8 - Email sent successfully");
    }
}