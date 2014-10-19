package p2pinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the RMI interface it has defined 1 interface that the peer client uses to
 * interface with a peer server.
 * Created by itua and Mina on 9/25/14.
 */
public interface P2PPeerService extends Remote {
    /**
     * Download the file by transfering the file over to the requesting peer
     * @param fileName file name the requesting peer wants to download from another peer server
     * @return array of bytes of the file
     * @throws RemoteException
     */
    public byte[] downloadFile(String fileName) throws RemoteException;


}
