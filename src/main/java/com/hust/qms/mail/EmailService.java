package com.hust.qms.mail;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;

import java.io.IOException;
import java.util.Map;

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
