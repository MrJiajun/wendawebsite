package com.nwecoder;

import com.nwecoder.dao.QuestionDAO;
import com.nwecoder.dao.UserDAO;
import com.nwecoder.model.Question;
import com.nwecoder.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendawebsiteApplication.class)

@Sql("/init-schema.sql")
public class InitDatabaseTests {
	@Autowired
	UserDAO userDAO;

    @Autowired
    QuestionDAO questionDAO;


    @Test
	public void initDatabase() {
		Random random = new Random();
		for(int i = 0; i < 11; ++i)
		{
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
			user.setName(String.format("User%d",i));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);

			user.setPassword("newpassword");
			userDAO.updatePassword(user);

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000 * 3600 * i);
			question.setCreatedDate(date);
			question.setUserId(i);
			question.setTitle(String.format("TITLE{%d}", i));
			question.setContent(String.format("love is the best present in the world %d",i));

			questionDAO.addQuestion(question);
		}

		Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
		userDAO.deleteById(1);
		Assert.assertNull(userDAO.selectById(1));
	}

}
