package com.example.text_recognition_app;

public class Medicine {
    String id;
    String name;
    String genName;
    String sideEff;
    String use;
    String price;
    public Medicine() {
    }

    public Medicine(String id, String name, String genName, String sideEff, String use, String price) {
        this.id = id;
        this.name = name;
        this.genName = genName;
        this.sideEff = sideEff;
        this.use = use;
        this.price = price;
    }

    public String getUse() {
        return use;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGenName() {
        return genName;
    }

    public String getSideEff() {
        return sideEff;
    }

    public String getPrice() {
        return price;
    }
}
