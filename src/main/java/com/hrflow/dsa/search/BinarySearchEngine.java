package com.hrflow.dsa.search;

import com.hrflow.model.Employee;

import java.util.*;

/**
 * High-performance Binary Search engine on sorted employee rosters.
 * Includes interactive benchmark comparison against O(N) linear scan.
 */
public class BinarySearchEngine {

    public static class BenchmarkResult {
        private final Employee foundEmployee;
        private final long linearSearchNanos;
        private final int linearComparisons;
        private final long binarySearchNanos;
        private final int binaryComparisons;
        private final int totalRecords;

        public BenchmarkResult(Employee foundEmployee, long linearSearchNanos, int linearComparisons,
                               long binarySearchNanos, int binaryComparisons, int totalRecords) {
            this.foundEmployee = foundEmployee;
            this.linearSearchNanos = linearSearchNanos;
            this.linearComparisons = linearComparisons;
            this.binarySearchNanos = binarySearchNanos;
            this.binaryComparisons = binaryComparisons;
            this.totalRecords = totalRecords;
        }

        public Employee getFoundEmployee() { return foundEmployee; }
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
     * Standard O(log N) iterative binary search by Employee ID.
     */
    public static int binarySearchById(List<Employee> sortedEmployees, String targetId) {
        int low = 0;
        int high = sortedEmployees.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = sortedEmployees.get(mid).getId().compareTo(targetId);

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
     * Executes both linear and binary search side-by-side to capture comparative timing and comparison counts.
     */
    public static BenchmarkResult compareSearchPerformance(List<Employee> employeeList, String targetId) {
        // Ensure sorted list for binary search
        List<Employee> sorted = new ArrayList<>(employeeList);
        Collections.sort(sorted);

        // 1. Linear Search
        int linearComparisons = 0;
        Employee linearResult = null;
        long startLinear = System.nanoTime();
        for (Employee emp : employeeList) {
            linearComparisons++;
            if (emp.getId().equals(targetId)) {
                linearResult = emp;
                break;
            }
        }
        long linearDuration = System.nanoTime() - startLinear;

        // 2. Binary Search
        int binaryComparisons = 0;
        Employee binaryResult = null;
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
                employeeList.size()
        );
    }
}
