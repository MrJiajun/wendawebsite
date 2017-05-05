package com.nwecoder.interceptor;

import com.nwecoder.dao.LoginTicketDAO;
import com.nwecoder.dao.UserDAO;
import com.nwecoder.model.HostHolder;
import com.nwecoder.model.LoginTicket;
import com.nwecoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/9 0009.
 */
@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception
    {
        String ticket = null;
        if(httpServletRequest.getCookies() != null)
        {
            for(Cookie cookie : httpServletRequest.getCookies())
            {
                if(cookie.getName().equals("ticket"))
                {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }

        if(ticket != null)
        {
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            if(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0)
            {
                return true;
            }

            User user = userDAO.selectById(loginTicket.getUserId());
            hostHolder.setUser(user);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object,ModelAndView modelAndView)  throws Exception
    {
        if(modelAndView != null && hostHolder.getUser() != null)
        {
            modelAndView.addObject("user",hostHolder.getUser());
        }
        return;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception e) throws Exception
    {
        hostHolder.clear();
    }

}
