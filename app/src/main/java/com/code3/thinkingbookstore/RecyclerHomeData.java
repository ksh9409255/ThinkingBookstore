package com.code3.thinkingbookstore;

public class RecyclerHomeData {
    private String imageView;
    private int postIdx;
    private String userName;
    private String bookSource;
    private String writer;
    private String bookName;

    public RecyclerHomeData(){

    }
    public RecyclerHomeData(String imageView,int postIdx,String userName,String bookSource,String writer,String bookName){
        this.imageView=imageView;
        this.postIdx=postIdx;
        this.userName=userName;
        this.bookSource=bookSource;
        this.writer=writer;
        this.bookName=bookName;
    }


    public String getImageView(){return imageView;}
    public void setImageView(String imageView){this.imageView=imageView;}

    public int getPostIdx(){return postIdx;}
    public void setPostIdx(int postIdx){this.postIdx=postIdx;}

    public String getUserName(){return userName;}
    public void setUserName(String userName){this.userName=userName;}

    public String getBookSource(){return bookSource;}
    public void setBookSource(String bookSource){this.bookSource=bookSource;}

    public String getWriter(){return writer;}
    public void setWriter(String writer){this.writer=writer;}

    public String getBookName(){return bookName;}
    public void setBookName(String bookName){this.bookName=bookName;}

}
