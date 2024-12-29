This is throwaway code and just tests a few basic concepts in bluecove


The shared object for gps is already compiled and in resources.  To recompile

g++ -shared -fPIC -o libgpsreader.so -I/usr/lib/jvm/java-22-amazon-corretto/include -I/usr/lib/jvm/java-22-amazon-corretto/include/linux gps_reader.cpp -lgps
