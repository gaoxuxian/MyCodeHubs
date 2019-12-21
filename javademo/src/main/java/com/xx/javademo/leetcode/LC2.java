package com.xx.javademo.leetcode;

/**
 * 给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
 *
 * 如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
 *
 * 您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 *
 * 示例：
 *
 * 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
 * 输出：7 -> 0 -> 8
 * 原因：342 + 465 = 807
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/add-two-numbers
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * 中等
 */
public class LC2 {

    // Definition for singly-linked list.
    public class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    // 解法 1 -- 按链表位对应相加，大于10保留进制数到下一次计算
    // 需要注意 l1 = {5}, l2 = {5} 的特殊情况
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode out = null;
        ListNode node = null;
        int val = 0;
        while (l1 != null || l2 != null || val != 0) {
            int v1 = l1 != null ? l1.val : 0;
            int v2 = l2 != null ? l2.val : 0;
            ListNode last = node;
            node = new ListNode(0);
            if (last != null) {
                last.next = node;
            } else {
                out = node;
            }
            val = v1 + v2 + val;
            if (val >= 10) {
                node.val = val - 10;
                val = 1;
            } else {
                node.val = val;
                val = 0;
            }
            l1 = l1 != null ? l1.next : null;
            l2 = l2 != null ? l2.next : null;
        }
        return out;
    }
}
