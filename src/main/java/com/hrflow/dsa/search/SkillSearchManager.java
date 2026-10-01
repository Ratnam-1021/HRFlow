package com.hrflow.dsa.search;

import com.hrflow.model.Candidate;

import java.util.*;

/**
 * Simplified Skill Search Engine.
 * Replaces complex Trie & DFS with:
 * - HashMap for candidate index lookups: O(1)
 * - Linear search/filtering for prefix autocomplete: O(N)
 * - Simple nested loop / HashSet matching for multi-skill queries.
 */
public class SkillSearchManager {
    // Canonical skill name mapping to candidate sets: O(1) lookup
    private final Map<String, Set<Candidate>> skillToCandidatesMap;
    // Distinct known skills list for linear search
    private final List<String> knownSkillsList;

    public SkillSearchManager() {
        this.skillToCandidatesMap = new HashMap<>();
        this.knownSkillsList = new ArrayList<>();
    }

    /**
     * Registers a candidate's skill in the HashMap index.
     */
    public void registerSkill(String skill, Candidate candidate) {
        if (skill == null || skill.trim().isEmpty()) {
            return;
        }

        String canonical = skill.trim();
        String lowerKey = canonical.toLowerCase();

        if (!skillToCandidatesMap.containsKey(lowerKey)) {
            skillToCandidatesMap.put(lowerKey, new HashSet<>());
            knownSkillsList.add(canonical);
        }

        if (candidate != null) {
            skillToCandidatesMap.get(lowerKey).add(candidate);
        }
    }

    /**
     * Skill autocomplete using simple Linear Search filtering.
     * (Replaces Trie DFS traversal)
     */
    public List<String> autocomplete(String prefix, int limit) {
        List<String> matches = new ArrayList<>();
        if (prefix == null || prefix.trim().isEmpty()) {
            return matches;
        }

        String searchLower = prefix.trim().toLowerCase();

        // Simple linear scan through known skills
        for (String skill : knownSkillsList) {
            if (skill.toLowerCase().startsWith(searchLower)) {
                matches.add(skill);
                if (matches.size() >= limit) {
                    break;
                }
            }
        }

        return matches;
    }

    /**
     * Retrieves all candidates possessing a specific skill via HashMap.
     */
    public Set<Candidate> getCandidatesBySkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return skillToCandidatesMap.getOrDefault(skill.trim().toLowerCase(), Collections.emptySet());
    }

    /**
     * Multi-skill matching using HashSet and simple nested loops.
     * (Replaces complex tree intersection)
     */
    public Set<Candidate> findCandidatesWithAllSkills(Collection<String> requiredSkills, Collection<Candidate> allCandidates) {
        if (requiredSkills == null || requiredSkills.isEmpty() || allCandidates == null) {
            return Collections.emptySet();
        }

        Set<Candidate> matchingCandidates = new HashSet<>();

        // Outer loop: Iterate through all candidates
        for (Candidate candidate : allCandidates) {
            Set<String> candidateSkills = candidate.getSkills();
            boolean hasAllSkills = true;

            // Inner loop: Check if candidate possesses every required skill
            for (String req : requiredSkills) {
                boolean foundThisSkill = false;
                for (String cSkill : candidateSkills) {
                    if (cSkill.equalsIgnoreCase(req.trim())) {
                        foundThisSkill = true;
                        break;
                    }
                }
                if (!foundThisSkill) {
                    hasAllSkills = false;
                    break; // Early exit: candidate misses at least one required skill
                }
            }

            if (hasAllSkills) {
                matchingCandidates.add(candidate);
            }
        }

        return matchingCandidates;
    }

    public void clear() {
        skillToCandidatesMap.clear();
        knownSkillsList.clear();
    }
}
