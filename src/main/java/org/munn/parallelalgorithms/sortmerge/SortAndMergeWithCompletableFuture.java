package org.munn.parallelalgorithms.sortmerge;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class SortAndMergeWithCompletableFuture {
    private static final int[] arr = {2, 29, 3, 0, 11, 8, 32, 94, 9, 1, 7};

    public static void main(String[] args) {
        CompletableFuture<int[]> evenFuture = CompletableFuture.supplyAsync(() -> sortNumbersBasedOnPredicate(arr, num -> num % 2 == 0));
        CompletableFuture<int[]> oddFuture = CompletableFuture.supplyAsync(() -> sortNumbersBasedOnPredicate(arr,  num -> num % 2 != 0));

        CompletableFuture<int[]> mergeFuture = evenFuture.thenCombine(oddFuture, SortAndMergeWithCompletableFuture::mergeArrays);

        mergeFuture.thenAccept(result -> System.out.println(Arrays.toString(result)))
                .join();
    }

    private static int[] sortNumbersBasedOnPredicate(int[] arr, Predicate<Integer> filterFn) {
        return Arrays.stream(arr).filter(filterFn::test).sorted().toArray();
    }

    private static int[] sortEvenNumbers(int[] arr) {
        return Arrays.stream(arr)
                .filter(num -> num % 2 == 0)
                .sorted()
                .toArray();
    }

    private static int[] sortOddNumbers(int[] arr) {
        return Arrays.stream(arr)
                .filter(num -> num % 2 != 0)
                .sorted()
                .toArray();
    }

    private static int[] mergeArrays(int[] evenArr, int[] oddArr) {
        int[] mergedArr = new int[arr.length];
        int evenIndex = 0, oddIndex = 0, mergedIndex = 0;

        while (evenIndex < evenArr.length && oddIndex < oddArr.length) {
            if (evenArr[evenIndex] < oddArr[oddIndex]) {
                mergedArr[mergedIndex++] = evenArr[evenIndex++];
            } else {
                mergedArr[mergedIndex++] = oddArr[oddIndex++];
            }
        }

        while (evenIndex < evenArr.length) {
            mergedArr[mergedIndex++] = evenArr[evenIndex++];
        }

        while (oddIndex < oddArr.length) {
            mergedArr[mergedIndex++] = oddArr[oddIndex++];
        }

        return mergedArr;
    }
}
