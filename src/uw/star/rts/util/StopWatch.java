package uw.star.rts.util;

import uw.star.rts.cost.CostFactor;
import uw.star.rts.util.*;

import java.util.*;


/**
 * A stop watch to keep track of time cost for many CostFactors
 * @author wliu
 *
 */
public class StopWatch {
    private Map<CostFactor,Boolean> running;
    private Map<CostFactor,Long> startTime;
    private Map<CostFactor,Long> stopTime;
    
    public StopWatch(){
    	startTime = new HashMap<CostFactor,Long>();
    	stopTime = new HashMap<CostFactor,Long>();
    	running = new HashMap<CostFactor,Boolean>();
    }
     
    public void start(CostFactor cf) {
        startTime.put(cf, System.currentTimeMillis());
        running.put(cf,true);
    }

    
    public void stop(CostFactor cf) {
        stopTime.put(cf, System.currentTimeMillis());
        running.put(cf,false);
    }

    
    //elaspsed time in milliseconds
    public long getElapsedTime(CostFactor cf) {
        if(!startTime.containsKey(cf))
        	return -1;
    	long elapsed;
        if (running.get(cf)) {
             elapsed = (System.currentTimeMillis() - startTime.get(cf));
        }
        else {
            elapsed = (stopTime.get(cf) - startTime.get(cf));
        }
        return elapsed;
    }
    
    
    //elaspsed time in seconds
    public long getElapsedTimeSecs(CostFactor cf) {
        if(!startTime.containsKey(cf))
        	return -1;
        long elapsed;
        if (running.get(cf)) {
            elapsed = ((System.currentTimeMillis() - startTime.get(cf)) / 1000);
        }
        else {
            elapsed = ((stopTime.get(cf) - startTime.get(cf)) / 1000);
        }
        return elapsed;
    }
}

