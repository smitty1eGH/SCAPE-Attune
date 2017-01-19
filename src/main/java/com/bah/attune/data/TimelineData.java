package com.bah.attune.data;

import java.util.ArrayList;
import java.util.List;

/***
 * The TimelineData class is essentially a wrapper class that holds the data
 * that is used by the timeline drawing library. There are two components: the
 * data - which is all of the points that are plotted on the graph and are
 * attached to a 'group' by having a property 'group' that contains the id of
 * the group it belongs to, and groups - which represents a row on the timeline
 * 
 *
 */
public class TimelineData
{
    private List<TimelineEvent> data;
    private List<TimelineGroup> groups;
    
    private List<String> types;


    public TimelineData()
    {
        this.data = new ArrayList<TimelineEvent>();
        this.groups = new ArrayList<TimelineGroup>();
    }


    public List<TimelineEvent> getData()
    {
        return data;
    }


    public void setData(List<TimelineEvent> data)
    {
        this.data = data;
    }


    public List<TimelineGroup> getGroups()
    {
        return groups;
    }


    public void setGroups(List<TimelineGroup> groups)
    {
        this.groups = groups;
    }


    public void addEvent(TimelineEvent event)
    {
        data.add(event);
    }


    public void addGroup(TimelineGroup group)
    {
        groups.add(group);
    }


    public boolean hasGroup(String id)
    {
        for (TimelineGroup group : groups)
        {
            if ( group.getId().equals(id) )
                return true;
        }
        return false;
    }
    
    
    public boolean hasEvent(String id)
    {
    	for (TimelineEvent event: data)
    	{
    		if (event.getId().equals(id))
    			return true;
    	}
    	return false;
    }


	public List<String> getTypes() {
		return types;
	}


	public void setTypes(List<String> types) {
		this.types = types;
	}
}
