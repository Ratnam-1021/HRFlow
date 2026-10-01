package com.hrflow.dsa.search;

import com.hrflow.model.Candidate;

import java.util.*;

/**
 * High-performance Binary Search engine on sorted candidate applicant pools.
 * Includes comparative benchmark against O(N) linear scan.
 */
public class BinarySearchEngine {

    public static class BenchmarkResult {
        private final Candidate foundCandidate;
        private final long linearSearchNanos;
        private final int linearComparisons;
        private final long binarySearchNanos;
        private final int binaryComparisons;
        private final int totalRecords;

        public BenchmarkResult(Candidate foundCandidate, long linearSearchNanos, int linearComparisons,
                               long binarySearchNanos, int binaryComparisons, int totalRecords) {
            this.foundCandidate = foundCandidate;
            this.linearSearchNanos = linearSearchNanos;
            this.linearComparisons = linearComparisons;
            this.binarySearchNanos = binarySearchNanos;
            this.binaryComparisons = binaryComparisons;
            this.totalRecords = totalRecords;
        }

        public Candidate getFoundCandidate() { return foundCandidate; }
        public long getLinearSearchNanos() { return linearSearchNanos; }
        public int getLinearComparisons() { return linearComparisons; }
        public long getBinarySearchNanos() { return binarySearchNanos; }
        public int getBinaryComparisons() { return binaryComparisons; }
        public int getTotalRecords() { return totalRecords; }
        public double getSpeedupFactor() {
            return (binarySearchNanos > 0) ? (double) linearSearchNanos / binarySearchNanos : 1.0;
        }
    }

    /**
     * Standard O(log N) iterative binary search by Candidate ID.
     */
    public static int binarySearchCandidateById(List<Candidate> sortedCandidates, String targetId) {
        int low = 0;
        int high = sortedCandidates.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = sortedCandidates.get(mid).getId().compareTo(targetId);

            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    /**
     * Executes both linear and binary search side-by-side to compare execution time and comparisons.
     */
    public static BenchmarkResult compareSearchPerformance(List<Candidate> candidateList, String targetId) {
        List<Candidate> sorted = new ArrayList<>(candidateList);
        sorted.sort(Comparator.comparing(Candidate::getId));

        // 1. Linear Search
        int linearComparisons = 0;
        Candidate linearResult = null;
        long startLinear = System.nanoTime();
        for (Candidate c : candidateList) {
            linearComparisons++;
            if (c.getId().equals(targetId)) {
                linearResult = c;
                break;
            }
        }
        long linearDuration = System.nanoTime() - startLinear;

        // 2. Binary Search
        int binaryComparisons = 0;
        Candidate binaryResult = null;
        long startBinary = System.nanoTime();
        int low = 0;
        int high = sorted.size() - 1;
        while (low <= high) {
            binaryComparisons++;
            int mid = low + (high - low) / 2;
            int cmp = sorted.get(mid).getId().compareTo(targetId);
            if (cmp == 0) {
                binaryResult = sorted.get(mid);
                break;
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        long binaryDuration = System.nanoTime() - startBinary;

        return new BenchmarkResult(
                binaryResult != null ? binaryResult : linearResult,
                linearDuration,
                linearComparisons,
                binaryDuration,
                binaryComparisons,
                candidateList.size()
        );
    }
}
