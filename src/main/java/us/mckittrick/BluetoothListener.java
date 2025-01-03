package us.mckittrick;

import javax.bluetooth.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BluetoothListener implements DiscoveryListener {
    private final Object inquiryCompletedEvent = new Object();
    private final RemoteDevice[] devices;
    private final String csvFilePath;
    private PrintWriter csvWriter;
    CaffeineListCache cache;
    VendorLookup vendorLookup;
    GpsReader gpsReader;

    public static final String BEEP = "\u0007";


    public BluetoothListener() {
        devices = new RemoteDevice[255]; // Arbitrary size
        vendorLookup = new VendorLookup();

        int maxCacheSize = 100000;
        String maxCacheSizeProperty = System.getProperty("max.cache.size");
        if (maxCacheSizeProperty != null) {
            try {
                maxCacheSize = Integer.parseInt(maxCacheSizeProperty);
            } catch (NumberFormatException e) {
                System.err.println("Invalid system property value, using default cache size of "+maxCacheSize+" entries.");
            }
        }

        String csvFilePathProperty = System.getProperty("csv.file.path");
        if (csvFilePathProperty == null) {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            csvFilePath = "/home/convention/bluetooth-" + timestamp + ".csv";
        } else {
            csvFilePath = csvFilePathProperty;
        }

        cache = new CaffeineListCache(maxCacheSize);

        try {
            csvWriter = new PrintWriter(new FileWriter(csvFilePath, true));
            csvWriter.println("Device Name,Device Address");
        } catch (IOException e) {
            e.printStackTrace();
        }

        gpsReader = new GpsReader();
        try {
            System.out.println("GpsReader initialized. " + gpsReader.getCoordinates());
        } catch (Exception e) {
            System.out.println("Failed to initialize GpsReader: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void startDiscovery() {
        try {
            // Start device inquiry
            DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
            agent.startInquiry(DiscoveryAgent.GIAC, this);
            synchronized (inquiryCompletedEvent) {
                inquiryCompletedEvent.wait(); // Wait for the inquiry to complete
            }
        } catch (InterruptedException | BluetoothStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deviceDiscovered(RemoteDevice device, DeviceClass cod) {

        String deviceName = "HIDDEN";

        try {
            deviceName = device.getFriendlyName(false);
        } catch (IOException e) {
            System.out.println("Could not get friendly name");
        }

        String deviceAddress = device.getBluetoothAddress();

        if (cache.checkCache(deviceAddress)) {
            return;
        } else {
            cache.put(deviceAddress);

            String oui = extractOUI(deviceAddress);
            String vendor = vendorLookup.getVendorNameByMacPrefix(oui);
            String gpsCoordinates = gpsReader.getCoordinates();

            System.out.println(BluetoothDetector.RED+"* Device found: " + deviceName + " [" + deviceAddress + "]       Manufacturer["+vendor+"]"+"   "+gpsCoordinates+BluetoothDetector.RESET+BEEP);
            csvWriter.println(deviceName + "," + deviceAddress+","+escapeCsv(vendor)+","+escapeCsv(generateGoogleMapsLink(gpsCoordinates)));
            csvWriter.flush();
        }

    }

    @Override
    public void inquiryCompleted(int discType) {
        synchronized (inquiryCompletedEvent) {
            inquiryCompletedEvent.notify();
        }
    }

    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        // Not used in this context
    }

    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] records) {
        // Not used in this context
    }

    public static String extractOUI(String bluetoothId) {
        if (bluetoothId == null || bluetoothId.length() < 6) {
            throw new IllegalArgumentException("Invalid Bluetooth ID");
        }
        // Extract the first 6 characters (3 bytes)
        String oui = bluetoothId.substring(0, 6);
        // Format the OUI with colons
        return formatOUI(oui);
    }

    private static String formatOUI(String oui) {
        StringBuilder formattedOUI = new StringBuilder();
        for (int i = 0; i < oui.length(); i++) {
            formattedOUI.append(oui.charAt(i));
            // Add a colon after every two characters, except for the last pair
            if (i % 2 == 1 && i < oui.length() - 1) {
                formattedOUI.append(':');
            }
        }
        return formattedOUI.toString();
    }

    public static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        // Check if the value needs to be escaped
        boolean needsEscaping = value.contains(",") || value.contains("\"") || value.contains("\n");

        if (!needsEscaping) {
            return value; // No need to escape
        }

        // Escape double quotes by replacing " with ""
        String escapedValue = value.replace("\"", "\"\"");

        // Enclose the value in double quotes
        return "\"" + escapedValue + "\"";
    }

    public static String generateGoogleMapsLink(String gpsCoordinates) {
        // Split the input string into latitude and longitude
        String[] coordinates = gpsCoordinates.split(",");
        if (coordinates.length != 2) {
            return "Malformed coordinates.  Unavailable.";
        }

        String latitude = coordinates[0].trim();
        String longitude = coordinates[1].trim();

        return String.format("https://www.google.com/maps?q=%s,%s", latitude, longitude);
    }

}
