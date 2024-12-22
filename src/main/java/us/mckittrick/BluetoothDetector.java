package us.mckittrick;


// throwaway project to show how to get bluetooth listings on linux
public class BluetoothDetector {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";

    public static final String GREEN = "\u001B[92m";

    public static void main(String[] args) {

            System.out.println("\n\n");
            System.out.println(GREEN +"[[ BLUETOOTH DETECTOR ]]"+RESET);

            System.out.println("---------------------------------------------------------------");
            BluetoothListener discovery = new BluetoothListener();
            discovery.startDiscovery();
    }
}
