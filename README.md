| **Travis CI** | **Appveyor** |
|---------------|--------------|
|[![Build Status](https://travis-ci.org/Blosc/JBlosc.svg?branch=master)](https://travis-ci.org/Blosc/JBlosc) |[![Build status](https://ci.appveyor.com/api/projects/status/am0bqlei05iw83rs?svg=true)](https://ci.appveyor.com/project/FrancescAlted/jblosc-9eoe9)|

# JBlosc: A Java interface for Blosc

JBlosc is a Java seamless interface for C-Blosc.  A simple example extracted from the unit tests:

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

JBlosc is *fast*.  See [benchmaks here](https://github.com/Blosc/JBlosc/blob/master/Benchmarks.md).

## Installation

### Installing C-Blosc

First of all, you need to install the [C-Blosc library](https://github.com/Blosc/c-blosc). In short, if you already have CMake and a C compiler in your system, executing the following commands should do the work:

```bash
cd c-blosc
mkdir build
cd build
cmake ..   # Add -DCMAKE_GENERATOR_PLATFORM=x64 for Win64 platforms
cmake --build . --target install
```

In Linux/Unix the Blosc library is typically installed in your system search path.  However, on Windows you will need to copy the blosc.dll somewhere in your PATH (e.g. `copy "c:\Program Files (x86)\blosc\lib\blosc.dll" c:\Windows\System32`).

Also check that your OS, Java Virtual Machine and Blosc library are all using the same architecture (either 32 or 64 bit).

### Installing JBlosc
After cloning the repo, move into the *inner* jblosc directory and build, test and install the package:

```
mvn clean install
```

If you want to use it in another Maven project, after installing JBlosc you can use it as a dependency like this:

```xml
    <dependencies>
        <dependency>
            <groupId>org.blosc</groupId>
            <artifactId>jblosc</artifactId>
            <version>JBLOSC_VERSION</version>
        </dependency>
    </dependencies>
```

Replace the `JBLOSC_VERSION` placeholder by the desired version.
