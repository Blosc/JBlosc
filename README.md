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
First of all, you need to install the Blosc library (visit https://github.com/Blosc/c-blosc for more details).

### Installing Blosc with Conan
If you have [conan](https://conan.io/) installed, executing ```conan install c-blosc/vX.Y.Z@francescalted/stable``` will download you Blosc binaries. Check for latest version [here](https://bintray.com/blosc/Conan/c-blosc%3Afrancescalted) and replace accordingly the vX.Y.Z placeholder.

Then you just need to check the directory $HOME/.conan/data/c-blosc/vX.Y.Z/francescalted/stable/package/ and copy the corresponding
.dll, .so or .dylib in the $PATH for Windows (recommended copy .dll in C:\Windows\System32) or in your shared library path for Unix 
(tipically /usr/lib or /usr/local/lib).

**Note**: You can manually download Blosc binaries from https://bintray.com/blosc/Conan/c-blosc%3Afrancescalted. In the Files section
inside the package folder there are all the Blosc binaries but be sure to check the conaninfo.txt for the version which matches your
SO and architecture.

### Installing Blosc with CMake
In short, if you already have CMake, executing the following commands should do the work:
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

### Installing JBlosc
Currently you can find JBlosc in JCenter and in Maven Central, so choose your prefered repository and add the corresponding dependencies.

### Using Maven
Add the following dependency code to pom.xml:

```xml
<dependency>
  <groupId>org.blosc</groupId>
  <artifactId>jblosc</artifactId>
  <version>JBLOSC_VERSION</version>
</dependency>
```
### Using Gradle
Add the following dependency to build.gradle:

```xml
compile 'org.blosc:jblosc:JBLOSC_VERSION'
```

Check https://bintray.com/blosc/Maven/JBlosc for the latest version.

### Manual installation
Move to the jblosc directory.

Build: ```mvn clean install```

If you want to use it in another maven project, after installing it you can use it as a dependency like this:

```xml

    <dependencies>
        <dependency>
            <groupId>org.blosc</groupId>
            <artifactId>jblosc</artifactId>
            <version>JBLOSC_VERSION_X.Y.Z</version>
        </dependency>
    </dependencies>
```
Do not change the version as it will be using the one which figures in pom.xml.
