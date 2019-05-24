package com.example.hoster;

public class Upload {
    private String mName;
    private String mImageUrl;

    public Upload(){
        //needed
    }

    public Upload(String name, String imageUrl) {
        if(name.trim().equals("")){
            name = "No Name";
        }
        this.mName = name;
        this.mImageUrl = imageUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String name) {
        this.mName = name;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String imageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
