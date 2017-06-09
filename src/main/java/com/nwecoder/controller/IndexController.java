package com.nwecoder.controller;

import com.nwecoder.model.User;
import com.nwecoder.service.WendaService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;



/**
 * Created by Administrator on 2017/2/16 0016.
 */

//@Controller
public class IndexController {

    @Autowired
    WendaService wendaService;

    @RequestMapping(path={"/","index"} ,method = {RequestMethod.GET})
    @ResponseBody
    public String index(HttpSession httpSession){

        return wendaService.getMessage(2)+" Hello NewCoder "+httpSession.getAttribute("msg");
    }

    @RequestMapping(path={"profile/{GId}/{UId}"})
    @ResponseBody
    public String profile(@PathVariable("UId")int UId,
                          @PathVariable("GId")String GId,
                          @RequestParam(value = "type",defaultValue =  "1")int type,
                          @RequestParam(value = "key",defaultValue = "zz", required = false)String key)
    {
        return String.format("The website name is Profile,the Param is  %s & %d,and type:%d  or key :%s",GId,UId,type,key);
    }

    @RequestMapping(path={"/vm"}, method = {RequestMethod.GET})
    public String template(Model model){
        model.addAttribute("value1","vvvvvvv");
        List<String> colors = Arrays.asList(new String[]{"RED","GREEN","BULE"});
        model.addAttribute("colors",colors);
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < 4; i++)
        {
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("map", map);

        model.addAttribute("user",new User("Deng"));

        return "home";
    }

    @RequestMapping(path = {"/request"}, method = {RequestMethod.GET})
    @ResponseBody
    public String request(Model model, HttpServletResponse response,
                          HttpServletRequest request,
                          HttpSession httpSession,
                          @CookieValue("JSESSIONID")String sessionId) {
        StringBuilder sb = new StringBuilder();
        sb.append("CookieValue : "+sessionId);
        Enumeration<String > headername = request.getHeaderNames();
        while(headername.hasMoreElements())
        {
            String name = headername.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }

        if(request.getCookies() != null)
        {
            for(Cookie cookie : request.getCookies())
            {
                sb.append("Cookie: "+cookie.getName()+", Value:"+ cookie.getValue());
            }
        }

        sb.append(request.getMethod() + "<br>");
        sb.append(request.getPathInfo() + "<br>");
        sb.append(request.getQueryString() + "<br>");
        sb.append(request.getRequestURI() + "<br>");
        sb.append(request.getMethod() + "<br>");

        response.addHeader("newcoder","himyworld");
        response.addCookie(new Cookie("username","mynewcoder"));


        return sb.toString();
    }

    @RequestMapping(path = {"/redirect/{code}"}, method = {RequestMethod.GET})
    public RedirectView redirect(@PathVariable("code") int code,
                           HttpSession httpSession) {
        httpSession.setAttribute("msg","jump from redirect");
        RedirectView red = new RedirectView("/", true);
        if(code == 301)
        {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @RequestMapping(path = {"/admin"}, method = {RequestMethod.GET})
    @ResponseBody
    public String admin(@RequestParam("key") String key){
        if("admin".equals(key))
        {
            return "hello administrator";
        }
        throw new IllegalArgumentException("参数不对");
    }

    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e)
    {
        return "error"+e.getMessage();
    }

}
