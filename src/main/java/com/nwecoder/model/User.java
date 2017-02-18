package com.nwecoder.model;

/**
 * Created by Administrator on 2017/2/17 0017.
 */
public class User {
    private String name;

    public User(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public String getDescrption()
    {
        return "This name is "+name;
    }
}
