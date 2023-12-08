package org.munn.parallelalgorithms.semaphore;

class Semaphore {
    private final int semaphoreId;
    private int acquiredByVisitor;
    private int acquiredCount;
    private int waitingVisitor;
    private int waitingCount;
    private boolean isLocked;

    Semaphore(int id) {
        this.semaphoreId = id;
        this.acquiredByVisitor = 0;
        this.waitingVisitor = 0;
        this.waitingCount = 0;
        this.acquiredCount = 0;
        this.isLocked = false;
    }

    public synchronized void acquireLock(int visitorId, int count) {
        while (isLocked) {
            waitingVisitor = visitorId;
            waitingCount = count;
            try {
                wait();
            } catch (InterruptedException ignored) {}
            waitingVisitor = 0;
            waitingCount = 0;
        }
        acquiredByVisitor = visitorId;
        acquiredCount = count;
        isLocked = true;
    }

    public synchronized void releaseLock() {
        isLocked = false;
        acquiredByVisitor = 0;
        acquiredCount = 0;
        notifyAll();
        try {
            Thread.sleep(2);
        } catch (InterruptedException ignored) {}
    }

    @Override
    public String toString() {
        return "(Semaphore " + semaphoreId + ", Locked: " + isLocked + ", Acquired by Visitor: ["
                + acquiredByVisitor + ", " + acquiredCount + "], Waiting Visitor: ["
                + waitingVisitor + ", " + waitingCount + "])";
    }
}
