package com.example.unilink.objects;

public class Updates {
    String title;
    String text;
    boolean cancellable;
    int version;

    public Updates() {
    }

    public Updates(String title, String text, boolean cancellable, int version) {
        this.title = title;
        this.text = text;
        this.cancellable = cancellable;
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }
}
