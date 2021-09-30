package com.smart_learn.core.helpers;

import lombok.Getter;

/**
 * Class which uses builder design pattern
 *
 * https://howtodoinjava.com/design-patterns/creational/builder-pattern-in-java/
 * */
public class ResponseInfo {

    @Getter
    private final boolean isOk;
    @Getter
    private final String info;
    @Getter
    private final int code;

    private ResponseInfo(Builder builder) {
        this.isOk = builder.isOk;
        this.info = builder.info;
        this.code = builder.code;
    }

    public static class Builder {
        private boolean isOk;
        private String info;
        private int code;

        public Builder() {}

        public Builder setIsOk(boolean isOk){
            this.isOk = isOk;
            return this;
        }

        public Builder setInfo(String info){
            this.info = info;
            return this;
        }

        public Builder setCode(int code){
            this.code = code;
            return this;
        }

        public ResponseInfo build(){
            ResponseInfo responseInfo = new ResponseInfo(this);
            validateObject(responseInfo);
            return responseInfo;
        }

        private void validateObject(ResponseInfo responseInfo) {
            //Do some basic validations to check
            //if user object does not break any assumption of system
        }
    }

}
