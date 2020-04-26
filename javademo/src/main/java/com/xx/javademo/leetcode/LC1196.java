package com.xx.javademo.leetcode;

import com.xx.javademo.R;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

public class LC1196 {
    class FizzBuzz {
        private int n;
        private Semaphore numSemaphore = new Semaphore(1);
        private Semaphore fizzSemaphore = new Semaphore(0);
        private Semaphore buzzSemaphore = new Semaphore(0);
        private Semaphore fizzbuzzSemaphore = new Semaphore(0);

        public FizzBuzz(int n) {
            this.n = n;
        }

        // printFizz.run() outputs "fizz".
        public void fizz(Runnable printFizz) throws InterruptedException {
            for (int i = 3; i <= n; i+=3) {
                if (i % 5 != 0) {
                    fizzSemaphore.acquire();
                    printFizz.run();
                    numSemaphore.release();
                }
            }
        }

        // printBuzz.run() outputs "buzz".
        public void buzz(Runnable printBuzz) throws InterruptedException {
            for (int i = 5; i <= n; i+=5) {
                if (i % 3 != 0) {
                    buzzSemaphore.acquire();
                    printBuzz.run();
                    numSemaphore.release();
                }
            }
        }

        // printFizzBuzz.run() outputs "fizzbuzz".
        public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
            for (int i = 15; i <= n; i+=15) {
                fizzbuzzSemaphore.acquire();
                printFizzBuzz.run();
                numSemaphore.release();
            }
        }

        // printNumber.accept(x) outputs "x", where x is an integer.
        public void number(IntConsumer printNumber) throws InterruptedException {
            for (int i = 1; i <= n; i++) {
                numSemaphore.acquire();
                if (i % 3 == 0 && i % 5 == 0) {
                    fizzbuzzSemaphore.release();
                } else if (i % 3 == 0) {
                    fizzSemaphore.release();
                } else if (i % 5 == 0) {
                    buzzSemaphore.release();
                } else {
                    printNumber.accept(i);
                    numSemaphore.release();
                }
            }
        }
    }
}
