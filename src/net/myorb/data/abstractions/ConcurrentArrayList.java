
package net.myorb.data.abstractions;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.util.ArrayList;
import java.util.List;

/**
 * list with Reentrant Read Write Lock controls
 * - copied from https://stackoverflow.com/questions/26424030/concurrent-arraylist
 * @param <T> manager for data type
 */
public class ConcurrentArrayList <T>
{

    private final Lock readLock;		/** use this to lock for write operations like add/remove */
    private final Lock writeLock;		/** use this to lock for read operations like get/iterator/contains.. */
    private final List <T> list;		/** the underlying list*/

    public ConcurrentArrayList ()
    {
        ReentrantReadWriteLock rwLock =
        		new ReentrantReadWriteLock ();
        this.writeLock = rwLock.writeLock ();
        this.readLock = rwLock.readLock ();
    	this.list = new ArrayList <> ();
    }

    /**
     * locked list ADD request
     * @param element item to be added
     */
    public void add (T element)
    {
        try { writeLock.lock (); list.add (element); }
        finally { writeLock.unlock (); }
    }

    /**
     * locked list GET request
     * @param index the entry number in the list
     * @return the requested element
     */
    public T get (int index)
    {
        try { readLock.lock (); return list.get (index); }
        finally { readLock.unlock (); }
    }

    /**
     * locked list REMOVE request
     * @param index the entry number in the list
     * @return the requested element
     */
    public T remove (int index)
    {
        
        try { readLock.lock (); return list.remove (index); }
        finally { readLock.unlock (); }
    }

    /**
     * treat as FIFO queue and take leading element
     * @return first element if present and NULL if not
     */
    public T getFirst ()
    {
        try
        {
        	readLock.lock ();
        	if (list.size () == 0) return null;
        	return list.remove (0);
        }
        finally { readLock.unlock (); }
    }

}

