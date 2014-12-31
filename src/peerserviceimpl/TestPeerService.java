package peerserviceimpl;

import java.rmi.RemoteException;


/**
 * Created by ituaijagbone on 9/25/14.
 */
public class TestPeerService {
    public static void main(String[] args) throws RemoteException {
//        String fileDir = "tmp";
//        IndexServiceImpl indexServiceImpl = new IndexServiceImpl();
//        PeerClient peerClient = new PeerClient(fileDir);
//        String[] fileNames = peerClient.getFilesInDir();
//        int portNumber = 2011;
//        String fileName = "foo.txt";
//        int peerOrder = indexServiceImpl.registry(fileNames);
//        System.out.println("Peer successfully registered: " + peerOrder);
//        ArrayList<Integer> result = indexServiceImpl.search(fileName, portNumber);
//        if (result.size() > 0) {
//            System.out.println("Peers where found containing file: " + fileName);
//            for (int i = 0; i < result.size(); i++) {
//                System.out.println("Port " + result.get(i));
//                byte[] fileData = new PeerServiceImpl("").downloadFile(fileName);
//                try {
//                    File file = new File(fileDir + "/1"+ fileName);
//                    System.out.println(fileData.length);
//                    BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file.getName()));
//                    output.write(fileData, 0, fileData.length);
//                    output.flush();
//                    output.close();
//                } catch (IOException e) {
//                    System.err.println("File Download Error");
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            System.out.println("No Peers where found containing file: " + fileName);
//        }
    }
}
