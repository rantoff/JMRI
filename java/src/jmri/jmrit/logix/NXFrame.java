package jmri.jmrit.logix;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import jmri.InstanceManager;
import jmri.implementation.SignalSpeedMap;
import jmri.jmrit.roster.RosterSpeedProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Frame for defining and launching an entry/exit warrant. An NX warrant is a
 * warrant that can be defined on the run without a pre-recorded learn mode
 * session using a set script for ramping startup and stop throttle settings.
 * <P>
 * The route can be defined in a form or by mouse clicking on the OBlock
 * IndicatorTrack icons.
 * <BR>
 * <hr>
 * This file is part of JMRI.
 * <P>
 * JMRI is free software; you can redistribute it and/or modify it under the
 * terms of version 2 of the GNU General Public License as published by the Free
 * Software Foundation. See the "COPYING" file for a copy of this license.
 * </P><P>
 * JMRI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * </P>
 *
 * @author Pete Cressman Copyright (C) 2009, 2010, 2015
 */
public class NXFrame extends WarrantRoute {

    private float _scale = 87.1f;
    private float _maxThrottle = 0.75f;
    private float _intervalTime = 0.0f;     // milliseconds
    private float _throttleIncr = 0.0f;
    private float _throttleFactor = 0.0f;

    JTextField _maxSpeedBox = new JTextField(6);
    JTextField _rampInterval = new JTextField(6);
    JTextField _rampIncre = new JTextField(6);
    JTextField _throttleFactorBox = new JTextField(6);
    JRadioButton _forward = new JRadioButton();
    JRadioButton _reverse = new JRadioButton();
    JCheckBox _stageEStop = new JCheckBox();
    JCheckBox _shareRouteBox = new JCheckBox();
    JCheckBox _haltStartBox = new JCheckBox();
    JCheckBox _calibrateBox = new JCheckBox();
//    JCheckBox _addTracker = new JCheckBox();
    JRadioButton _runAuto = new JRadioButton(Bundle.getMessage("RunAuto"));
    JRadioButton _runManual = new JRadioButton(Bundle.getMessage("RunManual"));
//  static boolean _addTracker = false;
    private boolean _haltStart = false;
    private float _maxSpeed = 0.6f;
    private float _minSpeed = 0.05f;

    protected JPanel _controlPanel;
    private JPanel _autoRunPanel;
    private JPanel _manualPanel;

    /**
     * Get the default instance of an NXFrame.
     *
     * @return the default instance, creating it if necessary
     * @deprecated since 4.7.4; use {@link #getDefault() } instead
     */
    @Deprecated
    static public NXFrame getInstance() {
        return getDefault();
    }

    /**
     * Get the default instance of an NXFrame.
     *
     * @return the default instance, creating it if necessary
     */
    static public NXFrame getDefault() {
        if (GraphicsEnvironment.isHeadless()) {
            return null;
        }
        NXFrame instance = InstanceManager.getOptionalDefault(NXFrame.class).orElseGet(() -> {
            return InstanceManager.setDefault(NXFrame.class, new NXFrame());
        });
        if (!instance.isVisible()) {
            WarrantPreferences preferences = WarrantPreferences.getDefault();
            instance.setScale(preferences.getLayoutScale());
            instance.updatePanel(preferences.getInterpretation());
            instance.setTrainInfo(null);
            instance.clearRoute();
        }
        return instance;
    }

    private NXFrame() {
        super();
    }

    public void init() {
        WarrantPreferences preferences = WarrantPreferences.getDefault();
        NXFrame instance = getDefault();
        instance.setScale(preferences.getLayoutScale());
        instance.setDepth(preferences.getSearchDepth());
        instance.setTimeInterval(preferences.getTimeIncrement());
        instance.setThrottleIncrement(preferences.getThrottleIncrement());
        instance.setThrottleFactor(preferences.getThrottleScale());
        instance.updatePanel(preferences.getInterpretation());
        makeMenus();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        _controlPanel = new JPanel();
        _controlPanel.setLayout(new BoxLayout(_controlPanel, BoxLayout.PAGE_AXIS));
        _controlPanel.add(Box.createVerticalGlue());
        _controlPanel.add(makeBlockPanels());
        _controlPanel.add(searchDepthPanel(false));

        _maxSpeedBox.addActionListener((ActionEvent e) -> {
            getBoxData();
        });
        _autoRunPanel = makeAutoRunPanel(jmri.InstanceManager.getDefault(SignalSpeedMap.class).getInterpretation());

        _controlPanel.add(_autoRunPanel);
        _controlPanel.add(Box.createVerticalStrut(STRUT_SIZE));

        _forward.setSelected(true);
        _stageEStop.setSelected(false);
        _haltStartBox.setSelected(_haltStart);
        _calibrateBox.setSelected(false);
//        _rampInterval.setText(Float.toString(_intervalTime / 1000));
        JPanel p = new JPanel();
        p.add(Box.createGlue());
        JButton button = new JButton(Bundle.getMessage("ButtonRunNX"));
        button.addActionListener((ActionEvent e) -> {
            makeAndRunWarrant();
        });
        p.add(button);
        p.add(Box.createHorizontalStrut(2 * STRUT_SIZE));
        button = new JButton(Bundle.getMessage("ButtonCancel"));
        button.addActionListener((ActionEvent e) -> {
            closeFrame();
        });
        p.add(button);
        p.add(Box.createGlue());
        _controlPanel.add(p);

        _controlPanel.add(Box.createVerticalStrut(STRUT_SIZE));
        _controlPanel.add(makeSwitchPanel());

        mainPanel.add(_controlPanel);
        getContentPane().add(mainPanel);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                closeFrame();
            }
        });
        setAlwaysOnTop(true);
        pack();
    }

    public void updatePanel(int interp) {
        if (_controlPanel == null) {
            return;
        }
        // find position of panel
        java.awt.Component[] list = _controlPanel.getComponents();
        int i = 0;
        while (i < list.length && !list[i].equals(_autoRunPanel)) {
            i++;
        }
        if (i < list.length) {
            _controlPanel.remove(_autoRunPanel);
            _autoRunPanel = makeAutoRunPanel(interp);
            _controlPanel.add(_autoRunPanel, i);
            pack();
        }
    }

    private JPanel makeSwitchPanel() {
        ButtonGroup bg = new ButtonGroup();
        bg.add(_runAuto);
        bg.add(_runManual);
        _runAuto.addActionListener((ActionEvent event) -> {
            enableAuto(true);
        });
        _runManual.addActionListener((ActionEvent event) -> {
            enableAuto(false);
        });
        _runAuto.setSelected(true);
        JPanel pp = new JPanel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.LINE_AXIS));
        pp.add(Box.createHorizontalStrut(STRUT_SIZE));
        pp.add(_runAuto);
        pp.add(Box.createHorizontalStrut(STRUT_SIZE));
        pp.add(_runManual);
        pp.add(Box.createHorizontalStrut(STRUT_SIZE));
        return pp;
    }

    @SuppressFBWarnings(value = "DB_DUPLICATE_SWITCH_CLAUSES", justification = "Same code for both cases")
    private JPanel makeAutoRunPanel(int interpretation) {
        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));
        float maxSpeed;
        float throttleIncr;
        String maxSpeedLabel;
        String throttleIncrLabel;
        switch (interpretation) {
            case SignalSpeedMap.PERCENT_NORMAL:
            case SignalSpeedMap.PERCENT_THROTTLE:
                maxSpeed = _maxThrottle;
                maxSpeedLabel = "MaxSpeed";
                throttleIncr = _minSpeed;
                throttleIncrLabel = "RampIncrement";
                break;
            case SignalSpeedMap.SPEED_MPH:
                maxSpeed = _maxThrottle * _throttleFactor * 223.69363f; // 2.2369363 is 3.6 converted by mile/km
                maxSpeedLabel = "MaxMph";
                throttleIncr = _minSpeed * _throttleFactor * 223.69363f;
                throttleIncrLabel = "MinMph";
                break;
            case SignalSpeedMap.SPEED_KMPH:
                maxSpeed = _maxThrottle * _throttleFactor * 360f;
                maxSpeedLabel = "MaxKMph";
                throttleIncr = _minSpeed * _throttleFactor * 360f;
                throttleIncrLabel = "MinKMph";
                break;
            default:
                maxSpeed = _maxThrottle;
                maxSpeedLabel = "MaxSpeed";
                throttleIncr = _minSpeed;
                throttleIncrLabel = "RampIncrement";
        }
        p1.add(makeTextBoxPanel(false, _maxSpeedBox, maxSpeedLabel, null));
        p1.add(makeTextBoxPanel(false, _rampInterval, "rampInterval", null));
        p1.add(makeTextBoxPanel(false, _rampIncre, throttleIncrLabel, "ToolTipRampIncrement"));
        p1.add(makeTextBoxPanel(false, _throttleFactorBox, "ThrottleScale", "ToolTipThrottleScale"));
        p1.add(makeTextBoxPanel(false, _shareRouteBox, "ShareRoute", "ToolTipShareRoute"));
        _maxSpeedBox.setText(Float.toString(maxSpeed));
        _rampInterval.setText(Float.toString(_intervalTime / 1000));
        _rampIncre.setText(Float.toString(throttleIncr));
        _throttleFactorBox.setText(Float.toString(_throttleFactor));

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));
//        JPanel trainPanel = makeTrainIdPanel(makeTextBoxPanel(
//                false, _shareRouteBox, "ShareRoute", "ToolTipShareRoute"));
        JPanel trainPanel = makeTrainIdPanel(null);
        p2.add(trainPanel);

        JPanel autoRunPanel = new JPanel();
        autoRunPanel.setLayout(new BoxLayout(autoRunPanel, BoxLayout.PAGE_AXIS));
        JPanel pp = new JPanel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.LINE_AXIS));
        pp.add(Box.createHorizontalStrut(STRUT_SIZE));
        pp.add(p1);
        pp.add(Box.createHorizontalStrut(STRUT_SIZE));
        pp.add(p2);
        pp.add(Box.createHorizontalStrut(STRUT_SIZE));
        autoRunPanel.add(pp);

        ButtonGroup bg = new ButtonGroup();
        bg.add(_forward);
        bg.add(_reverse);
        p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.LINE_AXIS));
        p1.add(Box.createHorizontalGlue());
        p1.add(makeTextBoxPanel(false, _forward, "forward", null));
        p1.add(makeTextBoxPanel(false, _reverse, "reverse", null));
        p1.add(Box.createHorizontalGlue());
        pp = new JPanel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.LINE_AXIS));
        pp.add(Box.createHorizontalStrut(STRUT_SIZE));
        pp.add(p1);
        pp.add(Box.createHorizontalStrut(STRUT_SIZE));
        pp.add(Box.createHorizontalStrut(2 * STRUT_SIZE));
        autoRunPanel.add(pp);

        JPanel ppp = new JPanel();
        ppp.setLayout(new BoxLayout(ppp, BoxLayout.LINE_AXIS));
        ppp.add(Box.createHorizontalStrut(STRUT_SIZE));
        ppp.add(makeTextBoxPanel(false, _stageEStop, "StageEStop", null));
        ppp.add(Box.createHorizontalStrut(STRUT_SIZE));
        ppp.add(makeTextBoxPanel(false, _haltStartBox, "HaltAtStart", null));
        ppp.add(Box.createHorizontalStrut(STRUT_SIZE));
        ppp.add(makeTextBoxPanel(false, _calibrateBox, "Calibrate", "calibBlockMessage"));
        ppp.add(Box.createHorizontalStrut(STRUT_SIZE));
        autoRunPanel.add(ppp);
        return autoRunPanel;
    }

    private void makeMenus() {
        setTitle(Bundle.getMessage("AutoWarrant"));
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        addHelpMenu("package.jmri.jmrit.logix.NXWarrant", true);
    }

    private void enableAuto(boolean enable) {
        Component[] comps = _controlPanel.getComponents();
        if (enable) {
            int idx = 0;
            while (idx < comps.length) {
                if (comps[idx].equals(_manualPanel)) {
                    break;
                }
                idx++;
            }
            _controlPanel.remove(idx);
            _autoRunPanel = makeAutoRunPanel(jmri.InstanceManager.getDefault(SignalSpeedMap.class).getInterpretation());
            _controlPanel.add(_autoRunPanel, idx);
        } else {
            int idx = 0;
            while (idx < comps.length) {
                if (comps[idx].equals(_autoRunPanel)) {
                    break;
                }
                idx++;
            }
            _controlPanel.remove(idx);
            _manualPanel = makeTrainIdPanel(null);
            _controlPanel.add(_manualPanel, idx);
        }
        pack();
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        String property = e.getPropertyName();
//        if (log.isDebugEnabled()) log.debug("propertyChange \""+property+
//                                            "\" old= "+e.getOldValue()+" new= "+e.getNewValue()+
//                                            " source= "+e.getSource().getClass().getName());
        if (property.equals("DnDrop")) {
            doAction(e.getSource());
        }
    }

    /**
     * Called by {@link jmri.jmrit.logix.RouteFinder#run()}. If all goes well,
     * WarrantTableFrame.runTrain(warrant) will run the warrant
     *
     * @param orders list of block orders
     */
    @Override
    public void selectedRoute(ArrayList<BlockOrder> orders) {
        if (log.isDebugEnabled()) {
            log.debug("NXFrame selectedRoute()");
        }
        String msg = null;
        String name = getTrainName();
        msg = checkLocoAddress();
        if (msg != null) {
            JOptionPane.showMessageDialog(this, msg,
                    Bundle.getMessage("WarningTitle"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        String s = ("" + Math.random()).substring(2);
        Warrant warrant = new Warrant("IW" + s, "NX(" + getAddress() + ")");
        warrant.setSpeedUtil(_speedUtil);
        _speedUtil.setWarrant(warrant);
        warrant.setTrainName(name);
        warrant.setBlockOrders(orders);
        _speedUtil.makeSpeedTree();
        int mode;
        if (!_runManual.isSelected()) {
            mode = Warrant.MODE_RUN;
            warrant.setShareRoute(_shareRouteBox.isSelected());
            msg = getBoxData();
            if (msg == null) {
                msg = makeCommands(warrant);
            }
            if (msg == null) {
                Calibrater calib = null;
                if (_calibrateBox.isSelected()) {
                    warrant.setViaOrder(getViaBlockOrder());
                    calib = new Calibrater(warrant, _forward.isSelected(), getLocation());
                    msg = calib.verifyCalibrate();
                    if (msg != null) {
                        calib = null;
                    }
                }
                warrant.setCalibrater(calib);
            }
        } else {
            mode = Warrant.MODE_MANUAL;
        }
        WarrantTableFrame tableFrame = WarrantTableFrame.getDefault();
        if (msg == null) {
            tableFrame.getModel().addNXWarrant(warrant);   //need to catch propertyChange at start
            if (log.isDebugEnabled()) {
                log.debug("NXWarrant added to table");
            }
            msg = tableFrame.runTrain(warrant, mode);
            tableFrame.scrollTable();
        }
        if (msg != null) {
            if (log.isDebugEnabled()) {
                log.debug("WarrantTableFrame run warrant. msg= " + msg + " Remove warrant " + warrant.getDisplayName());
            }
            tableFrame.getModel().removeWarrant(warrant);
        }

        if (msg == null && mode == Warrant.MODE_RUN) {
//            if (log.isDebugEnabled()) log.debug("Warrant "+warrant.getDisplayName()+" running.");
            if (_haltStartBox.isSelected()) {
                _haltStart = true;
                class Halter implements Runnable {

                    Warrant war;

                    Halter(Warrant w) {
                        war = w;
                    }

                    @Override
                    public void run() {
                        int limit = 0;
                        try {
                            // wait until _engineer is assigned so HALT can take effect
                            while (!war.controlRunTrain(Warrant.HALT) && limit < 3000) {
                                Thread.sleep(200);
                                limit += 200;
                            }
                        } catch (InterruptedException e) {
                            war.controlRunTrain(Warrant.HALT);
                        }
                    }
                }
                Halter h = new Halter(warrant);
                new Thread(h).start();
            } else {
                _haltStart = false;
            }
        }
        if (msg != null) {
            JOptionPane.showMessageDialog(this, msg,
                    Bundle.getMessage("WarningTitle"), JOptionPane.WARNING_MESSAGE);
        } else {
            closeFrame();
            if (log.isDebugEnabled()) {
                log.debug("Close Frame.");
            }
        }
    }

    protected void closeFrame() {
        clearTempWarrant();
        dispose();
    }

    // for the convenience of testing
    protected void setTimeInterval(float s) {
        _intervalTime = s;
    }

    /**
     * for the convenience of testing
     * @param increment the throttle increment
     */
    protected void setThrottleIncrement(float increment) {
        this._throttleIncr = increment;
        _minSpeed = _throttleIncr;
    }

    /**
     * for the convenience of testing
     * @param factor the throttle factor
     */
    protected void setThrottleFactor(float factor) {
        this._throttleFactor = factor;
    }

    // for the convenience of testing
    protected void setScale(float s) {
        _scale = s;
    }

    private String getBoxData() {
        String text = null;
        float maxSpeed;
        float minSpeed;
        float factor;
        try {
            text = _maxSpeedBox.getText();
            maxSpeed = Float.parseFloat(text);
            text = _rampIncre.getText();
            minSpeed = Float.parseFloat(text);
            text = _throttleFactorBox.getText();
            factor = Float.parseFloat(text);
        } catch (NumberFormatException nfe) {
            return Bundle.getMessage("MustBeFloat", text);
        }
        _throttleFactor = factor;
        String speedErr;
        switch (jmri.InstanceManager.getDefault(SignalSpeedMap.class).getInterpretation()) {
            case SignalSpeedMap.SPEED_MPH:
                _maxSpeed = maxSpeed / (_throttleFactor * 223.69363f);
                _minSpeed = minSpeed / (_throttleFactor * 223.69363f);
                speedErr = Bundle.getMessage("speedMph");
                break;
            case SignalSpeedMap.SPEED_KMPH:
                _maxSpeed = maxSpeed / (_throttleFactor * 360f);
                _minSpeed = minSpeed / (_throttleFactor * 360f);
                speedErr = Bundle.getMessage("speedKmph");
                break;
            default:
                _maxSpeed = maxSpeed;
                _minSpeed = minSpeed;
                speedErr = Bundle.getMessage("throttlesetting");
        }
        if (_maxSpeed > 1.0 || _maxSpeed < 0.01) {
            return Bundle.getMessage("badSpeed", speedErr, maxSpeed);
        }
        _maxThrottle = _maxSpeed;
        // throttle increment between 1/2 full and 1 step of 128 step throttle.
        if (_minSpeed > 0.5 || _minSpeed < 0.008 || _minSpeed >= _maxSpeed) {
            return Bundle.getMessage("badIncr", speedErr, minSpeed);
        }
        _throttleIncr = _minSpeed;
        try {
            text = _rampInterval.getText();
            _intervalTime = Float.parseFloat(text) * 1000;
            if (_intervalTime > 10000 || _intervalTime < 210) {
                return Bundle.getMessage("InvalidTime", text);
            }
        } catch (NumberFormatException nfe) {
            return Bundle.getMessage("InvalidTime", text);
        }
        return null;
    }

    /*
     * Return length of warrant route in mm.  Assume start and end is in the middle of first
     * and last blocks.  Use a default length for blocks with unspecified length.
     */
    private float getTotalLength() {

        List<BlockOrder> orders = getOrders();
        BlockOrder bo = orders.get(0);
        float len = bo.getPath().getLengthMm();
        if (len <= 0) {
            log.warn("Route on path {} in block {} has length zero.", bo.getPathName(), bo.getBlock().getDisplayName());
           return -1;
        }
        float totalLen = len / 2;      // estimated distance of the route
        for (int i = 1; i < orders.size() - 1; i++) {
            bo = orders.get(i);
            len = bo.getPath().getLengthMm();
            if (len <= 0) {
                log.warn("Route on path {} in block {} has length zero.", bo.getPathName(), bo.getBlock().getDisplayName());
                return -1;
            }
            totalLen += len;
        }
        bo = orders.get(orders.size() - 1);
        len = bo.getPath().getLengthMm();
        if (len <= 0) {
            log.warn("Route on path {} in block {} has length zero.", bo.getPathName(), bo.getBlock().getDisplayName());
            return -1;
        }
        totalLen += len / 2;
        return totalLen;
    }

    private float getRampLength(float totalLen, RosterSpeedProfile speedProfile, boolean isForward) {
        float speed = 0.0f;     // throttle setting
        float rampLength = 0.0f;
        int numSteps = 0;
        while (speed < _maxSpeed) {
            float dist = _speedUtil.getDistanceTraveled((speed + _minSpeed / 2), Warrant.Normal, _intervalTime, isForward);
            if (rampLength + dist <= totalLen / 2) {
                if ((speed + _minSpeed) > _maxSpeed) {
                    dist = dist * (_maxSpeed - speed) / _minSpeed;
                    speed = _maxSpeed;
                } else {
                    speed += _minSpeed;
                }
                rampLength += dist;
                numSteps++;
                if (log.isDebugEnabled()) {
                    log.debug("step " + numSteps + " dist= " + dist + " speed= " + speed
                            + " rampLength = " + rampLength);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("cannot get to _maxSpeed of {} and have enough length to decelerate. _maxSpeed set to {}",
                            _maxSpeed, speed);
                    _maxSpeed = speed;      // modify
                }
                break;
            }
        }
        // add the smidge of distance needed to reach _maxSpeed
//        rampLength += (_maxSpeed - speed)*_intervalTime*distanceFactor;
        if (log.isDebugEnabled()) {
            log.debug(numSteps + " speed steps of delta= "
                    + _minSpeed + " for rampLength = " + rampLength + " to reach speed " + _maxSpeed);
        }
        return rampLength;
    }

    private String makeCommands(Warrant w) {

        int nextIdx = 0;        // block index - increment after getting a block order
        List<BlockOrder> orders = getOrders();
        BlockOrder bo = orders.get(nextIdx++);
        String blockName = bo.getBlock().getDisplayName();
        boolean isForward = _forward.isSelected();

        w.addThrottleCommand(new ThrottleSetting(0, "F0", "true", blockName));
        if (isForward) {
            w.addThrottleCommand(new ThrottleSetting(1000, "Forward", "true", blockName));
            w.addThrottleCommand(new ThrottleSetting(1000, "F2", "true", blockName));
            w.addThrottleCommand(new ThrottleSetting(2500, "F2", "false", blockName));
            w.addThrottleCommand(new ThrottleSetting(1000, "F2", "true", blockName));
            w.addThrottleCommand(new ThrottleSetting(2500, "F2", "false", blockName));
        } else {
            w.addThrottleCommand(new ThrottleSetting(1000, "Forward", "false", blockName));
            w.addThrottleCommand(new ThrottleSetting(2000, "F3", "true", blockName));
            w.addThrottleCommand(new ThrottleSetting(500, "F3", "false", blockName));
            w.addThrottleCommand(new ThrottleSetting(500, "F3", "true", blockName));
            w.addThrottleCommand(new ThrottleSetting(500, "F1", "true", blockName));
        }

        // estimate for blocks of zero length - an estimate of ramp length
        RosterSpeedProfile speedProfile = _speedUtil.getSpeedProfile();
        if (speedProfile != null) {
            float s = speedProfile.getSpeed(_maxSpeed, isForward);
            if (s <= 0.0f || s == Float.POSITIVE_INFINITY) {
                speedProfile = null;
            }
        }
        float totalLen = getTotalLength();
        if (totalLen <= 0) {
            return Bundle.getMessage("zeroPathLength");
        }
        float rampLength = getRampLength(totalLen, speedProfile, isForward);

        if (log.isDebugEnabled()) {
            log.debug("_maxSpeed= {} ({} meters per sec), scale= {}", 
                    _maxSpeed, _speedUtil.modifySpeed(_maxSpeed, Warrant.Normal, isForward), _scale);
            log.debug("Route length= {}, rampLength= {}", totalLen, rampLength);
        }

        float blockLen = bo.getPath().getLengthMm();    // length of path in current block
        blockLen /= 2;

        // start train
        float speedTime = 0;    // ms time to complete speed step from last block
        float noopTime = 0;     // ms time for entry into next block
        float curSpeed = 0;
        // each speed step will last for _intervalTime ms
        float curDistance = 0;  // distance traveled in current block
        float remRamp = rampLength;
        if (log.isDebugEnabled()) {
            log.debug("Start Ramp Up in block \"" + blockName + "\" in "
                    + (int) speedTime + "ms, remRamp= " + remRamp + ", blockLen= " + blockLen);
        }

        while (remRamp > 0.0f) {       // ramp up loop

            curDistance = _speedUtil.getDistanceTraveled(curSpeed, Warrant.Normal, speedTime, isForward);
            while (curDistance < blockLen && curSpeed < _maxSpeed) {
                float dist = _speedUtil.getDistanceTraveled((curSpeed + _minSpeed / 2), Warrant.Normal, _intervalTime, isForward);
                float prevSpeed = curSpeed;
                if ((curSpeed + _minSpeed) > _maxSpeed) {
                    dist = dist * (_maxSpeed - curSpeed) / _minSpeed;
                    curSpeed = _maxSpeed;
                } else {
                    curSpeed += _minSpeed;
                }
                if (curDistance + dist <= blockLen && remRamp > 0.0f) {
                    curDistance += dist;
                    remRamp -= dist;
                    w.addThrottleCommand(new ThrottleSetting((int) speedTime, "Speed", Float.toString(curSpeed), blockName));
                    if (log.isDebugEnabled()) {
                        log.debug(" dist= " + dist);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Ramp Up in block \"" + blockName + "\" to speed " + curSpeed + " in "
                                + (int) speedTime + "ms to reach curDistance= " + curDistance + " with remRamp= " + remRamp);
                    }
                    speedTime = _intervalTime;
                } else {
                    curSpeed = prevSpeed;
                    break;
                }
            }

            // Possible case where curDistance can exceed the length of a block that was just entered.
            // Move to next block and adjust the distance times into that block
            if (curDistance >= blockLen) {
                noopTime = _speedUtil.getTimeForDistance(curSpeed, blockLen,isForward);   // time overrunning block
                speedTime = _speedUtil.getTimeForDistance(curSpeed, curDistance - blockLen, isForward); // time to next block
            } else {
                noopTime = _speedUtil.getTimeForDistance(curSpeed, blockLen - curDistance, isForward);   // time to next block
                speedTime = _intervalTime - noopTime;   // time to next speed change
            }

            // break out here if deceleration is to be started in this block
            if (totalLen - blockLen <= rampLength || curSpeed >= _maxSpeed) {
                break;
            }
            if (remRamp > 0.0f && nextIdx < orders.size()) {
                totalLen -= blockLen;
                if (log.isDebugEnabled()) {
                    log.debug("Leave RampUp block \"{}\"  curDistance= {}, blockLen= {} curSpeed = {} remRamp= {}",
                            blockName, curDistance, blockLen, curSpeed, remRamp);
                }
                bo = orders.get(nextIdx++);
                blockName = bo.getBlock().getDisplayName();
                blockLen = bo.getPath().getLengthMm();
                w.addThrottleCommand(new ThrottleSetting((int) noopTime, "NoOp", "Enter Block", blockName));
                if (log.isDebugEnabled()) log.debug("Enter block \"{}\" noopTime= {}, speedTime= {} blockLen= {}",
                            blockName, noopTime, speedTime, blockLen);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Ramp Up done at block \"{}\" curSpeed={} totalLen={} rampLength={} remRamp={}", 
                    blockName, curSpeed, totalLen, rampLength, remRamp);
        }

        if (totalLen - blockLen > rampLength) {
            totalLen -= blockLen;
            if (nextIdx < orders.size()) {    // not the last block
                bo = orders.get(nextIdx++);
                blockName = bo.getBlock().getDisplayName();
                blockLen = bo.getPath().getLengthMm();
                w.addThrottleCommand(new ThrottleSetting((int) noopTime, "NoOp", "Enter Block", blockName));
                if (log.isDebugEnabled()) {
                    log.debug("Enter block \"" + blockName + "\" noopTime= "
                            + noopTime + ", blockLen= " + blockLen);
                }
                curDistance = 0;
            }

            // run through mid route at max speed
            while (nextIdx < orders.size() && totalLen - blockLen > rampLength) {
                totalLen -= blockLen;
                // constant speed, get time to next block
                noopTime = _speedUtil.getTimeForDistance(curSpeed, blockLen - curDistance, isForward);   // time to next block
                speedTime = _intervalTime - noopTime;   // time to next speed change
                if (log.isDebugEnabled()) {
                    log.debug("Leave MidRoute block \"" + blockName + "\" noopTime= " + noopTime
                            + ", curDistance=" + curDistance + ", blockLen= " + blockLen + ", totalLen= " + totalLen);
                }
                bo = orders.get(nextIdx++);
                blockName = bo.getBlock().getDisplayName();
                blockLen = bo.getPath().getLengthMm();
                if (nextIdx == orders.size()) {
                    blockLen /= 2;
                }
                w.addThrottleCommand(new ThrottleSetting((int) noopTime, "NoOp", "Enter Block", blockName));
                if (log.isDebugEnabled()) {
                    log.debug("Enter block \"" + blockName + "\" noopTime= " + noopTime + ", blockLen= " + blockLen);
                }
                curDistance = 0;
            }
        } // else Start ramp down in current block

        // Ramp down.  use negative delta
        remRamp = rampLength;
        speedTime = _speedUtil.getTimeForDistance(curSpeed, totalLen - rampLength, isForward); // time to next block
        curDistance = totalLen - rampLength;
        if (log.isDebugEnabled()) {
            log.debug("Begin Ramp Down at block \"" + blockName + "\" curDistance= "
                    + curDistance + " SpeedTime= " + (int) speedTime + "ms, blockLen= " + blockLen + ", totalLen= " + totalLen
                    + ", rampLength= " + rampLength + " curSpeed= " + curSpeed);
        }

        while (curSpeed > 0) {
            if (nextIdx == orders.size()) { // at last block
                if (_stageEStop.isSelected()) {
                    w.addThrottleCommand(new ThrottleSetting(0, "Speed", "-0.5", blockName));
                    _intervalTime = 0;
                    break;
                }
            }

            do {
                float nextSpeed = Math.max(curSpeed - _minSpeed, 0);
                float dist = _speedUtil.getDistanceTraveled((curSpeed + nextSpeed / 2), Warrant.Normal, _intervalTime, isForward);
                curDistance += dist;
                curSpeed = nextSpeed;
                remRamp -= dist;
                w.addThrottleCommand(new ThrottleSetting((int) speedTime, "Speed", Float.toString(curSpeed), blockName));
                if (log.isDebugEnabled()) {
                    log.debug("Ramp Down in block \"" + blockName + "\" to speed "
                            + curSpeed + " in " + (int) speedTime + "ms to reach curDistance= " + curDistance + " with remRamp= " + remRamp);
                }
                if (curDistance >= blockLen) {
                    speedTime = _speedUtil.getTimeForDistance(curSpeed, curDistance - blockLen, isForward); // time to next block
                } else {
                    speedTime = _intervalTime;
                }
                noopTime = _intervalTime - speedTime;
            } while (curDistance < blockLen && curSpeed > 0);

            if (nextIdx < orders.size()) {
                totalLen -= blockLen;
                if (log.isDebugEnabled()) {
                    log.debug("Leave RampDown block \"{}\"  curDistance= {}, blockLen= {} curSpeed = {} remRamp= {}",
                            blockName, curDistance, blockLen, curSpeed, remRamp);
                }
                bo = orders.get(nextIdx++);
                blockName = bo.getBlock().getDisplayName();
                blockLen = bo.getPath().getLengthMm();
                if (nextIdx == orders.size()) {
                    blockLen /= 2;
                }
                w.addThrottleCommand(new ThrottleSetting((int) noopTime, "NoOp", "Enter Block", blockName));
                if (log.isDebugEnabled()) {
                    log.debug("Enter block \"" + blockName + "\" noopTime= " + noopTime + ", blockLen= " + blockLen);
                }
                curDistance = _speedUtil.getDistanceTraveled(curSpeed, Warrant.Normal, speedTime, isForward);
            } else {
                if (blockLen == 0) {
                    speedTime = 0;
                }
                break;
            }
        }
        // Ramp down finished
        if (curSpeed > 0) {   // cleanup fractional speeds. insure speed 0 - should never happen.
            log.warn("Ramp down LAST speed change in block \"" + blockName + "\" to speed 0  after "
                    + (int) speedTime + "ms. curSpeed= " + curSpeed + ", curDistance= " + curDistance + ", remRamp= " + remRamp);
            w.addThrottleCommand(new ThrottleSetting((int) speedTime, "Speed", "0.0", blockName));
        }
        w.addThrottleCommand(new ThrottleSetting(500, "F1", "false", blockName));
        w.addThrottleCommand(new ThrottleSetting(1000, "F2", "true", blockName));
        w.addThrottleCommand(new ThrottleSetting(3000, "F2", "false", blockName));
        w.addThrottleCommand(new ThrottleSetting(1000, "F0", "false", blockName));
        /*      if (_addTracker.isSelected()) {
            WarrantTableFrame._defaultAddTracker = true;
            w.addThrottleCommand(new ThrottleSetting(10, "START TRACKER", "", blockName));
        } else {
            WarrantTableFrame._defaultAddTracker = false;
        }*/
        return null;
    }

    private boolean makeAndRunWarrant() {
        String msg = checkLocoAddress();
        if (msg == null) {
            msg = getBoxData();
        }
        if (msg == null) {
            if (log.isDebugEnabled()) {
                log.debug("NXWarrant makeAndRunWarrant calls findRoute()");
            }
            msg = findRoute();
        }
        if (msg != null) {
            JOptionPane.showMessageDialog(this, msg,
                    Bundle.getMessage("WarningTitle"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private final static Logger log = LoggerFactory.getLogger(NXFrame.class.getName());
}
