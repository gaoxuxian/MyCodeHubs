package com.xx.javademo.leetcode;

import java.util.HashMap;

/**
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
 *
 * 示例 1:
 *
 * 输入: "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 * 示例 2:
 *
 * 输入: "bbbbb"
 * 输出: 1
 * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
 * 示例 3:
 *
 * 输入: "pwwkew"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
 *      请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-substring-without-repeating-characters
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * 中等
 */
public class LC3 {
    // 解法 1 --- 自己想的

    /**
     * 利用双指针i\j遍历，假设 s == "abcbbcab"
     *
     * 一开始 i = 0，j = 1，当j = 3时，才出现第一次重复子串，此时不重复子串最大长度应该是3，
     * 同时，j=3时出现重复，也就是说j=3的这个元素重复了，那么只要将i移动到 和j=3元素发生重复的那个元素下标+1，就解决了重复问题，以上例子，也就是将i移动到2(1+1)
     * 以此循环，直至 i\j 任意一方到达字符串尾部
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        int length = s.length();
        HashMap<Integer, Integer> map = new HashMap<>();
        int count = 0;
        int out = 0;
        int i= 0, j = -1;
        while (i <= length - 1 && j < length - 1) {
            j++;
            int key = s.charAt(j);
            if (map.containsKey(key)) {
                Integer index = map.get(key);
                i = Math.max(index + 1, i);
                count = j - i + 1;
                map.put(key, j);
            } else {
                map.put(key, j);
                count++;
            }
            out = Math.max(out, count);
        }

        return out;
    }
}
