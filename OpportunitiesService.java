package edu.famu.cop3060.yard.service;

import edu.famu.cop3060.yard.dto.OpportunityDTO;
import edu.famu.cop3060.yard.store.InMemoryOpportunityStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OpportunitiesService {

    private final InMemoryOpportunityStore store;

    public OpportunitiesService(InMemoryOpportunityStore store) {
        this.store = store;
    }

    public List<OpportunityDTO> getAll(String type, String q) {
        return store.findFiltered(type, q);
    }

    public Optional<OpportunityDTO> getById(String id) {
        return store.findById(id);
    }
}