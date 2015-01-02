package peerserviceimpl;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by itua ijagbone on 9/25/14.
 * List all files
 */
public class PeerClient {
    String fileDir;

    public PeerClient() {}

    public PeerClient(String fileDir) {
        this.fileDir = fileDir;
    }

    /**
     * Get all files in the application directory ignoring all folders and hidden files
     * @return string array of file names
     */
    public String[] getFilesInDir() {
        File file;
        File[] paths;
        List dirFiles = new ArrayList<String>();
        try {
            file = new File(getFileDir());
            if (file.isDirectory() && file.canRead()) {
                paths = file.listFiles();
                // Ignore all folders and hidden files
                for(int i = 0; i < paths.length; i++) {
                    if (paths[i].isDirectory() || paths[i].isHidden()) {
                        continue;
                    }
                    dirFiles.add(paths[i].getName());
                }
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
        return (String[]) dirFiles.toArray();
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    /**
     * Get File directory path
     * @return String containing directory of application
     */
    public String getFileDir() {
        return fileDir;
    }


}
