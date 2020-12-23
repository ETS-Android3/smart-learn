package com.smart_learn.core;

import lombok.Getter;
import lombok.Setter;

public class Pair<K,V> {

    @Getter @Setter
    private K first;

    @Getter @Setter
    private V second;

    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }
}