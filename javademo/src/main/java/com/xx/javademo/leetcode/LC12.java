package com.xx.javademo.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 罗马数字包含以下七种字符： I， V， X， L，C，D 和 M。
 *
 * 字符          数值
 * I             1
 * V             5
 * X             10
 * L             50
 * C             100
 * D             500
 * M             1000
 * 例如， 罗马数字 2 写做 II ，即为两个并列的 1。12 写做 XII ，即为 X + II 。 27 写做  XXVII, 即为 XX + V + II 。
 *
 * 通常情况下，罗马数字中小的数字在大的数字的右边。但也存在特例，例如 4 不写做 IIII，而是 IV。数字 1 在数字 5 的左边，所表示的数等于大数 5 减小数 1 得到的数值 4 。同样地，数字 9 表示为 IX。这个特殊的规则只适用于以下六种情况：
 *
 * I 可以放在 V (5) 和 X (10) 的左边，来表示 4 和 9。
 * X 可以放在 L (50) 和 C (100) 的左边，来表示 40 和 90。 
 * C 可以放在 D (500) 和 M (1000) 的左边，来表示 400 和 900。
 * 给定一个整数，将其转为罗马数字。输入确保在 1 到 3999 的范围内。
 *
 * 示例 1:
 *
 * 输入: 3
 * 输出: "III"
 * 示例 2:
 *
 * 输入: 4
 * 输出: "IV"
 * 示例 3:
 *
 * 输入: 9
 * 输出: "IX"
 * 示例 4:
 *
 * 输入: 58
 * 输出: "LVIII"
 * 解释: L = 50, V = 5, III = 3.
 * 示例 5:
 *
 * 输入: 1994
 * 输出: "MCMXCIV"
 * 解释: M = 1000, CM = 900, XC = 90, IV = 4.
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/integer-to-roman
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * 中等
 */
public class LC12 {
    // 解法 1 --- 自己想的解法，但速度15ms只战胜 LeetCode 8%
//    public String intToRoman(int num) {
//        // 先映射特殊的罗马符号
//        HashMap<Integer, String> romanMap = new HashMap<>();
//        romanMap.put(1, "I");
//        romanMap.put(4, "IV");
//        romanMap.put(5, "V");
//        romanMap.put(9, "IX");
//        romanMap.put(10, "X");
//        romanMap.put(40, "XL");
//        romanMap.put(50, "L");
//        romanMap.put(90, "XC");
//        romanMap.put(100, "C");
//        romanMap.put(400, "CD");
//        romanMap.put(500, "D");
//        romanMap.put(900, "CM");
//        romanMap.put(1000, "M");
//
//        StringBuilder stringBuilder = new StringBuilder();
//        while (num != 0) {
//            // 计算出当前 num 的幂是多少
//            int power = String.valueOf(num).length() - 1;
//            double pow = Math.pow(10, power);
//            // 计算当前 num 最高位的值pop
//            int pop = (int) (num / pow);
//            int loopTime = 0;
//            // 尝试去匹配 pop
//            if (romanMap.containsKey((int) (pop * pow))) {
//                stringBuilder.append(romanMap.get((int) (pop * pow)));
//            } else {
//                // 如果匹配不上，证明pop 在 1 - 4 或 6 - 8 之间
//                if (pop < 5) {
//                    loopTime = pop;
//                } else if (pop > 5) {
//                    loopTime = pop - 5;
//                    stringBuilder.append(romanMap.get((int) (5 * pow)));
//                }
//            }
//            // 根据当前最少单位填充罗马字符
//            while (loopTime > 0) {
//                if (romanMap.containsKey((int) pow)) {
//                    stringBuilder.append(romanMap.get((int) pow));
//                }
//                loopTime--;
//            }
//            // 计算下一位
//            num -= (pop * pow);
//        }
//        return stringBuilder.toString();
//    }

    /**
     * 解法 2 ---- LeetCode 上大神提供的 贪心解法
     *
     * 参考大佬们的思路 吃透之后 写出来
     * 贪心算法 我永远用最接近的去做比较
     *
     * 如果我去小卖部买55元的东西
     *
     * 你可以选择一张面值50的 和一张5块的
     * 也可以给一张100的让老板找零
     * 贪心算法就是前者
     *
     * 假定我买3块的东西 我先用5块去比较 太多了 老板问 你还有小点的纸币没 我找不开
     * 这时候 你给个两块 还差一块 又给了一块
     * 看着很蠢 但是这确实有效
     *
     * 作者：guo-tang-feng
     * 链接：https://leetcode-cn.com/problems/integer-to-roman/solution/tan-xin-suan-fa-by-guo-tang-feng/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public String intToRoman(int num) {
        // 也是映射罗马符号，当同时是为了方便做减法运算
        int[] moneys = new int[] {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] moneyToStr = new String[] {"M", "CM", "D","CD","C","XC","L","XL","X", "IX", "V", "IV", "I" };

        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        while (num != 0) {
            // 匹配当前 num 最大位是什么范围，1000 或者 500 或者 400 或者 100
            if (num >= moneys[index]) {
                // 匹配上就减去这个范围量
                num -= moneys[index];
                stringBuilder.append(moneyToStr[index]);
                index--;
            }
            // 每循环一次就找下一个index，所以上面如果减掉部分金额，需要index--，再进来判断一次是否大于之前的金额
            index++;
        }
        return stringBuilder.toString();
    }
}
