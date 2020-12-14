package com.code3.thinkingbookstore;

public class RecyclerHomeData {
    private String imageView;
    private String postIdx;
    private String userName;
    private String bookSource;
    private String writer;
    private String bookName;
    private String imagePath;

    public RecyclerHomeData(){

    }
    public RecyclerHomeData(String imageView,String userName,String bookSource,String writer,String bookName,String imagePath){
        this.imageView=imageView;
        this.userName=userName;
        this.bookSource=bookSource;
        this.writer=writer;
        this.bookName=bookName;
        this.imagePath=imagePath;
    }


    public String getImageView(){return imageView;}
    public void setImageView(String imageView){this.imageView=imageView;}

    public String getPostIdx(){return postIdx;}
    public void setPostIdx(String postIdx){this.postIdx=postIdx;}

    public String getUserName(){return userName;}
    public void setUserName(String userName){this.userName=userName;}

    public String getBookSource(){return bookSource;}
    public void setBookSource(String bookSource){this.bookSource=bookSource;}

    public String getWriter(){return writer;}
    public void setWriter(String writer){this.writer=writer;}

    public String getBookName(){return bookName;}
    public void setBookName(String bookName){this.bookName=bookName;}

    public String getImagePath(){return imagePath;}
    public void setImagePath(String imagePath){this.imagePath=imagePath;}

}
