package com.essa.util;
//builder class for creating email messages
public class EmailMessageBuilder {
    private String to;
    private String subject;
    private StringBuilder body;
    private String priority = "normal";
    
    public EmailMessageBuilder() {
        this.body = new StringBuilder();
    }
    
    public EmailMessageBuilder to(String email) {
        this.to = email;
        return this;
    }
    
    public EmailMessageBuilder subject(String subject) {
        this.subject = subject;
        return this;
    }
    
    public EmailMessageBuilder addLine(String line) {
        body.append(line).append("\n");
        return this;
    }
    
    public EmailMessageBuilder priority(String priority) {
        this.priority = priority;
        return this;
    }
    
    public EmailMessageBuilder ticketAssigned(String ticketTitle, String assigneeName) {
        return subject("Ticket Assigned to You")
               .addLine("A ticket has been assigned to you:")
               .addLine("Title: " + ticketTitle)
               .addLine("Assigned to: " + assigneeName);
    }
    
    public String build() {
        String emailContent = String.format("TO: %s\nSUBJECT: %s\nPRIORITY: %s\n\n%s", 
                            to, subject, priority, body.toString());
        
        System.out.println("Email created with content: " + emailContent);
        return emailContent;
    }
}