package gwt.interop.utils.shared.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterable<T> implements Iterable<T>, Iterator<T> {
    private int currentIndex = 0;
    private Array<T> hostArray;

    public ArrayIterable(Array<T> hostArray) {
        this.hostArray = hostArray;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < hostArray.getLength();
    }

    @Override
    public T next() {
        if (hasNext())
            return hostArray.get(currentIndex++);
        else
            throw new NoSuchElementException();
    }
}
