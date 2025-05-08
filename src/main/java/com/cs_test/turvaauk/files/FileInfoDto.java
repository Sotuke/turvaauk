package com.cs_test.turvaauk.files;

import java.time.LocalDateTime;

public class FileInfoDto {
    private Long id;
    private String name;
    private String mimeType;
    private long size;
    private LocalDateTime uploadedAt;
    private String path;

    public FileInfoDto() {}

    public FileInfoDto(Long id, String name, String mimeType, long size, LocalDateTime uploadedAt) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.size = size;
        this.uploadedAt = uploadedAt;
    }
    public FileInfoDto(Long id, String name, String mimeType, long size, LocalDateTime uploadedAt, String path) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.size = size;
        this.uploadedAt = uploadedAt;
        this.path = path;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
