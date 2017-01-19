package com.bah.attune.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bah.attune.dao.BaseDao;

public class AnalysisResult
{
    private List<Parameter> criteria;
    private List<Entity> entities;


    public List<Parameter> getCriteria()
    {
        return criteria;
    }


    public void setCriteria(List<Parameter> criteria)
    {
        this.criteria = criteria;
    }


    public List<Entity> getEntities()
    {
        // Sort the entities alphabetically by name by default
        if ( !entities.isEmpty() )
        {
            Collections.sort(entities, new Comparator<Entity>()
            {

                @Override
                public int compare(Entity one, Entity two)
                {
                    return one.getPropertyValue(BaseDao.NAME).compareTo(two.getPropertyValue(BaseDao.NAME));
                }

            });
        }

        return entities;
    }


    public void setEntities(List<Entity> entities)
    {
        this.entities = entities;
    }
}
