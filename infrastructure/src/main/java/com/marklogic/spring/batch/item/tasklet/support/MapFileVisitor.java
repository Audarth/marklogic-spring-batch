package com.marklogic.spring.batch.item.tasklet.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class MapFileVisitor extends SimpleFileVisitor<Path> {

    private final static Logger logger = LoggerFactory.getLogger(MapFileVisitor.class);

    private PathMatcher matcher;
    private Map<String, Path> fileMap;
    private int numberOfFiles = 0;
    private int numberOfMatches = 0;

    public MapFileVisitor() {
        this("*");
    }

    public MapFileVisitor(String pattern) {
        fileMap = new HashMap<String, Path>();
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    }

    public Map<String, Path> getFileMap() {
        return fileMap;
    }

    public int getNumberOfMatches() {
        return numberOfMatches;
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        numberOfFiles++;
        if (match(file)) {
            numberOfMatches++;
            String fileName = file.getFileName().toString();
            fileMap.put(fileName, file);
            logger.debug(fileName);
        }
        return FileVisitResult.CONTINUE;
    }

    // Invoke the pattern matching method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (match(dir)) {
            return FileVisitResult.CONTINUE;
        } else {
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

    // Compares the glob pattern against the file or directory name.
    // https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    private boolean match(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            numberOfMatches++;
            return true;
        } else {
            return false;
        }

    }

}
