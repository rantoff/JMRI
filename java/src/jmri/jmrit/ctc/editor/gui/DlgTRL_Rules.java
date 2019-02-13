package jmri.jmrit.ctc.editor.gui;

import jmri.jmrit.ctc.editor.code.AwtWindowProperties;
import jmri.jmrit.ctc.editor.code.CheckJMRIObject;
import jmri.jmrit.ctc.editor.code.CommonSubs;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import jmri.jmrit.ctc.ctcserialdata.CTCSerialData;
import jmri.jmrit.ctc.ctcserialdata.CodeButtonHandlerData;
import jmri.jmrit.ctc.ctcserialdata.ProjectsCommonSubs;
import jmri.jmrit.ctc.ctcserialdata.TrafficLockingEntry;

/**
 *
 * @author Gregory J. Bedlek Copyright (C) 2018, 2019
 * 
 */
public class DlgTRL_Rules extends javax.swing.JDialog {

    /**
     * Creates new form DlgTRL_Rules
     */
    private static final String FORM_PROPERTIES = "DlgTRL_Rules";
    private static final String PREFIX = "_mTRL_";
    private final AwtWindowProperties _mAwtWindowProperties;
    private boolean _mClosedNormally = false;
    public boolean closedNormally() { return _mClosedNormally; }
    private final CodeButtonHandlerData _mCodeButtonHandlerData;
    private final boolean _mIsLeftTraffic;
    private final CTCSerialData _mCTCSerialData;
    private final CheckJMRIObject _mCheckJMRIObject;
    private final ArrayList<Integer> _mArrayListOfSelectableOSSectionUniqueIDs;
    private final DefaultListModel<String> _mDefaultListModel;
    private boolean _mAddNewPressed;
    
    private final ArrayList<String> _mDefaultListModelOrig = new ArrayList<> ();
    private void initOrig() {
        int defaultListModelSize = _mDefaultListModel.getSize();
        for (int index = 0; index < defaultListModelSize; index++) {
            _mDefaultListModelOrig.add(_mDefaultListModel.get(index));
        }
    }
    private boolean dataChanged() {
        int defaultListModelSize = _mDefaultListModel.getSize();
        if (defaultListModelSize != _mDefaultListModelOrig.size()) return true;
        for (int index = 0; index < defaultListModelSize; index++) {
            if (!_mDefaultListModel.get(index).equals(_mDefaultListModelOrig.get(index))) return true;
        }
        return false;
    }
    
    public DlgTRL_Rules(java.awt.Frame parent, boolean modal,
                        AwtWindowProperties awtWindowProperties, CodeButtonHandlerData codeButtonHandlerData,
                        boolean isLeftTraffic,
                        CTCSerialData ctcSerialData, CheckJMRIObject checkJMRIObject) {
        super(parent, modal);
        initComponents();
        _mAwtWindowProperties = awtWindowProperties;
        _mCodeButtonHandlerData = codeButtonHandlerData;
        _mIsLeftTraffic = isLeftTraffic;
        _mCTCSerialData = ctcSerialData;
        _mCheckJMRIObject = checkJMRIObject;
        _mArrayListOfSelectableOSSectionUniqueIDs = CommonSubs.getArrayListOfSelectableOSSectionUniqueIDs(_mCTCSerialData.getCodeButtonHandlerDataArrayList());
        _mDefaultListModel = new DefaultListModel<>();
        _mTRL_TrafficLockingRulesSSVList.setModel(_mDefaultListModel);
        
        String trafficLockingRulesSSVList;
        String identifier = codeButtonHandlerData.myShortStringNoComma();
        if (isLeftTraffic) {
            this.setTitle("Edit Left traffic locking rules for " + identifier);
            _mRulesInfo.setText("If ANY of these rules are true, then the Left Traffic Direction Lever is allowed to be \"coded\".");
            trafficLockingRulesSSVList = _mCodeButtonHandlerData._mTRL_LeftTrafficLockingRulesSSVList;
        } else {
            this.setTitle("Edit Right traffic locking rules for " + identifier);
            _mRulesInfo.setText("If ANY of these rules are true, then the Right Traffic Direction Lever is allowed to be \"coded\".");
            trafficLockingRulesSSVList = _mCodeButtonHandlerData._mTRL_RightTrafficLockingRulesSSVList;
        }
//  Once you specify a model, then functions like JList.setListData may update the screen, but the model
//  DOES NOT SEE ANY OF THE DATA!  Therefore, I have to load the data via the model instead of directly:
        _mDefaultListModel.clear(); // Superflous but doesn't hurt in case GUI designer put something in there.....
        int ruleNumber = 1;
        for (String aString : ProjectsCommonSubs.getArrayListFromSSV(trafficLockingRulesSSVList)) {
            aString = renumberCSVString(aString, ruleNumber++);
            _mDefaultListModel.addElement(aString);
        }
        initOrig();
        _mAwtWindowProperties.setWindowState((java.awt.Window)this, FORM_PROPERTIES);        
        
        _mOccupancyExternalSensor1.setText("");
        _mOccupancyExternalSensor2.setText("");
        _mOccupancyExternalSensor3.setText("");
        _mOccupancyExternalSensor4.setText("");
        _mOccupancyExternalSensor5.setText("");
        _mOccupancyExternalSensor6.setText("");
        _mOccupancyExternalSensor7.setText("");
        _mOccupancyExternalSensor8.setText("");
        _mOccupancyExternalSensor9.setText("");
        _mOptionalExternalSensor1.setText("");
        _mOptionalExternalSensor2.setText("");
        CommonSubs.populateJComboBoxWithColumnDescriptions(_mOS_NumberEntry1, _mCTCSerialData);
        CommonSubs.populateJComboBoxWithColumnDescriptions(_mOS_NumberEntry2, _mCTCSerialData);
        CommonSubs.populateJComboBoxWithColumnDescriptions(_mOS_NumberEntry3, _mCTCSerialData);
        CommonSubs.populateJComboBoxWithColumnDescriptions(_mOS_NumberEntry4, _mCTCSerialData);
        CommonSubs.populateJComboBoxWithColumnDescriptions(_mOS_NumberEntry5, _mCTCSerialData);
        String[] normalAndReverse = new String[] { Bundle.getMessage("TLE_Normal"), Bundle.getMessage("TLE_Reverse") }; // NOI18N
        _mSwitchAlignment1.setModel(new javax.swing.DefaultComboBoxModel<>(normalAndReverse));
        _mSwitchAlignment2.setModel(new javax.swing.DefaultComboBoxModel<>(normalAndReverse));
        _mSwitchAlignment3.setModel(new javax.swing.DefaultComboBoxModel<>(normalAndReverse));
        _mSwitchAlignment4.setModel(new javax.swing.DefaultComboBoxModel<>(normalAndReverse));
        _mSwitchAlignment5.setModel(new javax.swing.DefaultComboBoxModel<>(normalAndReverse));
        enableTopPart(true);
        _mEditBelow.setEnabled(false);
        _mDelete.setEnabled(false);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _mSaveAndClose = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        _mTRL_TrafficLockingRulesSSVList = new javax.swing.JList<>();
        _mAddNew = new javax.swing.JButton();
        _mEditBelow = new javax.swing.JButton();
        _mDelete = new javax.swing.JButton();
        _mGroupingListAddReplace = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        _mOptionalExternalSensor1 = new javax.swing.JTextField();
        _mOptionalExternalSensor2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        _mRulesInfo = new javax.swing.JLabel();
        _mCancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        _mRuleEnabled = new javax.swing.JCheckBox();
        _mEnableALLRules = new javax.swing.JButton();
        _mDisableALLRules = new javax.swing.JButton();
        _mOS_NumberEntry1 = new javax.swing.JComboBox<>();
        _mOS_NumberEntry2 = new javax.swing.JComboBox<>();
        _mOS_NumberEntry3 = new javax.swing.JComboBox<>();
        _mOS_NumberEntry4 = new javax.swing.JComboBox<>();
        _mOS_NumberEntry5 = new javax.swing.JComboBox<>();
        _mSwitchAlignment1 = new javax.swing.JComboBox<>();
        _mSwitchAlignment2 = new javax.swing.JComboBox<>();
        _mSwitchAlignment3 = new javax.swing.JComboBox<>();
        _mSwitchAlignment4 = new javax.swing.JComboBox<>();
        _mSwitchAlignment5 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        _mOccupancyExternalSensor2 = new javax.swing.JTextField();
        _mOccupancyExternalSensor1 = new javax.swing.JTextField();
        _mOccupancyExternalSensor3 = new javax.swing.JTextField();
        _mOccupancyExternalSensor4 = new javax.swing.JTextField();
        _mOccupancyExternalSensor5 = new javax.swing.JTextField();
        _mOccupancyExternalSensor6 = new javax.swing.JTextField();
        _mOccupancyExternalSensor7 = new javax.swing.JTextField();
        _mOccupancyExternalSensor8 = new javax.swing.JTextField();
        _mOccupancyExternalSensor9 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getMessage("TitleDlgTRLRules"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        _mSaveAndClose.setText(Bundle.getMessage("ButtonSaveClose"));
        _mSaveAndClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mSaveAndCloseActionPerformed(evt);
            }
        });

        jLabel2.setText(Bundle.getMessage("LabelDlgTRLRulesRules"));

        _mTRL_TrafficLockingRulesSSVList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                _mTRL_TrafficLockingRulesSSVListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(_mTRL_TrafficLockingRulesSSVList);

        _mAddNew.setText(Bundle.getMessage("ButtonAddNew"));
        _mAddNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mAddNewActionPerformed(evt);
            }
        });

        _mEditBelow.setText(Bundle.getMessage("ButtonEditBelow"));
        _mEditBelow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mEditBelowActionPerformed(evt);
            }
        });

        _mDelete.setText(Bundle.getMessage("ButtonDelete"));
        _mDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mDeleteActionPerformed(evt);
            }
        });

        _mGroupingListAddReplace.setText(Bundle.getMessage("ButtonDlgTRLRulesUpdate"));
        _mGroupingListAddReplace.setEnabled(false);
        _mGroupingListAddReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mGroupingListAddReplaceActionPerformed(evt);
            }
        });

        jLabel1.setText(Bundle.getMessage("InfoDlgTRLRulesSep1"));

        jLabel8.setText(Bundle.getMessage("InfoDlgTRLRulesSection"));

        jLabel7.setText(Bundle.getMessage("InfoDlgTRLRulesSep2"));

        _mRulesInfo.setText(Bundle.getMessage("InfoDlgTRLRulesNote1"));

        _mCancel.setText(Bundle.getMessage("ButtonCancel"));
        _mCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mCancelActionPerformed(evt);
            }
        });

        jLabel4.setText(Bundle.getMessage("InfoDlgTRLRulesNote2"));

        jLabel10.setText(Bundle.getMessage("InfoDlgTRLRulesNote3"));

        _mRuleEnabled.setText(Bundle.getMessage("LabelDlgTRLRulesEnabled"));

        _mEnableALLRules.setText(Bundle.getMessage("ButtonDlgTRLRulesEnable"));
        _mEnableALLRules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mEnableALLRulesActionPerformed(evt);
            }
        });

        _mDisableALLRules.setText(Bundle.getMessage("ButtonDlgTRLRulesDisable"));
        _mDisableALLRules.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _mDisableALLRulesActionPerformed(evt);
            }
        });

        jLabel5.setText(Bundle.getMessage("InfoDlgTRLRulesSensor"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(_mRuleEnabled)
                                .addGap(18, 18, 18)
                                .addComponent(_mEnableALLRules, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(_mDisableALLRules, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(_mRulesInfo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 786, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(_mAddNew, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(_mEditBelow)
                                    .addComponent(_mDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(_mOptionalExternalSensor1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOptionalExternalSensor2, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(43, 43, 43)
                                        .addComponent(_mGroupingListAddReplace, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(_mCancel))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(_mOS_NumberEntry1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOS_NumberEntry2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOS_NumberEntry3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOS_NumberEntry4, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOS_NumberEntry5, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel8)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(_mSwitchAlignment1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mSwitchAlignment2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mSwitchAlignment3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mSwitchAlignment4, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mSwitchAlignment5, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel5)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(_mOccupancyExternalSensor1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOccupancyExternalSensor2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOccupancyExternalSensor3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOccupancyExternalSensor4, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOccupancyExternalSensor5, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(_mOccupancyExternalSensor6, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOccupancyExternalSensor7, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOccupancyExternalSensor8, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(_mOccupancyExternalSensor9, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel10))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(_mSaveAndClose)
                .addGap(397, 397, 397))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(_mAddNew)
                        .addGap(18, 18, 18)
                        .addComponent(_mEditBelow)
                        .addGap(18, 18, 18)
                        .addComponent(_mDelete))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mRulesInfo)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mRuleEnabled)
                    .addComponent(_mEnableALLRules)
                    .addComponent(_mDisableALLRules))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mOS_NumberEntry1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOS_NumberEntry2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOS_NumberEntry3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOS_NumberEntry4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOS_NumberEntry5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mSwitchAlignment1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mSwitchAlignment2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mSwitchAlignment3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mSwitchAlignment4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mSwitchAlignment5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mOccupancyExternalSensor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOccupancyExternalSensor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOccupancyExternalSensor3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOccupancyExternalSensor4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOccupancyExternalSensor5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mOccupancyExternalSensor6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOccupancyExternalSensor7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOccupancyExternalSensor8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOccupancyExternalSensor9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_mOptionalExternalSensor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mOptionalExternalSensor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(_mGroupingListAddReplace)
                    .addComponent(_mCancel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(_mSaveAndClose)
                .addGap(13, 13, 13))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void _mSaveAndCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mSaveAndCloseActionPerformed
        int size = _mDefaultListModel.getSize();
        String resultString = "";
        for (int index = 0; index < size; index++) {
            String thisEntry = _mDefaultListModel.getElementAt(index);
            resultString = (0 == index) ? thisEntry : resultString + ProjectsCommonSubs.SSV_SEPARATOR + thisEntry;
        }
        if (_mIsLeftTraffic) { _mCodeButtonHandlerData._mTRL_LeftTrafficLockingRulesSSVList = resultString; }
        else { _mCodeButtonHandlerData._mTRL_RightTrafficLockingRulesSSVList = resultString; }
        _mClosedNormally = true;
        _mAwtWindowProperties.saveWindowState(this, FORM_PROPERTIES);
        dispose();
    }//GEN-LAST:event__mSaveAndCloseActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        _mAwtWindowProperties.saveWindowState(this, FORM_PROPERTIES);
        if (CommonSubs.allowClose(this, dataChanged())) dispose();
    }//GEN-LAST:event_formWindowClosing

    private void _mTRL_TrafficLockingRulesSSVListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event__mTRL_TrafficLockingRulesSSVListValueChanged
        if (_mTRL_TrafficLockingRulesSSVList.isSelectionEmpty()) {
            _mEditBelow.setEnabled(false);        
            _mDelete.setEnabled(false);
        } else {
            _mEditBelow.setEnabled(true);        
            _mDelete.setEnabled(true);
        }
    }//GEN-LAST:event__mTRL_TrafficLockingRulesSSVListValueChanged

    private void _mAddNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mAddNewActionPerformed
        _mAddNewPressed = true;
        enableTopPart(false);
        _mTRL_TrafficLockingRulesSSVList.setEnabled(false);
        _mTRL_TrafficLockingRulesSSVList.clearSelection();
        _mOptionalExternalSensor1.setText("");
        _mOptionalExternalSensor2.setText("");
        _mOccupancyExternalSensor1.setText("");
        _mOccupancyExternalSensor2.setText("");
        _mOccupancyExternalSensor3.setText("");
        _mOccupancyExternalSensor4.setText("");
        _mOccupancyExternalSensor5.setText("");
        _mOccupancyExternalSensor6.setText("");
        _mOccupancyExternalSensor7.setText("");
        _mOccupancyExternalSensor8.setText("");
        _mOccupancyExternalSensor9.setText("");
        _mOS_NumberEntry1.setSelectedIndex(0);
        _mOS_NumberEntry2.setSelectedIndex(0);
        _mOS_NumberEntry3.setSelectedIndex(0);
        _mOS_NumberEntry4.setSelectedIndex(0);
        _mOS_NumberEntry5.setSelectedIndex(0);
        _mSwitchAlignment1.setSelectedIndex(0);
        _mSwitchAlignment2.setSelectedIndex(0);
        _mSwitchAlignment3.setSelectedIndex(0);
        _mSwitchAlignment4.setSelectedIndex(0);
        _mSwitchAlignment5.setSelectedIndex(0);
        _mGroupingListAddReplace.setText("Add this to the end of Rules list above");
        _mGroupingListAddReplace.setEnabled(true);
        _mRuleEnabled.setSelected(true);
        _mOS_NumberEntry1.requestFocusInWindow();
    }//GEN-LAST:event__mAddNewActionPerformed

    private void _mEditBelowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mEditBelowActionPerformed
        _mAddNewPressed = false;
        int selectedIndex = _mTRL_TrafficLockingRulesSSVList.getSelectedIndex();
        enableTopPart(false);
        _mTRL_TrafficLockingRulesSSVList.setEnabled(false);
        
        TrafficLockingEntry trafficLockingEntry = new TrafficLockingEntry(_mDefaultListModel.get(selectedIndex));
        _mOccupancyExternalSensor1.setText(trafficLockingEntry._mOccupancyExternalSensor1);
        _mOccupancyExternalSensor2.setText(trafficLockingEntry._mOccupancyExternalSensor2);
        _mOccupancyExternalSensor3.setText(trafficLockingEntry._mOccupancyExternalSensor3);
        _mOccupancyExternalSensor4.setText(trafficLockingEntry._mOccupancyExternalSensor4);
        _mOccupancyExternalSensor5.setText(trafficLockingEntry._mOccupancyExternalSensor5);
        _mOccupancyExternalSensor6.setText(trafficLockingEntry._mOccupancyExternalSensor6);
        _mOccupancyExternalSensor7.setText(trafficLockingEntry._mOccupancyExternalSensor7);
        _mOccupancyExternalSensor8.setText(trafficLockingEntry._mOccupancyExternalSensor8);
        _mOccupancyExternalSensor9.setText(trafficLockingEntry._mOccupancyExternalSensor9);
        _mOptionalExternalSensor1.setText(trafficLockingEntry._mOptionalExternalSensor1);
        _mOptionalExternalSensor2.setText(trafficLockingEntry._mOptionalExternalSensor2);
        int uniqueID;
        uniqueID = ProjectsCommonSubs.getIntFromStringNoThrow(trafficLockingEntry._mUniqueID1, -1);   // Technically should NEVER throw or return default, but for safety.  Default will NEVER be found!
        CommonSubs.setSelectedIndexOfJComboBoxViaUniqueID(_mOS_NumberEntry1, _mCTCSerialData, uniqueID);
        uniqueID = ProjectsCommonSubs.getIntFromStringNoThrow(trafficLockingEntry._mUniqueID2, -1);   // Technically should NEVER throw or return default, but for safety.  Default will NEVER be found!
        CommonSubs.setSelectedIndexOfJComboBoxViaUniqueID(_mOS_NumberEntry2, _mCTCSerialData, uniqueID);
        uniqueID = ProjectsCommonSubs.getIntFromStringNoThrow(trafficLockingEntry._mUniqueID3, -1);   // Technically should NEVER throw or return default, but for safety.  Default will NEVER be found!
        CommonSubs.setSelectedIndexOfJComboBoxViaUniqueID(_mOS_NumberEntry3, _mCTCSerialData, uniqueID);
        uniqueID = ProjectsCommonSubs.getIntFromStringNoThrow(trafficLockingEntry._mUniqueID4, -1);   // Technically should NEVER throw or return default, but for safety.  Default will NEVER be found!
        CommonSubs.setSelectedIndexOfJComboBoxViaUniqueID(_mOS_NumberEntry4, _mCTCSerialData, uniqueID);
        uniqueID = ProjectsCommonSubs.getIntFromStringNoThrow(trafficLockingEntry._mUniqueID5, -1);   // Technically should NEVER throw or return default, but for safety.  Default will NEVER be found!
        CommonSubs.setSelectedIndexOfJComboBoxViaUniqueID(_mOS_NumberEntry5, _mCTCSerialData, uniqueID);
        _mSwitchAlignment1.setSelectedItem(trafficLockingEntry._mSwitchAlignment1);
        _mSwitchAlignment2.setSelectedItem(trafficLockingEntry._mSwitchAlignment2);
        _mSwitchAlignment3.setSelectedItem(trafficLockingEntry._mSwitchAlignment3);
        _mSwitchAlignment4.setSelectedItem(trafficLockingEntry._mSwitchAlignment4);
        _mSwitchAlignment5.setSelectedItem(trafficLockingEntry._mSwitchAlignment5);
        _mGroupingListAddReplace.setText("Update selected item in the Rules list above");
        _mGroupingListAddReplace.setEnabled(true);
        _mRuleEnabled.setSelected(!trafficLockingEntry._mRuleEnabled.equals(Bundle.getMessage("TLE_RuleDisabled")));  // NOI18N  Default if invalid is ENABLED
        _mOS_NumberEntry1.requestFocusInWindow();
    }//GEN-LAST:event__mEditBelowActionPerformed

    private void _mDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mDeleteActionPerformed
        _mDefaultListModel.remove(_mTRL_TrafficLockingRulesSSVList.getSelectedIndex());
        for (int index = 0; index < _mDefaultListModel.size(); index++) {
            _mDefaultListModel.set(index, renumberCSVString(_mDefaultListModel.get(index), index + 1));
        }
        enableTopPart(true);
    }//GEN-LAST:event__mDeleteActionPerformed

    private void _mGroupingListAddReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mGroupingListAddReplaceActionPerformed
        TrafficLockingEntry trafficLockingEntry = new TrafficLockingEntry(  _mRuleEnabled.isSelected() ? Bundle.getMessage("TLE_RuleEnabled") : Bundle.getMessage("TLE_RuleDisabled"),  // NOI18N
                                                                            (String)_mSwitchAlignment1.getSelectedItem(),
                                                                            (String)_mSwitchAlignment2.getSelectedItem(),
                                                                            (String)_mSwitchAlignment3.getSelectedItem(),
                                                                            (String)_mSwitchAlignment4.getSelectedItem(),
                                                                            (String)_mSwitchAlignment5.getSelectedItem(),
                                                                            _mOccupancyExternalSensor1.getText(),
                                                                            _mOccupancyExternalSensor2.getText(),
                                                                            _mOccupancyExternalSensor3.getText(),
                                                                            _mOccupancyExternalSensor4.getText(),
                                                                            _mOccupancyExternalSensor5.getText(),
                                                                            _mOccupancyExternalSensor6.getText(),
                                                                            _mOccupancyExternalSensor7.getText(),
                                                                            _mOccupancyExternalSensor8.getText(),
                                                                            _mOccupancyExternalSensor9.getText(),
                                                                            _mOptionalExternalSensor1.getText(),
                                                                            _mOptionalExternalSensor2.getText());
        
        CheckJMRIObject.VerifyClassReturnValue verifyClassReturnValue = _mCheckJMRIObject.verifyClass(trafficLockingEntry);
        if (verifyClassReturnValue != null) { // Error:
            JOptionPane.showMessageDialog(this, verifyClassReturnValue.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
// Any uninitialized are null, and thats OK for "constructCSVStringFromArrayList":

        int osNumberSelectedIndex;
        osNumberSelectedIndex = _mOS_NumberEntry1.getSelectedIndex();
        if (osNumberSelectedIndex > 0) { // Something selected (-1 = none, 0 = blank i.e. none also).
            trafficLockingEntry._mUserText1 = (String)_mOS_NumberEntry1.getSelectedItem();
            trafficLockingEntry._mUniqueID1 = _mArrayListOfSelectableOSSectionUniqueIDs.get(osNumberSelectedIndex - 1).toString();
        }
        osNumberSelectedIndex = _mOS_NumberEntry2.getSelectedIndex();
        if (osNumberSelectedIndex > 0) { // Something selected (-1 = none, 0 = blank i.e. none also).
            trafficLockingEntry._mUserText2 = (String)_mOS_NumberEntry2.getSelectedItem();
            trafficLockingEntry._mUniqueID2 = _mArrayListOfSelectableOSSectionUniqueIDs.get(osNumberSelectedIndex - 1).toString();
        }
        osNumberSelectedIndex = _mOS_NumberEntry3.getSelectedIndex();
        if (osNumberSelectedIndex > 0) { // Something selected (-1 = none, 0 = blank i.e. none also).
            trafficLockingEntry._mUserText3 = (String)_mOS_NumberEntry3.getSelectedItem();
            trafficLockingEntry._mUniqueID3 = _mArrayListOfSelectableOSSectionUniqueIDs.get(osNumberSelectedIndex - 1).toString();
        }
        osNumberSelectedIndex = _mOS_NumberEntry4.getSelectedIndex();
        if (osNumberSelectedIndex > 0) { // Something selected (-1 = none, 0 = blank i.e. none also).
            trafficLockingEntry._mUserText4 = (String)_mOS_NumberEntry4.getSelectedItem();
            trafficLockingEntry._mUniqueID4 = _mArrayListOfSelectableOSSectionUniqueIDs.get(osNumberSelectedIndex - 1).toString();
        }
        osNumberSelectedIndex = _mOS_NumberEntry5.getSelectedIndex();
        if (osNumberSelectedIndex > 0) { // Something selected (-1 = none, 0 = blank i.e. none also).
            trafficLockingEntry._mUserText5 = (String)_mOS_NumberEntry5.getSelectedItem();
            trafficLockingEntry._mUniqueID5 = _mArrayListOfSelectableOSSectionUniqueIDs.get(osNumberSelectedIndex - 1).toString();
        }
        
        _mGroupingListAddReplace.setEnabled(false);
        enableTopPart(true);
        if (_mAddNewPressed) {
            trafficLockingEntry._mUserRuleNumber = getRuleNumberString(_mDefaultListModel.size() + 1);
            String newValue = trafficLockingEntry.toCSVString();
            _mDefaultListModel.addElement(newValue);
        }
        else {
            int selectedIndex = _mTRL_TrafficLockingRulesSSVList.getSelectedIndex();
            trafficLockingEntry._mUserRuleNumber = getRuleNumberString(selectedIndex + 1);
            String newValue = trafficLockingEntry.toCSVString();
            _mDefaultListModel.set(selectedIndex, newValue);
        }
        _mTRL_TrafficLockingRulesSSVList.setEnabled(true);
    }//GEN-LAST:event__mGroupingListAddReplaceActionPerformed

    private void lazy1(javax.swing.JComboBox<String> jComboBox) {
//  Verify user didn't select "self", since I don't want to screw up array indexes by eliminating it:
        int selectedIndex = jComboBox.getSelectedIndex();
        if (selectedIndex > 0) { // None and skip blank entry
            int osSectionSelectedUniqueID = _mArrayListOfSelectableOSSectionUniqueIDs.get(selectedIndex - 1);  // Correct for blank entry
            if (osSectionSelectedUniqueID == _mCodeButtonHandlerData._mUniqueID) {
                jComboBox.setSelectedIndex(0); // Back to blank!
            }
        }
    }
    
    private void _mCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mCancelActionPerformed
        enableTopPart(true);
        _mTRL_TrafficLockingRulesSSVList.setEnabled(true);
    }//GEN-LAST:event__mCancelActionPerformed

    private void _mEnableALLRulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mEnableALLRulesActionPerformed
        for (int index = 0; index < _mDefaultListModel.getSize(); index++) {
            TrafficLockingEntry trafficLockingEntry = new TrafficLockingEntry(_mDefaultListModel.get(index));
            trafficLockingEntry._mRuleEnabled = Bundle.getMessage("TLE_RuleEnabled");   // NOI18N
            _mDefaultListModel.set(index, trafficLockingEntry.toCSVString());
        }
    }//GEN-LAST:event__mEnableALLRulesActionPerformed

    private void _mDisableALLRulesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__mDisableALLRulesActionPerformed
        for (int index = 0; index < _mDefaultListModel.getSize(); index++) {
            TrafficLockingEntry trafficLockingEntry = new TrafficLockingEntry(_mDefaultListModel.get(index));
            trafficLockingEntry._mRuleEnabled = Bundle.getMessage("TLE_RuleDisabled");  // NOI18N
            _mDefaultListModel.set(index, trafficLockingEntry.toCSVString());
        }
    }//GEN-LAST:event__mDisableALLRulesActionPerformed

    private String renumberCSVString(String aString, int ruleNumber) {
            TrafficLockingEntry trafficLockingEntry = new TrafficLockingEntry(aString);
            trafficLockingEntry._mUserRuleNumber = getRuleNumberString(ruleNumber);
            return trafficLockingEntry.toCSVString();
    }
    private String getRuleNumberString(int ruleNumber) { return " Rule #:" + Integer.toString(ruleNumber); }
    
    private void enableTopPart(boolean enabled) {
        _mAddNew.setEnabled(enabled);
        _mOccupancyExternalSensor1.setEnabled(!enabled);
        _mOccupancyExternalSensor2.setEnabled(!enabled);
        _mOccupancyExternalSensor3.setEnabled(!enabled);
        _mOccupancyExternalSensor4.setEnabled(!enabled);
        _mOccupancyExternalSensor5.setEnabled(!enabled);
        _mOccupancyExternalSensor6.setEnabled(!enabled);
        _mOccupancyExternalSensor7.setEnabled(!enabled);
        _mOccupancyExternalSensor8.setEnabled(!enabled);
        _mOccupancyExternalSensor9.setEnabled(!enabled);
        _mOptionalExternalSensor1.setEnabled(!enabled);
        _mOptionalExternalSensor2.setEnabled(!enabled);
        _mOS_NumberEntry1.setEnabled(!enabled);
        _mOS_NumberEntry2.setEnabled(!enabled);
        _mOS_NumberEntry3.setEnabled(!enabled);
        _mOS_NumberEntry4.setEnabled(!enabled);
        _mOS_NumberEntry5.setEnabled(!enabled);
        _mRuleEnabled.setEnabled(!enabled);
        _mSwitchAlignment1.setEnabled(!enabled);
        _mSwitchAlignment2.setEnabled(!enabled);
        _mSwitchAlignment3.setEnabled(!enabled);
        _mSwitchAlignment4.setEnabled(!enabled);
        _mSwitchAlignment5.setEnabled(!enabled);
        _mGroupingListAddReplace.setEnabled(!enabled);
        _mCancel.setEnabled(!enabled);
        _mSaveAndClose.setEnabled(enabled);
        
        if (enabled) this.getRootPane().setDefaultButton(_mSaveAndClose);
        else this.getRootPane().setDefaultButton(_mGroupingListAddReplace);
    }
    
    private static ArrayList<String> getArrayListOfSelectableSwitchDirectionIndicators(CodeButtonHandlerData codeButtonHandlerData) {
        ArrayList<String> returnValue = new ArrayList<>();
        if (!codeButtonHandlerData._mSWDI_NormalInternalSensor.isEmpty()) {
            returnValue.add(codeButtonHandlerData._mSWDI_NormalInternalSensor);
        }
        if (!codeButtonHandlerData._mSWDI_ReversedInternalSensor.isEmpty()) {
            returnValue.add(codeButtonHandlerData._mSWDI_ReversedInternalSensor);
        }
        return returnValue;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _mAddNew;
    private javax.swing.JButton _mCancel;
    private javax.swing.JButton _mDelete;
    private javax.swing.JButton _mDisableALLRules;
    private javax.swing.JButton _mEditBelow;
    private javax.swing.JButton _mEnableALLRules;
    private javax.swing.JButton _mGroupingListAddReplace;
    private javax.swing.JComboBox<String> _mOS_NumberEntry1;
    private javax.swing.JComboBox<String> _mOS_NumberEntry2;
    private javax.swing.JComboBox<String> _mOS_NumberEntry3;
    private javax.swing.JComboBox<String> _mOS_NumberEntry4;
    private javax.swing.JComboBox<String> _mOS_NumberEntry5;
    private javax.swing.JTextField _mOccupancyExternalSensor1;
    private javax.swing.JTextField _mOccupancyExternalSensor2;
    private javax.swing.JTextField _mOccupancyExternalSensor3;
    private javax.swing.JTextField _mOccupancyExternalSensor4;
    private javax.swing.JTextField _mOccupancyExternalSensor5;
    private javax.swing.JTextField _mOccupancyExternalSensor6;
    private javax.swing.JTextField _mOccupancyExternalSensor7;
    private javax.swing.JTextField _mOccupancyExternalSensor8;
    private javax.swing.JTextField _mOccupancyExternalSensor9;
    private javax.swing.JTextField _mOptionalExternalSensor1;
    private javax.swing.JTextField _mOptionalExternalSensor2;
    private javax.swing.JCheckBox _mRuleEnabled;
    private javax.swing.JLabel _mRulesInfo;
    private javax.swing.JButton _mSaveAndClose;
    private javax.swing.JComboBox<String> _mSwitchAlignment1;
    private javax.swing.JComboBox<String> _mSwitchAlignment2;
    private javax.swing.JComboBox<String> _mSwitchAlignment3;
    private javax.swing.JComboBox<String> _mSwitchAlignment4;
    private javax.swing.JComboBox<String> _mSwitchAlignment5;
    private javax.swing.JList<String> _mTRL_TrafficLockingRulesSSVList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
