 #!/bin/bash

set -e
set -x
 
export LD_LIBRARY_PATH=/usr/local/lib
echo "yes" | sudo add-apt-repository ppa:kalakris/cmake
sudo apt-get update -qq
sudo apt-get install cmake
cd jblosc
git clone https://github.com/Blosc/c-blosc.git
cd c-blosc
rm -rf build
mkdir build
cd build
sudo cmake -DDEACTIVATE_ZLIB=ON -DCMAKE_GENERATOR_PLATFORM=x64 ..
sudo cmake --build . --target install