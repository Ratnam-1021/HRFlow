package com.hrflow.dsa.sort;

import com.hrflow.model.Candidate;

import java.util.*;

/**
 * Candidate Ranking using Array Sorting & Selection Sort.
 * Replaces complex Max-Heap and Top-K Heap with:
 * - Array + Standard Sorting: Collections.sort() / Array sort
 * - Selection Sort for Top-K extraction: Runs K passes to find top K candidates.
 */
public class CandidateSelectionSorter {

    /**
     * Extracts top K candidates using partial Selection Sort.
     * (Replaces Top-K MaxHeap)
     *
     * How it works:
     * In pass 0: finds the highest scoring candidate and swaps to index 0.
     * In pass 1: finds the 2nd highest scoring candidate and swaps to index 1.
     * ...
     * In pass K-1: finds the K-th highest scoring candidate.
     * Total iterations: K passes instead of a full sort. Very intuitive!
     */
    public static List<Candidate> getTopKViaSelectionSort(Collection<Candidate> candidatePool, int k) {
        if (candidatePool == null || candidatePool.isEmpty() || k <= 0) {
            return Collections.emptyList();
        }

        // Copy into an array/list
        List<Candidate> list = new ArrayList<>(candidatePool);
        int n = list.size();
        int iterations = Math.min(k, n);

        for (int i = 0; i < iterations; i++) {
            int maxIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (list.get(j).getOverallScore() > list.get(maxIndex).getOverallScore()) {
                    maxIndex = j;
                }
            }

            // Swap maximum element to index i
            if (maxIndex != i) {
                Candidate temp = list.get(i);
                list.set(i, list.get(maxIndex));
                list.set(maxIndex, temp);
            }
        }

        return new ArrayList<>(list.subList(0, iterations));
    }

    /**
     * Sorts all candidates in descending order using standard Array / List sorting.
     * (Replaces Max-Heap full extraction)
     */
    public static List<Candidate> sortAllCandidates(Collection<Candidate> candidatePool) {
        List<Candidate> sorted = new ArrayList<>(candidatePool);
        // Sort descending by score
        sorted.sort((c1, c2) -> Double.compare(c2.getOverallScore(), c1.getOverallScore()));
        return sorted;
    }

    /**
     * Re-scores candidates against required skills and extracts top K via Selection Sort.
     */
    public static List<Candidate> rankAndGetTopK(Collection<Candidate> candidatePool, Set<String> requiredSkills, int k) {
        for (Candidate c : candidatePool) {
            c.updateSkillMatch(requiredSkills);
        }
        return getTopKViaSelectionSort(candidatePool, k);
    }
}
