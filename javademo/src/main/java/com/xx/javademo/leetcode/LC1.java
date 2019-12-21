package com.xx.javademo.leetcode;

import java.util.HashMap;

/**
 * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 * <p>
 * 你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。
 * <p>
 * <p>
 * 给定 nums = [2, 7, 11, 15], target = 9
 * <p>
 * 因为 nums[0] + nums[1] = 2 + 7 = 9
 * 所以返回 [0, 1]
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/two-sum
 * <p>
 * 简单
 */
public class LC1 {
    // 解法 1 -- 暴力解法 时间复杂度 O(n*n)
    public int[] twoSum(int[] nums, int target) {
        int length = nums.length;
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    // 解法 2 -- 假设数组内元素都不会重复出现
//    public int[] twoSum(int[] nums, int target) {
//        HashMap<Integer, Integer> map = new HashMap<>();
//        // 先将元素放进map
//        int length = nums.length;
//        for (int i = 0; i < length; i++) {
//            map.put(nums[i], i);
//        }
//        for (int i = 0; i < length; i++) {
//            int key = target - nums[i];
//            if (map.containsKey(key)) {
//                return new int[]{i, map.get(key)};
//            }
//        }
//        return null;
//    }

    // 解法 3 -- 2 的优化版，一次遍历
//    public int[] twoSum(int[] nums, int target) {
//        HashMap<Integer, Integer> map = new HashMap<>();
//        // 先将元素放进map
//        int length = nums.length;
//        for (int i = 0; i < length; i++) {
//            if (map.containsKey(nums[i])) {
//                // 由于前面已经将差值放入map，所以先return map记录的下标
//                return new int[]{map.get(nums[i]), i};
//            }
//            // 将差值放入map中，等待后面遍历判断
//            map.put(target - nums[i], i);
//        }
//        return null;
//    }
}
