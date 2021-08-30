package com.kidd.myapplication;

public class ModelPost {

String pId, pTitle,pDescription,pTime, uid, uName,uEmail,pImage,uDp,groupId,groupTitle,groupIcon,pLike,Shared,ShareTo,ShareName,ShareDp;

    public ModelPost() {
    }

    public ModelPost(String pId, String pTitle, String pDescription, String pTime,
                     String uid, String uName, String uEmail, String pImage, String uDp,
                     String groupId, String groupTitle, String groupIcon,String pLike,String Shared,
                     String ShareTo, String ShareName,String ShareDp) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescription = pDescription;
        this.pTime = pTime;
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.pImage = pImage;
        this.uDp = uDp;
        this.groupId = groupId;
        this.groupTitle = groupTitle;
        this.groupIcon = groupIcon;
        this.Shared = Shared;
        this.ShareTo = ShareTo;
        this.ShareName = ShareName;
        this.ShareDp = ShareDp;
    }

    public String getShareName() {
        return ShareName;
    }

    public void setShareName(String shareName) {
        ShareName = shareName;
    }

    public String getShareDp() {
        return ShareDp;
    }

    public void setShareDp(String shareDp) {
        ShareDp = shareDp;
    }

    public String getShareTo() {
        return ShareTo;
    }

    public void setShareTo(String shareTo) {
        ShareTo = shareTo;
    }

    public String getShared() {
        return Shared;
    }

    public void setShared(String shared) {
        Shared = shared;
    }

    public String getpLike() {
        return pLike;
    }

    public void setpLike(String pLike) {
        this.pLike = pLike;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
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

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }
}
