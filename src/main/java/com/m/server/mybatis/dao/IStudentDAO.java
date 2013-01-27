package com.m.server.mybatis.dao;

import com.m.server.mybatis.domain.StudentDO;

public interface IStudentDAO {
    
    public StudentDO getStudentById(long id);

    public void add(StudentDO student);
}
