package jmri.jmrit.operations.automation.actions;

import java.text.MessageFormat;
import javax.swing.JOptionPane;
import jmri.jmrit.operations.automation.Automation;
import jmri.jmrit.operations.automation.AutomationItem;
import jmri.jmrit.operations.routes.RouteLocation;
import jmri.jmrit.operations.trains.Train;

public abstract class Action {

    public static final String ACTION_COMPLETE_CHANGED_PROPERTY = "actionComplete"; // NOI18N
    public static final String ACTION_HALT_CHANGED_PROPERTY = "actionHalt"; // NOI18N
    public static final String ACTION_RUNNING_CHANGED_PROPERTY = "actionRunning"; // NOI18N
    public static final String ACTION_GOTO_CHANGED_PROPERTY = "actionGoto"; // NOI18N

    public static final int OKAY = 0;
    public static final int HALT = 1;
    public static final int CLOSED = JOptionPane.CLOSED_OPTION; // -1
    public static final int NO_MESSAGE_SENT = -2;
    public static final int FINISH_FAILED = -3;

    protected AutomationItem _automationItem = null;

    abstract public int getCode();

    abstract public String getName();

    abstract public void doAction();

    abstract public void cancelAction();

    /**
     * for combo boxes
     */
    public String toString() {
        return getName();
    }

    /**
     * Mask off menu bits.
     * 
     * @param code
     * @return code & ActionCodes.CODE_MASK
     */
    protected int getCode(int code) {
        return code & ActionCodes.CODE_MASK;
    }

    public boolean isTrainMenuEnabled() {
        return (getCode() & ActionCodes.ENABLE_TRAINS) == ActionCodes.ENABLE_TRAINS;
    }

    public boolean isRouteMenuEnabled() {
        return (getCode() & ActionCodes.ENABLE_ROUTES) == ActionCodes.ENABLE_ROUTES;
    }

    public boolean isMessageOkEnabled() {
        return (getCode() & ActionCodes.OK_MESSAGE) == ActionCodes.OK_MESSAGE;
    }

    public boolean isMessageFailEnabled() {
        return (getCode() & ActionCodes.FAIL_MESSAGE) == ActionCodes.FAIL_MESSAGE;
    }

    public boolean isAutomationMenuEnabled() {
        return (getCode() & ActionCodes.ENABLE_AUTOMATION_LIST) == ActionCodes.ENABLE_AUTOMATION_LIST;
    }

    public boolean isGotoMenuEnabled() {
        return (getCode() & ActionCodes.ENABLE_GOTO_LIST) == ActionCodes.ENABLE_GOTO_LIST;
    }

    public void setAutomationItem(AutomationItem item) {
        _automationItem = item;
    }

    public AutomationItem getAutomationItem() {
        return _automationItem;
    }

    public String getActionString() {
        return getFormatedMessage("{0} {1} {2} {3} {4}");
    }

    public void setRunning(boolean running) {
        if (getAutomationItem() != null) {
            boolean old = getAutomationItem().isActionRunning();
            getAutomationItem().setActionRunning(running);
            if (old != running) {
                firePropertyChange(ACTION_RUNNING_CHANGED_PROPERTY, old, running);
            }
        }
    }

    /**
     * Completes the action by displaying the correct message if there's one.
     * Will halt if the option to halt the automation is enabled or the user
     * requested the automation to halt.
     * 
     * @param success true if action succeeded
     * @return OKAY, HALT, CLOSED, NO_MESSAGE_SENT, FINISH_FAILED
     */
    public int finishAction(boolean success) {
        int response = FINISH_FAILED;
        if (getAutomationItem() != null) {
            setRunning(true);
            getAutomationItem().setActionSuccessful(success);
            setRunning(false);
            String message = getAutomationItem().getMessage();
            Object[] buttons = new Object[]{Bundle.getMessage("OK"), Bundle.getMessage("HALT")};
            if (!success) {
                message = getAutomationItem().getMessageFail();
                if (getAutomationItem().isHaltFailureEnabled()) {
                    buttons = new Object[]{Bundle.getMessage("HALT")}; // Must halt, only the HALT button shown
                }
            }
            response = sendMessage(message, buttons, success);
            if (response == HALT || (!success && getAutomationItem().isHaltFailureEnabled())) {
                firePropertyChange(ACTION_HALT_CHANGED_PROPERTY, !success, success);
            } else {
                firePropertyChange(ACTION_COMPLETE_CHANGED_PROPERTY, !success, success);
            }
        }
        return response;
    }

    /**
     * Displays message if there's one.
     * 
     * @param buttons the buttons to display
     * @return which button was pressed, NO_MESSAGE_SENT, CLOSED
     */
    public int sendMessage(String message, Object[] buttons, boolean success) {
        int response = NO_MESSAGE_SENT;
        if (getAutomationItem() != null && !message.equals(AutomationItem.NONE)) {
            // use formatter to create title
            String title = getAutomationItem().getId() +
                    " " +
                    (success ? "" : Bundle.getMessage("Failed")) +
                    " {0} {1} {2} {3} {4}";

            response = JOptionPane.showOptionDialog(null, getFormatedMessage(message), getFormatedMessage(title),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, buttons
                    , null);
        }
        return response;
    }

    public String getFormatedMessage(String message) {
        String trainName = "";
        Train train = getAutomationItem().getTrain();
        if (train != null) {
            trainName = " " + train.getName();
        }
        String routeLocationName = "";
        RouteLocation rl = getAutomationItem().getRouteLocation();
        if (rl != null) {
            routeLocationName = " " + rl.getName();
        }
        String automationName = "";
        Automation automation = getAutomationItem().getAutomationToRun();
        if (automation != null) {
            automationName = " " + automation.getName();
        }
        String itemId = "";
        AutomationItem item = getAutomationItem().getGotoAutomationItem();
        if (item != null) {
            itemId = " " + item.getId();
        }
        return MessageFormat.format(message, new Object[]{getName(), trainName, routeLocationName, automationName, itemId});
    }

    java.beans.PropertyChangeSupport pcs = new java.beans.PropertyChangeSupport(this);

    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    protected void firePropertyChange(String p, Object old, Object n) {
        pcs.firePropertyChange(p, old, n);
    }

}
