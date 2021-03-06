package p2pinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This is the RMI interface. It contains interfaces that a peer uses to
 * interface with other peers.
 * Created by Itua on 9/25/14.
 */
public interface P2PPeerService extends Remote {
    /**
     * Download the file by transferring the file over to the requesting peer
     * @param fileName file name the requesting peer wants to download from another peer server
     * @return array of bytes of the file
     * @throws RemoteException
     */
    public byte[] downloadFile(String fileName) throws RemoteException;

    /**
     * Returns an array list containing concatenation of ip address and port number of peers that have the requested file
     * @param portIds an array list holding all visited peers in search for file so far
     * @param fileName file name
     * @param ttl time to live of the request
     * @return ArrayList containing concatenation of peer id and port number
     */
    public ArrayList<String> query(ArrayList<String> portIds, String fileName, int ttl) throws RemoteException;

    /**
     * Update file names in directory if there is any  change
     * @param fileNames file name
     * @throws RemoteException
     */
    public void updateFiles(String[] fileNames) throws RemoteException;

}
