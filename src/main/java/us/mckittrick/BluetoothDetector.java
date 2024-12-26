package us.mckittrick;


// throwaway project to show how to get bluetooth listings on linux
public class BluetoothDetector {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";

    public static final String GREEN = "\u001B[92m";

    public BluetoothDetector() {
        int interval = 20;

        String intervalProperty = System.getProperty("detect.interval");
        if (intervalProperty != null) {
            try {
                interval = Integer.parseInt(intervalProperty);
            } catch (NumberFormatException e) {
                System.err.println("Invalid system property value, using default interval of "+interval+" seconds.");
            }
        }

        System.out.println("\n\n");
        System.out.println(GREEN +"[[ BLUETOOTH DETECTOR ]]"+RESET);
        System.out.println("---------------------------------------------------------------");
        System.out.println("Polling interval: "+interval+" seconds");
        BluetoothListener discovery = new BluetoothListener();

        while (true) {
            try {
                discovery.startDiscovery();
                Thread.sleep(interval * 1000);
            } catch (InterruptedException e) {
                System.out.println("Loop interrupted. Exiting...");
                break;
            }
        }

    }



    public static void main(String[] args) {
        BluetoothDetector bluetoothDetector = new BluetoothDetector();
    }
}
