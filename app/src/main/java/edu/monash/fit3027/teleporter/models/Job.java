package edu.monash.fit3027.teleporter.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by niaz on 9/5/17.
 */

public class Job implements Parcelable{

    private double m_sStartLat;
    private double m_sEndLat;
    private double m_sStartLng;
    private double m_sEndLng;
    private String m_sItemName;
    private String m_sItemType;
    private String m_sDeliveryType;
    private String m_sRecipientName;
    private String userId;
    private double currentLat;
    private double currentLng;
    private String teleporterId;
    private String jobId;

    public Job(String jobId, String userId, double m_sStartLat, double m_sStartLng, double m_sEndLat, double m_sEndLng, String m_sRecipientName, String m_sItemName, String m_sItemType, String m_sDeliveryType, String m_sTeleporterID){

        this.jobId = jobId;
        this.userId = userId;
        this.m_sStartLat = m_sStartLat;
        this.m_sStartLng = m_sStartLng;
        this.m_sEndLat = m_sEndLat;
        this.m_sEndLng = m_sEndLng;
        this.m_sRecipientName = m_sRecipientName;
        this.m_sDeliveryType = m_sDeliveryType;
        this.m_sItemName = m_sItemName;
        this.m_sItemType = m_sItemType;
        this.currentLng = m_sStartLng;
        this.currentLat = m_sStartLat;
        this.teleporterId = m_sTeleporterID;

    }

    public Job(){

    }

    private Job(Parcel in){
        jobId = in.readString();
        userId = in.readString();
        m_sStartLat = in.readDouble();
        m_sStartLng = in.readDouble();
        m_sEndLat = in.readDouble();
        m_sEndLng = in.readDouble();
        m_sRecipientName = in.readString();
        m_sDeliveryType = in.readString();
        m_sItemName = in.readString();
        m_sItemType = in.readString();
        currentLat = in.readDouble();
        currentLng = in.readDouble();
        teleporterId = in.readString();
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jobId);
        dest.writeString(userId);
        dest.writeDouble(m_sStartLat);
        dest.writeDouble(m_sStartLng);
        dest.writeDouble(m_sEndLat);
        dest.writeDouble(m_sEndLng);
        dest.writeString(m_sRecipientName);
        dest.writeString(m_sDeliveryType);
        dest.writeString(m_sItemName);
        dest.writeString(m_sItemType);
        dest.writeDouble(currentLat);
        dest.writeDouble(currentLng);
        dest.writeString(teleporterId);
    }


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public double getM_sStartLat() {
        return m_sStartLat;
    }

    public void setM_sStartLat(double m_sStartLat) {
        this.m_sStartLat = m_sStartLat;
    }

    public double getM_sEndLat() {
        return m_sEndLat;
    }

    public void setM_sEndLat(double m_sEndLat) {
        this.m_sEndLat = m_sEndLat;
    }

    public double getM_sStartLng() {
        return m_sStartLng;
    }

    public void setM_sStartLng(double m_sStartLng) {
        this.m_sStartLng = m_sStartLng;
    }

    public double getM_sEndLng() {
        return m_sEndLng;
    }

    public void setM_sEndLng(double m_sEndLng) {
        this.m_sEndLng = m_sEndLng;
    }

    public String getM_sItemName() {
        return m_sItemName;
    }

    public void setM_sItemName(String m_sItemName) {
        this.m_sItemName = m_sItemName;
    }

    public String getM_sDeliveryType() {
        return m_sDeliveryType;
    }

    public void setM_sDeliveryType(String m_sDeliveryType) {
        this.m_sDeliveryType = m_sDeliveryType;
    }

    public String getM_sRecipientName() {
        return m_sRecipientName;
    }

    public void setM_sRecipientName(String m_sRecipientName) {
        this.m_sRecipientName = m_sRecipientName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }

    public String getTeleporterId() {
        return teleporterId;
    }

    public void setTeleporterId(String teleporterId) {
        this.teleporterId = teleporterId;
    }

    public String getM_sItemType() {
        return m_sItemType;
    }

    public void setM_sItemType(String m_sItemType) {
        this.m_sItemType = m_sItemType;
    }
}
