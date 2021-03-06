package array.hashTable;

import list.ListNode;
import list.SinglyLinkedList;
import util.Constants;

/**
 * Contains operational implementations for the HashTable data structure.
 * Utilizes separate chaining hash collision and prime number dynamic resizing mechanics.
 */
public class HashTable<K, V> {

    private ListNode<Entry<K, V>>[] table;
    private int size;
    private final int initSize = 5;
    private final double loadFactor = 0.70;
    private final int resizeFactor = 2;

    public HashTable() {
        this.table = new ListNode[this.initSize];
        for(int i = 0; i < this.table.length; i++) {
            this.table[i] = new ListNode();
        }
    }

    /**
     * Returns the value associated with a specified key.
     * @param key key to use during indexing.
     * @return value paired to key.
     * @
     */
    public V get(K key) {
        try {
            checkKey(key);
            ListNode<Entry<K, V>> bucket = getBucket(key);
            while(bucket != null) {
                if(bucket.getData().getKey().equals(key)) {
                    return bucket.getData().getValue();
                }
            }
            return null;
        } catch(IllegalArgumentException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Adds a new key-value pair to the table.
     * If key already exists, the existing value at the key index is replaced with the supplied value object.
     * Uses a wrapper method to initialize / handle logic required during insertion process.
     * @param key key to use during indexing.
     * @param value value to pair with key.
     * @return true if the insertion was successful, false if otherwise.
     * @
     */
    public boolean put(K key, V value) {
        try {
            checkKey(key);
            if(put(new Entry(key, value))) {
                tryResize();
                return true;
            } else {
                return false;
            }
        } catch(IllegalArgumentException e) {
            System.out.println(e);
            return false;
        }
    }
    private boolean put(Entry<K, V> entry)  {
        // if bucket's data at hash index is empty, add entry
        if(this.table[hashFunction(entry.getKey())] == null) { // if bucket is null
            this.table[hashFunction(entry.getKey())] = new ListNode(entry);
            this.size++;
            return true;
        } else if(this.table[hashFunction(entry.getKey())].getData() == null) { // if bucket holds no entry data
            this.table[hashFunction(entry.getKey())].setData(entry);
            this.size++;
            return true;
        } else { // if bucket contains data:
            // iterate through bucket until a. bucket with data containing key is found, b. bucket with no entry data is found, or c. null bucket is found
            ListNode<Entry<K, V>> tempBucket = this.table[hashFunction(entry.getKey())];
            if(tempBucket.getData().getKey().equals(entry.getKey())) { // if bucket contains correct entry key data
                tempBucket.getData().setValue(entry.getValue());
                return true;
            }
            while(tempBucket.getNext() != null) {
                if(tempBucket.getData().getKey().equals(entry.getKey())) { // if bucket contains correct entry key data
                    tempBucket.getData().setValue(entry.getValue());
                    return true;
                } else { // check next bucket in list
                    tempBucket = tempBucket.getNext();
                }
            }
            // null bucket has been found, add new entry data:
            tempBucket.setNext(new ListNode(entry));
            this.size++;
            return true;
        }
    }

    /**
     * Determines whether or not table contains a specified key.
     * @param key key to search for.
     * @return true if the table contains the key index, false if otherwise.
     * @
     */
    public boolean containsKey(K key) {
        try {
            checkKey(key);
            ListNode<Entry<K, V>> bucket = getBucket(key);
            while(bucket != null) {
                if(bucket.getData() != null) {
                    if(bucket.getData().getKey().equals(key)) {
                        return true;
                    }
                }
                bucket = bucket.getNext();
            }
            return false;
        } catch(IllegalArgumentException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Removes a key-value pair from the table, referenced using a specified key index.
     * @param key key index of entry to remove.
     * @return true if the removal was successful, false if otherwise
     * @
     */
    public boolean remove(K key) {
        try {
            checkKey(key);
            ListNode<Entry<K, V>> bucket = getBucket(key);
            ListNode<Entry<K, V>> prev = null;
            while(bucket != null) {
                if(bucket.getData().getKey().equals(key)) {
                    break;
                }
                prev = bucket;
                bucket = bucket.getNext();
            }
            if(bucket == null) {
                return false;
            }
            if(prev != null) {
                prev.setNext(bucket.getNext());
            } else {
                this.table[hashFunction(key)] = bucket.getNext();
            }
            this.size--;
            return true;
        } catch(IllegalArgumentException e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Quickly obtains a 'bucket' of entries from a given index in the table.
     * @param key key index to use during retrieval.
     * @return a ListNode object containing the entry corresponding to the key's hashed index.
     */
    private ListNode<Entry<K, V>> getBucket(K key) {
        try {
            checkKey(key);
            return this.table[hashFunction(key)];
        } catch(IllegalArgumentException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Produces a random index within the table for a given key.
     * Does not allow negative indexes to form.
     * @param key key object to be used in hash formula.
     * @return an index value relative to the underlying table array.
     */
    private int hashFunction(K key) throws IllegalArgumentException {
        try {
            checkKey(key);
            int hash = key.hashCode() % this.table.length;
            return (hash < 0) ? hash * -1 : hash;
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Dynamically resizes the underlying table array using specified load and resize factors, under certain conditions.
     * Once table is resized, all of the previous table's elements are rehashed into spots within the newly-allocated array, in order to ensure reliable future hashes.
     * @
     */
    private void tryResize() {
        if(this.size / (double)this.table.length > this.loadFactor) {
            int newSize = this.table.length * this.resizeFactor;
            while(newSize % 2 == 0 || newSize % 3 == 0) { // find > double current size prime number for new table size.
                newSize++;
            }
            SinglyLinkedList<ListNode<Entry<K, V>>> oldEntries = new SinglyLinkedList(); // store current data to be rehashed later.
            for(int i = 0; i < this.table.length; i++) {
                if(this.table[i].getData() != null) {
                    oldEntries.insertEnd(this.table[i]);
                }
            }
            this.table = new ListNode[newSize];
            for(int i = 0; i < this.table.length; i++) {
                this.table[i] = new ListNode();
            }
            for(int i = 0; i < oldEntries.getSize(); i++) { // rehash old values into newly-allocated array.
                ListNode<Entry<K, V>> oldEntry = oldEntries.getElementAt(i);
                while(oldEntry != null) {
                    put(oldEntry.getData().getKey(), oldEntry.getData().getValue());
                    this.size--; // ensure that size isn't being artificially inflated during rehash
                    oldEntry = oldEntry.getNext();
                }
            }
        }
    }

    /**
     * Gets the current number of elements within the table.
     * @return an integer representing the number of table elements.
     * @
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Determines whether or not any elements exist within the table.
     * @return true if any elements exist, false if otherwise.
     * @
     */
    public boolean isEmpty() {
        return this.size <= 0;
    }

    /**
     * Checks a given key for validity, throws an exception if null key is encountered.
     * @param key key to check.
     * @throws IllegalArgumentException
     */
    private void checkKey(K key) throws IllegalArgumentException {
        if(key == null) {
            throw new IllegalArgumentException(String.format(Constants.ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE_INPUT_NULL, "key"));
        }
    }

}
