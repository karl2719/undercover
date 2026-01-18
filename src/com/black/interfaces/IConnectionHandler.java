package com.black.interfaces;

public interface IConnectionHandler {

    void onDirectJoinRequested(String ipAddress, int port, String username);
    

    void onDiscoveredServerJoinRequested(String username);
    

    void onLeaveRequested();
    

    void onSendMessageRequested();
    

    void onPanelSwitchRequested(String panelName);
    

    void showError(String message);
    

    void clearError();
    

    void onBackToMenuRequested();
}
