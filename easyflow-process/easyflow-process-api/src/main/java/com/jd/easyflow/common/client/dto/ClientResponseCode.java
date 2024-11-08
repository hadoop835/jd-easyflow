package com.jd.easyflow.common.client.dto;

/**
 * @author liyuliang5
 *
 */
public  enum ClientResponseCode {
    
    SUCCESS("0000000","Success"),

    FAIL("0000001","Fail"),

    DATA_EMPTY("0000004","Data not exists"),

    FIELD_EMPTY("0000005","Field empty"),

    INVALID("0000002","Validate fail"),

    EXCEPTION("9999999","System exception"),
    ;

    private String code;

    private String desc;

    ClientResponseCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
