package indexserverimpl;

import java.util.Arrays;

/**
 * Created by ituaijagbone on 9/25/14.
 */
public class PeerObject {
    int portNumber;
    String[] fileNames;
    String peerId;

    public PeerObject() {

    }

    public PeerObject(String[] fileNames, String peerId, int portNumber) {
        this.fileNames = fileNames;
        this.peerId = peerId;
        this.portNumber = portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setFileNames(String[] fileNames) {
        this.fileNames = fileNames;
    }

    public String[] getFileNames() {
        return fileNames;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getPeerId() {
        return peerId;
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
