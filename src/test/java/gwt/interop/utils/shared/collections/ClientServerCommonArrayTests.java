package gwt.interop.utils.shared.collections;

import java.util.List;

public class ClientServerCommonArrayTests {

    public static void run() {
        //TODO convert to independent JUnit tests
        Array<String> a = ArrayFactory.create();

        a.push("Val1");
        a.push("Val2");

        String v = a.get(0);
        assert(v.equals("Val1"));

        a.set(1, "Modified");
        assert(a.get(1).equals("Modified"));

        assert(a.getLength() == 2);

        a.setLength(1);
        assert(a.getLength() == 1);

        /* concat */
        a = createArray("AValue1");
        Array<String> b = createArray("BValue2","BValue3");
        Array<String> c = a.concat(b);
        assert(toString(c).equals("AValue1,BValue2,BValue3"));

        /* concatValue */
        a = createArray("AValue1");
        b = a.concatValue("BValue1");
        assert(toString(b).equals("AValue1,BValue1"));

        /* every */
        a = createArray("AValue","AValue");
        assert(a.every((current) -> current.equals("AValue")));
        assert(a.every((current, index, array) -> current.equals("AValue")));

        /* filter */
        a = createArray("AValue","AValue","NotAValue");

        b = a.filter((current) -> current.equals("NotAValue"));
        assert(toString(b).equals("NotAValue"));

        b = a.filter((current, index, array) -> current.equals("NotAValue"));
        assert(toString(b).equals("NotAValue"));

        /* forEach */
        a = createArray("a","b","c");
        StringBuilder sb = new StringBuilder();
        a.forEachElem((current, index, array) -> sb.append(current));
        assert(sb.toString().equals("abc"));

        /* indexOf */
        a = createArray("a","b","c","b");
        assert(a.indexOf("b") == 1);
        assert(a.indexOf("b", 2) == 3);

        /* lastIndexOf */
        a = createArray("a","b","c","b","b");
        assert(a.lastIndexOf("b") == 4);
        assert(a.lastIndexOf("b", 3) == 3);

        /* reverse */
        a = createArray("a","b","c");
        b = a.reverse();
        assert(toString(b).equals("c,b,a"));

        /* sort */
        a = createArray("c","b","a");
        b = a.sort();
        assert(toString(a).equals("a,b,c"));
        assert(toString(b).equals("a,b,c"));

        a = createArray("c","b","a");
        b = a.sort((o1, o2) -> o2.compareTo(o1));
        assert(toString(a).equals("c,b,a"));
        assert(toString(b).equals("c,b,a"));

        /* slice */
        a = createArray("a","b","c","d");
        b = a.slice(1,3);
        assert(toString(b).equals("b,c"));

        /* some */
        a = createArray("a","b","c","d");
        assert(a.some((current) -> current.equals("b")));

        /* splice */
        a = createArray("a","b","c","d");
        b = a.splice(2);
        assert(toString(a).equals("a,b"));
        assert(toString(b).equals("c,d"));

        a = createArray("a","b","c","d");
        b = a.splice(0);
        assert(toString(a).equals(""));
        assert(toString(b).equals("a,b,c,d"));

        a = createArray("a","b","c","d");
        b = a.splice(3);
        assert(toString(a).equals("a,b,c"));
        assert(toString(b).equals("d"));

        a = createArray("a","b","c","d");
        b = a.splice(4);
        assert(toString(a).equals("a,b,c,d"));
        assert(toString(b).equals(""));

        a = createArray("a","b","c","d");
        b = a.splice(1, 2);

        assert(toString(a).equals("a,d"));
        assert(toString(b).equals("b,c"));

        a = createArray("a","b","c","d");
        b = a.splice(1, 2,"e","f");
        assert(toString(a).equals("a,e,f,d"));
        assert(toString(b).equals("b,c"));

        a = createArray("a","b","c","d");
        b = a.splice(0, 2,"e","f");
        assert(toString(a).equals("e,f,c,d"));
        assert(toString(b).equals("a,b"));

        a = createArray("a","b","c","d");
        b = a.splice(0, 0,"e","f");
        assert(toString(a).equals("e,f,a,b,c,d"));
        assert(toString(b).equals(""));

        a = createArray("a","b","c","d");
        b = a.splice(4, 0,"e","f");
        assert(toString(a).equals("a,b,c,d,e,f"));
        assert(toString(b).equals(""));

        /* shift */
        a = createArray("a","b","c");
        assert(a.shift().equals("a"));
        assert(toString(a).equals("b,c"));

        /* unshift */
        a = createArray("a","b","c");
        a.unshift("d");
        assert(toString(a).equals("d,a,b,c"));

        /* map */
        Array<Integer> intArray = createArray(1,2,3);

        b = intArray.map(Object::toString);
        assert(toString(b).equals("1,2,3"));

        b = intArray.map((current, index, array) -> current.toString());
        assert(toString(b).equals("1,2,3"));

        /* reduce */
        intArray = createArray(1,2,3);

        double accum = intArray.reduce((prev, current) -> prev + current, 0);
        assert(accum == 6);

        accum = intArray.reduce((prev, current) -> prev + current, 0);
        assert(accum == 6);

        accum = intArray.reduce((prev, current, index, array) -> prev + current, 0);
        assert(accum == 6);

        /* reduceRight */
        intArray = createArray(1,2,3,4);

        accum = intArray.reduceRight((prev, current) -> current - prev, 0);
        assert(accum == -2);

        accum = intArray.reduceRight((prev, current) -> current - prev, 0);
        assert(accum == -2);

        accum = intArray.reduceRight((prev, current, index, array) -> current - prev, 0);
        assert(accum == -2);

        /* create */
        a = ArrayFactory.create();
        a.push("100");
        a.push("200");
        assert(toString(a).equals("100,200"));

        //Standard Java Collection integration tests

        Array<String> a1 = createArray("a", "b", "c");
        StringBuilder test = new StringBuilder();

        for (String val : a1.asIterable()) {
            test.append(val);
        }
        assert (test.toString().equals("abc"));

        //Standard Java List support

        a1 = createArray("a", "b", "c");
        List<String> a1List = a1.asList();

        test = new StringBuilder();

        for (String val : a1List) {
            test.append(val);
        }
        assert (test.toString().equals("abc"));

        a1List.add("d");
        assert(toString(a1List).equals("a,b,c,d"));
        assert(toString(a1).equals("a,b,c,d"));

        a1List.remove(0);
        assert(toString(a1List).equals("b,c,d"));
        assert(toString(a1).equals("b,c,d"));

        assert(a1List.size() == 3);

        assert(a1List.contains("c"));

        assert(a1List.get(1).equals("c"));

        a1List.clear();
        assert(toString(a1List).equals(""));
        assert(toString(a1).equals(""));

        a1 = createArray("c","b","a");
        test = new StringBuilder();

        a1.stream().forEach(test::append);

        assert(test.toString().equals("cba"));
    }

    @SafeVarargs
    private static <T> Array<T> createArray(T ...values) {
        Array<T> o = ArrayFactory.create();

        for(T value : values) {
            o.push(value);
        }
        return o;
    }

    private static String toString(Array<String> a) {
        return a.join(",");
    }

    private static String toString(List<String> a) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(String s : a) {
            sb.append(s);

            if (i < a.size()-1) {
                sb.append(",");
            }
            i++;
        }
        return sb.toString();
    }
}
