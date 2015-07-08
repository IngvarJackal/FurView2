package ru.furry.furview2.system;

public class AsyncCounter {
    private int value;
    private int curValue;
    private int maxWeight = 1;
    private int curWeight = 0;

    public AsyncCounter(int initval, int weight) {
        this.value = initval;
        this.curValue = initval;
        this.maxWeight = weight;
    }

    public synchronized void reset() {
        this.curValue = value;
    }

    public synchronized int getVal() {
        return this.curValue;
    }

    public synchronized void increase() {
        this.curWeight += 1;
        if (this.curWeight == maxWeight) {
            this.curValue += 1;
            this.curWeight = 0;
        }
    }

}
