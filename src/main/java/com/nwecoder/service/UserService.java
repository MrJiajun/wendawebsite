package com.nwecoder.service;


import com.nwecoder.dao.LoginTicketDAO;
import com.nwecoder.dao.UserDAO;
import com.nwecoder.model.LoginTicket;
import com.nwecoder.model.User;
import com.nwecoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



/**
 * Created by Administrator on 2017/2/24 0024.
 */

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, Object> register(String username, String password)
    {
        Map<String, Object> map = new HashMap<String, Object>();
        //System.out.println(password + " 1");
        if(StringUtils.isBlank(username))
        {
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password))
        {
            map.put("msg","密码不能为空");
            return map;
        }

        //System.out.println(password + " 1");
        User user = userDAO.selectByName(username);

        if(user != null)
        {
            map.put("msg","用户名已经被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));

        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String, Object> login(String username, String password)
    {
        Map<String, Object> map = new HashMap<String, Object>();

        if(StringUtils.isBlank(username))
        {
            map.put("msg","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password))
        {
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if(user == null)
        {
            map.put("msg","用户名不存在");
            return map;
        }

        if(WendaUtil.MD5(password +user.getSalt()).equals(user.getPassword()))
        {
            map.put("msg","密码错误");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public String addLoginTicket(int userId)
    {
        LoginTicket loginTicket  = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(3600*24*100 + now.getTime());
        loginTicket.setExpired(now);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public User getUser(int id)
    {
        return userDAO.selectById(id);
    }

    public void logout(String ticket){
        loginTicketDAO.updateStaus(ticket, 1);
    }
}
