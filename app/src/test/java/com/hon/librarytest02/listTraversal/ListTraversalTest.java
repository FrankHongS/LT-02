package com.hon.librarytest02.listTraversal;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuaihua on 2021/4/26 12:05 PM
 * Email: shuaihua@staff.sina.com.cn
 */
public class ListTraversalTest {

    @Test
    public void traversalList() {
        List<String> list = new ArrayList<>();
        list.add("Joey");
        list.add("Chandler");
        list.add("Ross");

        for (String str : list) {
            list.remove(0);
        }
        System.out.println(list);
//        System.out.println(~2);
    }

}
