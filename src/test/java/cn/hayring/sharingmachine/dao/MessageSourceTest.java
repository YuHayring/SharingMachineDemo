package cn.hayring.sharingmachine.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.testng.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class MessageSourceTest {
//
//    @Autowired
//    private MessageSource messageSource;
//
//    @Test
//    public void assertMSNull() {
//        assertNotNull(messageSource);
//        System.out.println(messageSource.getMessage("user.arg_error",null, null));
//    }

}
