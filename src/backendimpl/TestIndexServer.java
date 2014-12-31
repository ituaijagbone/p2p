package backendimpl;

import java.rmi.RemoteException;

/**
 * Created by ituaijagbone on 9/25/14.
 */
public class TestIndexServer {
    public static void main(String[] args) throws RemoteException{
        IndexServiceImpl indexServiceImpl = new IndexServiceImpl();
        String[] fileNames = {"foo.mp3", "voo.docx", "doe.xls"};
        int portNumber = 2011;
        String fileName = "foo.mp3";
//        int peerOrder = indexServiceImpl.registry(fileNames);
//        System.out.println("Peer successfully registered: " + peerOrder);
//        ArrayList<Integer> result = indexServiceImpl.search(fileName, portNumber);
//        if (result.size() > 0) {
//            System.out.println("Peers where found containing file: " + fileName);
//            for (int i = 0; i < result.size(); i++)
//                System.out.println("Port " + result.get(i));
//        } else {
//            System.out.println("No Peers where found containing file: " + fileName);
//        }
    }
}
