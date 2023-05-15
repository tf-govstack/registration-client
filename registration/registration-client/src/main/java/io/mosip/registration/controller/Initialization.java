package io.github.tf-govstack.registration.controller;

import com.sun.javafx.application.LauncherImpl;
import io.github.tf-govstack.registration.preloader.ClientPreLoader;

import java.nio.file.Path;


public class Initialization {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("logback.configurationFile", Path.of("lib", "logback.xml").toFile().getCanonicalPath());
        LauncherImpl.launchApplication(ClientApplication.class, ClientPreLoader.class, args);
    }
}
