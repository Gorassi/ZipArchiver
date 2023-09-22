package com.javarush.task.task31.task3110;

import com.javarush.task.task31.task3110.command.ExitCommand;
import com.javarush.task.task31.task3110.exception.PathIsNotFoundException;
import com.javarush.task.task31.task3110.exception.WrongZipFileException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFileManager {
    // Полный путь zip файла
    private final Path zipFile;

    public ZipFileManager(Path zipFile) {
        this.zipFile = zipFile;
    }

    public void createZip(Path source) throws Exception {
        // Проверяем, существует ли директория, где будет создаваться архив
        // При необходимости создаем ее
        Path zipDirectory = zipFile.getParent();
        if (Files.notExists(zipDirectory))
            Files.createDirectories(zipDirectory);

        // Создаем zip поток
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFile))) {

            if (Files.isDirectory(source)) {
                // Если архивируем директорию, то нужно получить список файлов в ней
                FileManager fileManager = new FileManager(source);
                List<Path> fileNames = fileManager.getFileList();

                // Добавляем каждый файл в архив
                for (Path fileName : fileNames)
                    addNewZipEntry(zipOutputStream, source, fileName);

            } else if (Files.isRegularFile(source)) {

                // Если архивируем отдельный файл, то нужно получить его директорию и имя
                addNewZipEntry(zipOutputStream, source.getParent(), source.getFileName());
            } else {

                // Если переданный source не директория и не файл, бросаем исключение
                throw new PathIsNotFoundException();
            }
        }
    }

    public List<FileProperties> getFilesList() throws Exception {
        // Проверяем существует ли zip файл
        if (!Files.isRegularFile(zipFile)) {
            throw new WrongZipFileException();
        }

        List<FileProperties> files = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();

            while (zipEntry != null) {
                // Поля "размер" и "сжатый размер" не известны, пока элемент не будет прочитан
                // Давайте вычитаем его в какой-то выходной поток
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                copyData(zipInputStream, baos);

                FileProperties file = new FileProperties(zipEntry.getName(), zipEntry.getSize(), zipEntry.getCompressedSize(), zipEntry.getMethod());
                files.add(file);
                zipEntry = zipInputStream.getNextEntry();
            }
        }

        return files;
    }

    public void extractAll(Path outputFolder) throws Exception{

    ConsoleHelper.writeMessage("Method extractAll() started...");
    ZipFile zip = new ZipFile(zipFile.toFile());

    Enumeration en = zip.entries();
    String name;
    long size;
    boolean isDir;
    BufferedReader reader = null;
    BufferedWriter writer = null;
    while(en.hasMoreElements()){
        ZipEntry entry = (ZipEntry) en.nextElement();
        name = entry.getName();
        size = entry.getSize();
        isDir = entry.isDirectory();
        if(!isDir){
            InputStream zin = zip.getInputStream(entry);
            File file = new File("C:/22/" + name);
            if(!file.exists()) Files.createFile(file.toPath());
            OutputStream out = new FileOutputStream(file);
            reader = new BufferedReader(new InputStreamReader(zin));
            writer = new BufferedWriter(new OutputStreamWriter(out));
            String str;
            while( (str = reader.readLine()) != null) writer.write(str + "\n");
        } else {
            String dir = "C:/22/" + name;
            ConsoleHelper.writeMessage("Name for new dir : " + dir);
            if(!Files.exists(Paths.get(dir))) Files.createDirectory(Paths.get(dir));
        }
        reader.close();
        writer.close();
        ConsoleHelper.writeMessage("------------- >>> \n");

    }

    }

    private void addDirectory(ZipInputStream zin, Path path) throws Exception {
        //Files.createDirectory(newDir);
        ConsoleHelper.writeMessage("Method addDirectory : " + path);
        if(!Files.exists(path)) Files.createDirectory(path);
    }

    private void extractFiles(ZipInputStream zin, Path path) throws Exception{
        OutputStream out = Files.newOutputStream(path);
        for(int c = zin.read(); c != -1; c = zin.read() ) out.write(c);
        out.flush();
        zin.closeEntry();
        out.close();
    }

    private void addNewZipEntry(ZipOutputStream zipOutputStream, Path filePath, Path fileName) throws Exception {
        Path fullPath = filePath.resolve(fileName);
        try (InputStream inputStream = Files.newInputStream(fullPath)) {
            ZipEntry entry = new ZipEntry(fileName.toString());

            zipOutputStream.putNextEntry(entry);

            copyData(inputStream, zipOutputStream);

            zipOutputStream.closeEntry();
        }
    }

    private void copyData(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[8 * 1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
    }
}
