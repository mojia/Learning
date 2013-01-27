package com.m.server.mybatis.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import com.m.server.mybatis.dao.IStudentDAO;
import com.m.server.mybatis.domain.StudentDO;

public class DBTest {
    private SqlSessionFactory factory;

    @Before
    public void setup() throws IOException {
        Reader reader = 
                Resources.getResourceAsReader("mybatis-config.xml");
        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    @Test
    public void testcase01_GetStudentById() {
        SqlSession session = factory.openSession();
        IStudentDAO dao = session.getMapper(IStudentDAO.class);
        StudentDO student = dao.getStudentById(1);
        System.out.println(student);
    }
}
