package edu.famu.cop3060.yard.dto;

import java.util.List;

public class OpportunityDTO {

    private final String id;
    private final String title;
    private final String type;
    private final String sponsor;
    private final String deadline;
    private final String description;
    private final List<String> tags;
    private final String url;

    public OpportunityDTO(String id, String title, String type,
                          String sponsor, String deadline,
                          String description, List<String> tags,
                          String url) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.sponsor = sponsor;
        this.deadline = deadline;
        this.description = description;
        this.tags = tags;
        this.url = url;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getSponsor() { return sponsor; }
    public String getDeadline() { return deadline; }
    public String getDescription() { return description; }
    public List<String> getTags() { return tags; }
    public String getUrl() { return url; }
}
