package org.mangorage.mmlg.tasks;

import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class RunGame extends JavaExec {

    @Inject
    public RunGame(String group) {
        setGroup(group);
        setDescription("Runs the game with the bootJar and mods");
        setWorkingDir(getProject().getProjectDir().toPath().resolve("build").resolve("run").toFile());
        getMainClass().set("org.mangorage.boot.Boot");

        setDependsOn(List.of("build"));
    }

    @TaskAction
    public void run() {
        Path runDir = getWorkingDir().toPath();
        Path modsDir = runDir.resolve("mods");
        Path classpathDir = runDir.resolve("classpath-game");

        Path bootJar = getAll(getProject().getConfigurations().getByName("bootJar").getSingleFile().toPath());
        Path mcJar = getProject().getConfigurations().getByName("gameJar").getSingleFile().toPath();

        getProject().getConfigurations().getByName("modJar").getFiles().forEach(file -> {
            try {
                copyFileTo(file.toPath(), modsDir.resolve(file.getName()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            copyFileTo(mcJar, classpathDir.resolve("minecraft.jar"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setClasspath(getProject().files(bootJar));
        super.exec(); // 👈 This is what actually runs the Java process
    }

    @Override
    public List<String> getArgs() {
        return List.of("--minecraftJar", "jarHere!");
    }

    public static Path getAll(Path input) {
        if (input.toString().contains("-all")) return input;
        String filename = input.getFileName().toString();
        int dotIndex = filename.lastIndexOf('.');
        String newFilename = (dotIndex != -1)
                ? filename.substring(0, dotIndex) + "-all" + filename.substring(dotIndex)
                : filename + "-all";
        return input.getParent() != null
                ? input.getParent().resolve(newFilename)
                : Path.of(newFilename);
    }

    public static Path copyFileTo(Path source, Path dest) throws IOException {
        System.out.println("Copied " + source + " -> " + dest);
        Files.createDirectories(dest.getParent());
        return Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}

