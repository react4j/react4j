package gwt.interop.utils.shared.collections;

import javaemul.internal.ArrayHelper;

import java.util.*;

import static javaemul.internal.InternalPreconditions.checkPositionIndexes;
import static javaemul.internal.InternalPreconditions.checkState;

import static javaemul.internal.InternalPreconditions.checkElement;
import static javaemul.internal.InternalPreconditions.checkElementIndex;
import static javaemul.internal.InternalPreconditions.checkPositionIndex;

/**
 * An adapter class to expose a Native JS Array object as a Java List
 * @param <E> The type of array element the adapter accesses
 */
public class ArrayListAdapter<E> extends AbstractList<E> implements List<E>,
        Cloneable, RandomAccess {

    /**
     * This field holds a JavaScript array.
     */
    private transient E[] array;

    public ArrayListAdapter(Array<?> src) {
        array = JsArrayHelper.castAsArray(src);
    }

    @Override
    public boolean add(E o) {
        array[array.length] = o;
        return true;
    }

    @Override
    public void add(int index, E o) {
        checkPositionIndex(index, array.length);
        ArrayHelper.insertTo(array, index, o);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Object[] cArray = c.toArray();
        int len = cArray.length;
        if (len == 0) {
            return false;
        }
        ArrayHelper.insertTo(array, array.length, cArray);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index, array.length);
        Object[] cArray = c.toArray();
        int len = cArray.length;
        if (len == 0) {
            return false;
        }
        ArrayHelper.insertTo(array, index, cArray);
        return true;
    }

    @Override
    public void clear() {
        ArrayHelper.setLength(array, 0);
    }

    public Object clone() {
        return new java.util.ArrayList<E>(this);
    }

    @Override
    public boolean contains(Object o) {
        return (indexOf(o) != -1);
    }

    public void ensureCapacity(int ignored) {
        // Ignored.
    }

    @Override
    public E get(int index) {
        checkElementIndex(index, array.length);
        return array[index];
    }

    @Override
    public int indexOf(Object o) {
        return indexOf(o, 0);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int i = 0, last = -1;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public E next() {
                checkElement(hasNext());

                last = i++;
                return array[last];
            }

            @Override
            public void remove() {
                checkState(last != -1);

                ArrayListAdapter.this.remove(i = last);
                last = -1;
            }
        };
    }

    @Override
    public boolean isEmpty() {
        return array.length == 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return lastIndexOf(o, size() - 1);
    }

    @Override
    public E remove(int index) {
        E previous = get(index);
        ArrayHelper.removeFrom(array, index, 1);
        return previous;
    }

    @Override
    public boolean remove(Object o) {
        int i = indexOf(o);
        if (i == -1) {
            return false;
        }
        remove(i);
        return true;
    }

    @Override
    public E set(int index, E o) {
        E previous = get(index);
        array[index] = o;
        return previous;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public Object[] toArray() {
        return ArrayHelper.clone(array, 0, array.length);
    }

    /*
     * Faster than the iterator-based implementation in AbstractCollection.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] out) {
        int size = array.length;
        if (out.length < size) {
            out = ArrayHelper.createFrom(out, size);
        }
        for (int i = 0; i < size; ++i) {
            out[i] = (T) array[i];
        }
        if (out.length > size) {
            out[size] = null;
        }
        return out;
    }

    public void trimToSize() {
        // We are always trimmed to size.
    }

    @Override
    protected void removeRange(int fromIndex, int endIndex) {
        checkPositionIndexes(fromIndex, endIndex, array.length);
        int count = endIndex - fromIndex;
        ArrayHelper.removeFrom(array, fromIndex, count);
    }

    private int indexOf(Object o, int index) {
        for (; index < array.length; ++index) {
            if (Objects.equals(o, array[index])) {
                return index;
            }
        }
        return -1;
    }

    int lastIndexOf(Object o, int index) {
        for (; index >= 0; --index) {
            if (Objects.equals(o, array[index])) {
                return index;
            }
        }
        return -1;
    }

    void setSize(int newSize) {
        ArrayHelper.setLength(array, newSize);
    }
}
