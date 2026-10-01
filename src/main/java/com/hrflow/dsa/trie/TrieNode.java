package com.hrflow.dsa.trie;

import com.hrflow.model.Candidate;

import java.util.*;

/**
 * Node within the Skill Trie.
 * Stores character transitions, end-of-word markers, and candidate references for inverted indexing.
 */
public class TrieNode {
    private final Map<Character, TrieNode> children;
    private boolean isEndOfWord;
    private String canonicalSkillName;
    private final Set<Candidate> matchingCandidates;

    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
        this.canonicalSkillName = null;
        this.matchingCandidates = new HashSet<>();
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public String getCanonicalSkillName() {
        return canonicalSkillName;
    }

    public void setCanonicalSkillName(String canonicalSkillName) {
        this.canonicalSkillName = canonicalSkillName;
    }

    public Set<Candidate> getMatchingCandidates() {
        return matchingCandidates;
    }

    public void addCandidate(Candidate candidate) {
        this.matchingCandidates.add(candidate);
    }
}
