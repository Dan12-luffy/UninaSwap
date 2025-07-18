package com.uninaswap.services;

import com.uninaswap.dao.CategoryDao;
import com.uninaswap.dao.CategoryDaoImpl;
import com.uninaswap.model.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private static final CategoryService instance = new CategoryService();
    private final CategoryDao categoryDao;

    private CategoryService() {
        this.categoryDao = new CategoryDaoImpl();
    }

    public static CategoryService getInstance() {
        return instance;
    }

    public List<Category> getAllCategories() throws Exception {
        return categoryDao.findAll();
    }

    public String getCategoryById(int categoryId) throws Exception {
        return categoryDao.findById(categoryId);
    }

    public int getCategoryIdByName(String name) throws Exception {
        return categoryDao.getCategoryIdByName(name);
    }
}
