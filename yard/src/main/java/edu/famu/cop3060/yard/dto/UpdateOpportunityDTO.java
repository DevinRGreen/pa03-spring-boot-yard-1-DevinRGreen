package edu.famu.cop3060.yard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * Data Transfer Object for fully replacing an existing opportunity via PUT.
 * The id is supplied via the URL path, not in this body.
 */
public class UpdateOpportunityDTO {

    @NotBlank(message = "title must not be blank")
    @Size(max = 120, message = "title must not exceed 120 characters")
    private String title;

    @NotBlank(message = "type must not be blank")
    @Pattern(
        regexp = "(?i)Scholarship|Internship|Organization|Event|Fellowship",
        message = "type must be one of: Scholarship, Internship, Organization, Event, Fellowship"
    )
    private String type;

    @NotBlank(message = "sponsor must not be blank")
    private String sponsor;

    @NotBlank(message = "deadline must not be blank")
    private String deadline;

    @NotBlank(message = "description must not be blank")
    @Size(max = 500, message = "description must not exceed 500 characters")
    private String description;

    @NotNull(message = "tags must not be null")
    @Size(min = 1, message = "tags must contain at least one entry")
    private List<String> tags;

    @NotBlank(message = "url must not be blank")
    @URL(message = "url must be a well-formed URL")
    private String url;

    // --- Constructors ---

    public UpdateOpportunityDTO() {}

    public UpdateOpportunityDTO(String title, String type, String sponsor, String deadline,
                                String description, List<String> tags, String url) {
        this.title = title;
        this.type = type;
        this.sponsor = sponsor;
        this.deadline = deadline;
        this.description = description;
        this.tags = tags;
        this.url = url;
    }

    // --- Getters and Setters ---

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
