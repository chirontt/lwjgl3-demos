package org.lwjgl.graalvm;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.Pointer.*;

import java.util.Optional;

import org.lwjgl.Version;
import org.lwjgl.Version.BuildType;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.RecomputeFieldValue.Kind;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
/*
@TargetClass(org.lwjgl.Version.class)
final class Target_org_lwjgl_Version {
    
    @Alias
    @RecomputeFieldValue(kind = Kind.FromAlias, isFinal = true)
    public static int VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION;

    @Alias
    @RecomputeFieldValue(kind = Kind.FromAlias, isFinal = true)
    public static BuildType BUILD_TYPE;

    @Substitute
    public static String getVersion() {
        String implVersion = Version.class.getPackage().getImplementationVersion();
        return String.valueOf(VERSION_MAJOR) +
                '.' + VERSION_MINOR +
                '.' + VERSION_REVISION + BUILD_TYPE.postfix +
                ' ' + Optional.ofNullable(implVersion).orElse("SNAPSHOT");
    }
}
*/

@TargetClass(org.lwjgl.system.ThreadLocalUtil.class)
final class Target_org_lwjgl_system_ThreadLocalUtil {

    @Alias
    @RecomputeFieldValue(kind = Kind.FromAlias, isFinal = true)
    private static long JNI_NATIVE_INTERFACE;

    @Alias
    @RecomputeFieldValue(kind = Kind.FromAlias, isFinal = true)
    private static long FUNCTION_MISSING_ABORT;

    @Substitute
    public static void setFunctionMissingAddresses(int functionCount, int index) {
        if (functionCount == 0) {
            long missingCaps = memGetAddress(JNI_NATIVE_INTERFACE + Integer.toUnsignedLong(index) * POINTER_SIZE);
            if (missingCaps != NULL) {
                getAllocator().free(missingCaps);
                memPutAddress(JNI_NATIVE_INTERFACE + Integer.toUnsignedLong(index) * POINTER_SIZE, NULL);
            }
        } else {
            long missingCaps = getAllocator().malloc(Integer.toUnsignedLong(functionCount) * POINTER_SIZE);
            for (int i = 0; i < functionCount; i++) {
                memPutAddress(missingCaps + Integer.toUnsignedLong(i) * POINTER_SIZE, FUNCTION_MISSING_ABORT);
            }

            //the whole purpose of substituting this method is just to remove the following line
            //(which causes the generated native image to crash!)
            //memPutAddress(JNI_NATIVE_INTERFACE + Integer.toUnsignedLong(index) * POINTER_SIZE, missingCaps);
        }
    }
}

/** Dummy class with the file's name. */
public class LwjglSubstitutions {
}