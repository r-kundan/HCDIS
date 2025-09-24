package com.app.harcdis.point_forward_flow.model;

public class ForwardedModel {

    String UID;
    String latitude;
    String longitude;
    String assigner_name;

    String assign_date;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAssigner_name() {
        return assigner_name;
    }

    public void setAssigner_name(String assigner_name) {
        this.assigner_name = assigner_name;
    }

    public String getAssign_date() {
        return assign_date;
    }

    public void setAssign_date(String assign_date) {
        this.assign_date = assign_date;
    }

    public ForwardedModel(String UID, String latitude, String longitude, String assigner_name, String assign_date) {
        this.UID = UID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.assigner_name = assigner_name;
        this.assign_date = assign_date;
    }
}
