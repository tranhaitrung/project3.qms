package com.hust.qms.mail;



public interface EmailService {
    void sendSimpleMessage(String to,
                           String subject,
                           String text);
    void sendSimpleMessageUsingTemplate(String to,
                                        String subject,
                                        String ...templateModel);
    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);

}
