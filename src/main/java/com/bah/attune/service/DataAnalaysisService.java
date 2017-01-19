package com.bah.attune.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bah.attune.dao.BaseDao;
import com.bah.attune.dao.DataAnalysisDao;

@Service
public class DataAnalaysisService extends BaseService 
{
	@Autowired
	private DataAnalysisDao dao;
	
	@Override
    public BaseDao getDao()
    {
        return dao;
    }
}
