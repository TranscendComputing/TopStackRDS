/**
 * 
 */
package com.msi.tough.rdsquery;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.msi.tough.core.Appctx;
import com.msi.tough.query.Action;
import com.msi.tough.utils.RDSQueryFaults;

/**
 * @author tdhite
 */
public class RDSQueryImpl
{
    private final static Logger logger = Appctx.getLogger(RDSQueryImpl.class
        .getName());

    private final Map<String, Action> actionMap;

    public RDSQueryImpl(final Map<String, Action> actionMap)
    {
        this.actionMap = actionMap;
    }

    public void process(final HttpServletRequest req,
        final HttpServletResponse resp) throws Exception
    {
    	String actionName = req.getParameter("Action");
    	if(actionName == null || "".equals(actionName)){
    		throw RDSQueryFaults.MissingAction();
    	}
        final Action a = this.actionMap.get(actionName);
        if (a == null)
        {
            logger.debug("No action exists for " + req.getQueryString());
            logger.debug("Those that exist are:");
            for (Entry<String, Action> item : this.actionMap.entrySet())
            {
                logger.error("\"" + item.getKey() + "\"");
            }
            throw RDSQueryFaults.InvalidAction(actionName + " is not a valid action available in RDS service.");
        }
        else
        {
            logger.debug("calling action " + a);
            a.process(req, resp);
        }
    }
}
