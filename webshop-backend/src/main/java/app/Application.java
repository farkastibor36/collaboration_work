package app;

import controller.ConsoleMenu;
import exceptions.FailedToDeleteException;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws FailedToDeleteException, IOException, InterruptedException {
        ConsoleMenu consolemenu = new ConsoleMenu();
        consolemenu.start();
    }
}