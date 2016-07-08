package io.github.psychesworld.mappergenerator.annotations.mapper;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractMapper<T1, T2> {
    protected AbstractMapper() {
    }

    public abstract T2 map(T1 t1);

//    public abstract T1 reverseMap(T2 t2);

    public Collection<T2> map(Collection<T1> t1Collection) {
        ArrayList<T2> t2Collection = new ArrayList<>();
        for (T1 t1 : t1Collection) {
            t2Collection.add(map(t1));
        }
        return t2Collection;
    }
//
//    public Collection<T1> reverseMap(Collection<T2> t2Collection) {
//        ArrayList<T1> t1Collection = new ArrayList<>();
//        for (T2 t2 : t2Collection) {
//            t1Collection.add(reverseMap(t2));
//        }
//        return t1Collection;
//    }
}
