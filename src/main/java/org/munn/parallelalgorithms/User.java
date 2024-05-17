package org.munn.parallelalgorithms;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class User implements Serializable {
    private String name;
    private transient String password; // This field will not be serialized
    private final int capacity;

    /** Main lock guarding all access */
    final ReentrantLock lock = new ReentrantLock();
    public User(String name, String password) {
        this.capacity = -1;
        this.name = name;
        this.password = password;
    }

    public String toString() {
        return "User{name='" + name + "', password='" + password + "'}";
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        User user = new User("Alice", "secret123");
        user.lock.lock();
        // Serialize the User object to a file
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("user.dat"));
        out.writeObject(user);
        out.close();

        // Deserialize the User object from the file
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("user.dat"));
        User deserializedUser = (User) in.readObject();
        in.close();
        user.lock.unlock();
        System.out.println("Deserialized User: " + deserializedUser);
    }
}

