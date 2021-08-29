    package com.kidd.myapplication;

public class   ModelGroup {
    String groupId,groupTitle,groupDesc,groupIcon,timestamp,owner;

    public ModelGroup() {
    }

    public ModelGroup(String groupId, String groupTitle, String groupDesc, String groupIcon, String timestamp, String owner) {
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupDesc = groupDesc;
        this.groupIcon = groupIcon;
        this.timestamp = timestamp;
        this.owner = owner;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
