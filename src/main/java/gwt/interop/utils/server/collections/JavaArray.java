package gwt.interop.utils.server.collections;

import gwt.interop.utils.shared.collections.Array;

import java.util.*;
import java.util.stream.Stream;

public class JavaArray<T> implements Array<T> {
    private ArrayList<T> internalArray;

    public JavaArray() {
        internalArray = new ArrayList<>();
    }

    public JavaArray(ArrayList<T> from) { internalArray = from; }

    public JavaArray(JavaArray<T> from) {
        internalArray = new ArrayList<>(from.internalArray);
    }

    @SafeVarargs
    public JavaArray(T ...values) {
        internalArray = new ArrayList<>();
        internalArray.addAll(0, Arrays.asList(values));
    }

    @Override
    public Array<T> concat(Array<T> a) {
        JavaArray<T> out = new JavaArray<>(this);

        a.forEachElem((ForEachFn<T>) out::push);

        return out;
    }

    @Override
    public Array<T> concatValue(T a) {
        JavaArray<T> out = new JavaArray<>(this);
        out.push(a);
        return out;
    }

    @Override
    public T get(int index) {
        return internalArray.get(index);
    }

    @Override
    public String join(String separator) {
        StringBuilder out = new StringBuilder();
        Iterator<T> iterator = internalArray.iterator();

        while(iterator.hasNext()) {
            out.append(iterator.next());
            if (iterator.hasNext()) {
                out.append(separator);
            }
        }

        return out.toString();
    }

    @Override
    public int getLength() {
        return internalArray.size();
    }

    @Override
    public void setLength(int newLength) {
        if (newLength < internalArray.size()) {
            while(internalArray.size() > newLength) {
                internalArray.remove(internalArray.size()-1);
            }
        }else {
            internalArray.ensureCapacity(newLength);
        }
    }

    @Override
    public void push(T value) {
        internalArray.add(value);
    }

    @Override
    public void push(T[] value) {

    }

    @Override
    public void set(int index, T value) {
        internalArray.set(index, value);
    }

    @Override
    public T shift() {
        return internalArray.remove(0);
    }

    @Override
    public void unshift(T value) {
        internalArray.add(0, value);
    }

    @Override
    public Array<T> reverse() {
        JavaArray<T> out = new JavaArray<>();

        for(int i = internalArray.size() - 1; i >=0 ; i--) {
            out.internalArray.add(internalArray.get(i));
        }

        return out;
    }

    @Override
    public Array<T> slice(int startIndx, int endIndx) {
        JavaArray<T> out = new JavaArray<>();

        int index = 0;
        for(T value : internalArray) {
            if (index >= startIndx && index < endIndx) {
                out.push(value);
            }
            index++;
        }
        return out;
    }

    @Override
    public Array<T> splice(int startIndx, int deleteCount) {
        return splice(startIndx, deleteCount, (T[])null);
    }

    @Override
    public Array<T> splice(int startIndx) {
        return splice(startIndx, internalArray.size() - startIndx, (T[])null);
    }

    @Override
    public Array<T> splice(int startIndx, int deleteCount, T... toAdd) {
        JavaArray<T> out = new JavaArray<>();

        if (startIndx >= 0) {
            int index = 0;
            Iterator<T> iterator = internalArray.listIterator();

            while(iterator.hasNext()) {
                T value = iterator.next();

                if (index >= startIndx) {
                    if (deleteCount > 0) {
                        out.push(value);
                        iterator.remove();
                        deleteCount--;
                    }

                    if (deleteCount == 0) {
                        break;
                    }
                }
                index++;
            }
        }else {
            throw new UnsupportedOperationException();
        }

        if (toAdd != null) {
            internalArray.addAll(startIndx, Arrays.asList(toAdd));
        }

        return out;
    }

    @Override
    public int indexOf(T searchElement, int fromIndex) {
        int index = 0;

        for(T elem : internalArray) {
            if (index >= fromIndex && (elem != null && elem.equals(searchElement))) {
                return index;
            }

            index += 1;
        }
        return -1;
    }

    @Override
    public int indexOf(T searchElement) {
        return internalArray.indexOf(searchElement);
    }

    @Override
    public int lastIndexOf(T searchElement, int fromIndex) {


        for(int i = fromIndex; i >=0 ; i--) {
            T value = internalArray.get(i);

            if (searchElement.equals(value)) {
                return i;
            }
            i--;
        }

        return -1;
    }

    @Override
    public int lastIndexOf(T searchElement) {
        return internalArray.lastIndexOf(searchElement);
    }

    @Override
    public void forEachElem(ForEachFn<T> fn) {
        for(T elem : internalArray) {
            fn.exec(elem);
        }
    }

    @Override
    public void forEachElem(ForEachFullFn<T> fn) {
        int index = 0;
        for(T elem : internalArray) {
            fn.exec(elem, index++, this);
        }
    }

    @Override
    public Array<T> filter(TestFn<T> fn) {
        JavaArray<T> out = new JavaArray<>();

        for(T elem : internalArray) {
            if (fn.test(elem)) {
                out.push(elem);
            }
        }
        return out;
    }

    @Override
    public Array<T> filter(TestFullFn<T> fn) {
        JavaArray<T> out = new JavaArray<>();
        int index = 0;

        for(T elem : internalArray) {
            if (fn.test(elem, index++, this)) {
                out.push(elem);
            }
        }
        return out;
    }

    @Override
    public T find(TestFn<T> fn) {
        for(T elem : internalArray) {
            if (fn.test(elem)) {
                return elem;
            }
        }
        return null;
    }

    @Override
    public T find(TestFullFn<T> fn) {
        int index = 0;

        for(T elem : internalArray) {
            if (fn.test(elem, index++, this)) {
                return elem;
            }
        }
        return null;
    }

    @Override
    public <T2> Array<T2> map(MapFn<T, T2> fn) {
        JavaArray<T2> out = new JavaArray<>();

        for(T elem : internalArray) {
            out.push(fn.doMap(elem));
        }
        return out;
    }

    @Override
    public <T2> Array<T2> map(MapFullFn<T, T2> fn) {
        JavaArray<T2> out = new JavaArray<>();
        int index = 0;

        for(T elem : internalArray) {
            out.push(fn.doMap(elem, index++, this));
        }
        return out;
    }

    @Override
    public <A> A reduce(ReduceFn<A, T> fn, A initialValue) {

        for(T elem : internalArray) {
            initialValue = fn.doReduce(initialValue, elem);
        }
        return initialValue;
    }

    @Override
    public <A> A reduce(ReduceFullFn<A, T> fn, A initialValue) {
        int index = 0;

        for(T elem : internalArray) {
            initialValue = fn.doReduce(initialValue, elem, index++, this);
        }
        return initialValue;
    }

    @Override
    public <A> A reduceRight(ReduceFn<A, T> fn, A initialValue) {
        for(int i = internalArray.size() - 1; i >=0 ; i--) {
            initialValue = fn.doReduce(initialValue, get(i));
        }
        return initialValue;
    }

    @Override
    public <A> A reduceRight(ReduceFullFn<A, T> fn, A initialValue) {
        for(int i = internalArray.size() - 1; i >=0 ; i--) {
            initialValue = fn.doReduce(initialValue, get(i), i, this);
        }
        return initialValue;
    }

    @Override
    public boolean some(TestFn<T> fn) {
        for(T elem : internalArray) {
            if (fn.test(elem))
                return true;
        }
        return false;
    }

    @Override
    public boolean some(TestFullFn<T> fn) {
        int index = 0;
        for(T elem : internalArray) {
            if (fn.test(elem, index++, this))
                return true;
        }
        return false;
    }

    @Override
    public boolean every(TestFn<T> fn) {
        for(T elem : internalArray) {
            if (!fn.test(elem))
                return false;
        }
        return true;
    }

    @Override
    public boolean every(TestFullFn<T> fn) {
        int index = 0;
        for(T elem : internalArray) {
            if (!fn.test(elem, index++, this))
                return false;
        }
        return true;
    }

    @Override
    public Array<T> sort(SortFn<T> fn) {

        internalArray.sort((o1, o2) -> fn.compare(o1, o1));
        return this;
    }

    @Override
    public Array<T> sort() {
        //Kind of a hack just for compatibility. Only works with Array<String>
        Collections.sort(internalArray, (o1, o2) -> ((String)o1).compareTo((String)o2));
        return this;
    }

    /*********************************************************************************************
     * Java Compatibility
     *********************************************************************************************/

    public Iterable<T> asIterable() {
        return internalArray;
    }

    public Stream<T> stream() {
        return internalArray.stream();
    }

    public List<T> asList() { return internalArray; }
}
