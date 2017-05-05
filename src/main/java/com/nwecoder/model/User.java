package com.nwecoder.model;

/**
 * Created by Administrator on 2017/2/17 0017.
 */
public class User {
    private int id;
    private String password;
    private String name;
    private String salt;
    private String headUrl;


    public User()
    {

    }

    public User(String name)
    {
        this.name = name;
        this.password = "";
        this.salt = "";
        this.headUrl = "";
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

    public String getPassword(){
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String  getSalt(){
        return salt;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }

    public String getHeadUrl()
    {
        return headUrl;
    }

    public void setHeadUrl(String headUrl)
    {
        this.headUrl = headUrl;
    }

    public int getId(){
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
