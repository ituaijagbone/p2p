package peerserviceimpl;

import backendimpl.PeerServiceImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;


public class PeerApp {
    public static void main(String args[]) {
        /**
         * Command line arguments
         * -p specifies port number to run application on
         * -d specifies directory where application stores its files
         * -c specifies configuration file
         */
        String fileDir = "tmp/"; // Default directory to use
        int thisServerPort = 1033; // Default Port number to use
        String[] neighbors = null;
        String myIPAddress = "localhost"; // Default IP Address to use
        try {
            if (args[0].equals("-p")) {
                try {
                    thisServerPort = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.err.println("Argument " + args[1] + " must be an integer");
                    System.exit(1);
                } catch (Exception e) {
                    System.err.println("Argument  must be specified");
                    System.exit(1);
                }
            } else {
                System.out.println("Syntax - PeerApp -p port -d directory");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Port argument not specified");
            System.exit(1);
        }

        try {
            if (args[2].equals("-d")) {
                try {
                    File file = new File(args[3]);
                    if (file.isDirectory() && file.canRead() && file.canWrite())
                        fileDir = file.getPath() + "/";
                } catch (Exception e) {
                    System.out.println("Can't use directory, argument must be specified");
                    System.exit(1);
                }
            } else {
                System.out.println("No directory specified");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("No directory argument not specified");
            System.exit(1);
        }

        try {
            if (args[4].equals("-c")) {
                try {
                    File file = new File(args[5]);
                    ArrayList<String> tmpN = new ArrayList<String>();
                    if (file.isFile() && file.canRead()) {
                        try {
                            Scanner in = new Scanner(file);
                            if (in.hasNext("myipaddress")) {
                                myIPAddress = in.nextLine().split(" ")[1];
                            }
                            while (in.hasNextLine()) {
                                tmpN.add(in.nextLine());
                            }
                        } catch (IOException io) {
                            System.err.println("Can't open file");
                            System.exit(1);
                        }
                        neighbors = tmpN.toArray(new String[tmpN.size()]);
                    } else {
                        System.err.println("Can't open file");
                        System.exit(1);
                    }
                } catch (Exception e) {
                    System.err.println("Can't open file, argument must be specified");
                    System.exit(1);
                }
            }
        } catch (Exception e) {

        }

        /**
         * Get all files in the application's directory
         */
        PeerClient peerClient = new PeerClient(fileDir);
        String[] fileNames = peerClient.getFilesInDir();
        System.out.println(Arrays.toString(fileNames)); // Debug: checking if it prints out correct results
        String peerId = UUID.randomUUID().toString();

        /**
         * Start-up application server.
         */
        PeerServiceImpl peerService = new PeerServiceImpl(fileDir);
        peerService.register(peerId, fileNames, thisServerPort, neighbors, myIPAddress);
        PeerServer peerServer = new PeerServer(peerService, "" + thisServerPort, thisServerPort);
        peerServer.run();

        /**
         * Connect application client to application server
         */
        PeerToPeerServer ppServer = new PeerToPeerServer("" + thisServerPort, myIPAddress, thisServerPort);

        /**
         * Start terminal interface
         */
        PeerCommandLine peerCommandLine = new PeerCommandLine("Peer number " + peerId,
                ppServer,
                peerId,
                fileDir
        );
        Thread t = new Thread(peerCommandLine);
        t.start();

        UpdatePeerFiles updatePeerFiles = new UpdatePeerFiles(peerClient,
                peerService, fileNames, peerId);
        Thread t2 = new Thread(updatePeerFiles);
        t2.start();

    }
}
