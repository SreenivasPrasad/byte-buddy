package net.bytebuddy.build.gradle.android.classpath;

import com.android.build.api.AndroidPluginVersion;
import com.android.build.api.variant.Variant;
import org.gradle.api.file.FileCollection;

import java.lang.reflect.InvocationTargetException;

/**
 * Needed to query the runtime classpath for an Android project, which process has changed in recent versions of the
 * AGP plugin, so each method gets its own implementation.
 */
public interface DependenciesClasspathProvider {

    /**
     * Returns the appropriate {@link DependenciesClasspathProvider} implementation based on the AGP version that the host
     * project is running.
     *
     * @param currentVersion The current AGP version used in the host project.
     */
    static DependenciesClasspathProvider getInstance(AndroidPluginVersion currentVersion) {
        boolean isLowerThan73 = currentVersion.compareTo(new AndroidPluginVersion(7, 3)) < 0;
        try {
            if (isLowerThan73) {
                return (DependenciesClasspathProvider) Class.forName("net.bytebuddy.build.gradle.android.classpath.impl.LegacyDependenciesClasspathProvider").getDeclaredConstructor().newInstance();
            } else {
                return (DependenciesClasspathProvider) Class.forName("net.bytebuddy.build.gradle.android.classpath.impl.DefaultDependenciesClasspathProvider").getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    FileCollection getRuntimeClasspath(Variant variant);
}
