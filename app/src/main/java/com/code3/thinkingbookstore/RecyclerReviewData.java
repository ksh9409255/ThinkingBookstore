package com.code3.thinkingbookstore;

public class RecyclerReviewData {
    private String userName;
    private String date;
    private String content;

    public RecyclerReviewData(){

    }
    public RecyclerReviewData(String userName,String date,String content){
        this.userName=userName;
        this.date=date;
        this.content=content;
    }


    public String getUserName(){return userName;}
    public void setUserName(String userName){this.userName=userName;}

    public String getDate(){return date;}
    public void setDate(String date){this.date=date;}

    public String getcontent(){return content;}
    public void setContent(String content){this.content=content;}
}
