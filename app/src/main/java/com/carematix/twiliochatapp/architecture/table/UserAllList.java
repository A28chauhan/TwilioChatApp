package com.carematix.twiliochatapp.architecture.table;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.carematix.twiliochatapp.architecture.converter.LinksTypeConverter;
import com.carematix.twiliochatapp.architecture.table.beans.Links;

import java.util.List;

@Entity(tableName = "User_List")
public
class UserAllList {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "droUserId")
    private int droUserId;

    @ColumnInfo(name = "droUserRoleId")
    private int droUserRoleId;

    @ColumnInfo(name = "firstName")
    private String firstName;
    @ColumnInfo(name = "lastName")
    private String lastName;
    @ColumnInfo(name = "droProgramUserId")
    private int droProgramUserId;

    public UserAllList(int droUserId, int droUserRoleId, String firstName, String lastName, int droProgramUserId) {
        this.droUserId = droUserId;
        this.droUserRoleId = droUserRoleId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.droProgramUserId = droProgramUserId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDroUserId() {
        return droUserId;
    }

    public void setDroUserId(int droUserId) {
        this.droUserId = droUserId;
    }

    public int getDroUserRoleId() {
        return droUserRoleId;
    }

    public void setDroUserRoleId(int droUserRoleId) {
        this.droUserRoleId = droUserRoleId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getDroProgramUserId() {
        return droProgramUserId;
    }

    public void setDroProgramUserId(int droProgramUserId) {
        this.droProgramUserId = droProgramUserId;
    }

    /*@ColumnInfo(name = "droUserId")
    private String sid;

    @ColumnInfo(name = "attributes")
    private String attributes;

    @ColumnInfo(name = "identity")
    private String identity;

    @ColumnInfo(name = "links")
    @TypeConverters(LinksTypeConverter.class)
    private List<Links> links;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "accountSid")
    private String accountSid;

    @ColumnInfo(name = "serviceSid")
    private String serviceSid;

    @ColumnInfo(name = "friendlyName")
    private String friendlyName;

    @ColumnInfo(name = "roleSid")
    private String roleSid;

    @ColumnInfo(name = "isOnline")
    private String isOnline;

    @ColumnInfo(name = "isNotifiable")
    private String isNotifiable;

    @ColumnInfo(name = "dateCreated")
    private String dateCreated;

    @ColumnInfo(name = "dateUpdated")
    private String dateUpdated;

    @ColumnInfo(name = "joinedChannelsCount")
    private Integer joinedChannelsCount;

    public UserAllList(String sid, String attributes, String identity, List<Links> links, String url, String accountSid, String serviceSid, String friendlyName, String roleSid, String isOnline, String isNotifiable, String dateCreated, String dateUpdated, Integer joinedChannelsCount) {
        this.sid = sid;
        this.attributes = attributes;
        this.identity = identity;
        this.links = links;
        this.url = url;
        this.accountSid = accountSid;
        this.serviceSid = serviceSid;
        this.friendlyName = friendlyName;
        this.roleSid = roleSid;
        this.isOnline = isOnline;
        this.isNotifiable = isNotifiable;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.joinedChannelsCount = joinedChannelsCount;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public List<Links> getLinks() {
        return links;
    }

    public void setLinks(List<Links> links) {
        this.links = links;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getServiceSid() {
        return serviceSid;
    }

    public void setServiceSid(String serviceSid) {
        this.serviceSid = serviceSid;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getRoleSid() {
        return roleSid;
    }

    public void setRoleSid(String roleSid) {
        this.roleSid = roleSid;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getIsNotifiable() {
        return isNotifiable;
    }

    public void setIsNotifiable(String isNotifiable) {
        this.isNotifiable = isNotifiable;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Integer getJoinedChannelsCount() {
        return joinedChannelsCount;
    }

    public void setJoinedChannelsCount(Integer joinedChannelsCount) {
        this.joinedChannelsCount = joinedChannelsCount;
    }*/
}
