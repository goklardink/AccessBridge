package com.example.accessbridgeproject.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DurakGeometry {

    @SerializedName("coordinates")
    private List<Double> coordinates;

    public double getLongitude() { return coordinates.get(0); }
    public double getLatitude()  { return coordinates.get(1); }
}