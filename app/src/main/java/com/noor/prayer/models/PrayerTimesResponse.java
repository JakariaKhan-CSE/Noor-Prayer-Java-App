package com.noor.prayer.models;

import com.google.gson.annotations.SerializedName;

public class PrayerTimesResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("timings")
        private Timings timings;

        public Timings getTimings() {
            return timings;
        }
    }

    public static class Timings {
        @SerializedName("Fajr")
        private String fajr;
        @SerializedName("Dhuhr")
        private String dhuhr;
        @SerializedName("Asr")
        private String asr;
        @SerializedName("Maghrib")
        private String maghrib;
        @SerializedName("Isha")
        private String isha;

        public String getFajr() { return fajr; }
        public String getDhuhr() { return dhuhr; }
        public String getAsr() { return asr; }
        public String getMaghrib() { return maghrib; }
        public String getIsha() { return isha; }
    }
}
