package com.hrflow.dsa.heap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Custom Generic Max-Heap implementation.
 * Provides O(log N) insertions, O(log N) deletions, and O(1) top access.
 *
 * @param <T> Element type to be stored in the heap
 */
public class MaxHeap<T> {
    private final List<T> elements;
    private final Comparator<T> comparator;

    public MaxHeap() {
        this(null);
    }

    @SuppressWarnings("unchecked")
    public MaxHeap(Comparator<T> comparator) {
        this.elements = new ArrayList<>();
        if (comparator != null) {
            this.comparator = comparator;
        } else {
            // Default natural comparator
            this.comparator = (a, b) -> ((Comparable<T>) a).compareTo(b);
        }
    }

    /**
     * Builds a heap from an initial list in O(N) linear time using bottom-up heapify.
     */
    public static <E> MaxHeap<E> fromList(List<E> items, Comparator<E> comparator) {
        MaxHeap<E> heap = new MaxHeap<>(comparator);
        heap.elements.addAll(items);
        for (int i = (heap.elements.size() / 2) - 1; i >= 0; i--) {
            heap.sink(i);
        }
        return heap;
    }

    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot insert null into heap");
        }
        elements.add(item);
        swim(elements.size() - 1);
    }

    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        return elements.get(0);
    }

    public T extractMax() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        T max = elements.get(0);
        T lastItem = elements.remove(elements.size() - 1);
        if (!isEmpty()) {
            elements.set(0, lastItem);
            sink(0);
        }
        return max;
    }

    /**
     * Extracts the top K elements in O(K log N) time without mutating original state.
     */
    public List<T> extractTopK(int k) {
        List<T> result = new ArrayList<>();
        // Clone internal elements array
        MaxHeap<T> copy = new MaxHeap<>(this.comparator);
        copy.elements.addAll(this.elements);

        int count = Math.min(k, copy.size());
        for (int i = 0; i < count; i++) {
            result.add(copy.extractMax());
        }
        return result;
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public void clear() {
        elements.clear();
    }

    // --- Internal Heap Mechanics ---

    private void swim(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (compare(elements.get(index), elements.get(parent)) > 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void sink(int index) {
        int size = elements.size();
        while (index < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < size && compare(elements.get(left), elements.get(largest)) > 0) {
                largest = left;
            }
            if (right < size && compare(elements.get(right), elements.get(largest)) > 0) {
                largest = right;
            }

            if (largest != index) {
                swap(index, largest);
                index = largest;
            } else {
                break;
            }
        }
    }

    private int compare(T a, T b) {
        return comparator.compare(a, b);
    }

    private void swap(int i, int j) {
        T temp = elements.get(i);
        elements.set(i, elements.get(j));
        elements.set(j, temp);
    }
}
