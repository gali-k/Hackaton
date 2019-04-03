package com.taboola.voicerecommendation.model;

import java.util.Objects;

public class RecognizedText {

    public RecognizedText(long dateTime, String text) {
        this.dateTime = dateTime;
        this.text = text;
    }

    private long dateTime;
    private String text;

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecognizedText that = (RecognizedText) o;
        return dateTime == that.dateTime &&
                Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, text);
    }

    @Override
    public String toString() {
        return "RecognizedText{" +
                "dateTime=" + dateTime +
                ", text='" + text + '\'' +
                '}';
    }
}