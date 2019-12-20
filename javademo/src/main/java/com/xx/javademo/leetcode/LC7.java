package com.xx.javademo.leetcode;

/**
 * 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
 *
 * 示例 1:
 *
 * 输入: 123
 * 输出: 321
 *  示例 2:
 *
 * 输入: -123
 * 输出: -321
 * 示例 3:
 *
 * 输入: 120
 * 输出: 21
 * 注意:
 *
 * 假设我们的环境只能存储得下 32 位的有符号整数，则其数值范围为 [−231,  231 − 1]。请根据这个假设，如果反转后整数溢出那么就返回 0。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/reverse-integer
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * 简单
 */
public class LC7 {
    // 解法 1 ---- int 转 String
//    public int reverse(int x) {
//        if (x == 0) return 0;
//        int sign = 1; // 符号位
//        if (x < 0) {
//            sign = -1;
//        }
//
//        String sx = String.valueOf(Math.abs(x));
//        int length = sx.length();
//
//        // 求反
//        boolean canBeZero = false;
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = length - 1; i >= 0; i--) {
//            char charAt = sx.charAt(i);
//            if (!canBeZero && charAt > '0') {
//                canBeZero = true;
//            }
//            if (canBeZero && charAt >= '0' && charAt <= '9') {
//                stringBuilder.append(charAt);
//            }
//        }
//
//        String s = stringBuilder.toString();
//        int sLength = s.length();
//        if (sLength == 1) {
//            return sign * Integer.valueOf(s);
//        } else if (sign == -1 && sLength < length - 1) {
//            return sign * Integer.valueOf(s);
//        } else if (sign == 1 && sLength < length) {
//            return sign * Integer.valueOf(s);
//        } else {
//            Integer firstHalf = Integer.valueOf(s.substring(0, length / 2));
//            Integer secondHalf = Integer.valueOf(s.substring(length / 2));
//
//            if (sign == 1) {
//                String max = String.valueOf(Integer.MAX_VALUE);
//                int firstMax = Integer.valueOf(max.substring(0, max.length() / 2));
//                int secondMax = Integer.valueOf(max.substring(max.length() / 2));
//                if (firstHalf <= firstMax && secondHalf <= secondMax) {
//                    return sign * Integer.valueOf(s);
//                }
//            } else {
//                String min = String.valueOf(Integer.MIN_VALUE);
//                int firstMax = Integer.valueOf(min.substring(1, min.length() / 2 + 1));
//                int secondMax = Integer.valueOf(min.substring(min.length() / 2 + 1));
//                if (firstHalf <= firstMax && secondHalf <= secondMax) {
//                    return sign * Integer.valueOf(s);
//                }
//            }
//        }
//        return 0;
//    }

    // 解法 2 --- 根据max == 2147483647, min == -2147483648 的规则
    public int reverse(int x) {
        int rev = 0;
        while (x != 0) {
            int pop = x % 10;
            x /= 10;
            if (rev > Integer.MAX_VALUE / 10 || (rev == Integer.MAX_VALUE / 10 && pop > 7)) return 0;
            if (rev < Integer.MIN_VALUE / 10 || (rev == Integer.MIN_VALUE / 10 && pop < -8)) return 0;
            rev = rev * 10 + pop;
        }
        return rev;
    }
}
