package indexserverimpl;

import p2pinterfaces.P2PServerService;

import java.rmi.RemoteException;

import java.util.*;

/**
 * Created by ituaijagbone on 9/22/14.
 */
public class IndexServiceImpl implements P2PServerService  {

    HashMap<String, PeerObject> peers = new HashMap<String, PeerObject>();
    HashMap<Integer, Integer> portToPeer = new HashMap<Integer, Integer>();

    public IndexServiceImpl(){}

    @Override
    public synchronized String registry(String[] fileNames, int portNumber) throws RemoteException {
        String id = generatePeerId();
        PeerObject peer = new PeerObject(fileNames, id, portNumber);
        peers.put(id, peer);
        return peer.getPeerId();
    }

    @Override
    public ArrayList<String> search(String fileName, String peerId) throws RemoteException {
        ArrayList<String> result = new ArrayList<String>();
        Iterator it = peers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, PeerObject> pairs = (Map.Entry)it.next();
            PeerObject peer = pairs.getValue();
            if (!peer.getPeerId().equals(peerId) && peer.fileNames != null) {
                if (peer.searchFiles(fileName))
                    result.add(peer.getPeerId()+" "+ peer.getPortNumber());
            }
        }
        return result;
    }

    @Override
    public synchronized void notFound(String peerId) throws RemoteException {
        removePeer(peerId);
    }

    @Override
    public synchronized void updateFiles(String[] fileNames, String peerId) throws RemoteException {
        PeerObject peerObject = peers.get(peerId);
        if (peerObject == null)
            return;
        peerObject.setFileNames(fileNames);
        System.err.println("File update on Peer Id: " + peerObject.getPeerId() + ": " + Arrays.toString(fileNames));
    }

    public synchronized void removePeer(String peerId) {
        try {
            peers.remove(peerId);
            System.out.println();
            System.err.println("Update: Removed Peer Id: " + peerId + " from list");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generatePeerId() {
        return UUID.randomUUID().toString();
    }

    public synchronized void listAllPeers() {
        Iterator it = peers.entrySet().iterator();
        if (peers.size() > 0) {
            int i = 0;
            while (it.hasNext()) {
                Map.Entry<String, PeerObject> pairs = (Map.Entry)it.next();
                PeerObject peerObject = pairs.getValue();
                System.out.println((i+1) + " - Peer Id: " + peerObject.getPeerId() +
                        " Number of Files: " + peerObject.getFileNames().length);
                System.out.println("----------------------------------------------");
                i += 1;
            }
            System.out.println("Total Number of peers: " + peers.size());
        } else {
            System.out.println("No peers registered");
        }
    }
}

