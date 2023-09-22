package com.javarush.task.task31.task3110.command;

import com.javarush.task.task31.task3110.ConsoleHelper;
import com.javarush.task.task31.task3110.ZipFileManager;
import com.javarush.task.task31.task3110.exception.PathIsNotFoundException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZipExtractCommand extends ZipCommand {
    @Override
    public void execute() throws Exception {
        try {
            ConsoleHelper.writeMessage("Извлечение архива.");

            ZipFileManager zipFileManager = getZipFileManager();

            ConsoleHelper.writeMessage("Введите полное имя директории для извлечения архива:");
            Path outputFolder = Paths.get(ConsoleHelper.readString());
//            Path outputFolder = Paths.get("C://22");
            ConsoleHelper.writeMessage("Path for extract (outfolder) : " + outputFolder);
            if(!Files.exists(outputFolder)) Files.createDirectory(outputFolder);
            if(!Files.isDirectory(outputFolder)) throw new PathIsNotFoundException();
            zipFileManager.extractAll(outputFolder);

            ConsoleHelper.writeMessage("Архив извлечён.");

        } catch (PathIsNotFoundException e) {
            ConsoleHelper.writeMessage("Вы неверно указали имя директории.");
        }
    }
}
