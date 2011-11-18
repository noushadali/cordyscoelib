package com.cordys.coe.tools.snapshot.data;

import com.cordys.coe.tools.snapshot.config.JMXCounter;
import com.cordys.coe.tools.snapshot.data.JMXCounterResult.ResultWrapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class is used to convert the JMX counter result to a proper XML stream and vice versa.
 *
 * @author  localpg
 */
public class JMXCounterResultAdapter extends XmlAdapter<JMXCounterResult, Map<JMXCounter, Object>>
{
    /**
     * @see  javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override public Map<JMXCounter, Object> unmarshal(JMXCounterResult v)
                                                throws Exception
    {
        Map<JMXCounter, Object> retVal = new LinkedHashMap<JMXCounter, Object>();
        List<ResultWrapper> all = v.getResultWrapperList();

        for (ResultWrapper rw : all)
        {
            retVal.put(rw.getJmxCounter(), rw.getValue());
        }

        return retVal;
    }

    /**
     * @see  javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override public JMXCounterResult marshal(Map<JMXCounter, Object> v)
                                       throws Exception
    {
        JMXCounterResult retVal = new JMXCounterResult();

        for (JMXCounter key : v.keySet())
        {
            retVal.addResultWrapper(key, v.get(key));
        }

        return retVal;
    }
}
