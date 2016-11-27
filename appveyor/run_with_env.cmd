@ECHO OFF

git clone https://github.com/Blosc/c-blosc.git

cd c-blosc
rmdir build /s /q
mkdir build
cd build
cmake -DDEACTIVATE_ZLIB=ON -DCMAKE_GENERATOR_PLATFORM=x64 ..
cmake --build . --target install