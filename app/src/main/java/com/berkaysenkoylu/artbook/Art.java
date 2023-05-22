package com.berkaysenkoylu.artbook;

import java.io.Serializable;

public class Art implements Serializable {
    int id;
    String artname;
    String painterName;
    String year;
    byte[] imageBlob;

    public Art(int id, String artname, String painterName, byte[] imageBlob, String year) {
        this.id = id;
        this.artname = artname;
        this.painterName = painterName;
        this.imageBlob = imageBlob;
        this.year = year;
    }
}
