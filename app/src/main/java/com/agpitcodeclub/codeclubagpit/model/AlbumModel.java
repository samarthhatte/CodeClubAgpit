package com.agpitcodeclub.codeclubagpit.model;

public class AlbumModel {

    private String id;
    private String albumName;
    private String coverImage;

    // Required empty constructor (Firestore needs this)
    public AlbumModel() {}

    public AlbumModel(String id, String albumName, String coverImage) {
        this.id = id;
        this.albumName = albumName;
        this.coverImage = coverImage;
    }

    public String getId() {
        return id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getCoverImage() {
        return coverImage;
    }
}
