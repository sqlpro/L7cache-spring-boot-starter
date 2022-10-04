package com.example.demo.dao;

import com.example.demo.Dept;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeptDAOImplement implements DeptDao{
    private final SqlSession sqlSession;

    public DeptDAOImplement(@Qualifier("ociSqlSessionTemplate") SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<Dept> selectList() {
        return sqlSession.selectList("com.example.demo.dao.DeptDao.selectList");
    }
}
