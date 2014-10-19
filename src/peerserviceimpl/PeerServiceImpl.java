package peerserviceimpl;

import indexserverimpl.PeerObject;
import p2pinterfaces.P2PPeerService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ituaijagbone on 9/25/14.
 */
public class PeerServiceImpl implements P2PPeerService{
    String fileDir = "";
    HashMap<String, PeerObject> peers = new HashMap<String, PeerObject>();
    HashMap<Integer, Integer> portToPeer = new HashMap<Integer, Integer>();
    PeerObject peer = null;
    int neighbourPorts[] = null;
    private PeerToPeerServer ppServer;

    public PeerServiceImpl(String fileDir) {
        this.fileDir = fileDir;
    }

    public byte[] downloadFile(String fileName) {
        String filePath = "tmp/" + fileName;
        if (!fileDir.isEmpty())
            filePath = fileDir + fileName;

        try {
            File file = new File(filePath);
            byte buffer[] = new byte[(int)file.length()];
            BufferedInputStream input = new BufferedInputStream(
                    new FileInputStream(filePath));
            input.read(buffer, 0, buffer.length);
            input.close();
            return buffer;
        } catch (IOException e) {
            System.err.println("File download Error on server");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArrayList<Integer> query(ArrayList<Integer> portIds, String fileName, int ttl) {
        portIds.add(peer.getPortNumber());
        ArrayList<Integer> result = new ArrayList<Integer>();

        for (int i: neighbourPorts) {
            try {
                if (!portIds.contains(new Integer(i))) {
                    ppServer = new PeerToPeerServer("" + i, i);
                    ArrayList<Integer> tmp = ppServer.query(portIds, fileName, ttl);
                    if (tmp != null) {
                        for (int j = 0; j < tmp.size(); j++) {
                            Integer tmpPort = tmp.get(i);
                            if (!result.contains(tmpPort)) {
                                result.add(tmpPort);
                            }
                        }
                    }
//                    result.addAll(ppServer.query(portIds, fileName, ttl));
                }
            }catch (MalformedURLException murle) {
                System.out.println("MalformedURLException - Wrong url cannot reach peer");
                continue;
            } catch (RemoteException re) {
                System.out.println("RemoteException - Cannot reach peer. ");
                continue;
            } catch (NotBoundException nbe) {
                System.out.println("NotBoundException - Cannot reach peer. ");
                continue;
            }
        }
        Integer tmp = search(fileName);
        if (tmp != null && !result.contains(peer.getPortNumber()))
            result.add(peer.getPortNumber());
        return result;
    }

    /**
     * Create the peer model
     * @param peerId peer id
     * @param fileNames array contain files in peer's directory
     * @param portNumber port  number
     */
    public void register(String peerId, String[] fileNames, int portNumber) {
        String id = peerId;
        peer = new PeerObject(fileNames, id, portNumber);
    }

    public Integer search(String fileName) {
        Integer result = null;
        if (peer.searchFiles(fileName))
            result = peer.getPortNumber();
        return result;
    }
}
