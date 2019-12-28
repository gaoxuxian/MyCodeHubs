package com.xx.javademo.leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LC46 {

    private void backtracking(List<List<Integer>> result, List<Integer> input, int start, int end) {
        if (start == end) {
            result.add(new ArrayList<>(input));
        }
        for (int i = start; i < end; i++) {
            Collections.swap(input, start, i);
            backtracking(result, input, start + 1, end);
            Collections.swap(input, start, i);
        }
    }

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        ArrayList<Integer> numList = new ArrayList<>();
        for (int num : nums) {
            numList.add(num);
        }
        backtracking(result, numList, 0, nums.length);
        return result;
    }
}
