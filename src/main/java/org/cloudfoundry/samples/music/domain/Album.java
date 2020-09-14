package org.cloudfoundry.samples.music.domain;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Table
@Entity
@Document(indexName = "album")
public class Album {

    @org.springframework.data.annotation.Id
    @Id
    @PrimaryKey
    @Column(length=40)
    private String id = UUID.randomUUID().toString();

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)

    private String artist;

    @Field(type = FieldType.Text)
    private String releaseYear;

    @Field(type = FieldType.Text)
    private String genre;

    @Field(type = FieldType.Integer)
    private int trackCount;

    @Field(type = FieldType.Text)
    private String albumId;

    public Album() {
    }

    public Album(String title, String artist, String releaseYear, String genre) {
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
