package edu.famu.cop3060.yard.service;

import edu.famu.cop3060.yard.dto.CreateOpportunityDTO;
import edu.famu.cop3060.yard.dto.OpportunityDTO;
import edu.famu.cop3060.yard.dto.UpdateOpportunityDTO;
import edu.famu.cop3060.yard.store.InMemoryOpportunityStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Business logic layer for the Yard API.
 * Delegates data access to InMemoryOpportunityStore.
 * Handles ID generation for new records.
 * Has no awareness of HTTP or request/response details.
 */
@Service
public class OpportunitiesService {

    private static final Logger logger = LoggerFactory.getLogger(OpportunitiesService.class);

    private final InMemoryOpportunityStore store;

    // Counter starts at 11 so new IDs do not collide with the 10 seeded records
    private final AtomicInteger idCounter = new AtomicInteger(11);

    // Constructor-based dependency injection — no field @Autowired
    public OpportunitiesService(InMemoryOpportunityStore store) {
        this.store = store;
    }

    // -------------------------------------------------------------------------
    // Read Operations (Part 1)
    // -------------------------------------------------------------------------

    /**
     * Returns all opportunities, optionally filtered by type and/or keyword.
     */
    public List<OpportunityDTO> getOpportunities(String type, String q) {
        return store.findFiltered(type, q);
    }

    /**
     * Returns the opportunity matching the given ID, or empty if not found.
     */
    public Optional<OpportunityDTO> getOpportunityById(String id) {
        return store.findById(id);
    }

    // -------------------------------------------------------------------------
    // Write Operations (Part 2)
    // -------------------------------------------------------------------------

    /**
     * Creates a new opportunity from the supplied DTO.
     * Generates a unique ID server-side before delegating to the store.
     */
    public OpportunityDTO createOpportunity(CreateOpportunityDTO dto) {
        String generatedId = String.format("opp-%03d", idCounter.getAndIncrement());

        OpportunityDTO newOpp = new OpportunityDTO(
            generatedId,
            dto.getTitle(),
            dto.getType(),
            dto.getSponsor(),
            dto.getDeadline(),
            dto.getDescription(),
            dto.getTags(),
            dto.getUrl()
        );

        OpportunityDTO saved = store.create(newOpp);
        logger.info("Created new opportunity with generated id={}, title={}", saved.getId(), saved.getTitle());
        return saved;
    }

    /**
     * Fully replaces the opportunity at the given ID with the supplied data.
     * Returns the updated DTO if found, or empty Optional if the ID does not exist.
     */
    public Optional<OpportunityDTO> updateOpportunity(String id, UpdateOpportunityDTO dto) {
        OpportunityDTO replacement = new OpportunityDTO(
            id,
            dto.getTitle(),
            dto.getType(),
            dto.getSponsor(),
            dto.getDeadline(),
            dto.getDescription(),
            dto.getTags(),
            dto.getUrl()
        );
        return store.update(id, replacement);
    }

    /**
     * Deletes the opportunity with the given ID.
     * Returns true if deleted, false if not found.
     */
    public boolean deleteOpportunity(String id) {
        return store.delete(id);
    }
}
