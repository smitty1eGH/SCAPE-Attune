package com.bah.attune.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bah.attune.dao.BaseDao;
import com.bah.attune.data.AttuneException;

/*
 * Base class for all Service classes to supply invokeDao() for their controller classes
 */
public class BaseService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseService.class);

    // Sub service class with overwrite this method to provide the real target
    // Dao
    public BaseDao getDao()
    {
        return null;
    }

    // This helper method provides fast lane access method for the Dao methods
    // it use reflection to call the target Dao method. It can pass any numbers
    // of
    // parameters of any type
    public Object invokeDao(String methodName, Object... params) throws AttuneException
    {
        // loop through all target Dao methods, find the matching one and call
        // it with the parameters, 0 to any
        Method[] methodList = getDao().getClass().getMethods();
        for (Method method : methodList)
        {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == params.length
                    && matchSignature(method, params))
            {
                try
                {
                    return method.invoke(getDao(), params);
                } 
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
                {
                    e.printStackTrace();
                    throw new AttuneException("Error occured in invokeDao: " + methodName + ", Parameters: , Error:"
                            + params + e.toString());
                }
            }

        }

        return null;
    }

    // check parameter types so overloaded methods can be called also
    private boolean matchSignature(Method method, Object... params)
    {
        boolean matched = true;

        for (int i = 0; i < params.length; i++)
        {
            if (params[i].getClass() != method.getParameterTypes()[i])
            {
                matched = false;
                break;
            }
        }

        return matched;
    }
}
