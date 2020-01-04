package com.xx.javademo;

import com.xx.javademo.leetcode.LC15;
import com.xx.javademo.leetcode.LC20;
import com.xx.javademo.leetcode.LC22;
import com.xx.javademo.leetcode.LC23;
import com.xx.javademo.leetcode.LC28;
import com.xx.javademo.leetcode.LC46;
import com.xx.javademo.store.SortUtil;

import java.util.Arrays;

public class Enter {

    public static void FindNumsAppearOnce(int[] nums, int num1[], int num2[]) {
        int diff = 0;
        for (int num : nums)
            diff ^= num;
        diff &= -diff;
        for (int num : nums) {
            if ((num & diff) == 0)
                num1[0] ^= num;
            else
                num2[0] ^= num;
        }
    }

    public static void main(String[] args) {
//        LC20 lc20 = new LC20();
//        System.out.println(lc20.isValid("()[]{}"));
//        SortUtil.RadixSort(new int[]{3, 44, 38, 5, 47, 15, 36, 26, 27, 2, 46, 4, 19, 50, 48});
//        int[] ints = new int[]{4, 2, 1, 5, 3, 2, 7, 9, 4};
//        SortUtil.QuitSort(ints, 0, ints.length - 1);
//        SortUtil.CountingSort(ints);
//        System.out.println(ints);

//        LC23 lc23 = new LC23();
//
//        LC23.ListNode[] list = new LC23.ListNode[3];
//        LC23.ListNode node = new LC23.ListNode(1);
//        list[0] = node;
//        node.next = new LC23.ListNode(4);
//        node = node.next;
//        node.next = new LC23.ListNode(5);
//
//        node = new LC23.ListNode(1);
//        list[1] = node;
//        node.next = new LC23.ListNode(3);
//        node = node.next;
//        node.next = new LC23.ListNode(4);
//
//        node = new LC23.ListNode(2);
//        list[2] = node;
//        node.next = new LC23.ListNode(6);
//
//        lc23.mergeKLists(list);

//        LC28 lc28 = new LC28();
//        lc28.strStrV2("mississippi", "issip");

//        LC46 lc46 = new LC46();
//        lc46.permute(new int[]{1, 2, 3});

//        LC22 lc22 = new LC22();
//        lc22.generateParenthesis(3);

        FindNumsAppearOnce(new int[]{11,22,11,23,23,45}, new int[1], new int[1]);
    }
}
