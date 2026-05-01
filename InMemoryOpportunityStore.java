package edu.famu.cop3060.yard.store;

import edu.famu.cop3060.yard.dto.OpportunityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryOpportunityStore {

    private static final Logger logger =
            LoggerFactory.getLogger(InMemoryOpportunityStore.class);

    private final Map<String, OpportunityDTO> opportunityMap = new HashMap<>();
    private final List<OpportunityDTO> opportunityList = new ArrayList<>();

    public InMemoryOpportunityStore() {
        seedData();
        logger.info("Seeded {} opportunities into the in-memory store.",
                opportunityList.size());
    }

    private void seedData() {

        add(new OpportunityDTO("opp-001", "UNCF STEM Scholarship",
                "Scholarship", "UNCF", "2025-04-15",
                "Scholarship for STEM students.",
                List.of("STEM", "undergrad", "paid"),
                "https://uncf.org"));

        add(new OpportunityDTO("opp-002", "Google HBCU Residency",
                "Fellowship", "Google", "2025-03-30",
                "Summer tech fellowship.",
                List.of("tech", "paid", "summer"),
                "https://google.com"));

        add(new OpportunityDTO("opp-003", "NSBE Chapter",
                "Organization", "NSBE", "N/A",
                "Engineering student org.",
                List.of("engineering", "networking", "STEM"),
                "https://nsbe.org"));

        add(new OpportunityDTO("opp-004", "Homecoming Step Show",
                "Event", "SGA", "2025-10-01",
                "Campus culture event.",
                List.of("culture", "campus"),
                "https://campus.edu"));

        add(new OpportunityDTO("opp-005", "Goldman Sachs Internship",
                "Internship", "Goldman Sachs", "2025-02-15",
                "Finance internship.",
                List.of("finance", "paid", "junior"),
                "https://goldmansachs.com"));

        add(new OpportunityDTO("opp-006", "Teach for America Fellowship",
                "Fellowship", "TFA", "2025-05-01",
                "Leadership fellowship.",
                List.of("leadership", "service"),
                "https://teachforamerica.org"));

        add(new OpportunityDTO("opp-007", "Freshman Leadership Council",
                "Organization", "Student Affairs", "N/A",
                "Leadership org for freshmen.",
                List.of("freshmen", "leadership"),
                "https://campus.edu"));

        add(new OpportunityDTO("opp-008", "Engineering Career Fair",
                "Event", "Engineering Dept", "2025-09-10",
                "Meet hiring companies.",
                List.of("engineering", "career", "STEM"),
                "https://campus.edu"));
    }

    private void add(OpportunityDTO dto) {
        opportunityMap.put(dto.getId(), dto);
        opportunityList.add(dto);
    }

    public List<OpportunityDTO> findAll() {
        return Collections.unmodifiableList(opportunityList);
    }

    public Optional<OpportunityDTO> findById(String id) {
        return Optional.ofNullable(opportunityMap.get(id));
    }

    public List<OpportunityDTO> findFiltered(String type, String q) {

        return opportunityList.stream()
                .filter(o -> type == null ||
                        o.getType().equalsIgnoreCase(type))
                .filter(o -> q == null ||
                        o.getTitle().toLowerCase().contains(q.toLowerCase()) ||
                        o.getTags().stream()
                                .anyMatch(tag ->
                                        tag.toLowerCase().contains(q.toLowerCase())))
                .collect(Collectors.toList());
    }
}
