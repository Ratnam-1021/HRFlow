package com.hrflow.dsa.trie;

import com.hrflow.model.Candidate;

import java.util.*;

/**
 * Trie (Prefix Tree) implementation specialized for HR Skill Autocomplete and Inverted Candidate Retrieval.
 * Enables O(L) prefix searches where L is the query length, independent of the number of candidates.
 */
public class SkillTrie {
    private final TrieNode root;

    public SkillTrie() {
        this.root = new TrieNode();
    }

    /**
     * Inserts a skill into the trie, associating it with an optional candidate.
     */
    public void insertSkill(String skill, Candidate candidate) {
        if (skill == null || skill.trim().isEmpty()) {
            return;
        }

        String normalized = skill.trim().toLowerCase();
        TrieNode current = root;

        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            current = current.getChildren().computeIfAbsent(ch, k -> new TrieNode());
        }

        current.setEndOfWord(true);
        if (current.getCanonicalSkillName() == null) {
            current.setCanonicalSkillName(skill.trim());
        }
        if (candidate != null) {
            current.addCandidate(candidate);
        }
    }

    /**
     * Autocompletes skills matching a prefix (e.g., "jav" -> ["Java", "JavaFX", "JavaScript"]).
     */
    public List<String> autocomplete(String prefix, int limit) {
        List<String> results = new ArrayList<>();
        if (prefix == null) {
            return results;
        }

        String normalized = prefix.trim().toLowerCase();
        TrieNode current = root;

        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            current = current.getChildren().get(ch);
            if (current == null) {
                return results; // prefix not found
            }
        }

        collectSkillsDFS(current, results, limit);
        return results;
    }

    private void collectSkillsDFS(TrieNode node, List<String> results, int limit) {
        if (results.size() >= limit) {
            return;
        }

        if (node.isEndOfWord() && node.getCanonicalSkillName() != null) {
            results.add(node.getCanonicalSkillName());
        }

        for (TrieNode child : node.getChildren().values()) {
            collectSkillsDFS(child, results, limit);
            if (results.size() >= limit) {
                break;
            }
        }
    }

    /**
     * Retrieves all candidates tagged with a specific skill.
     */
    public Set<Candidate> getCandidatesBySkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            return Collections.emptySet();
        }

        String normalized = skill.trim().toLowerCase();
        TrieNode current = root;

        for (int i = 0; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            current = current.getChildren().get(ch);
            if (current == null) {
                return Collections.emptySet();
            }
        }

        return current.isEndOfWord() ? Collections.unmodifiableSet(current.getMatchingCandidates()) : Collections.emptySet();
    }

    /**
     * Multi-skill query: finds candidates possessing ALL given skills (Set Intersection).
     */
    public Set<Candidate> findCandidatesWithAllSkills(Collection<String> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Candidate> resultSet = null;

        for (String skill : skills) {
            Set<Candidate> matches = getCandidatesBySkill(skill);
            if (resultSet == null) {
                resultSet = new HashSet<>(matches);
            } else {
                resultSet.retainAll(matches);
            }
            if (resultSet.isEmpty()) {
                break; // Short-circuit early if intersection is empty
            }
        }

        return (resultSet != null) ? resultSet : Collections.emptySet();
    }
}
