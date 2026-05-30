package com.example.accessbridgeproject.models;

import com.google.gson.annotations.SerializedName;

public class DurakProperties {

    @SerializedName("ID")          private String id;
    @SerializedName("ADI")         private String name;
    @SerializedName("DURAK_KODU")  private String stopCode;
    @SerializedName("DURAK_TIPI")  private String stopType;
    @SerializedName("YON_BILGISI") private String direction;
    @SerializedName("DURUMU")      private String status;
    @SerializedName("ILCEID")      private String districtId;

    public String getName()      { return name; }
    public String getStopType()  { return stopType; }
    public String getDirection() { return direction; }
    public String getStopCode()  { return stopCode; }
    public String getId()        { return id; }
}