package peerserviceimpl;

import indexserverimpl.IndexApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by ituaijagbone on 9/25/14.
 */
public class PeerClient {
    String fileDir;

    public PeerClient() {}

    public PeerClient(String fileDir) {
        this.fileDir = fileDir;
    }
    public String[] getFilesInDir() {
        File file = null;
        String[] paths = null;
        try {
            file = new File(getFileDir());
            if (file.isDirectory() && file.canRead()) {
                paths = file.list();
            } else {
                System.out.println("Directory does not exist, creating temp directory in current folder");
                file = new File("tmp");
                file.mkdir();
            }
        } catch (Exception e) {
            System.out.println("Error creating directory, creating temp directory in current folder");
            file = new File("tmp");
            file.mkdir();
        }
        return paths;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getFileDir() {
        return fileDir;
    }


}
