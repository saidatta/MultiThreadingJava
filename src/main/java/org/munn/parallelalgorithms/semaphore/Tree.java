package org.munn.parallelalgorithms.semaphore;

class Tree {
    private final int size;
    private final Semaphore[] locks;

    Tree(int size) {
        this.size = size;
        this.locks = new Semaphore[size];
        for (int i = 0; i < size; i++) {
            locks[i] = new Semaphore(i);
        }
    }

    int getSize() {
        return size;
    }

    void traverseUpFrom(int visitorId) {
        int currentNode = visitorId;
        while (currentNode / 2 != 0) {
            locks[currentNode / 2].acquireLock(visitorId - size + 1, 0);
            currentNode /= 2;
        }
    }

    void traverseDownTo(int visitorId) {
        if (visitorId == 0) return;
        traverseDownTo(visitorId / 2);
        locks[visitorId].releaseLock();
    }

    @Override
    public String toString() {
        StringBuilder treeStructure = new StringBuilder();
        int levelMarker = 1, level = 1;
        for (int i = 1; i < size; i++) {
            treeStructure.append(locks[i].toString());
            if (i == levelMarker) {
                levelMarker += Math.pow(2, level);
                level++;
                treeStructure.append("\n");
            } else {
                treeStructure.append("    ");
            }
        }
        return treeStructure.toString();
    }
}
