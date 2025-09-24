package com.app.harcdis.model;

public class LatLongModels {
    String lati;
    String longi;

    public LatLongModels(String lati, String longi) {
        this.lati = lati;
        this.longi = longi;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }
}
