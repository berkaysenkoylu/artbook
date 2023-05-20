package com.berkaysenkoylu.artbook;

import java.io.Serializable;

public class Art implements Serializable {
    int id;
    String artname;
    String painterName;
    String year;
    String imageUri;

    public Art(int id, String artname, String painterName, String imageUri, String year) {
        this.id = id;
        this.artname = artname;
        this.painterName = painterName;
        this.imageUri = imageUri;
        this.year = year;
    }
}
