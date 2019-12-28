package com.xx.javademo.leetcode;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 合并 k 个排序链表，返回合并后的排序链表。请分析和描述算法的复杂度。
 *
 * 示例:
 *
 * 输入:
 * [
 *   1->4->5,
 *   1->3->4,
 *   2->6
 * ]
 * 输出: 1->1->2->3->4->4->5->6
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/merge-k-sorted-lists
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * 困难
 */
public class LC23 {
    /**
     * 解法1 -- 暴力解法 --- 执行时间 406 ms，击败 6% java
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists.length == 1) return lists[0];
        int length = lists.length;
        ListNode head = new ListNode(-1);
        ListNode pre = head;
        ListNode min = null;
        int minIndex;
        boolean breakLoop;
        do {
            breakLoop = true;
            minIndex = -1;
            for (int i = 0; i < length; i++) {
                ListNode node = lists[i];
                if (node != null) {
                    breakLoop = false;
                    if (min == null || min.val > node.val) {
                        min = node;
                        minIndex = i;
                    }
                }
            }
            if (minIndex >= 0) {
                ListNode node = lists[minIndex];
                lists[minIndex] = node.next;
            }
            pre.next = min;
            pre = min;
            min = null;
        } while (!breakLoop);

        return head.next;
    }

    /**
     * 解法2 -- 优先队列排序，执行时间 --- 6ms，击败 62.97% java
     */
    public ListNode mergeKListsV2(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        PriorityQueue<ListNode> queue = new PriorityQueue<>(lists.length, new Comparator<ListNode>() {
            @Override
            public int compare(ListNode o1, ListNode o2) {
                if (o1.val < o2.val) return -1;
                else if (o1.val > o2.val) return 1;
                return 0;
            }
        });

        for (ListNode node : lists) {
            if (node != null) {
                queue.add(node);
            }
        }

        ListNode head = new ListNode(0);
        ListNode pre = head;
        while (!queue.isEmpty()) {
            pre.next = queue.poll();
            pre = pre.next;
            if (pre != null && pre.next != null) queue.add(pre.next);
        }
        return head.next;
    }

    /**
     * 解法3 -- 分而治之 归并排序 ---- 执行时间 2ms，击败 100% Java
     */
    public ListNode mergeKListsV3(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        return merge(lists, 0, lists.length - 1);
    }

    private ListNode merge(ListNode[] listNodes, int left, int right) {
        if (left == right) return listNodes[left];
        int mid = (left + right) / 2;
        ListNode l1 = merge(listNodes, left, mid);
        ListNode l2 = merge(listNodes, mid + 1, right);
        return mergeTwoLists(l1, l2);
    }

    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;
        ListNode head = new ListNode(0);
        ListNode pre = head;
        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                pre.next = l1;
                l1 = l1.next;
            } else {
                pre.next = l2;
                l2 = l2.next;
            }
            pre = pre.next;
        }

        if (l1 != null) {
            pre.next = l1;
        }
        if (l2 != null) {
            pre.next = l2;
        }
        return head.next;
    }

     public static class ListNode {
         int val;
         public ListNode next;
         public ListNode(int x) { val = x; }
     }
}
