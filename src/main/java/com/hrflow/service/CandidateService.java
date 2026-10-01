package com.hrflow.service;

import com.hrflow.dsa.heap.CandidateRankingHeap;
import com.hrflow.dsa.trie.SkillTrie;
import com.hrflow.model.Candidate;
import com.hrflow.storage.DatabaseManager;

import java.util.*;

/**
 * Service managing candidates with Trie skill autocomplete and MaxHeap ranking.
 */
public class CandidateService {
    private final DatabaseManager db;
    private final List<Candidate> candidates;
    private final SkillTrie skillTrie;
    private final Map<String, Set<Candidate>> candidatesBySkillMap;

    public CandidateService() {
        this.db = DatabaseManager.getInstance();
        this.candidates = new ArrayList<>();
        this.skillTrie = new SkillTrie();
        this.candidatesBySkillMap = new HashMap<>();
        reload();
    }

    public synchronized void reload() {
        candidates.clear();
        candidatesBySkillMap.clear();
        candidates.addAll(db.loadAllCandidates());

        for (Candidate c : candidates) {
            for (String skill : c.getSkills()) {
                String clean = skill.trim();
                skillTrie.insertSkill(clean, c);
                candidatesBySkillMap.computeIfAbsent(clean.toLowerCase(), k -> new HashSet<>()).add(c);
            }
        }
    }

    public List<Candidate> getAllCandidates() {
        return Collections.unmodifiableList(candidates);
    }

    public List<String> autocompleteSkills(String query, int limit) {
        return skillTrie.autocomplete(query, limit);
    }

    public Set<Candidate> searchCandidatesBySkill(String skill) {
        return skillTrie.getCandidatesBySkill(skill);
    }

    public Set<Candidate> searchCandidatesBySkills(Collection<String> skills) {
        return skillTrie.findCandidatesWithAllSkills(skills);
    }

    /**
     * Ranks candidates against a role's required skill set using CandidateRankingHeap.
     */
    public List<Candidate> rankCandidates(Set<String> requiredSkills, int topK) {
        CandidateRankingHeap heap = CandidateRankingHeap.rankForRole(candidates, requiredSkills);
        return heap.getTopK(topK);
    }

    public void updateCandidateStatus(Candidate candidate, Candidate.CandidateStatus newStatus) {
        candidate.setStatus(newStatus);
        db.insertCandidates(List.of(candidate));
    }

    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
        for (String skill : candidate.getSkills()) {
            skillTrie.insertSkill(skill.trim(), candidate);
            candidatesBySkillMap.computeIfAbsent(skill.trim().toLowerCase(), k -> new HashSet<>()).add(candidate);
        }
        db.insertCandidates(List.of(candidate));
    }
}
