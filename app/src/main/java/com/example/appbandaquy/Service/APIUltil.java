package com.example.appbandaquy.Service;

public class APIUltil {
    public static final String baseUrl = "https://storediviceonline.000webhostapp.com/";

    public static DataClient getData(){
        return RetrofitClient.getRetrofit(baseUrl).create(DataClient.class);
    }
}
