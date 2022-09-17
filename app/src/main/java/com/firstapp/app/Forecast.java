package com.firstapp.app;

public class Forecast {

    private String temperature = "test", condition = "test", weekday = "test";

    public Forecast(String temperature, String condition, String weekday) {
        this.temperature = temperature;
        this.condition = condition;
        this.weekday = weekday;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
}
