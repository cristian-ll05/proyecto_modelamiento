package com.tienda.models;


import java.util.Date;

public class Sale {
    private int id;
    private int userId;
    private Date date;

    public Sale(int id, int userdId, Date date){
        this.id = id;
        this.userId = userdId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public Date getDate() {
        return date;
    }

    
}

