package util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

/**
 * Created by s113958 on 18-4-2015.
 */
public class Storage {

    public static void exportToFile(SerializableBraitenbergSimulation obj, JFrame frame) {
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Export Braitenberg Simulation");
        fc.setFileFilter(new FileNameExtensionFilter("Text file", "txt"));
        fc.setApproveButtonText("Export");

        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                String path;
                if (file.getPath().endsWith(".txt"))
                    path = file.getPath();
                else
                    path = file.getPath() + ".txt";

                FileOutputStream fileOut = new FileOutputStream(path);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(obj);
                out.close();
                fileOut.close();
            }
            catch(IOException i)  {
                i.printStackTrace();
            }
        }
    }

    public static SerializableBraitenbergSimulation importFromFile(JFrame frame) {
        SerializableBraitenbergSimulation obj = null;

        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import Braitenberg Simulation");
        fc.setFileFilter(new FileNameExtensionFilter("Text file", "txt"));
        fc.setApproveButtonText("Open");

        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                FileInputStream fileIn = new FileInputStream(file.getPath());
                ObjectInputStream in = new ObjectInputStream(fileIn);
                obj = (SerializableBraitenbergSimulation) in.readObject();
                in.close();
                fileIn.close();
                return obj;
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        }
        else {
            return null;
        }
    }

}
