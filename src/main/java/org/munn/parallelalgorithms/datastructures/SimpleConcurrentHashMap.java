package org.munn.parallelalgorithms.datastructures;

import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;

public class SimpleConcurrentHashMap<K, V> {
    private final int numberOfBuckets;
    private final List<Bucket<K, V>> buckets;

    public SimpleConcurrentHashMap(int numberOfBuckets) {
        this.numberOfBuckets = numberOfBuckets;
        this.buckets = new ArrayList<>(numberOfBuckets);
        for (int i = 0; i < numberOfBuckets; i++) {
            buckets.add(new Bucket<>());
        }
    }

    private int getBucketIndex(K key) {
        return key.hashCode() % numberOfBuckets;
    }

    public void put(K key, V value) {
        int bucketIndex = getBucketIndex(key);
        Bucket<K, V> bucket = buckets.get(bucketIndex);
        bucket.put(key, value);
    }

    public V get(K key) {
        int bucketIndex = getBucketIndex(key);
        Bucket<K, V> bucket = buckets.get(bucketIndex);
        return bucket.get(key);
    }

    private static class Bucket<K, V> {
        private final ReentrantLock lock = new ReentrantLock();
        private final List<Node<K, V>> nodes = new ArrayList<>();

        public void put(K key, V value) {
            lock.lock();
            try {
                for (Node<K, V> node : nodes) {
                    if (node.key.equals(key)) {
                        node.value = value;
                        return;
                    }
                }
                nodes.add(new Node<>(key, value));
            } finally {
                lock.unlock();
            }
        }

        public V get(K key) {
            lock.lock();
            try {
                for (Node<K, V> node : nodes) {
                    if (node.key.equals(key)) {
                        return node.value;
                    }
                }
                return null;
            } finally {
                lock.unlock();
            }
        }
    }

    private static class Node<K, V> {
        final K key;
        V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public static void main(String[] args) {
        SimpleConcurrentHashMap<String, Integer> map = new SimpleConcurrentHashMap<>(10);

        // Testing put operation
        map.put("Key1", 100);
        map.put("Key2", 200);
        map.put("Key3", 300);

        // Testing get operation
        System.out.println("Value for 'Key1': " + map.get("Key1"));
        System.out.println("Value for 'Key2': " + map.get("Key2"));
        System.out.println("Value for 'Key3': " + map.get("Key3"));

        // Testing get operation for a non-existing key
        System.out.println("Value for 'Key4' (non-existing): " + map.get("Key4"));

        // Testing updating an existing key
        map.put("Key1", 111);
        System.out.println("Updated value for 'Key1': " + map.get("Key1"));
    }
}

