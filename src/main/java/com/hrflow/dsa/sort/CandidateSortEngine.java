package com.hrflow.dsa.sort;

import com.hrflow.model.Candidate;

import java.util.*;

/**
 * Candidate Ranking Engine powered by Insertion Sort and Selection Sort.
 * 
 * 1. Selection Sort: Performs K passes over candidate pool to extract the Top-K highest scoring applicants.
 * 2. Insertion Sort: Incrementally sorts candidates by score in O(N^2) worst / O(N) best case.
 */
public class CandidateSortEngine {

    /**
     * Top-K extraction using partial Selection Sort.
     * In pass i, it scans the remaining list and selects the maximum score,
     * swapping it to position i.
     */
    public static List<Candidate> getTopKViaSelectionSort(Collection<Candidate> candidatePool, int k) {
        if (candidatePool == null || candidatePool.isEmpty() || k <= 0) {
            return Collections.emptyList();
        }

        List<Candidate> list = new ArrayList<>(candidatePool);
        int n = list.size();
        int iterations = Math.min(k, n);

        for (int i = 0; i < iterations; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (list.get(j).getOverallScore() > list.get(maxIdx).getOverallScore()) {
                    maxIdx = j;
                }
            }

            if (maxIdx != i) {
                Candidate temp = list.get(i);
                list.set(i, list.get(maxIdx));
                list.set(maxIdx, temp);
            }
        }

        return new ArrayList<>(list.subList(0, iterations));
    }

    /**
     * Sorts candidates in descending order of score using Insertion Sort.
     * Builds the sorted array one item at a time by shifting smaller elements rightward.
     */
    public static List<Candidate> sortByInsertionSort(Collection<Candidate> candidatePool) {
        List<Candidate> list = new ArrayList<>(candidatePool);
        int n = list.size();

        for (int i = 1; i < n; i++) {
            Candidate key = list.get(i);
            int j = i - 1;

            // Move elements of list[0..i-1] that have smaller score than key.score
            // to one position ahead of their current position (descending sort)
            while (j >= 0 && list.get(j).getOverallScore() < key.getOverallScore()) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }

        return list;
    }

    /**
     * Recalculates candidate match scores against job requirements,
     * then extracts the Top-K candidates using Selection Sort.
     */
    public static List<Candidate> rankAndGetTopK(Collection<Candidate> candidatePool, Set<String> requiredSkills, int k) {
        for (Candidate c : candidatePool) {
            c.updateSkillMatch(requiredSkills);
        }
        return getTopKViaSelectionSort(candidatePool, k);
    }
}
