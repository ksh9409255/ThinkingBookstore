package com.code3.thinkingbookstore;

import android.net.Uri;

import java.net.URL;

public class RecyclerMypageData {
    private String imageView;
    private int bookIdx;
    private String oneReview;
    private String date;


    public RecyclerMypageData(){

    }
    public RecyclerMypageData(String imageView,int bookIdx,String oneReview,String date){
        this.imageView=imageView;
        this.bookIdx=bookIdx;
        this.oneReview=oneReview;
        this.date=date;
    }


    public String getImageView(){return imageView;}
    public void setImageView(String imageView){this.imageView=imageView;}

    public int getBookIdx(){return bookIdx;}
    public void setBookIdx(int bookIdx){this.bookIdx=bookIdx;}

    public String getOneReview(){return oneReview;}
    public void setOneReview(String oneReview){this.oneReview=oneReview;}

    public String getDate(){return date;}
    public void setDate(String date){this.date=date;}
}
