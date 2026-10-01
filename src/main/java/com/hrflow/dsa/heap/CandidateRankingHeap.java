package com.hrflow.dsa.heap;

import com.hrflow.model.Candidate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Domain-specific ranking engine for Job Candidates using MaxHeap.
 * Automatically recalibrates candidate scores against job descriptions and yields the top K applicants.
 */
public class CandidateRankingHeap {
    private final MaxHeap<Candidate> heap;

    public CandidateRankingHeap() {
        this.heap = new MaxHeap<>();
    }

    public void addCandidate(Candidate candidate) {
        heap.insert(candidate);
    }

    public void addAll(Collection<Candidate> candidates) {
        for (Candidate c : candidates) {
            heap.insert(c);
        }
    }

    /**
     * Re-scores candidates against target role criteria and builds a prioritized MaxHeap.
     */
    public static CandidateRankingHeap rankForRole(Collection<Candidate> candidates, Set<String> requiredSkills) {
        CandidateRankingHeap rankingHeap = new CandidateRankingHeap();
        for (Candidate c : candidates) {
            c.updateSkillMatch(requiredSkills);
            rankingHeap.addCandidate(c);
        }
        return rankingHeap;
    }

    public Candidate getTopCandidate() {
        return heap.peek();
    }

    public Candidate popTopCandidate() {
        return heap.extractMax();
    }

    public List<Candidate> getTopK(int k) {
        return heap.extractTopK(k);
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void clear() {
        heap.clear();
    }
}
