package com.example.accessbridgeproject.network;

import com.example.accessbridgeproject.models.DurakResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface IettApiService {

    @GET("download/iett-otobus-duraklar..geojson")
    Call<DurakResponse> getStops();
}