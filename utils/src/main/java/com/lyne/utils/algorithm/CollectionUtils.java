package com.lyne.utils.algorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by liht on 2017/12/28.
 */

public class CollectionUtils {

    public static boolean isEmpty(Collection c){
        return c == null || c.isEmpty();
    }

    public static <T> Collection<T> deepCopy(Collection<T> src) throws IOException, ClassNotFoundException {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        Collection<T> dest = (Collection<T>) in.readObject();
        return dest;
    }

    /**
     * 必须实现T's equal
     * @param l1
     * @param l2
     * @param <T>
     * @return
     */
    public static <T> boolean isEqual(List<T> l1, List<T> l2){
        if (isEmpty(l1) && isEmpty(l2)){
            return true;
        }

        if (isEmpty(l1) || isEmpty(l2)){
            return false;
        }

        if (l1.size() != l2.size()){
            return false;
        }

        List<T> retainList = new ArrayList<>(l2);
        retainList.retainAll(l1);
        return retainList.size() == l1.size();
    }
}
