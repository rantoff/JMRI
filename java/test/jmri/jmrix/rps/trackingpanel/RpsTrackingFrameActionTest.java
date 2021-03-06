package jmri.jmrix.rps.trackingpanel;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import jmri.jmrix.rps.RpsSystemConnectionMemo;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class RpsTrackingFrameActionTest {

    private RpsSystemConnectionMemo memo = null;

    @Test
    public void testCTor() {
        RpsTrackingFrameAction t = new RpsTrackingFrameAction(memo);
        Assert.assertNotNull("exists",t);
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
        memo = new RpsSystemConnectionMemo();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(RpsTrackingFrameActionTest.class);

}
