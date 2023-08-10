package com.example.lecturenotesapp;

public class Note {
    private String text;
    private String title;

    public Note(String text, String title) {
        this.text = text;
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}

