#include <iostream>
#include <gps.h>
#include <cstring>
#include <unistd.h>
#include <jni.h>

extern "C" {
    JNIEXPORT jstring JNICALL Java_us_mckittrick_GpsReader_getCoordinates(JNIEnv *env, jobject obj) {
        gps_data_t gpsData;

        // Open a connection to the GPS daemon
        if (gps_open("localhost", DEFAULT_GPSD_PORT, &gpsData) != 0) {
            return env->NewStringUTF("Error connecting to GPS daemon");
        }

        // Set the GPS to read the data
        gps_stream(&gpsData, WATCH_ENABLE | WATCH_JSON, NULL);

        char message[256]; // Buffer for the message
        while (true) {
            // Wait for GPS data
            if (gps_waiting(&gpsData, 5000000)) { // 5 seconds timeout
                if (gps_read(&gpsData, message, sizeof(message)) == -1) {
                    break; // Exit on read error
                }

                // Check if we have a fix
                if (gpsData.set & LATLON_SET) {
                    // Create a string with latitude and longitude
                    std::string coordinates = std::to_string(gpsData.fix.latitude) + "," + std::to_string(gpsData.fix.longitude);
                    gps_stream(&gpsData, WATCH_DISABLE, NULL);
                    gps_close(&gpsData);
                    return env->NewStringUTF(coordinates.c_str());
                }
            }
        }

        // Close the GPS connection
        gps_stream(&gpsData, WATCH_DISABLE, NULL);
        gps_close(&gpsData);
        return env->NewStringUTF("No GPS fix available");
    }
}
