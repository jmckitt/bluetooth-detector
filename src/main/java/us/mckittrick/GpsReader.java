package us.mckittrick;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class GpsReader {

    static {
        try {
            // Extract the native library from the JAR
            String libName = "libgpsreader.so";
            InputStream in = GpsReader.class.getClassLoader().getResourceAsStream(libName);
            if (in == null) {
                throw new RuntimeException("Native library not found: " + libName);
            }

            File tempLib = File.createTempFile("libgpsreader", ".so");
            tempLib.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempLib)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.load(tempLib.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public native String getCoordinates();

    public static void main(String[] args) {
        GpsReader gpsReader = new GpsReader();
        String coordinates = gpsReader.getCoordinates();
        System.out.println("Coordinates: " + coordinates);
    }
}

