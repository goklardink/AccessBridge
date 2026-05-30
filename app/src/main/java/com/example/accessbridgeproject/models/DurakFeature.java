package com.example.accessbridgeproject.models;

import com.google.gson.annotations.SerializedName;

public class DurakFeature {

    @SerializedName("properties")
    private DurakProperties properties;

    @SerializedName("geometry")
    private DurakGeometry geometry;

    public DurakProperties getProperties() { return properties; }
    public DurakGeometry getGeometry()     { return geometry; }
}