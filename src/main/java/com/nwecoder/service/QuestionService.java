package com.nwecoder.service;

import com.nwecoder.dao.QuestionDAO;
import com.nwecoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/2/24 0024.
 */
@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;
    public int addQuesion(Question question){
        //敏感词过滤（Html标签的过滤）
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));

        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public List<Question > getLatestQuestions(int userId, int offset, int limit)
    {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }
}

