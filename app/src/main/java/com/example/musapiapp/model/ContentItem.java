package com.example.musapiapp.model;

import com.example.musapiapp.dto.*;

public class ContentItem {
    public enum Type { CANCION, ALBUM, ARTISTA, LISTA }

    private Type type;
    private Object dto;
    private boolean showSave;

    public ContentItem(Type type, Object dto, boolean showSave) {
        this.type = type;
        this.dto = dto;
        this.showSave = showSave;
    }
    public Type getType() { return type; }
    public Object getDto() { return dto; }
    public boolean isShowSave() { return showSave; }
}
