package IO;

import javax.swing.*;
import java.io.*;

/**
 * Wrapper class for java.io.
 * Contains very basic IO methods.
 */
class Utility {

    /**
     * Returns a folder that the user selects from a file chooser window.
     * @return  the directory
     */
    static File getDirectory () {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showDialog(null, "Choose folder");

        File myDirectory;

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            myDirectory = fileChooser.getSelectedFile();
        } else {
            throw new RuntimeException("Unable to grab directory.");
        }

        return myDirectory;
    }

    /**
     * Returns a file that the user selects from a file chooser window.
     * @return  the file
     */
    static File getFile () {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(null);

        File myDirectory;

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            myDirectory = fileChooser.getSelectedFile();
        } else {
            throw new RuntimeException("Unable to grab directory.");
        }

        return myDirectory;
    }

    /**
     * Create a file in the specified directory and writes to it.
     * @param directory     the directory to create the file in
     * @param fileName      the name of the file
     * @param content       the content to add to the file
     */
    static void write (File directory, String fileName, String content) {

        try {
            FileWriter fileWriter = new FileWriter(directory + File.separator + fileName);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Reads a file from the hard drive.
     * @param file  the file to read
     * @return      the contents of the file
     */
    static String read (File file) {

        StringBuilder output = new StringBuilder();

        // This will reference one line at a time
        String line;

        try {
            FileReader fileReader = new FileReader(file);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                output.append(line);
                output.append("\n");
            }

            bufferedReader.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }

        return new String(output);
    }

}
