package org.munn.parallelalgorithms.semaphore;

import java.io.*;
import java.util.*;

public class Start {
    static final long startTime = System.currentTimeMillis();
    static PrintWriter outputWriter = null;
    private static final String runtimeLogFilePath = "output.log";
    private static final String eventLogFilePath = "event.log";
    static StringBuilder runtimeLog = new StringBuilder();

    public static void main(String[] args) {
        Properties systemProperties = loadSystemProperties("system.properties");
        int numberOfThreads = Integer.parseInt(systemProperties.getProperty("threads"));
        int numberOfIterations = Integer.parseInt(systemProperties.getProperty("iterations"));
        int[] sleepTimes = new int[numberOfThreads];
        int[] operationTimes = new int[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            sleepTimes[i] = Integer.parseInt(systemProperties.getProperty("thread." + i + ".sleepTime"));
            operationTimes[i] = Integer.parseInt(systemProperties.getProperty("thread." + i + ".operationTime"));
        }

        try (FileWriter reportWriter = new FileWriter(runtimeLogFilePath, false);
             FileWriter eventWriter = new FileWriter(eventLogFilePath, false)) {
            outputWriter = new PrintWriter(reportWriter);
            PrintWriter eventLogWriter = new PrintWriter(eventWriter);

            Tree sharedTree = new Tree(numberOfThreads);
            TreeVisitor[] visitors = new TreeVisitor[numberOfThreads];

            for (int i = 0; i < numberOfThreads; i++) {
                visitors[i] = new TreeVisitor(i + numberOfThreads, numberOfIterations, sharedTree, sleepTimes[i], operationTimes[i]);
                visitors[i].start();
            }

            waitForThreadsCompletion(visitors);

            eventLogWriter.println("sequence:\n");
            eventLogWriter.print(runtimeLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Properties loadSystemProperties(String filePath) {
        Properties properties = new Properties();
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(filePath)))) {
            scanner.useDelimiter("\\n|=");
            while (scanner.hasNext()) {
                String key = scanner.next().trim();
                String value = scanner.next().trim();
                properties.setProperty(key, value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static void waitForThreadsCompletion(TreeVisitor[] visitors) {
        for (TreeVisitor visitor : visitors) {
            try {
                visitor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Rest of the classes (TreeVisitor, Semaphore, Tree) go here with similar refactoring
}

