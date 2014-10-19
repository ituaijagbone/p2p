package peerserviceimpl;

import p2pinterfaces.P2PPeerService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by ituaijagbone on 9/25/14.
 */
public class PeerServiceImpl implements P2PPeerService{
    String fileDir = "";
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
}
