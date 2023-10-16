package com.javarush.task.task31.task3110.command;

import com.javarush.task.task31.task3110.ConsoleHelper;
import com.javarush.task.task31.task3110.ZipFileManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class ZipCommand implements Command {

    public ZipFileManager getZipFileManager() throws Exception {
            ConsoleHelper.writeMessage("Введите полный путь файла архива:");
            Path zipPath = Paths.get(ConsoleHelper.readString());
            String str = "C://11/myzip3.zip";
//            Path zipPath = Paths.get(str);
            ConsoleHelper.writeMessage("path to atchive : " + zipPath.toFile());
        return new ZipFileManager(zipPath);
    }
}