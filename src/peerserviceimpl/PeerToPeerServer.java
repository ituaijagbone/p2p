package peerserviceimpl;

import p2pinterfaces.P2PPeerService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class PeerToPeerServer {
    private P2PPeerService peerService;
    String rmiName = "";
    int serverPort = 1097; // Default server port
    String ipAddress = "localhost"; // Default ip address
    public PeerToPeerServer(String peerId, String ipAddress, int serverPort) {
        this.rmiName = peerId;
        this.serverPort = serverPort;
        this.ipAddress = ipAddress;
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
//        try {
//            Registry registry = LocateRegistry.getRegistry(serverPort);
//            peerService = (P2PPeerService)registry.lookup(rmiName);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//        }
        try {
            Registry registry = LocateRegistry.getRegistry(this.ipAddress, this.serverPort);
            peerService = (P2PPeerService)registry.lookup(rmiName);
        } catch (Exception e) {
//            System.err.println(e.getMessage());
            System.err.println("Neighbor: " + ipAddress+":"+serverPort + " not found");
            e.printStackTrace();
        }

    }

    /**
     * Download file from peer
     * @param fileName file name
     * @return returns file in array of bytes
     */
    public byte[] downloadFile(String fileName) {
        byte[] fileData = null;
        try {
            fileData = peerService.downloadFile(fileName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return fileData;
    }

    /**
     * Query peer for file and send broadcast to its neighbours
     * @param portIds array of ports already visited
     * @param fileName file name
     * @param ttl time to live for the broadcast
     * @return array list containing port numbers that peer can connect to download file if found
     */
    public ArrayList<String> query(ArrayList<String> portIds, String fileName, int ttl) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            result = peerService.query(portIds, fileName, ttl);
        } catch (RemoteException re) {
            System.out.println("RemoteException - Cannot reach peer. ");
        }
        return result;
    }

    /**
     * Update file names in peer's directory in case there is a change
     * @param fileNames file name
     */
    public void updateFiles(String[] fileNames) {
        try {
            peerService.updateFiles(fileNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
