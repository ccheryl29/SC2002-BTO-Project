package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an enquiry submitted by an applicant regarding a project
 */
public class Enquiry implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private Applicant applicant;
    private Project project;
    private String question;
    private List<Reply> replies;
    private Date dateSubmitted;
    private EnquiryStatus status;
    
    /**
     * Enum representing the status of an enquiry
     */
    public enum EnquiryStatus {
        PENDING,    // Awaiting a first response
        RESPONDED   // Has received at least one response
    }
    
    /**
     * Class representing a reply to an enquiry
     */
    public static class Reply implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String content;
        private User respondedBy;
        private Date dateResponded;
        
        /**
         * Constructor for Reply
         * 
         * @param content The reply content
         * @param respondedBy The user who provided the reply
         */
        public Reply(String content, User respondedBy) {
            this.content = content;
            this.respondedBy = respondedBy;
            this.dateResponded = new Date();
        }
        
        /**
         * Gets the content of the reply
         * 
         * @return The reply content
         */
        public String getContent() {
            return content;
        }
        
        /**
         * Gets the user who provided the reply
         * 
         * @return The user who responded
         */
        public User getRespondedBy() {
            return respondedBy;
        }
        
        /**
         * Gets the date the reply was submitted
         * 
         * @return The reply date
         */
        public Date getDateResponded() {
            return dateResponded;
        }
        
        @Override
        public String toString() {
            return "Reply from " + respondedBy.getName() + " on " + dateResponded + ":\n" + content;
        }
    }
    
    /**
     * Constructor for Enquiry
     * 
     * @param id Unique identifier for the enquiry
     * @param applicant The applicant who submitted the enquiry
     * @param project The project the enquiry is about
     * @param question The enquiry question
     */
    public Enquiry(String id, Applicant applicant, Project project, String question) {
        this.id = id;
        this.applicant = applicant;
        this.project = project;
        this.question = question;
        this.replies = new ArrayList<>();
        this.dateSubmitted = new Date();
        this.status = EnquiryStatus.PENDING;
    }
    
    /**
     * Gets the ID of the enquiry
     * 
     * @return The enquiry ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the ID of the enquiry
     * 
     * @param id The new enquiry ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gets the applicant who submitted the enquiry
     * 
     * @return The applicant
     */
    public Applicant getApplicant() {
        return applicant;
    }
    
    /**
     * Gets the project the enquiry is about
     * 
     * @return The project
     */
    public Project getProject() {
        return project;
    }
    
    /**
     * Gets the enquiry question
     * 
     * @return The question
     */
    public String getQuestion() {
        return question;
    }
    
    /**
     * Sets the enquiry question (for editing)
     * 
     * @param question The new question
     */
    public void setQuestion(String question) {
        this.question = question;
    }
    
    /**
     * Gets all replies to the enquiry
     * 
     * @return The list of replies
     */
    public List<Reply> getReplies() {
        return replies;
    }
    
    /**
     * Gets the most recent reply to the enquiry
     * 
     * @return The most recent reply, or null if no replies
     */
    public Reply getLatestReply() {
        if (replies.isEmpty()) {
            return null;
        }
        return replies.get(replies.size() - 1);
    }
    
    /**
     * Gets the date the enquiry was submitted
     * 
     * @return The submission date
     */
    public Date getDateSubmitted() {
        return dateSubmitted;
    }
    
    /**
     * Gets the status of the enquiry
     * 
     * @return The enquiry status
     */
    public EnquiryStatus getStatus() {
        return status;
    }
    
    /**
     * Checks if the enquiry has been responded to
     * 
     * @return true if responded, false otherwise
     */
    public boolean isResponded() {
        return status == EnquiryStatus.RESPONDED;
    }
    
    /**
     * Adds a reply to the enquiry
     * 
     * @param content The reply content
     * @param respondedBy The user providing the reply
     * @return The created reply
     */
    public Reply addReply(String content, User respondedBy) {
        Reply reply = new Reply(content, respondedBy);
        replies.add(reply);
        
        // Update status to RESPONDED if this is the first reply
        if (status == EnquiryStatus.PENDING) {
            status = EnquiryStatus.RESPONDED;
        }
        
        return reply;
    }
    
    /**
     * Updates the enquiry question
     * 
     * @param newQuestion The new question text
     */
    public void updateQuestion(String newQuestion) {
        this.question = newQuestion;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Enquiry ID: ").append(id).append("\n");
        sb.append("Project: ").append(project.getName()).append("\n");
        sb.append("Submitted by: ").append(applicant.getName()).append(" (").append(applicant.getNRIC()).append(")\n");
        sb.append("Date Submitted: ").append(dateSubmitted).append("\n");
        sb.append("Question: ").append(question).append("\n");
        sb.append("Status: ").append(status).append("\n");
        
        if (isResponded()) {
            sb.append("Replies (").append(replies.size()).append("):\n");
            for (int i = 0; i < replies.size(); i++) {
                sb.append("  ").append(i + 1).append(". ");
                sb.append(replies.get(i).getContent()).append("\n");
                sb.append("     By: ").append(replies.get(i).getRespondedBy().getName());
                sb.append(" on ").append(replies.get(i).getDateResponded()).append("\n");
            }
        } else {
            sb.append("No replies yet\n");
        }
        
        return sb.toString();
    }
} 