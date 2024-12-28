    package us.mckittrick;

    import com.google.gson.Gson;
    import com.google.gson.reflect.TypeToken;

    import java.io.BufferedReader;
    import java.io.InputStreamReader;
    import java.lang.reflect.Type;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class VendorLookup {
        private Map<String, String> vendorMap;

        public VendorLookup() {
            vendorMap = new HashMap<>();
            loadVendors();
        }

        private void loadVendors() {
            Gson gson = new Gson();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getClass().getClassLoader().getResourceAsStream("mac-vendors-export.json")))) {
                Type listType = new TypeToken<List<Vendor>>() {
                }.getType();
                List<Vendor> vendors = gson.fromJson(reader, listType);

                for (Vendor vendor : vendors) {
                    vendorMap.put(vendor.getMacPrefix().toLowerCase(), vendor.getVendorName());
                }
            } catch (Exception e) {
                System.err.println("Error reading the JSON file: " + e.getMessage());
            }
        }

        public String getVendorNameByMacPrefix(String macPrefix) {
            String vendor = vendorMap.get(macPrefix.toLowerCase());

            if (vendor == null) {
                return "Unknown";
            } else {
                return vendor;
            }
        }
    }

    // Class to represent the structure of the JSON data
    class Vendor {
        private String macPrefix;
        private String vendorName;
        private boolean privateVendor;
        private String blockType;
        private String lastUpdate;

        // Getters
        public String getMacPrefix() {
            return macPrefix;
        }

        public String getVendorName() {
            return vendorName;
        }
    }

