package edu.famu.cop3060.yard.controller;

import edu.famu.cop3060.yard.dto.CreateOpportunityDTO;
import edu.famu.cop3060.yard.dto.OpportunityDTO;
import edu.famu.cop3060.yard.dto.UpdateOpportunityDTO;
import edu.famu.cop3060.yard.service.OpportunitiesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for the Yard API.
 * Maps all HTTP requests to the appropriate service methods.
 * Returns correct HTTP status codes and JSON responses.
 * Contains no business logic or data access code.
 */
@RestController
@RequestMapping("/api/opportunities")
public class OpportunitiesController {

    private static final Logger logger = LoggerFactory.getLogger(OpportunitiesController.class);

    private final OpportunitiesService service;

    // Constructor-based dependency injection — no field @Autowired
    public OpportunitiesController(OpportunitiesService service) {
        this.service = service;
    }

    // -------------------------------------------------------------------------
    // Part 1 — Read Endpoints
    // -------------------------------------------------------------------------

    /**
     * GET /api/opportunities
     * Returns all opportunities. Supports optional ?type= and ?q= query parameters.
     * Always returns 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<OpportunityDTO>> listOpportunities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String q) {

        logger.info("GET /api/opportunities — type={}, q={}",
            type != null ? type : "<empty>",
            q != null ? q : "<empty>");

        List<OpportunityDTO> results = service.getOpportunities(type, q);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/opportunities/{id}
     * Returns a single opportunity by ID.
     * Returns 200 OK if found, 404 Not Found otherwise.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityDTO> getOpportunity(@PathVariable String id) {
        logger.info("GET /api/opportunities/{}", id);

        Optional<OpportunityDTO> result = service.getOpportunityById(id);
        return result
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // -------------------------------------------------------------------------
    // Part 2 — Write Endpoints
    // -------------------------------------------------------------------------

    /**
     * POST /api/opportunities
     * Creates a new opportunity listing.
     * Request body must match CreateOpportunityDTO — all fields required.
     * Returns 201 Created with the new record and a Location header.
     * Returns 400 Bad Request with field-level errors if validation fails.
     */
    @PostMapping
    public ResponseEntity<OpportunityDTO> createOpportunity(
            @Valid @RequestBody CreateOpportunityDTO body) {

        logger.info("POST /api/opportunities — creating opportunity with title={}", body.getTitle());

        OpportunityDTO created = service.createOpportunity(body);

        URI location = URI.create("/api/opportunities/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * PUT /api/opportunities/{id}
     * Fully replaces an existing opportunity listing.
     * Request body must match UpdateOpportunityDTO — all fields required.
     * Returns 200 OK with the updated record if the ID exists.
     * Returns 404 Not Found if the ID does not exist.
     * Returns 400 Bad Request with field-level errors if validation fails.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OpportunityDTO> updateOpportunity(
            @PathVariable String id,
            @Valid @RequestBody UpdateOpportunityDTO body) {

        logger.info("PUT /api/opportunities/{}", id);

        Optional<OpportunityDTO> updated = service.updateOpportunity(id, body);

        if (updated.isEmpty()) {
            logger.warn("PUT /api/opportunities/{} — record not found, update aborted", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated.get());
    }

    /**
     * DELETE /api/opportunities/{id}
     * Removes an opportunity listing permanently.
     * Returns 204 No Content if the record was found and deleted.
     * Returns 404 Not Found if the ID does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable String id) {
        logger.info("DELETE /api/opportunities/{}", id);

        boolean deleted = service.deleteOpportunity(id);

        if (!deleted) {
            logger.warn("DELETE /api/opportunities/{} — record not found, deletion aborted", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
