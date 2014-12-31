package peerserviceimpl;

import p2pinterfaces.P2PPeerService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

class PeerServer{
    private PeerServiceImpl peerService;
    String rmiName = "";
    int serverPort;
    PeerServer(PeerServiceImpl peerService, String peerId, int serverPort) {
        this.peerService = peerService;
        this.rmiName = peerId;
        this.serverPort = serverPort;
    }

    public void run() {
        try {
            /*
            Create a RMI server connection
             */
            P2PPeerService stub = (P2PPeerService) UnicastRemoteObject.exportObject(peerService, 0);
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.rebind(rmiName, stub);
            System.out.println("Peer App Bound");
        } catch(ExportException e) {
            try {
                P2PPeerService stub = (P2PPeerService) UnicastRemoteObject.exportObject(peerService, 0);
                Registry registry = LocateRegistry.getRegistry(serverPort);
                registry.rebind(rmiName, stub);
                System.out.println("Peer App Bound");
            } catch (Exception e1) {

            }
        }catch (Exception e) {
            System.err.println("Error creating Peer Server on port " + serverPort);
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
