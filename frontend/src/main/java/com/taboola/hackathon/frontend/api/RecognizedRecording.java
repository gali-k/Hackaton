package com.taboola.hackathon.frontend.api;

import java.io.File;
import java.util.Objects;

public class RecognizedRecording {

    private long dateTime;
    private File recording;

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public File getRecording() {
        return recording;
    }

    public void setRecording(File recording) {
        this.recording = recording;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecognizedRecording)) return false;
        RecognizedRecording that = (RecognizedRecording) o;
        return dateTime == that.dateTime &&
                Objects.equals(recording, that.recording);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, recording);
    }
}
