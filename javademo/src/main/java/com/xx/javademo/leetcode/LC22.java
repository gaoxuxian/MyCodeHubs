package com.xx.javademo.leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LC22 {
    private Stack<Character> stack = new Stack<>();
    private int count = 0;
    private void backtracking(List<String> result, char[] arr, int start, int end) {
        if (start < end) {
            if (stack.size() + 1 <= end/2 && count + 1 <= end/2) {
                arr[start] = '(';
                stack.push('(');
                count++;
                backtracking(result, arr, start + 1, end);
                arr[start] = ' ';
                if (!stack.empty()) {
                    stack.pop();
                }
                count--;
            }

            if (!stack.empty()) {
                stack.pop();
                arr[start] = ')';
                backtracking(result, arr, start + 1, end);
                arr[start] = ' ';
                stack.push('(');
            }
        }

        if (stack.empty() && start == end) {
            StringBuilder builder = new StringBuilder();
            for (char str : arr) {
                builder.append(str);
            }
            result.add(builder.toString());
        }
    }

    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        char[] arr = new char[2*n];
        backtracking(result, arr, 0, 2*n);
        return result;
    }
}
