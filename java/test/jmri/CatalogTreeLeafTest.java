package jmri;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class CatalogTreeLeafTest {

    @Test
    public void testCTor() {
        CatalogTreeLeaf t = new CatalogTreeLeaf("testleaf","testpath",1);
        Assert.assertNotNull("exists",t);
    }

    @Before
    public void setUp() {
        jmri.util.JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        jmri.util.JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(CatalogTreeLeafTest.class);

}
