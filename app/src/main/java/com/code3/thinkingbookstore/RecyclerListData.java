package com.code3.thinkingbookstore;

public class RecyclerListData {
    private String imageView;
    private int bookIdx;

    public RecyclerListData(){

    }
    public RecyclerListData(String imageView,int bookIdx){
        this.imageView=imageView;
        this.bookIdx=bookIdx;
    }


    public String getImageView(){return imageView;}
    public void setImageView(String imageView){this.imageView=imageView;}

    public int getBookIdx(){return bookIdx;}
    public void setBookIdx(int bookIdx){this.bookIdx=bookIdx;}
}
