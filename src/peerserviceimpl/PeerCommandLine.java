package peerserviceimpl;

import java.io.*;
import java.util.ArrayList;

/**
 * Runn commandline terminal
 */
class PeerCommandLine implements Runnable {
    private Thread t;
    private String threadName;
    private PeerToPeerServer ppServer;
    private String peerId;
    private String fileDir;
    private ArrayList<String> names = null;
    PeerCommandLine(String threadName,
                    PeerToPeerServer ppServer,
                    String peerId,
                    String fileDir) {
        this.threadName = threadName;
        this.ppServer = ppServer;
        this.peerId = peerId;
        this.fileDir = fileDir;
    }

    @Override
    public void run() {
        startTerminal();

    }

    /**
     * Save file downloaded to directory
     * @param fileData file data gotten from peer in bytes
     * @param fileName file name
     */
    private void saveFileToDir(byte[] fileData, String fileName) {
        try {
            File dir = new File(fileDir);
            File file = new File(dir, fileName);
            BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file));
            output.write(fileData, 0, fileData.length);
            output.flush();
            output.close();
            System.out.println("File Downloaded Successfully");
        } catch (IOException e) {
            System.err.println("File Download Error");
            e.printStackTrace();
        }
    }

    /**
     * Terminal interface
     */
    private void startTerminal() {
        BufferedReader input;
        try {
            input = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                System.out.println
                        ("1 - Search for file ");
                System.out.println
                        ("2 - Exit ");
                System.out.println();
                System.out.print("Choice: ");

                String line = input.readLine();
                Integer choice = new Integer(line);
                int value = choice.intValue();
                switch (value){
                    case 1:
                        System.out.print("Enter file name: ");
                        String fileName = input.readLine();
                        ArrayList<String> result;
                        long startTime = System.nanoTime();
                        // Query peer servers for file
                        result = ppServer.query(new ArrayList<String>(), fileName, 4);
                        double estimatedTime = (double)(System.nanoTime() - startTime)/1000000000.0;
                        System.out.println("Search time took: " + estimatedTime);
                        // Print query result(s) if any was found
                        if (result.size() > 0) {
                            System.out.println("Peer(s) were found containing file: " + fileName);
                            for (int i = 0; i < result.size(); i++) {
                                System.out.println((i + 1) +" - Peer " + (i + 1) + ": " + result.get(i));
                            }
                            String choicePeer;
                            if (result.size() > 1) {
                                System.out.print("Select Peer: ");
                                line = input.readLine();
                                choice = new Integer(line);
                                value = choice.intValue();

                                try {
                                    choicePeer = result.get(value - 1) ;
                                } catch (Exception e) {
                                    System.out.println("Invalid option chosen going for the first one");
                                    choicePeer = result.get(0);
                                }
                            } else {
                                choicePeer = result.get(0);
                            }
                            String parts[] = choicePeer.split(":");
                            try {
                                // Make connection to selected peer to download file
                                ppServer = new PeerToPeerServer(parts[1], parts[0], Integer.parseInt(parts[1]));
                                saveFileToDir(ppServer.downloadFile(fileName), fileName);
                            } catch (Exception e) {
                                System.out.println("Exception - Cannot reach peer");
                            }


                        } else {
                            System.out.println("No Peers where found containing file: " + fileName);
                        }
                        break;
                    case 2:
                        input.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid Option");
                        break;
                }
            }

        } catch (IOException e) {

        } catch (NumberFormatException nb) {

        }
    }

}
