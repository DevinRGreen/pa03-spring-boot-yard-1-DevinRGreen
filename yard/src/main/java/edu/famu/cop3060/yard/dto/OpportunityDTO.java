package edu.famu.cop3060.yard.dto;

import java.util.List;

/**
 * Data Transfer Object representing a single opportunity in the Yard API.
 * Used for all API responses across both Part 1 and Part 2.
 */
public class OpportunityDTO {

    private String id;
    private String title;
    private String type;
    private String sponsor;
    private String deadline;
    private String description;
    private List<String> tags;
    private String url;

    // --- Constructors ---

    public OpportunityDTO() {}

    public OpportunityDTO(String id, String title, String type, String sponsor,
                          String deadline, String description, List<String> tags, String url) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.sponsor = sponsor;
        this.deadline = deadline;
        this.description = description;
        this.tags = tags;
        this.url = url;
    }

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSponsor() { return sponsor; }
    public void setSponsor(String sponsor) { this.sponsor = sponsor; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
