package org.munn.parallelalgorithms.sortmerge;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * This class sorts even and odd numbers from an input array concurrently using multiple threads.
 * It also merges the sorted even and odd arrays into a single array with even numbers first, followed by odd numbers.
 */
public class ConcurrentArraySorter {
    private int[] inputArray;

    public void setInputArray(int[] array) {
        this.inputArray = array;
    }

    public static void main(String[] args) throws InterruptedException {
        ConcurrentArraySorter sorter = new ConcurrentArraySorter();
        sorter.readInputArray();
        sorter.sortAndMergeUsingThreads();
        sorter.sortAndMergeUsingCompletableFuture();
    }

    /**
     * Reads the input array from the user.
     */
    private void readInputArray() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the input array as comma-separated integers:");
            String[] inputArrayAsString = scanner.nextLine().split(",");
            if (inputArrayAsString.length == 0) {
                System.out.println("Invalid input array! Try re-running the program.");
                System.exit(1);
            }

            inputArray = new int[inputArrayAsString.length];
            for (int i = 0; i < inputArrayAsString.length; i++) {
                String numStr = inputArrayAsString[i].trim();
                try {
                    inputArray[i] = Integer.parseInt(numStr);
                } catch (NumberFormatException nfe) {
                    throw new RuntimeException("Invalid input: " + numStr, nfe);
                }
            }
        }
    }

    /**
     * Sorts even and odd numbers from the input array concurrently using multiple threads,
     * and then merges the sorted arrays into a single array with even numbers first, followed by odd numbers.
     */
    private void sortAndMergeUsingThreads() throws InterruptedException {
        if (inputArray == null || inputArray.length == 0) {
            System.out.println("Input array is empty.");
            return;
        }

        // Create Runnable objects for sorting even and odd numbers
        ArraySorter evenSorter = new ArraySorter(inputArray, num -> num % 2 == 0);
        ArraySorter oddSorter = new ArraySorter(inputArray,  num -> num % 2 != 0);

        // Create and start threads for sorting even and odd numbers
        Thread evenThread = new Thread(evenSorter);
        Thread oddThread = new Thread(oddSorter);
        evenThread.start();
        oddThread.start();

        // Wait for the threads to complete
        evenThread.join();
        oddThread.join();

        // Get the sorted arrays
        int[] sortedEvenArray = evenSorter.getSortedArray();
        int[] sortedOddArray = oddSorter.getSortedArray();

        // Merge the sorted arrays
        ArrayMerger merger = new ArrayMerger(sortedEvenArray, sortedOddArray);
        Thread mergeThread = new Thread(merger);
        mergeThread.start();
        mergeThread.join();

        // Get the combined array
        int[] combinedArray = merger.getCombinedArray();

        // Print the sorted arrays
        System.out.println("Sorted Even array:");
        printArray(sortedEvenArray);

        System.out.println("Sorted Odd array:");
        printArray(sortedOddArray);

        System.out.println("Sorted Combined array with Evens first and then Odds:");
        printArray(combinedArray);
        System.out.println("----------------------------\n");
    }

    /**
     * Sorts even and odd numbers from the input array concurrently using CompletableFuture,
     * and then merges the sorted arrays into a single array with even numbers first, followed by odd numbers.
     */
    private void sortAndMergeUsingCompletableFuture() {
        if (inputArray == null || inputArray.length == 0) {
            System.out.println("Input array is empty.");
            return;
        }

        CompletableFuture<int[]> futureThread1 =
                CompletableFuture.supplyAsync(() -> Arrays.stream(inputArray)
                                .filter(i -> i % 2 == 0) // Even numbers
                                .sorted()
                                .toArray())
                        .thenCombine(CompletableFuture.supplyAsync(
                                        () -> Arrays.stream(inputArray)
                                                .filter(i -> i % 2 != 0) // Odd numbers
                                                .sorted()
                                                .toArray()),
                                (sortedEvens, sortedOdds) ->
                                        IntStream.concat(Arrays.stream(sortedEvens),
                                                Arrays.stream(sortedOdds)).toArray());
        try {
            printArray(futureThread1.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the elements of an integer array.
     */
    private static void printArray(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }

    /**
     * This class sorts numbers from an input array based on a given predicate.
     */
    private static class ArraySorter implements Runnable {

        private final int[] inputArray;
        private final Predicate<Integer> filterCriteria;
        private int[] sortedArray;

        /**
         * Constructor for ArraySorter.
         * @param arr the array of integers to sort.
         * @param filterCriteria the predicate to apply for filtering.
         */
        ArraySorter(int[] arr, Predicate<Integer> filterCriteria) {
            this.inputArray = arr;
            this.filterCriteria = filterCriteria;
        }

        @Override
        public void run() {
            sortedArray = Arrays.stream(inputArray)
                    .filter(filterCriteria::test)
                    .sorted()
                    .toArray();
        }

        /**
         * Retrieves the sorted array.
         * @return the sorted array.
         */
        int[] getSortedArray() {
            return sortedArray;
        }
    }

    /**
     * This class merges two sorted arrays, one containing even numbers and the other containing odd numbers,
     * into a single array with even numbers first, followed by odd numbers.
     */
    private static class ArrayMerger implements Runnable {
        private final int[] evenArray;
        private final int[] oddArray;
        private int[] combinedArray;

        ArrayMerger(int[] evenArr, int[] oddArr) {
            this.evenArray = evenArr;
            this.oddArray = oddArr;
        }

        @Override
        public void run() {
            combinedArray = IntStream.concat(Arrays.stream(evenArray), Arrays.stream(oddArray)).toArray();
        }

        int[] getCombinedArray() {
            return combinedArray;
        }
    }
}