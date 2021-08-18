package com.carematix.twiliochatapp.bean.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProgramInfo implements Serializable {

    @SerializedName("programId")
    @Expose
    long programId ;

    @SerializedName("organizationName")
    @Expose
    String organizationName;

    @SerializedName("programName")
    @Expose
    String programName;

    @SerializedName("logoUrl")
    @Expose
    String logoUrl;

    public long getProgramId() {
        return programId;
    }

    public void setProgramId(long programId) {
        this.programId = programId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
