# JBlosc

| **Travis CI** | **Appveyor** |
|---------------|--------------|
|[![Build Status](https://travis-ci.org/Blosc/JBlosc.svg?branch=master)](https://travis-ci.org/Blosc/JBlosc) |[![Build status](https://ci.appveyor.com/api/projects/status/am0bqlei05iw83rs?svg=true)](https://ci.appveyor.com/project/FrancescAlted/jblosc-9eoe9)|

Java interface for Blosc

The purpose of this project is to create a Java interface for the compressor Blosc. JNA has been chosen as the mechanism to communicate with the Blosc shared library.

A simple example extracted from the unit tests:
```java
    int SIZE = 100 * 100 * 100;
    ByteBuffer ibb = ByteBuffer.allocateDirect(SIZE * PrimitiveSizes.DOUBLE_FIELD_SIZE);
    for (int i = 0; i < SIZE; i++) {
        ibb.putDouble(i);
    }
    JBlosc jb = new JBlosc();
    ByteBuffer obb = ByteBuffer.allocateDirect(ibb.limit() + JBlosc.OVERHEAD);
    jb.compress(5, Shuffle.BYTE_SHUFFLE, PrimitiveSizes.DOUBLE_FIELD_SIZE, ibb, ibb.limit(), obb, obb.limit());
    ByteBuffer abb = ByteBuffer.allocateDirect(ibb.limit());
    jb.decompress(obb, abb, abb.limit());
    jb.destroy();
    assertEquals(ibb, abb);
```
## Installation
First of all, you need to install the Blosc library (visit https://github.com/Blosc/c-blosc for more details), in short, if you already have CMake, executing the following commands should do the work:
```bash
git clone https://github.com/Blosc/c-blosc.git
cd c-blosc
mkdir build
cd build
cmake -DCMAKE_GENERATOR_PLATFORM=x64 ..
cmake --build . --target install
```
Tipically in Linux/Unix the Blosc library is installed in your system search path, however, in Windows you will need to add blosc.dll to your PATH (```copy "c:\Program Files (x86)\blosc\lib\blosc.dll" c:\Windows\System32```).

Also check that your OS, Java Virtual Machine and Blosc library are using the same architecture (either 32 or 64 bit).

Build: ```mvn clean install```

If you want to use it in another maven project, after installing it you can use it as a dependency like this:

```xml

    <dependencies>
        <dependency>
            <groupId>org.blosc</groupId>
            <artifactId>jblosc</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
```