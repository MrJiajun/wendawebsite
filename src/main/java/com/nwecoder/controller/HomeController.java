package com.nwecoder.controller;




import com.nwecoder.model.HostHolder;
import com.nwecoder.model.Question;
import com.nwecoder.model.ViewObject;
import com.nwecoder.service.QuestionService;
import com.nwecoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/24 0024.
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    private List<ViewObject> getQuestion(int userId, int offset, int limit)
    {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<ViewObject>();

        for(Question question : questionList)
        {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }

        return vos;
    }


    @RequestMapping(path = {"/","index"}, method = {RequestMethod.GET})
   // @ResponseBody
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop){

        model.addAttribute("vos",getQuestion(0, 0, 10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET})
    // @ResponseBody
    public String userIndex(Model model,
                        @PathVariable("userId") int userId){

        model.addAttribute("vos",getQuestion(userId, 0, 10));
        return "index";
    }
}
