package zSearchEngine;

public class HashMap<K, T> {
    private static final double LOAD_FACTOR = 0.75;
    private static final int DEFAULT_SIZE = 16;

    private LinkedList<Container<K, T>>[] buckets;
    private  int capacity;
    private int size; // Number of elements in the map

    // Constructor with specified capacity
    public HashMap(int capacity) {
        this.capacity = capacity;
        this.buckets = new LinkedList[this.capacity];
        this.size = 0;
    }

    // Default constructor
    public HashMap() {
        this(DEFAULT_SIZE);
    }

    // Helper method to calculate the hash value for a key
    private int hash(K key) {
        return (key == null) ? 0 : Math.abs(key.hashCode()) % buckets.length;
    }

    // Put a key and value
    public void put(K key, T value) {
        int bucketIndex = hash(key);

        // If the bucket is empty, initialize it
        if (buckets[bucketIndex] == null) {
            buckets[bucketIndex] = new LinkedList<>();
        }

        //if the key already exists,  update the value 
        for (int i = 0; i < buckets[bucketIndex].getSize(); i++) {
            Container<K, T> entry = buckets[bucketIndex].get(i); // Get the entry at index i
            if (entry.key.equals(key)) {
                entry.value = value; // Update existing value
                return;
            }
        }
        // Add the new key-value pair to the bucket
        buckets[bucketIndex].insert(new Container<>(key, value));
        size++;

        // Resize the table if the load factor got exceded  
        if ((double) size / buckets.length > LOAD_FACTOR) {
            resize();
        }
    }


    // Get the value associated with a key
    public T get(K key) {
        int bucketIndex = hash(key);
        if (buckets[bucketIndex] == null) {
            return null; // No such key exists
        }
        
        for (int i = 0; i < buckets[bucketIndex].getSize(); i++) {
            Container<K, T> entry = buckets[bucketIndex].get(i); // Get the entry at index i
            if (entry.key.equals(key)) {
                return entry.value; // Return the value if key is found
            }
        }
        return null; // Key not found
    }
    
    


    // Remove a key-value pair by key
    public boolean remove(K key) {
        int bucketIndex = hash(key);
        if (buckets[bucketIndex] == null) {
            return false; // No such key exists
        }

        // Iterate through the linked list at the specific bucket.
        buckets[bucketIndex].findFirst();
        for (int i = 0; i < buckets[bucketIndex].getSize(); i++) {
            Container<K, T> entry = buckets[bucketIndex].get(i); // Get the entry at index i
            if (entry.key.equals(key)) {
            	
                buckets[bucketIndex].remove(); 
                size--; // Decrease the size of the map
                return true;
            }
            buckets[bucketIndex].findNext();
        }

        return false; // Key not found
    }

    
    

    // Resize the hash table when the load factor exceeds the threshold
    private void resize() {
        LinkedList<Container<K, T>>[] oldBuckets = buckets;
        capacity *= 2; // Double the capacity
        buckets = new LinkedList[capacity];
        size = 0;

        // Rehash all entries.
        for (int i = 0; i < oldBuckets.length; i++) {
            if (oldBuckets[i] != null) {
                // Iterate through the linked list at oldBuckets[i]
                for (int j = 0; j < oldBuckets[i].getSize(); j++) {
                    Container<K, T> entry = oldBuckets[i].get(j); // Get the entry at index j
                    put(entry.key, entry.value); // Rehash entries into new buckets
                }
            }
        }
    }


    public int size() {
        return size;
    }

    // Check if the map contains a specific key
    public boolean containsKey(K key) {
        int bucketIndex = hash(key);
        
        if (buckets[bucketIndex] == null) {
            return false;
        }
        
        for (int i = 0; i < buckets[bucketIndex].getSize(); i++) {
            Container<K, T> entry = buckets[bucketIndex].get(i); // Get the entry at index i
            if (entry.key.equals(key)) {
                return true; // Key found
            }
        }
        return false; // Key not found
    }


    public boolean containsValue(T value) {
        
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                // Loop through each entry in the bucket
                for (int j = 0; j < buckets[i].getSize(); j++) {
                    Container<K, T> entry = buckets[i].get(j); // Get the entry at index j
                    if (entry.value.equals(value)) {
                        return true; // Value found
                    }
                }
            }
        }

        return false; // Value not found
    }


        
    // Clear all entries in the map
    public void clear() {
        buckets = new LinkedList[capacity];
        size = 0;
    }

   
    public void print() {
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] != null) {
                for (int j = 0; j < buckets[i].getSize(); j++) {
                    Container<K, T> entry = buckets[i].get(j); // Get the entry at index j
                    System.out.println("Bucket " + i + ": " + entry.key + " => " + entry.value);
                }
            }
        }
    }

    
 // Container class representing key-value pair
    private static class Container<K, T> {
        K key;
        T value;

        Container(K key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
