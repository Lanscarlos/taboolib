package io.izzel.taboolib.module.dependency;

import io.izzel.taboolib.TabooLib;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.util.Files;

import java.io.File;
import java.util.Arrays;

public class TDependency {

    // 阿里 http://maven.aliyun.com/nexus/content/groups/public
    public static final String MAVEN_REPO = "http://repo1.maven.org/maven2";

    /**
     * 请求一个插件作为依赖，这个插件将会在所有已经添加的 Jenkins 仓库、Maven 仓库寻找
     * <p>
     * 阻塞线程进行下载/加载
     *
     * @param args 插件名称，下载地址（可选）
     * @return 是否成功加载了依赖
     */
    public static boolean requestPlugin(String... args) {
        return false;
    }

    /**
     * 请求一个库作为依赖，这个库将会在 Maven Central、oss.sonatype 以及自定义的 Maven 仓库寻找
     * <p>
     * 阻塞线程进行下载/加载
     *
     * @param type 依赖名，格式为 groupId:artifactId:version
     * @return 是否成功加载库，如果加载成功，插件将可以任意调用使用的类
     */
    public static boolean requestLib(String type, String repo, String url) {
        // 清理大小为 0 的依赖文件
        File libFolder = new File(TabooLib.getPlugin().getDataFolder(), "/libs");
        if (libFolder.exists()) {
            Arrays.stream(libFolder.listFiles()).filter(listFile -> listFile.length() == 0).forEach(File::delete);
        }
        if (type.matches(".*:.*:.*")) {
            String[] arr = type.split(":");
            File file = new File(TabooLib.getPlugin().getDataFolder(), "/libs/" + String.join("-", arr) + ".jar");
            if (file.exists()) {
                TDependencyLoader.addToPath(TabooLib.getPlugin(), file);
                return true;
            } else {
                if (downloadMaven(repo, arr[0], arr[1], arr[2], file, url)) {
                    TDependencyLoader.addToPath(TabooLib.getPlugin(), file);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private static boolean downloadMaven(String url, String groupId, String artifactId, String version, File target, String dl) {
        TLocale.Logger.info("DEPENDENCY.DOWNLOAD-START", target.getName());
        return Files.downloadFile(dl.length() == 0 ? url + "/" + groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar" : dl, Files.file(target));
    }
}
