package jmri.jmrix.mrc;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class MrcTurnoutTest {

    @Test
    public void testCTor() {
        MrcSystemConnectionMemo memo = new MrcSystemConnectionMemo();
        MrcInterfaceScaffold tc = new MrcInterfaceScaffold();
        memo.setMrcTrafficController(tc);
        jmri.InstanceManager.store(memo, MrcSystemConnectionMemo.class);
        MrcTurnout t = new MrcTurnout(5,tc,"test");
        Assert.assertNotNull("exists",t);
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(MrcTurnoutTest.class);

}
