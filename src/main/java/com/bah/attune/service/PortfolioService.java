package com.bah.attune.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bah.attune.dao.BaseDao;
import com.bah.attune.dao.PortfolioDao;

@Service public class PortfolioService extends BaseService
{
    @Autowired private PortfolioDao dao;

    @Override
    public BaseDao getDao()
    {
        return dao;
    }
}
