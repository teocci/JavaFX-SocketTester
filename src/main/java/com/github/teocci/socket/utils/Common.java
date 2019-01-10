package com.github.teocci.socket.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-04
 */
public class Common
{
    private static Pattern EXCLAMATION_PATH = Pattern.compile("/([^/!]*!)/");

    private static final Map<URL, File> FILE_REGISTRY = new ConcurrentHashMap<>();

    public static File getFileFromResources(String fileName)
    {
        ClassPathResource resource = new ClassPathResource(fileName);
        try {
            System.out.println("resource: " + resource.getURL());
        } catch (IOException e) {
            e.printStackTrace();
        }


//
//        File file = null;
//        if (url != null) {
//            System.out.println(url.toString());
//            try {
//                file = new File(url.toURI());
//            } catch (URISyntaxException e) {
//                try {
//                    file = new File(url.getPath());
//                } catch (Exception ne) {
//                    ne.printStackTrace();
//                }
//            }
//        }

        return null;
    }

    public static File getFileFromJar(String fileName)
    {
        ClassPathResource resource = new ClassPathResource(fileName);
        try {
            URL resourceUrl = resource.getURL();
            if (ResourceUtils.isJarURL(resourceUrl)) {
                File tempFile = FILE_REGISTRY.get(resourceUrl);
                if (tempFile == null || !tempFile.exists()) {
                    System.out.println("Extracting File for URL: {} " + resourceUrl);
                    tempFile = getFile(resource, "tmp");
                    FILE_REGISTRY.put(resourceUrl, tempFile);
                } else {
                    System.out.println("File found in registry for URL: {} " + resourceUrl);
                }
                return new File(tempFile.toURI());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static File getFile(Resource resource, String extractPath) throws IOException
    {
        return ResourceUtils.isJarURL(resource.getURL()) ? getFromJar(resource, extractPath) : resource.getFile();
    }

    private static File getFromJar(Resource resource, String extractPath) throws IOException
    {
        Preconditions.checkArgument(extractPath != null, "Extract Path cannot be null");
        FileObject file = VFS.getManager().resolveFile(maybeFixUri(resource));
        File extractDir;
        extractDir = new File(extractPath);
        if (!extractDir.exists() || !extractDir.isDirectory()) {
            FileUtils.forceMkdir(extractDir);
            System.out.println("TEMP EXTRACT DIR CREATED {} " + extractDir.getAbsolutePath());
        }
        return copyToDir(file, extractDir);
    }

    private static String maybeFixUri(Resource resource) throws IOException
    {
        String uri = resource.getURI().toString();
        uri = maybeFixUriPrefix(uri);
        uri = maybeFixExclamationPath(uri);
        return uri;
    }

    private static File copyToDir(FileObject jarredFile, File destination) throws IOException
    {
        return copyToDir(jarredFile, destination, true);
    }

    private static File copyToDir(FileObject jarredFile, File destination, boolean retryIfImaginary) throws IOException
    {
        switch (jarredFile.getType()) {
            case FILE:
                return copyFileToDir(jarredFile, destination);
            case FOLDER:
                return copyDirToDir(jarredFile, destination);
            case IMAGINARY:
                if (retryIfImaginary) {
                    System.out.println("Imaginary file found, retrying extraction");
                    VFS.getManager().getFilesCache().removeFile(jarredFile.getFileSystem(), jarredFile.getName());
                    FileObject newJarredFile = VFS.getManager().resolveFile(jarredFile.getName().getURI());
                    return copyToDir(newJarredFile, destination, false);
                } else {
                    System.out.println("Imaginary file found after retry, abandoning retry");
                }

            default:
                throw new IllegalStateException("File Type not supported: " + jarredFile.getType());
        }
    }

    private static File copyDirToDir(FileObject jarredDir, File destination) throws FileSystemException
    {
        File tempDir = new File(destination, jarredDir.getName().getBaseName());
        createDir(tempDir);
        Arrays.stream(jarredDir.getChildren())
                .forEach(fileObject -> {
                    try {
                        copyToDir(fileObject, tempDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return tempDir;
    }

    private static File copyFileToDir(FileObject jarredFile, File destination) throws IOException
    {
        File tempFile = new File(destination, jarredFile.getName().getBaseName());
        createFile(tempFile);
        System.out.println("TEMP FILE CREATED {} " + tempFile.getAbsolutePath());
        FileUtils.copyInputStreamToFile(jarredFile.getContent().getInputStream(), tempFile);
        return tempFile;
    }

    private static void createDir(File file)
    {
        if (!file.exists() && !file.mkdir()) {
            throw new IllegalStateException(String.format("Could not create temp directory: %s", file.getAbsolutePath()));
        }
    }

    private static void createFile(File file) throws IOException
    {
        if (file.exists()) {
            FileUtils.forceDelete(file);
        }
        if (!file.createNewFile()) {
            throw new IllegalStateException(String.format("Could not create temp jarredFile: %s", file.getAbsolutePath()));
        }
    }


    private static String maybeFixExclamationPath(String uri)
    {
        String fixedUri = uri;
        Matcher matcher = EXCLAMATION_PATH.matcher(uri);
        while (matcher.find()) {
            String match = matcher.group(1);
            if (!match.endsWith(".jar!")) {
                fixedUri = fixedUri.replaceFirst(match, match.substring(0, match.length() - 1));
            }
        }
        return fixedUri;
    }

    private static String maybeFixUriPrefix(String uri)
    {
        int numOfJarsInResource = numbOfJars(uri);
        String jarPrefix = jarPrefix(numOfJarsInResource);
        String fixedUri = jarPrefix + uri.substring(4);
        return fixedUri;
    }

    private static String jarPrefix(int n)
    {
        return IntStream.range(0, n)
                .mapToObj(num -> "jar:")
                .reduce((r, l) -> r + l)
                .orElse("jar:");
    }

    private static int numbOfJars(String uri)
    {
        Matcher matcher = Pattern.compile("\\.jar!").matcher(uri);
        int matches = 0;
        while (matcher.find()) {
            matches++;
        }
        return matches;
    }

    public static File getFileFromURL(String fileName)
    {
        URL url = Common.class.getResource(fileName);
        File file = null;
        if (url != null) {
            System.out.println(url.toString());
            try {
                file = new File(url.toURI());
            } catch (URISyntaxException e) {
                try {
                    file = new File(url.getPath());
                } catch (Exception ne) {
                    ne.printStackTrace();
                }
            }
        }

        return file;
    }

    public static InputStream accessFile(String fileName)
    {
        // this is the path within the jar file
        InputStream input = Common.class.getResourceAsStream(fileName);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = Common.class.getClassLoader().getResourceAsStream(fileName);
        }

        return input;
    }
}
