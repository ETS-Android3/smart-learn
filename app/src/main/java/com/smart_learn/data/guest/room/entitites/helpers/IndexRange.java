package com.smart_learn.data.guest.room.entitites.helpers;

import lombok.Getter;

@Getter
public class IndexRange {
    private final int start;
    private final int end;

    public IndexRange(int start, int end) {
        this.start = start;
        this.end = end;
    }
}
