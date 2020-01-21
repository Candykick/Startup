package com.candykick.startup.model;

// 자료실 파일 DB에서 정보를 읽어오기 위한 MODEL

import java.util.List;

public class ReferenceModel {

    private long doctype;
    private String filename;
    private String fileimage;
    private String filepath;
    private List<String> subimage;

    public ReferenceModel() {}
    public ReferenceModel(long docType, String fileName, String fileImage, String filePath, List<String> subimage) {
        this.doctype = docType;
        this.filename = fileName;
        this.fileimage = fileImage;
        this.filepath = filePath;
        this.subimage = subimage;
    }

    public long getDoctype() {
        return doctype;
    }
    public String getFileimage() {
        return fileimage;
    }
    public String getFilename() {
        return filename;
    }
    public String getFilepath() {
        return filepath;
    }
    public List<String> getSubimage() {
        return subimage;
    }

    public void setDoctype(long doctype) {
        this.doctype = doctype;
    }
    public void setFileimage(String fileimage) {
        this.fileimage = fileimage;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public void setSubimage(List<String> subimage) {
        this.subimage = subimage;
    }
}
