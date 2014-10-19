package peerserviceimpl;

import p2pinterfaces.P2PPeerService;
import p2pinterfaces.P2PServerService;

import java.io.*;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;

/**Creat the rmi server connection for a peer
 * Created by ituaijagbone on 9/26/14.
 */
class PeerServer{
    private PeerServiceImpl peerService;
    String rmiName = "";
    int serverPort;
    PeerServer(PeerServiceImpl peerService, String peerId, int serverPort) {
        this.peerService = peerService;
        this.rmiName = peerId;
        this.serverPort = serverPort;
    }

    public void run() {
//        if (System.getSecurityManager() == null)
//            System.setSecurityManager(new RMISecurityManager());

        try {
            /*
            Create a RMI server connection
             */
            P2PPeerService stub = (P2PPeerService) UnicastRemoteObject.exportObject(peerService, 0);
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.rebind(rmiName, stub);
            System.out.println("Peer App Bound");
        } catch(ExportException e) {
            try {
                P2PPeerService stub = (P2PPeerService) UnicastRemoteObject.exportObject(peerService, 0);
                Registry registry = LocateRegistry.getRegistry(serverPort);
                registry.rebind(rmiName, stub);
                System.out.println("Peer App Bound");
            } catch (Exception e1) {

            }
        }catch (Exception e) {
            System.err.println("Error creating Peer Server on port " + serverPort);
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
/**
 * Create the connection of a peer client to
 */
class PeerToIndexServer {
//    private IndexServiceImpl indexService;
private P2PServerService indexService;
    String rmiName = "IndexServer";
    int serverPort = 1098;
    PeerToIndexServer() {
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }

        try {
            Registry registry = LocateRegistry.getRegistry(serverPort);
            indexService = (P2PServerService)registry.lookup(rmiName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public String start(String[] fileNames, int portNumber) {
        String peerId = "";
        try {
            peerId = indexService.registry(fileNames, portNumber);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return peerId;
    }

    public ArrayList<String> searchFile(String fileName, String peerId) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            result = indexService.search(fileName, peerId);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public void notFound(String peerId) {
        try {
            indexService.notFound(peerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateFiles(String[] fileNames, String peerId) {
        try {
            indexService.updateFiles(fileNames, peerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

class PeerToPeerServer {
//    private PeerServiceImpl peerService;
    private P2PPeerService peerService;
    String rmiName = "";
    int serverPort = 1097;
    PeerToPeerServer(String peerId, int serverPort) throws RemoteException, MalformedURLException, NotBoundException{
        this.rmiName = peerId;
        this.serverPort = serverPort;
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
//        try {
//            Registry registry = LocateRegistry.getRegistry(serverPort);
//            peerService = (P2PPeerService)registry.lookup(rmiName);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            e.printStackTrace();
//        }
        Registry registry = LocateRegistry.getRegistry(this.serverPort);
        peerService = (P2PPeerService)registry.lookup(rmiName);
    }

    public byte[] downloadFile(String fileName) {
        byte[] fileData = null;
        try {
            fileData = peerService.downloadFile(fileName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return fileData;
    }
}

/**
 * Runn commandline terminal
 */
class PeerCommandLine implements Runnable {
    private Thread t;
    private String threadName;
    private PeerToIndexServer piServer;
    private PeerToPeerServer ppServer;
    private String peerId;
    private String fileDir;
    private boolean isExperiment = false;
    private ArrayList<String> names = null;
    PeerCommandLine(String threadName,
                    PeerToIndexServer piServer,
                    String peerId,
                    String fileDir) {
        this.threadName = threadName;
        this.piServer = piServer;
        this.peerId = peerId;
        this.fileDir = fileDir;
    }
    PeerCommandLine(String threadName,
                    PeerToIndexServer piServer,
                    String peerId,
                    String fileDir, boolean isExperiment, ArrayList<String> names) {
        this.threadName = threadName;
        this.piServer = piServer;
        this.peerId = peerId;
        this.fileDir = fileDir;
        this.isExperiment = isExperiment;
        this.names = names;
    }
    @Override
    public void run() {
        if (isExperiment)
            experimentRelated();
        else
            userRelated();

    }

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

    private void userRelated() {
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
                        ArrayList<String> result = null;
                        ArrayList<Double> estimatedTimes = new ArrayList<Double>();
                        //
                        long startTime = System.nanoTime();
                        for (int i = 0; i < 1000; i++) {
                            long startTimeInner = System.nanoTime();
                            result = piServer.searchFile(fileName, peerId);
                            estimatedTimes.add((double)(System.nanoTime() - startTimeInner)/1000000000.0);
                        }
                        double estimatedTime = (double)(System.nanoTime() - startTime)/1000000000.0;
                        System.out.println("Search time took: " + estimatedTime);
//                            System.out.println("Search time took: " + estimatedTimes+ " 1 time");
                        if (result.size() > 0) {
                            System.out.println("Peer(s) were found containing file: " + fileName);
                            for (int i = 0; i < result.size(); i++) {
                                System.out.println((i + 1) +" - Peer " + (i + 1));
                            }
                            String choicePeer = "";
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
                            String parts[] = choicePeer.split("\\s+");
                            try {
                                ppServer = new PeerToPeerServer(parts[0], Integer.parseInt(parts[1]));
                                saveFileToDir(ppServer.downloadFile(fileName), fileName);
                            } catch (MalformedURLException murle) {
                                System.out.println("MalformedURLException - Wrong url cannot reach peer");
                                piServer.notFound(parts[0]);
                            } catch (RemoteException re) {
                                System.out.println("RemoteException - Cannot reach peer. ");
                                piServer.notFound(parts[0]);
                            } catch (NotBoundException nbe) {
                                System.out.println("NotBoundException - Cannot reach peer. ");
                                piServer.notFound(parts[0]);
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

    private void experimentRelated() {
        BufferedReader input;
        try {
            input = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                System.out.println
                        ("1 - Run Experiment ");
                System.out.println
                        ("2 - Exit ");
                System.out.println();
                System.out.print("Choice: ");

                String line = input.readLine();
                Integer choice = new Integer(line);
                int value = choice.intValue();
                switch (value){
                    case 1:
                        ArrayList<Double> averageTimes = new ArrayList<Double>();
                        ArrayList<Double> estimatedTimes = new ArrayList<Double>();
                        long startTimeLoop = System.nanoTime();
                        for (int j = 0; j < names.size(); j++) {
                            double totalTime = 0.0;
                            String fileName = names.get(j);
                            long startTime = System.nanoTime();
                            for (int i = 0; i < 1000; i++) {
                                long startTimeInner = System.nanoTime();
                                piServer.searchFile(fileName, peerId);
                                totalTime += (double)(System.nanoTime() - startTimeInner)/1000000000.0;
                            }
                            double estimatedTime = (double)(System.nanoTime() - startTime)/1000000000.0;
                            double averageTime = totalTime/1000.0;
                            averageTimes.add(averageTime);
                            estimatedTimes.add(estimatedTime);
                            System.out.println("file Name: " + fileName + " Average Time: " +
                                    averageTime + " Estimated Time: " + estimatedTime);
                            System.out.println();
                        }
                        double estimatedTimeLoop = (double)(System.nanoTime() - startTimeLoop)/1000000000.0;
                        printToCsv(averageTimes, estimatedTimes);
                        System.out.println();
                        System.out.println("run time took: " + estimatedTimeLoop + "s");

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

    private void printToCsv(ArrayList<Double> averageTimes,
                            ArrayList<Double> estimatedTimes) {
       try {
           File dir = new File(fileDir);
           File file = new File(dir, "evaluationresult.csv");
           FileWriter writer = new FileWriter(file);
           writer.append("File Name");
           writer.append(",");
           writer.append("Average Time");
           writer.append(",");
           writer.append("Total Run Time");
           writer.append('\n');
           for (int i = 0; i < names.size(); i++) {
               writer.append(names.get(i));
               writer.append(",");
               writer.append("" + averageTimes.get(i));
               writer.append(",");
               writer.append("" + estimatedTimes.get(i));
               writer.append('\n');
           }
           writer.flush();
           writer.close();
           System.err.println("Experiment successfully written to file found in: " + file.getAbsolutePath());
       } catch (IOException e) {
            e.printStackTrace();
       }

    }
}

class UpdatePeerFiles implements Runnable {
    String[] currentFileNames;
    PeerClient peerClient;
    PeerToIndexServer piServer;
    String peerId;

    UpdatePeerFiles(PeerClient peerClient,
                    PeerToIndexServer piServer,
                    String[] currentFileNames,
                    String peerId) {
        this.currentFileNames = currentFileNames;
        this.peerClient = peerClient;
        this.piServer = piServer;
        this.peerId = peerId;
    }

    @Override
    public void run() {
        try {
            for (;;) {
                Thread.sleep(3000);
                String[] tmpFiles = peerClient.getFilesInDir();
                if (!Arrays.equals(currentFileNames, tmpFiles)) {
                    piServer.updateFiles(tmpFiles, peerId);
                    currentFileNames = tmpFiles;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class PeerApp {
    public static void main(String args[]) {
        /**
        *   Command line arguments go here
         */
        String fileDir = "tmp/";
        int thisServerPort = 1033;
        ArrayList<String> experimentInput = new ArrayList<String>();
        boolean isExperiment = false;

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
            if (args[4].equals("-e")) {
                try {
                    File file = new File(args[5]);
                    System.out.println(args[5]);
                    if (file.isFile() && file.canRead()) {
                        try {
                            FileReader fileR = new FileReader(file);
                            BufferedReader br = new BufferedReader(fileR);
                            String line;
                            while((line = br.readLine()) != null) {
                                experimentInput.add(line);
                            }
                            isExperiment = true;
                            fileR.close();
                            br.close();
                        } catch (IOException io) {
                            System.err.println("Can't open file");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("Can't open file dvsdvcds");
                        System.exit(1);
                    }
                } catch (Exception e) {
                    System.err.println("Can't open file, argument must be specified");
                    System.exit(1);
                }
            }
        } catch (Exception e) {

        }



        PeerToIndexServer peerToIndexServer = new PeerToIndexServer();

        PeerClient peerClient = new PeerClient(fileDir);
        String[] fileNames = peerClient.getFilesInDir();
        System.out.println(Arrays.toString(fileNames));
        String peerId = peerToIndexServer.start(fileNames, thisServerPort);
        System.out.println("Welcome, you have successfully registered as a peer ");

        PeerServiceImpl peerService = new PeerServiceImpl(fileDir);
        PeerServer peerServer = new PeerServer(peerService, peerId, thisServerPort);
        peerServer.run();

        if (isExperiment) {
            PeerCommandLine peerCommandLine = new PeerCommandLine("Peer number " + peerId,
                    peerToIndexServer,
                    peerId,
                    fileDir,
                    isExperiment,
                    experimentInput
            );
            Thread t = new Thread(peerCommandLine);
            t.start();
        } else {
            PeerCommandLine peerCommandLine = new PeerCommandLine("Peer number " + peerId,
                    peerToIndexServer,
                    peerId,
                    fileDir
            );
            Thread t = new Thread(peerCommandLine);
            t.start();

            UpdatePeerFiles updatePeerFiles = new UpdatePeerFiles(peerClient,
                    peerToIndexServer, fileNames, peerId);
            Thread t2 = new Thread(updatePeerFiles);
            t2.start();
        }

    }
}
