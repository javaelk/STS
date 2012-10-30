package uw.star.rts.util;

import static org.junit.Assert.*;

import org.junit.Test;

import uw.star.rts.cost.CostFactor;
import uw.star.rts.util.StopWatch;

public class StopWatchTest {

	@Test
	public void testGetElapsedTime() {
		StopWatch sw = new StopWatch();
		sw.start(CostFactor.ApplyTechniqueCost);
		sw.stop(CostFactor.ApplyTechniqueCost);
		assertTrue("elapsedtime",sw.getElapsedTime(CostFactor.ApplyTechniqueCost)<2);
		assertEquals("elapsedtime","0", sw.getElapsedTimeSecs(CostFactor.ApplyTechniqueCost));
	}


}
