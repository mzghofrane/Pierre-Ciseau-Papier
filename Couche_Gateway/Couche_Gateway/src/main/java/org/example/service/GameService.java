package org.example.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameService extends Remote {
    String play(String playerID, String playerChoice) throws RemoteException;

    int getFinalScore(String playerID) throws RemoteException;

    String[] gameHistory(String playerID) throws RemoteException;
}
