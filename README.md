# lwjgl3-demos + GraalVM native image

Demo suite for LWJGL 3, as described in the [upstream repo](https://github.com/LWJGL/lwjgl3-demos)
with screenshots.

---

This fork provides build scripts to compile the project to native executable using
the GraalVM native-image utility.

All demos in [src](src/org/lwjgl/demo) are included in the build. Most demos are portable
across different OSes, but some are GPU-specific, like the [Vulkan demos](src/org/lwjgl/demo/vulkan)
which work only on discrete GPU with supporting Vulkan driver, or the
[CUDA demos](src/org/lwjgl/demo/cuda) which require a NVIDIA graphics card to run.

Gradle and Maven build scripts are provided for building the project,
which requires JDK 11+ or GraalVM 21+ (for native image).

## GraalVM pre-requisites

### Setup for GraalVM native-image utility and C compiler

The [GraalVM native-image](https://www.graalvm.org/reference-manual/native-image) page
shows how to set up GraalVM and its native-image utility for common platforms.
[Gluon](https://gluonhq.com/) also provides some setup
[details](https://docs.gluonhq.com/#_platforms) for GraalVM native-image creation.

### Native-image configuration files

The GraalVM native-image utility will use the configuration files in platform-specific
[folder](graal-cfg) to assist in the native-image generation.

The configuration files were generated when running the demos in standard JVM with a
[GraalVM agent](https://www.graalvm.org/reference-manual/native-image/BuildConfiguration/#assisted-configuration-of-native-image-builds),
which tracks all usages of dynamic features of an execution of the demos
and writes the info to the configuration files.
Usage of the agent is contained in the Gradle build script [(line 134)](build.gradle#L134),
or in the Maven build script [(line 171)](pom.xml#L171), which can be turned on (i.e. uncommented)
and the demos are re-run to update the configuration files, if need be.

The above agent is not perfect; it sometimes misses some classes referenced via reflection
which is used extensively in LWJGL. In the case of the LWJGL demos, many `org.lwjgl.bgfx.*`
and `org.lwjgl.assimp.*` classes need be added manually to the configuration files,
to avoid ClassNotFoundException being thrown when running the [bgfx](src/org/lwjgl/demo/bgfx)
or [assimp](src/org/lwjgl/demo/opengl/assimp) demos in the generated native image.

## Gradle build tasks

### Run in standard JVM

To build and run the demos in standard JVM with Gradle, execute the `run` task:

	gradlew run

By default, the graphical demo launcher application is executed
by the above `run` task without parameter. To run a specific demo, e.g.
[WavefrontObjDemo](src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java), execute the `run` task
with that specific demo class as parameter:

	gradlew run --args=opengl.assimp.WavefrontObjDemo

System properties can be passed on to the running demo with the -D parameter,
e.g. to print out some debug info in the console:

	gradlew run --args=opengl.assimp.WavefrontObjDemo -Dorg.lwjgl.util.Debug=true

The above tasks can use any standard JDK 11+.

### Produce native executable

To generate native executable, GraalVM 22+ need be set up as mentioned in
*GraalVM pre-requisites* section above.

Once GraalVM is set up and available in the path, run the `nativeCompile` task:

	gradlew nativeCompile

The `nativeCompile` task would take a while to compile all demo source code and
link them with the LWJGL libraries into an executable file.
The resulting `lwjgl3-demos` file is (in Linux):

	build/native/nativeCompile/lwjgl3-demos

(or if building on a Windows machine:

	build\native\nativeCompile\lwjgl3-demos.exe

)

which can then be run to show the graphical demo launcher application:

	./build/native/nativeCompile/lwjgl3-demos

Or to run a demo class directly as a parameter (e.g.
[DepthEdgeShaderDemo20](src/org/lwjgl/demo/opengl/fbo/DepthEdgeShaderDemo20.java)):

	./build/native/nativeCompile/lwjgl3-demos opengl.fbo.DepthEdgeShaderDemo20

Or to run a demo class directly with its own parameters (e.g.
[Bump](src/org/lwjgl/demo/bgfx/Bump.java)):

	./build/native/nativeCompile/lwjgl3-demos bgfx.Bump --gl --intel

System properties can be passed on to the running demo with the -D parameter,
e.g. to print out some debug info in the console:

	./build/native/nativeCompile/lwjgl3-demos opengl.fbo.DepthEdgeShaderDemo20 -Dorg.lwjgl.util.Debug=true

(or if building on a Windows machine:

	build\native\nativeCompile\lwjgl3-demos.exe
	build\native\nativeCompile\lwjgl3-demos.exe bgfx.Bump --gl --intel
	build\native\nativeCompile\lwjgl3-demos.exe opengl.fbo.DepthEdgeShaderDemo20
	build\native\nativeCompile\lwjgl3-demos.exe opengl.fbo.DepthEdgeShaderDemo20 -Dorg.lwjgl.util.Debug=true

)

## Maven build tasks

### Run in standard JVM

To build and run the demos in standard JVM with Maven, execute the
`compile` then `exec:exec` tasks:

	mvnw compile
	mvnw exec:exec

By default, the graphical demo launcher application is executed
by the above `exec:exec` task without parameter. To run a specific demo, e.g.
[WavefrontObjDemo](src/org/lwjgl/demo/opengl/assimp/WavefrontObjDemo.java), execute the `exec:exec` task
with that specific demo class as value of the property `class`:

	mvnw exec:exec -Dclass=opengl.assimp.WavefrontObjDemo

System properties can be passed on to the running demo with the -Dsys.props parameter,
e.g. to print out some debug info in the console:

	mvnw exec:exec -Dclass=opengl.assimp.WavefrontObjDemo -Dsys.props="-Dorg.lwjgl.util.Debug=true"

The above tasks can use any standard JDK 11+.

### Produce native executable

To generate native executable, GraalVM 22+ need be set up as mentioned in
*GraalVM pre-requisites* section above.

Once GraalVM is set up and available in the path, run the `package` task:

	mvnw package

The `package` task would take a while to compile all demo source code and
link them with the LWJGL libraries into an executable file.
The resulting `lwjgl3-demos` file is (in Linux):

	target/lwjgl3-demos

(or if building on a Windows machine:

	target\lwjgl3-demos.exe

)

which can then be run to show the graphical demo launcher application:

	./target/lwjgl3-demos

Or to run a demo class directly as a parameter (e.g.
[Demo33Ubo](src/org/lwjgl/demo/opengl/raytracing/Demo33Ubo.java)):

	./target/lwjgl3-demos opengl.raytracing.Demo33Ubo

Or to run a demo class directly with its own parameters (e.g.
[Bump](src/org/lwjgl/demo/bgfx/Bump.java)):

	./target/lwjgl3-demos bgfx.Bump --gl --intel

System properties can be passed on to the running demo with the -D parameter,
e.g. to print out some debug info in the console:

	./target/lwjgl3-demos opengl.raytracing.Demo33Ubo -Dorg.lwjgl.util.Debug=true

(or if building on a Windows machine:

	target\lwjgl3-demos.exe
	target\lwjgl3-demos.exe bgfx.Bump --gl --intel
	target\lwjgl3-demos.exe opengl.raytracing.Demo33Ubo
	target\lwjgl3-demos.exe opengl.raytracing.Demo33Ubo -Dorg.lwjgl.util.Debug=true

)

