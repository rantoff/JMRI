package jmri.jmrix.openlcb.swing.monitor;

import jmri.jmrix.can.CanSystemConnectionMemo;
import jmri.jmrix.can.TrafficControllerScaffold;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class MonitorActionTest {

    private TrafficControllerScaffold tcs = null; 
    private CanSystemConnectionMemo memo = null;

    @Test
    public void testCTor() {
        MonitorAction t = new MonitorAction();
        Assert.assertNotNull("exists",t);
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
        JUnitUtil.resetProfileManager();
        jmri.util.JUnitUtil.initConfigureManager();
        jmri.util.JUnitUtil.initDefaultUserMessagePreferences();
        tcs = new TrafficControllerScaffold();
        memo = new CanSystemConnectionMemo();
        memo.setTrafficController(tcs);
        jmri.InstanceManager.setDefault(CanSystemConnectionMemo.class,memo);
    }

    @After
    public void tearDown() {
        memo.dispose();
        memo = null;
        tcs.terminateThreads();
        tcs = null;
        JUnitUtil.tearDown();

    }

    // private final static Logger log = LoggerFactory.getLogger(MonitorActionTest.class);

}
