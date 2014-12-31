package peerserviceimpl;

import java.util.Arrays;

class UpdatePeerFiles implements Runnable {
    String[] currentFileNames;
    PeerClient peerClient;
    PeerServiceImpl peerService;
    String peerId;

    UpdatePeerFiles(PeerClient peerClient,
                    PeerServiceImpl peerService,
                    String[] currentFileNames,
                    String peerId) {
        this.currentFileNames = currentFileNames;
        this.peerClient = peerClient;
        this.peerService= peerService;
        this.peerId = peerId;
    }

    @Override
    public void run() {
        try {
            for (;;) {
                Thread.sleep(3000);
                String[] tmpFiles = peerClient.getFilesInDir();
                if (!Arrays.equals(currentFileNames, tmpFiles)) {
                    peerService.updateFiles(tmpFiles);
                    currentFileNames = tmpFiles;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
