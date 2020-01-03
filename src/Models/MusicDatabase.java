package Models;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MusicDatabase extends HashMap<Integer,Music> {
    public static MusicDatabase inst = null;
    private AtomicInteger lastMusicID;

    public static MusicDatabase getInstance() {
        if(inst == null) {
            ReentrantLock lock = new ReentrantLock();
            try {
                lock.lock();
                inst = new MusicDatabase();
            }finally {
                lock.unlock();
            }

        }
        return inst;
    }

    private MusicDatabase() {
        lastMusicID = new AtomicInteger();
    }

    @Override
    public synchronized int size() {
        return super.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public synchronized  Music get(Object key) {
        return super.get(key);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public synchronized Music put(Integer key, Music value) {
        value.setMusicID(lastMusicID.getAndIncrement());
        return super.put(value.getMusicID(), value);
    }

    @Override
    public synchronized void putAll(Map m) {
        super.putAll(m);
    }

    @Override
    public synchronized Music  remove(Object key) {
        return super.remove(key);
    }

    @Override
    public synchronized Collection values() {
        return super.values();
    }

    public int getLastMusicIDAndIncrement() {
        return lastMusicID.getAndIncrement();
    }
}
