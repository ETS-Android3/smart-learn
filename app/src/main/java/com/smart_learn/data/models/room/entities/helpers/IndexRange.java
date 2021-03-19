package com.smart_learn.data.models.room.entities.helpers;

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
