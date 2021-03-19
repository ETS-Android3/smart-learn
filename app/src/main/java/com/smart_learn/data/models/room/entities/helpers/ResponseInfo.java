package com.smart_learn.data.models.room.entities.helpers;

import lombok.Getter;

@Getter
public class ResponseInfo {
    private final boolean isOk;
    private final String info;

    public ResponseInfo(boolean isOk, String info) {
        this.isOk = isOk;
        this.info = info;
    }
}
