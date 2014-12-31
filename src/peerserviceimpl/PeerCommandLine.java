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
    private boolean isExperiment = false;
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
    PeerCommandLine(String threadName,
                    PeerToPeerServer ppServer,
                    String peerId,
                    String fileDir, boolean isExperiment, ArrayList<String> names) {
        this.threadName = threadName;
        this.ppServer = ppServer;
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
     * User related commandline line interaction
     */
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
                        ArrayList<String> result;
                        long startTime = System.nanoTime();
                        result = ppServer.query(new ArrayList<String>(), fileName, 4);
                        double estimatedTime = (double)(System.nanoTime() - startTime)/1000000000.0;
                        System.out.println("Search time took: " + estimatedTime);
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

    /**
     * Experiment/evaluation related
     */
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
                            for (int i = 0; i < 200; i++) {
                                long startTimeInner = System.nanoTime();
                                ppServer.query(new ArrayList<String>(), fileName, 5);
                                totalTime += (double)(System.nanoTime() - startTimeInner)/1000000000.0;
                            }
                            double estimatedTime = (double)(System.nanoTime() - startTime)/1000000000.0;
                            double averageTime = totalTime/200.0;
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
            double total = 0.0;
            double totalEst = 0.0;
            for (int i = 0; i < names.size(); i++) {
                writer.append(names.get(i));
                writer.append(",");
                total += averageTimes.get(i);
                writer.append("" + averageTimes.get(i));
                writer.append(",");
                totalEst += estimatedTimes.get(i);
                writer.append("" + estimatedTimes.get(i));
                writer.append('\n');
            }
            writer.append("Total average");
            writer.append(",");
            writer.append("" + total/averageTimes.size());
            writer.append(",");
            writer.append("" + totalEst/estimatedTimes.size());
            writer.append('\n');
            writer.flush();
            writer.close();
            System.err.println("Experiment successfully written to file found in: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
