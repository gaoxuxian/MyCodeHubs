package com.xx.javademo.leetcode;

public class LC28 {
    /**
     * 解法1 --- 直接调用String 的api求解，执行时间 0ms，击败100% Java
     */
    public int strStr(String haystack, String needle) {
        return haystack.indexOf(needle);
    }

    /**
     * 解法2 --- 暴力解法 --- 借鉴 LC3 最大不重复子串的想法
     *
     * 时间复杂度 O(m*n)，m是haystack子串的遍历次数，n是needle的长度
     */
    public int strStrV2(String haystack, String needle) {
        if (needle == null || needle.equals("")) return 0;
        if (haystack == null || needle.length() > haystack.length()) return -1;

        int hLen = haystack.length();
        int nLen = needle.length();

        int result = -1;
        int hIndex = 0, nIndex = 0;
        while (hIndex < hLen) {
            char h = haystack.charAt(hIndex);
            if (nIndex < nLen) {
                if ((hLen - 1 - hIndex) < (nLen - 1 - nIndex)) {
                    return -1;
                }
                char n = needle.charAt(nIndex);
                if (h == n) {
                    if (result == -1) {
                        result = hIndex;
                    }
                    hIndex++;
                    nIndex++;
                } else {
                    if (result == -1) {
                        hIndex++;
                    } else {
                        // 如果当前不相等，就从上一次相等的地方再比较一次
                        hIndex = result + 1;
                    }
                    result = -1;
                    nIndex = 0;
                }
            } else {
                break;
            }
        }

        return result;
    }
}
