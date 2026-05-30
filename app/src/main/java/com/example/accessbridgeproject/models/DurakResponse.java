package com.example.accessbridgeproject.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DurakResponse {

    @SerializedName("features")
    private List<DurakFeature> features;

    public List<DurakFeature> getFeatures() { return features; }
}