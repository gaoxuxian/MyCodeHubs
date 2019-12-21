package com.xx.javademo.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 给定一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？找出所有满足条件且不重复的三元组。
 *
 * 注意：答案中不可以包含重复的三元组。
 *
 * 例如, 给定数组 nums = [-1, 0, 1, 2, -1, -4]，
 *
 * 满足要求的三元组集合为：
 * [
 *   [-1, 0, 1],
 *   [-1, -1, 2]
 * ]
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/3sum
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * 中等 --- 去重没有思路
 */
public class LC15 {
//    public List<List<Integer>> threeSum(int[] nums) {
//        int length = nums.length;
//        List<List<Integer>> out = new LinkedList<>();
//        for (int i = 0; i < length; i++) {
//            for (int j = i + 1; j < length; j++) {
//                for (int k = j + 1; k < length; k++) {
//                    if (nums[i] + nums[j] + nums[k] == 0) {
//                        ArrayList<Integer> result = new ArrayList<>();
//                        result.add(nums[i]);
//                        result.add(nums[j]);
//                        result.add(nums[k]);
//                        out.add(result);
//                    }
//                }
//            }
//        }
//        return out;
//      }

    // 解法 --- 网上大神的解法
    /**
     * 算法流程：
     * 特判，对于数组长度 nn，如果数组为 nullnull 或者数组长度小于 33，返回 [][]。
     * 对数组进行排序。
     * 遍历排序后数组：
     * 若 nums[i]>0nums[i]>0：因为已经排序好，所以后面不可能有三个数加和等于 00，直接返回结果。
     * 对于重复元素：跳过，避免出现重复解
     * 令左指针 L=i+1L=i+1，右指针 R=n-1R=n−1，当 L<RL<R 时，执行循环：
     * 当 nums[i]+nums[L]+nums[R]==0nums[i]+nums[L]+nums[R]==0，执行循环，判断左界和右界是否和下一位置重复，去除重复解。并同时将 L,RL,R 移到下一位置，寻找新的解
     * 若和大于 00，说明 nums[R]nums[R] 太大，RR 左移
     * 若和小于 00，说明 nums[L]nums[L] 太小，LL 右移
     * 复杂度分析
     * 时间复杂度：O\left(n^{2}\right)O(n
     * 2
     *  )，数组排序 O(N \log N)O(NlogN)，遍历数组 O\left(n\right)O(n)，双指针遍历 O\left(n\right)O(n)，总体 O(N \log N)+O\left(n\right)*O\left(n\right)O(NlogN)+O(n)∗O(n)，O\left(n^{2}\right)O(n
     * 2
     *  )
     * 空间复杂度：O(1)O(1)
     *
     * 作者：zhu_shi_fu
     * 链接：https://leetcode-cn.com/problems/3sum/solution/pai-xu-shuang-zhi-zhen-zhu-xing-jie-shi-python3-by/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     * @param nums
     * @return
     */
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 3) return result;

        // 从小到大排序
        Arrays.sort(nums);
        int length = nums.length;
        if (nums[0] > 0 || nums[length - 1] < 0) return result;
        int left , right;
        for (int i = 0; i < length; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                 continue;
            }
            left = i + 1;
            right = length - 1;
            while (left < right) {
                if (nums[i] + nums[left] + nums[right] == 0) {
                    ArrayList<Integer> sum = new ArrayList<>();
                    sum.add(nums[i]);
                    sum.add(nums[left]);
                    sum.add(nums[right]);
                    result.add(sum);
                    while (left < right && nums[left] == nums[left + 1]) {
                        left += 1;
                    }
                    while (left < right && nums[right] == nums[right - 1]) {
                        right -= 1;
                    }
                    left += 1;
                    right -= 1;
                } else if (nums[i] + nums[left] + nums[right] > 0) {
                    right -= 1;
                } else {
                    left += 1;
                }
            }
        }

        return result;
    }
}
