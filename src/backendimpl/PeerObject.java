package backendimpl;

import java.util.Arrays;

/**
 * Created by ituaijagbone on 9/25/14.
 */
public class PeerObject {
    int portNumber;
    String[] fileNames;
    String peerId;
    String ipAddress;

    public PeerObject() {

    }

    public PeerObject(String[] fileNames, String peerId, int portNumber, String ipAddress) {
        this.fileNames = fileNames;
        this.peerId = peerId;
        this.portNumber = portNumber;
        this.ipAddress = ipAddress;
    }

    /**
     * set port number
     * @param portNumber port number of the peer
     */
    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    /**
     *
     * @return port number
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * Set file name
     * @param fileNames file name
     */
    public void setFileNames(String[] fileNames) {
        this.fileNames = fileNames;
    }

    /**
     *
     * @return all file names under peer directory
     */
    public String[] getFileNames() {
        return fileNames;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getPeerId() {
        return peerId;
    }

    public String getIpAddress() {
        return ipAddress;
    }
    public boolean searchFiles(String searchValue) {
        Arrays.sort(getFileNames());
        boolean found = false;

        int i = Arrays.binarySearch(getFileNames(), searchValue);
        if (i >= 0) {
            found = true;
        }

        return found;
    }
}
