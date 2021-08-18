package com.carematix.twiliochatapp.bean;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public
class User {

    @SerializedName("userName")
    @Expose
    public String name;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("language")
    @Expose
    public String language;
    @SerializedName("timezone")
    @Expose
    public String timeZone;
    @SerializedName("organizationProgramId")
    @Expose
    public String organizationProgramId;

    @SerializedName("programUserId")
    @Expose
    public String programUserId;

    @SerializedName("userSurveySessionId")
    @Expose
    public String userSurveySessionId;


    @SerializedName("source")
    @Expose
    public String source;

    //dnToken
    @SerializedName("dnToken")
    @Expose
    public String dnToken;

    public User(String userName , String password, String language, String timeZone, String source,String dnToken){
        this.name = userName;
        this.password = password;
        this.language = language;
        this.timeZone = timeZone;
        this.source = source;
        this.dnToken = dnToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getOrganizationProgramId() {
        return organizationProgramId;
    }

    public void setOrganizationProgramId(String organizationProgramId) {
        this.organizationProgramId = organizationProgramId;
    }

    public String getProgramUserId() {
        return programUserId;
    }

    public void setProgramUserId(String programUserId) {
        this.programUserId = programUserId;
    }

    public String getUserSurveySessionId() {
        return userSurveySessionId;
    }

    public void setUserSurveySessionId(String userSurveySessionId) {
        this.userSurveySessionId = userSurveySessionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDnToken() {
        return dnToken;
    }

    public void setDnToken(String dnToken) {
        this.dnToken = dnToken;
    }
}
