package org.example.service;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RPSGameService extends Remote {
    void initializeRPSGame(String playerName) throws RemoteException;

    String playRound(String userChoice) throws RemoteException;


    String getGameHistory(String playerName) throws RemoteException;
}
