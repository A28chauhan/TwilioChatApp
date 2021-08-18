package com.carematix.twiliochatapp.bean.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public
class DroLanguage {
    @Expose
    @SerializedName("code")
    private String code;

    @Expose
    @SerializedName("desc")
    private String desc;

    @Expose
    @SerializedName("id")
    private Integer id;

    public DroLanguage(Integer paramInteger, String paramString1, String paramString2) {
        this.id = paramInteger;
        this.code = paramString1;
        this.desc = paramString2;
    }

    public DroLanguage(String paramString) {
        this.desc = paramString;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public Integer getId() {
        return this.id;
    }

    public void setCode(String paramString) {
        this.code = paramString;
    }

    public void setDesc(String paramString) {
        this.desc = paramString;
    }

    public void setId(Integer paramInteger) {
        this.id = paramInteger;
    }



}
