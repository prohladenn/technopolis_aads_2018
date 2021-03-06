package ru.mail.polis.collections.set.hash.todo;

import ru.mail.polis.collections.set.hash.IOpenHashTable;
import ru.mail.polis.collections.set.hash.IOpenHashTableEntity;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementation open addressed hash table with double hashing
 *
 * <a href="https://en.wikipedia.org/wiki/Double_hashing">Double_hashing</a>
 * <p>
 * Use {@link IOpenHashTableEntity#hashCode(int, int)} for hash code calculating
 * <p>
 * Use load = from 0.5f to 0.75f included
 *
 * @param <E> the type of elements maintained by this hash table
 */
@SuppressWarnings("ALL")
public class OpenHashTable<E extends IOpenHashTableEntity> implements IOpenHashTable<E> {

    private Object[] data;
    private boolean[] deleted;
    private final float load = 0.5f;
    private int length = 0;

    private final int DEFAULT = 16;

    public OpenHashTable() {
        this.data = new Object[DEFAULT];
        this.deleted = new boolean[DEFAULT];
    }

    /**
     * Adds the specified element to this set if it is not already present.
     *
     * @param value element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     * element
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the element prevents it from being added to this collection
     *                                  In other words if {@link IOpenHashTableEntity#hashCode(int, int)} specified element is incorrect.
     */
    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (length >= load * tableSize()) {
            resize();
        }
        for (int i = 0; i < tableSize(); i++) {
            int hash = value.hashCode(tableSize(), i);
            if (data[hash] == null || deleted[hash]) {
                length++;
                data[hash] = value;
                return true;
            }
            if (value.equals(data[hash])) {
                return false;
            }
        }
        throw new IllegalArgumentException();
    }

    private void resize() {
        Object[] oldTable = data;
        data = new Object[tableSize() * 2];
        deleted = new boolean[tableSize() * 2];
        int oldSize = length;
        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null && !deleted[i]) {
                add((E) oldTable[i]);
            }
        }
        length = oldSize;
    }

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param value object to be removed from this set, if present
     * @return {@code true} if this set contained the specified element
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the element prevents it from being added to this collection
     *                                  In other words if {@link IOpenHashTableEntity#hashCode(int, int)} specified element is incorrect.
     */
    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < tableSize(); i++) {
            int hash = value.hashCode(tableSize(), i);
            if (data[hash] == null) {
                return false;
            }
            if (value.equals(data[hash]) && !deleted[hash]) {
                length--;
                deleted[hash] = true;
                return true;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Returns {@code true} if this collection contains the specified element.
     * aka collection contains element el such that {@code Objects.equals(el, value) == true}
     *
     * @param value element whose presence in this collection is to be tested
     * @return {@code true} if this collection contains the specified element
     * @throws NullPointerException     if the specified element is null
     * @throws IllegalArgumentException if some property of the element prevents it from being added to this collection
     *                                  In other words if {@link IOpenHashTableEntity#hashCode(int, int)} specified element is incorrect.
     */
    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < tableSize(); i++) {
            int hash = value.hashCode(tableSize(), i);
            if (data[hash] == null) {
                return false;
            }
            if (value.equals(data[hash]) && !deleted[hash]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of elements in this collection.
     *
     * @return the number of elements in this collection
     */
    @Override
    public int size() {
        return length;
    }

    /**
     * Returns {@code true} if this collection contains no elements.
     *
     * @return {@code true} if this collection contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Removes all of the elements from this collection.
     * The collection will be empty after this method returns.
     */
    @Override
    public void clear() {
        this.length = 0;
        this.data = new Object[DEFAULT];
        this.deleted = new boolean[DEFAULT];
    }

    /**
     * Returns an iterator over the elements in this set.
     *
     * @return an {@code Iterator} over the elements in this collection
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int next = 0;
            private int lastNext = -1;
            private int p = size();

            @Override
            public boolean hasNext() {
                return p > 0;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                while (data[next] == null || deleted[next]) {
                    next++;
                }
                p--;
                lastNext = next++;
                return (E) data[lastNext];
            }

            @Override
            public void remove() {
                if (lastNext < 0) {
                    throw new IllegalStateException();
                }
                length--;
                deleted[lastNext] = true;
                lastNext = -1;
            }
        };
    }

    @Override
    public int tableSize() {
        return data.length;
    }
}
