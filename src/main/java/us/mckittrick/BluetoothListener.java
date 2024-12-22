package us.mckittrick;

import javax.bluetooth.*;
import java.io.IOException;

public class BluetoothListener implements DiscoveryListener {
    private final Object inquiryCompletedEvent = new Object();
    private final RemoteDevice[] devices;

    public BluetoothListener() {
        devices = new RemoteDevice[255]; // Arbitrary size
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

        System.out.println(BluetoothDetector.RED+"* Device found: " + deviceName + " [" + device.getBluetoothAddress() + "]"+BluetoothDetector.RESET);
    }

    @Override
    public void inquiryCompleted(int discType) {
        synchronized (inquiryCompletedEvent) {
            inquiryCompletedEvent.notify();
        }
        System.out.println("Device inquiry completed.");
    }

    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        // Not used in this context
    }

    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] records) {
        // Not used in this context
    }
}
