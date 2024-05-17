package org.munn.parallelalgorithms.sortmerge;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SortMergeExecutors {
    private static final int N = 11; // Size of the array
    private static final int[] arr = {2, 29, 3, 0, 11, 8, 32, 94, 9, 1, 7}; // Initial array
    private static final int[] evenArr = new int[N]; // Array to store even numbers
    private static final int[] oddArr = new int[N]; // Array to store odd numbers
    private static final Semaphore semaphore = new Semaphore(1); // Semaphore for synchronization

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3); // Create a thread pool with 3 threads

        // Submit tasks to the thread pool
        executorService.submit(new SortEvenNumbers());
        executorService.submit(new SortOddNumbers());
        executorService.submit(new MergeArrays());

        executorService.shutdown(); // Shutdown the thread pool
    }

    private static class SortEvenNumbers implements Runnable {
        @Override
        public void run() {
            int evenIndex = 0;
            for (int num : arr) {
                if (num % 2 == 0) {
                    evenArr[evenIndex++] = num;
                }
            }
            Arrays.sort(evenArr, 0, evenIndex); // Sort even numbers
        }
    }

    private static class SortOddNumbers implements Runnable {
        @Override
        public void run() {
            int oddIndex = 0;
            for (int num : arr) {
                if (num % 2 != 0) {
                    oddArr[oddIndex++] = num;
                }
            }
            Arrays.sort(oddArr, 0, oddIndex); // Sort odd numbers
        }
    }

    private static class MergeArrays implements Runnable {
        @Override
        public void run() {
            try {
                semaphore.acquire(); // Acquire the semaphore
                int evenIndex = 0, oddIndex = 0, arrIndex = 0;
                while (evenIndex < evenArr.length && oddIndex < oddArr.length) {
                    if (evenArr[evenIndex] != 0) {
                        arr[arrIndex++] = evenArr[evenIndex++];
                    } else {
                        arr[arrIndex++] = oddArr[oddIndex++];
                    }
                }
                semaphore.release(); // Release the semaphore
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
