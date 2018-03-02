package org.ballerinalang.grpc.utils;

import com.google.protobuf.DescriptorProtos;
import org.ballerinalang.grpc.exception.BalGenToolException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class BalFileGenerationUtils {
    
    /**
     *
     * @param descPath
     */
    public static void createMetaFolder(String descPath) {
        new File(descPath).getParentFile().mkdirs();
        byte dataBytes[] = new byte[0];
        try {
            Path file = Paths.get(descPath);
            Files.write(file, dataBytes);
        } catch (IOException e) {
            throw new BalGenToolException("Error creating .desc meta files.", e);
        }
    }
    
    /**
     *
     * @param command
     * @throws UnsupportedEncodingException
     */
    public static  void  generateDescroptor(String command) throws UnsupportedEncodingException {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase(Locale.ENGLISH).startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            ProcessBuilder dir = builder.command("cmd.exe", "/c", "dir");
        } else {
            builder.command("sh", "-c", command);
        }
        builder.directory(new File(System.getProperty("user.home")));
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
           throw new BalGenToolException("Error in executing protoc command '" + command + "'." ,e);
        }
        new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8")).lines()
                .forEach(System.out::println);
    }
    
    /**
     *
     * @param decedentPath
     * @return
     */
    public  static String getDescriptorPath(String decedentPath){
    return BalGenerationConstants.META_DEPENDENCY_LOCATION + decedentPath
            .substring(decedentPath.lastIndexOf("/"), decedentPath.length())
            .replace(".proto", "") + ".desc";
    }
    
    public static byte[] getRootByteArray(String exePath, String protoPath, String descriptorPath)
            throws InterruptedException, IOException {
        String command = exePath + " \\\n" +
                "--proto_path=" + protoPath.substring(0, protoPath.lastIndexOf('/')) + " \\\n" +
                protoPath + " \\\n" +
                "--descriptor_set_out=" + descriptorPath;
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase(Locale.ENGLISH).startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", "dir");
        } else {
            builder.command("sh", "-c", command);
        }
        builder.directory(new File(System.getProperty("user.home")));
        Process process = null;
        process = builder.start();
        new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8")).lines()
                .forEach(System.out::println);
        process.waitFor();
        File initialFile = new File(descriptorPath);
        InputStream targetStream = new FileInputStream(initialFile);
        DescriptorProtos.FileDescriptorSet set = DescriptorProtos.FileDescriptorSet.parseFrom(targetStream);
        if (set.getFileList().size() > 0) {
            return set.getFile(0).toByteArray();
        } else {
            return null;
        }
    }
}
