package com.example.demo.dao;

import com.example.demo.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface DeptDao {
    List<Dept> selectList();
}
