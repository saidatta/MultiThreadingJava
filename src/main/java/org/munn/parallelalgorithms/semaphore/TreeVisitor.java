package org.munn.parallelalgorithms.semaphore;

class TreeVisitor extends Thread {
    private final int visitorId;
    private final Tree sharedTree;
    private final int sleepTime;
    private final int operationTime;
    private final int numberOfIterations;

    TreeVisitor(int id, int iterations, Tree tree, int sleep, int operation) {
        this.visitorId = id;
        this.numberOfIterations = iterations;
        this.sharedTree = tree;
        this.sleepTime = sleep;
        this.operationTime = operation;
    }

    @Override
    public void run() {
        for (int iteration = 0; iteration < numberOfIterations; iteration++) {
            performNonCriticalOperation();
            sharedTree.traverseUpFrom(visitorId);
            performCriticalOperation();
            logTraversalStatus(iteration);
            sharedTree.traverseDownTo(visitorId / 2);
        }
    }

    private void performCriticalOperation() {
        try {
            Thread.sleep(operationTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void performNonCriticalOperation() {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void logTraversalStatus(int iteration) {
        long currentTime = System.currentTimeMillis();
        Start.outputWriter.println("UnixTime=" + (currentTime - Start.startTime));
        Start.runtimeLog.append("Visitor[").append(visitorId - sharedTree.getSize() + 1).append("-")
                .append(iteration).append("]:").append(currentTime - Start.startTime).append("\n");
    }
}
