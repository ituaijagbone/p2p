package backendimpl;

import p2pinterfaces.P2PPeerService;
import peerserviceimpl.PeerToPeerServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by itua ijagbone on 9/25/14.
 */
public class PeerServiceImpl implements P2PPeerService{
    String fileDir = "";
    PeerObject peer = null;
    String neighbourPorts[] = null;
    private PeerToPeerServer ppServer;

    public PeerServiceImpl(String fileDir) {
        this.fileDir = fileDir;
    }

    /**
     * Returns file that requesting peer wants to download
     * @param fileName file name the requesting peer wants to download from another peer server
     * @return byte array contain the file
     */
    @Override
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

    /**
     * Queries peers for file. Client peer connects with neighboring peer. This cycle
     * continues util all neighboring peers of neighboring peers has been reached based
     * on the time to live value. Each peer uses binary search find the file in its own local directory,
     * if returns an Array List containing the concatenation of the IP Address and port number of
     * the peer with the file.
     * @param portIds an array list holding all visited peers in search for file so far
     * @param fileName file name
     * @param ttl time to live of the request
     * @return
     */
    @Override
    public ArrayList<String> query(ArrayList<String> portIds, String fileName, int ttl) {
        // Add peer to already visited peers to avoid finite loop
        portIds.add(peer.getIpAddress()+":"+peer.getPortNumber());
        ArrayList<String> result = new ArrayList<String>();
        // Return result if time to live of query has expired
        if (ttl <= 0) {
            return result;
        }
        ttl = ttl - 1;
        for (String i: neighbourPorts) {
            try {
                String ipPort[] = i.split(" ");
                if (!portIds.contains(ipPort[0]+":"+ipPort[1])) {
                    ppServer = new PeerToPeerServer(ipPort[1], ipPort[0], Integer.parseInt(ipPort[1]));
                    ArrayList<String> tmp = ppServer.query(portIds, fileName, ttl);
                    if (tmp != null) {
                        for (int j = 0; j < tmp.size(); j++) {
                            String tmpPort = tmp.get(j);
                            if (!result.contains(tmpPort)) {
                                result.add(tmpPort);
                            }
                        }
                    }
//                    result.addAll(ppServer.query(portIds, fileName, ttl));
                }
            }catch (Exception e) {
                System.out.println("MalformedURLException - Wrong url cannot reach peer");
                continue;
            }
        }
        // Search for file in local directory
        String strTmp = search(fileName);
        if (strTmp != null && !result.contains(strTmp)) {
            result.add(strTmp);
        }
        return result;
    }

    @Override
    public synchronized void updateFiles(String[] fileNames) throws RemoteException {
        peer.setFileNames(fileNames);
    }

    /**
     * Create the peer model
     * @param peerId peer id
     * @param fileNames array containing files in peer's directory
     * @param portNumber port  number
     * @param neighbourPorts port numbers of peer's neighbor
     * @param ipAddress ip address that peer is running on (default is localhost)
     */
    public void register(String peerId, String[] fileNames, int portNumber, String[] neighbourPorts, String ipAddress) {
        String id = peerId;
        peer = new PeerObject(fileNames, id, portNumber, ipAddress);
        this.neighbourPorts = neighbourPorts;
    }

    /**
     * Search the peer object through its array of file names if the file name is present
     * @param fileName file name
     * @return ipaddress + port number of the peer if file exist or null
     */
    public String search(String fileName) {
        String result = null;
        if (peer.searchFiles(fileName))
            result = peer.getIpAddress()+":"+peer.getPortNumber();
        return result;
    }
}
