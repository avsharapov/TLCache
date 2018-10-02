package ru.letnes.cache.filesutil;

import java.io.*;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Интерфейс для собственной реализации работы с файловой системой.
 */
public interface IFiles {
    Path getPath(String uri);

    Path getPath(Path path);

    Path createTempDir(String prefix) throws IOException;

    OutputStream getOutputStream(Path path) throws IOException;

    ObjectOutput getObjectOutputStream(OutputStream outputStream) throws IOException;

    InputStream getInputStream(Path path) throws IOException;

    ObjectInput getObjectInputStream(InputStream inputStream) throws IOException;

    Stream<Path> getPathStream(Path path) throws IOException;

    boolean deleteIfExist(Path path) throws IOException;

    String getDefaultFileSystemSeparator();

    String getRandomUUID();
}
