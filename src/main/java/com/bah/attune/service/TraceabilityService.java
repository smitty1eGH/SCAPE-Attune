package com.bah.attune.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bah.attune.dao.BaseDao;
import com.bah.attune.dao.TraceabilityDao;

@Service public class TraceabilityService extends BaseService
{
    @Autowired private TraceabilityDao dao;

    @Override
    public BaseDao getDao()
    {
        return dao;
    }
}
