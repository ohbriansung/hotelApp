package concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * A custom reentrant read/write lock that allows:
 * 1) Multiple readers (when there is no writer). Any thread can acquire multiple read locks (if nobody is writing).
 * 2) One writer (when nobody else is writing or reading).
 * 3) A writer is allowed to acquire a read lock while holding the write lock.
 * 4) A writer is allowed to acquire another write lock while holding the write lock.
 * 5) A reader can not acquire a write lock while holding a read lock.
 */
public class ReentrantReadWriteLock {
    private Map<Long, Integer> reader;
    private Map<Long, Integer> writer;

    /**
     * Constructor for ReentrantReadWriteLock. Initialize the data structures and logger.
     */
    public ReentrantReadWriteLock() {
        reader = new HashMap<>();
        writer = new HashMap<>();
    }

    /**
     * Return true if the current thread holds a read lock.
     *
     * @return boolean
     */
    public synchronized boolean isReadLockHeldByCurrentThread() {
        long currentThreadId = Thread.currentThread().getId();

        if (reader.containsKey(currentThreadId)) return true;

        return false;
    }

    /**
     * Return true if the current thread holds a write lock.
     *
     * @return boolean
     */
    public synchronized boolean isWriteLockHeldByCurrentThread() {
        long currentThreadId = Thread.currentThread().getId();

        if (writer.containsKey(currentThreadId)) return true;

        return false;
    }

    /**
     * Non-blocking method that attempts to acquire the read lock. Returns true
     * if successful.
     * Checks conditions (whether it can acquire the read lock), and if they are true,
     * updates readers info.
     *
     * Note that if conditions are false (can not acquire the read lock at the moment), this method
     * does NOT wait, just returns false
     *
     * Thread can only receive a readlock when there is no writelock or the writelock is currently hold by this thread.
     * If this thread currently hold a readlock, add the count of the readlock of this thread.
     * If not, add a new thread id to the map to show that this thread holds a readlock.
     *
     * @return boolean
     */
    public synchronized boolean tryAcquiringReadLock() {
        long currentThreadId = Thread.currentThread().getId();

        if (writer.size() == 0 || isWriteLockHeldByCurrentThread()){
            if (isReadLockHeldByCurrentThread()) {
                int count = reader.get(currentThreadId);
                reader.put(currentThreadId, count + 1);
            }
            else {
                reader.put(currentThreadId, 1);
            }
            return true;
        }

        return false;
    }

    /**
     * Non-blocking method that attempts to acquire the write lock. Returns true
     * if successful.
     * Checks conditions (whether it can acquire the write lock), and if they are true,
     * updates writers info.
     *
     * Note that if conditions are false (can not acquire the write lock at the moment), this method
     * does NOT wait, just returns false
     *
     * Thread can only receive a writelock when there is no readlock.
     * If this thread currently hold a writelock, add the count of the writelock of this thread.
     * If not, add a new thread id to the map to show that this thread holds a writelock.
     *
     * @return boolean
     */
    public synchronized boolean tryAcquiringWriteLock() {
        long currentThreadId = Thread.currentThread().getId();

        if (reader.size() == 0) {
            if (writer.size() == 0) {
                writer.put(currentThreadId, 1);
                return true;
            }
            else if (isWriteLockHeldByCurrentThread()) {
                int count = writer.get(currentThreadId);
                writer.put(currentThreadId, count + 1);
                return true;
            }
        }

        return false;
    }

    /**
     * Blocking method that will return only when the read lock has been acquired.
     * Calls tryAcquiringReadLock, and as long as it returns false, waits.
     * Catches InterruptedException.
     */
    public synchronized void lockRead() {
        while (!tryAcquiringReadLock()) {
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                System.out.println("Exception while running the lockRead: " + e);
            }
        }
    }

    /**
     * Releases the read lock held by the calling thread. Other threads might still be holding read locks.
     * If this thread holds multi readlocks then minus the count in the map.
     * If no more readers after unlocking, calls notifyAll().
     */
    public synchronized void unlockRead() {
        long currentThreadId = Thread.currentThread().getId();

        if (isReadLockHeldByCurrentThread()) {
            int count = reader.get(currentThreadId);
            if (count > 1) reader.put(currentThreadId, count - 1);
            else if (count == 1) reader.remove(currentThreadId);
        }

        if (reader.size() == 0) notifyAll();
    }

    /**
     * Blocking method that will return only when the write lock has been acquired.
     * Calls tryAcquiringWriteLock, and as long as it returns false, waits.
     * Catches InterruptedException.
     */
    public synchronized void lockWrite() {
        while (!tryAcquiringWriteLock()) {
            try {
                this.wait();
            }
            catch (InterruptedException e) {
                System.out.println("Exception while running the lockWrite: " + e);
            }
        }
    }

    /**
     * Releases the write lock held by the calling thread. The calling thread may continue to hold a read lock.
     * If this thread holds multi writelocks then minus the count in the map.
     * If the number of writers becomes 0, calls notifyAll.
     */

    public synchronized void unlockWrite() {
        long currentThreadId = Thread.currentThread().getId();

        if (isWriteLockHeldByCurrentThread()) {
            int count = writer.get(currentThreadId);
            if (count > 1) writer.put(currentThreadId, count - 1);
            else if (count == 1) writer.remove(currentThreadId);
        }

        if (writer.size() == 0) notifyAll();
    }
}
