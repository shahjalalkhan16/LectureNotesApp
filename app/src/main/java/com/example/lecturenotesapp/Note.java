package com.example.lecturenotesapp;

public class Note {
    private String title;
    private String text;

    public Note(String text, String title) {
        this.title = title;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}

