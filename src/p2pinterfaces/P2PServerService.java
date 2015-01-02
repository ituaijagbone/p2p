package p2pinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.ArrayList;

/**
 * This is an RMI interface. It has defined 4 interfaces that the peer client uses to
 * interface with the client.
 * Created by Mina and Itua on 9/22/14.
 */
public interface P2PServerService extends Remote {
    /**
     * This registers the peer.
     * @param fileNames file names from the peer's shared directory
     * @param portNumber peer's port number
     * @return the unique id of the peer which is generated by the central index server
     * @throws RemoteException
     */
    public String registry(String[] fileNames, int portNumber) throws RemoteException;

    /**
     * Searches for filename among registered peers registered on the central server
     * @param fileName file name to search for
     * @param peerId the peer id of the peer requesting the file
     * @return ArrayList containing all peerIds found or null if none
     * @throws RemoteException
     */
    public ArrayList<String> search(String fileName, String peerId) throws RemoteException;

    /**
     * Reports to the Index Server that a peer is not reachable, index server deletes the file
     * @param peerId peerId of the peer that cannot be reached by requesting peer
     * @throws RemoteException
     */
    public void notFound(String peerId) throws RemoteException;
    public void updateFiles(String[] fileNames, String peerId) throws RemoteException;
}
