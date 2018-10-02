package ru.letnes.cache.filesUtil;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Класс скрывающий реализацию работы в файловой системой. Позволяет
 * по  потребности заменять реализацию тех или иных методов на собственные,
 * а так же упрощает процесс тестирования.
 *
 * @author avsharapov
 */
public class FilesImpl implements IFiles {
    @Override
    public Path getPath(String uri) {
        return Paths.get(uri);
    }

    @Override
    public Path getPath(Path path) {
        return Paths.get(path.toUri());
    }

    @Override
    public Path createTempDir(String prefix) throws IOException {
        return Files.createTempDirectory(prefix);
    }

    @Override
    public OutputStream getOutputStream(Path path) throws IOException {
        return Files.newOutputStream(path);
    }

    @Override
    public ObjectOutputStream getObjectOutputStream(OutputStream outputStream) throws IOException {
        return new ObjectOutputStream(outputStream);
    }

    @Override
    public InputStream getInputStream(Path path) throws IOException {
        return Files.newInputStream(path);
    }

    @Override
    public ObjectInputStream getObjectInputStream(InputStream inputStream) throws IOException {
        return new ObjectInputStream(inputStream);
    }

    @Override
    public Stream<Path> getPathStream(Path path) throws IOException {
        return Files.walk(path);
    }

    @Override
    public boolean deleteIfExist(Path path) throws IOException {
        return Files.deleteIfExists(path);
    }

    @Override
    public String getDefaultFileSystemSeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    @Override
    public String getRandomUUID() {
        return UUID.randomUUID().toString();
    }
}
