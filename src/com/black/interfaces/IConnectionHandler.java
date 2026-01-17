package com.black.interfaces;

/**
 * Interface for handling connection-related actions from UI panels.
 */
public interface IConnectionHandler {
    /**
     * Request to join a server using direct connection.
     * @param ipAddress Server IP address
     * @param port Server port
     * @param username User's chosen username
     */
    void onDirectJoinRequested(String ipAddress, int port, String username);
    
    /**
     * Request to join a discovered server.
     * @param username User's chosen username
     */
    void onDiscoveredServerJoinRequested(String username);
    
    /**
     * Request to leave the current server.
     */
    void onLeaveRequested();
    
    /**
     * Request to send a chat message.
     */
    void onSendMessageRequested();
    
    /**
     * Request to switch between connection panels.
     * @param panelName Name of the panel to switch to
     */
    void onPanelSwitchRequested(String panelName);
    
    /**
     * Display an error message to the user.
     * @param message Error message to display
     */
    void showError(String message);
    
    /**
     * Clear the current error message.
     */
    void clearError();
}
