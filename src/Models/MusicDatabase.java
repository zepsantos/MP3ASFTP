package Models;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MusicDatabase extends HashMap<Integer,Music> {
    public static MusicDatabase inst = null;
    private ReentrantLock uploadLock;
    private AtomicInteger lastMusicID;

    public static MusicDatabase getInstance() {
        if(inst == null) inst = new MusicDatabase();
        return inst;
    }

    public MusicDatabase() {
        lastMusicID = new AtomicInteger();
        uploadLock = new ReentrantLock();

    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public Music get(Object key) {
        return super.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public Music put(Integer key, Music value) {
        value.setMusicID(lastMusicID.getAndIncrement());
        return super.put(key, value);
    }

    @Override
    public void putAll(Map m) {
        super.putAll(m);
    }

    @Override
    public Music remove(Object key) {
        return super.remove(key);
    }

    @Override
    public Collection values() {
        return super.values();
    }
}
