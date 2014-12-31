package backendimpl;

import p2pinterfaces.P2PServerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by ituaijagbone on 9/25/14.
 */

class IndexServer {
    private IndexServiceImpl indexService;
    String rmiName = "IndexServer";
    int serverPort = 1098;
    IndexServer(IndexServiceImpl indexService) {
        this.indexService = indexService;
    }

    public void run() {
//        if (System.getSecurityManager() == null)
//            System.setSecurityManager(new RMISecurityManager());

        try {
            P2PServerService stub =
                    (P2PServerService) UnicastRemoteObject.exportObject(indexService, 0);
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.bind(rmiName, stub);
            System.out.println("Index App Bound");
        } catch (Exception e) {
            System.out.println("Error Creating IndexServer. Existing.");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

class IndexCommandLine implements Runnable {
    private Thread t;
    private String threadName;
    private IndexServiceImpl indexService;

    IndexCommandLine(String threadName, IndexServiceImpl indexService) {
        this.threadName = threadName;
        this.indexService = indexService;
    }
    @Override
    public void run() {
        BufferedReader input;

        try {
            for (;;) {
                input = new BufferedReader(new InputStreamReader(System.in));
                System.out.println
                        ("1 - List all peers registered ");
                System.out.println
                        ("2 - Exit ");
                System.out.println();
                System.out.print("Choice: ");

                String line = input.readLine();
                Integer choice = new Integer(line);
                int value = choice.intValue();
                switch (value) {
                    case 1:
                        indexService.listAllPeers();
                        break;
                    case 2:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid Option Selected");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {

        }
    }
}
public class IndexApp {
    public static void main(String args[]) {
        IndexServiceImpl indexService = new IndexServiceImpl();
        IndexServer indexServer = new IndexServer(indexService);
        indexServer.run();
        IndexCommandLine indexCommandLine = new IndexCommandLine(
                "IndexServer", indexService
        );
        Thread t = new Thread(indexCommandLine);
        t.start();
    }
}

