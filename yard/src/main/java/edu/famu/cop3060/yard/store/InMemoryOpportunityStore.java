package edu.famu.cop3060.yard.store;

import edu.famu.cop3060.yard.dto.OpportunityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * In-memory data store for opportunity listings.
 * Seeds realistic HBCU-relevant data at startup.
 * Exposes read and write methods consumed by OpportunitiesService.
 * Contains no business logic or HTTP awareness.
 */
@Component
public class InMemoryOpportunityStore {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryOpportunityStore.class);

    private final Map<String, OpportunityDTO> opportunityMap = new LinkedHashMap<>();
    private final List<OpportunityDTO> opportunityList = new ArrayList<>();

    public InMemoryOpportunityStore() {
        List<OpportunityDTO> seed = List.of(
            new OpportunityDTO(
                "opp-001",
                "UNCF STEM Scholars Program",
                "Scholarship",
                "UNCF",
                "2025-04-15",
                "Merit-based scholarship for HBCU undergraduates pursuing STEM degrees. "
                    + "Awards up to $5,000 per academic year with mentorship opportunities.",
                List.of("STEM", "undergrad", "paid", "merit-based"),
                "https://uncf.org/programs/uncf-stem-scholars-program"
            ),
            new OpportunityDTO(
                "opp-002",
                "Google HBCU Career Residency",
                "Fellowship",
                "Google",
                "2025-02-28",
                "Paid summer fellowship placing HBCU students inside Google product teams. "
                    + "Includes housing stipend and full-time offer consideration.",
                List.of("tech", "paid", "summer", "fellowship", "Google"),
                "https://buildyourfuture.withgoogle.com/programs/hbcu-cs"
            ),
            new OpportunityDTO(
                "opp-003",
                "National Society of Black Engineers — FAMU Chapter",
                "Organization",
                "NSBE",
                "2025-09-01",
                "Campus chapter of NSBE connecting Black engineering students with industry "
                    + "partners, scholarships, and national conferences.",
                List.of("engineering", "networking", "STEM", "professional development"),
                "https://www.nsbe.org/membership"
            ),
            new OpportunityDTO(
                "opp-004",
                "Homecoming Step Show Registration",
                "Event",
                "Student Government Association",
                "2025-10-01",
                "Annual homecoming step show open to all Greek and independent organizations. "
                    + "Cash prizes awarded to top three finishers.",
                List.of("culture", "performing arts", "campus", "homecoming"),
                "https://studentgov.famu.edu/homecoming"
            ),
            new OpportunityDTO(
                "opp-005",
                "Goldman Sachs Summer Analyst Program",
                "Internship",
                "Goldman Sachs",
                "2025-11-01",
                "Competitive 10-week paid internship at Goldman Sachs for junior and senior "
                    + "students. Divisions include investment banking, engineering, and risk.",
                List.of("finance", "paid", "junior", "senior", "Wall Street"),
                "https://www.goldmansachs.com/careers/students/programs/americas/summer-analyst.html"
            ),
            new OpportunityDTO(
                "opp-006",
                "Thurgood Marshall College Fund Leadership Institute",
                "Fellowship",
                "TMCF",
                "2025-03-31",
                "Intensive leadership development program for high-achieving HBCU students. "
                    + "Includes a week-long Washington D.C. residency and networking with executives.",
                List.of("leadership", "paid", "D.C.", "networking", "HBCU"),
                "https://www.tmcf.org/students-alumni/programs/leadership-institute"
            ),
            new OpportunityDTO(
                "opp-007",
                "Microsoft HBCU Scholarship",
                "Scholarship",
                "Microsoft",
                "2025-03-15",
                "Annual scholarship for HBCU students pursuing degrees in computer science, "
                    + "engineering, or a related technical field. Award includes a paid internship offer.",
                List.of("tech", "STEM", "undergrad", "paid", "computer science"),
                "https://careers.microsoft.com/us/en/usscholarshipprogram"
            ),
            new OpportunityDTO(
                "opp-008",
                "Alpha Kappa Alpha Sorority — Xi Xi Omega Chapter",
                "Organization",
                "Alpha Kappa Alpha Sorority, Inc.",
                "2025-10-15",
                "Graduate chapter of AKA serving the FAMU community through scholarship, "
                    + "service, and sisterhood programming.",
                List.of("Greek life", "service", "women", "networking", "sisterhood"),
                "https://www.aka1908.com"
            ),
            new OpportunityDTO(
                "opp-009",
                "JPMorgan Chase HBCU Scholars Program",
                "Scholarship",
                "JPMorgan Chase",
                "2025-01-31",
                "Scholarship and internship pipeline for HBCU students interested in finance, "
                    + "data analytics, and technology. Includes mentorship with JPMorgan employees.",
                List.of("finance", "data", "tech", "paid", "freshmen", "sophomore"),
                "https://careers.jpmorgan.com/us/en/students/programs"
            ),
            new OpportunityDTO(
                "opp-010",
                "Research Experience for Undergraduates — NSF",
                "Internship",
                "National Science Foundation",
                "2025-02-15",
                "NSF-funded summer research program placing undergraduates in university labs "
                    + "across the country. Stipend, housing, and travel included.",
                List.of("research", "STEM", "paid", "summer", "virtual", "freshmen", "sophomore", "junior"),
                "https://www.nsf.gov/crssprgm/reu"
            )
        );

        for (OpportunityDTO opp : seed) {
            opportunityMap.put(opp.getId(), opp);
            opportunityList.add(opp);
        }

        logger.info("Seeded {} opportunities into the in-memory store.", opportunityList.size());
    }

    // -------------------------------------------------------------------------
    // Read Methods
    // -------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of all opportunities.
     */
    public List<OpportunityDTO> findAll() {
        return Collections.unmodifiableList(opportunityList);
    }

    /**
     * Returns the opportunity matching the given ID, or empty if not found.
     */
    public Optional<OpportunityDTO> findById(String id) {
        return Optional.ofNullable(opportunityMap.get(id));
    }

    /**
     * Returns opportunities filtered by optional type and/or keyword (q).
     * Filtering rules:
     *   - type: case-insensitive match on the type field
     *   - q: case-insensitive match on the title OR any tag entry
     * Both filters may be active simultaneously.
     */
    public List<OpportunityDTO> findFiltered(String type, String q) {
        return opportunityList.stream()
            .filter(opp -> type == null || opp.getType().equalsIgnoreCase(type))
            .filter(opp -> {
                if (q == null) return true;
                String keyword = q.toLowerCase();
                boolean titleMatch = opp.getTitle().toLowerCase().contains(keyword);
                boolean tagMatch = opp.getTags() != null &&
                    opp.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(keyword));
                return titleMatch || tagMatch;
            })
            .toList();
    }

    // -------------------------------------------------------------------------
    // Write Methods (Part 2)
    // -------------------------------------------------------------------------

    /**
     * Stores a fully formed OpportunityDTO (ID already generated by service).
     * Adds to both the map and the list.
     */
    public OpportunityDTO create(OpportunityDTO dto) {
        opportunityMap.put(dto.getId(), dto);
        opportunityList.add(dto);
        return dto;
    }

    /**
     * Replaces the existing opportunity at the given ID with the provided DTO.
     * Returns the updated DTO if found, or empty Optional if the ID does not exist.
     */
    public Optional<OpportunityDTO> update(String id, OpportunityDTO dto) {
        if (!opportunityMap.containsKey(id)) {
            return Optional.empty();
        }
        opportunityMap.put(id, dto);
        // Replace in list by index to maintain list consistency
        for (int i = 0; i < opportunityList.size(); i++) {
            if (opportunityList.get(i).getId().equals(id)) {
                opportunityList.set(i, dto);
                break;
            }
        }
        return Optional.of(dto);
    }

    /**
     * Removes the opportunity with the given ID from both the map and the list.
     * Returns true if found and deleted, false if the ID did not exist.
     */
    public boolean delete(String id) {
        if (!opportunityMap.containsKey(id)) {
            return false;
        }
        opportunityMap.remove(id);
        opportunityList.removeIf(opp -> opp.getId().equals(id));
        return true;
    }
}
