package com.nwecoder.interceptor;

/**
 * Created by Administrator on 2017/5/5 0005.
 */

import com.nwecoder.model.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Created by Administrator on 2017/4/9 0009.
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception
    {
        if(hostHolder.getUser() == null)
        {
            httpServletResponse.sendRedirect("/reglogin?next="+httpServletRequest.getRequestURI());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object,ModelAndView modelAndView)  throws Exception
    {
        return;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object, Exception e) throws Exception
    {
        return;
    }
}

