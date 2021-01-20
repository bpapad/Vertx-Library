package com.library.vertx.classes;

public class Book{
    private int bookId;
    private String bookName;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Book(String bookName, int bookId) {
        this.bookName = bookName;
        this.bookId = bookId;
    }

    public Book(String bookName){
        this.bookName = bookName;
    }
}
