package org.mangorage.mmlg.tasks;

import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class RunGame extends JavaExec {


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
        // Ensure parent directory exists
        System.out.println("Copied " + source + " -> " + dest);

        Files.createDirectories(dest.getParent());

        // Copy file (overwrite if exists)
        return Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    @Inject
    public RunGame(String group) {
        setGroup(group);

        Path bootJar = getAll(getProject().getConfigurations().getByName("bootJar").getSingleFile().toPath());
        Path mcJar = getProject().getConfigurations().getByName("gameJar").getSingleFile().toPath();

        try {
            copyFileTo(mcJar, getProject().getProjectDir().toPath().resolve("build").resolve("run").resolve("classpath-game").resolve("minecraft.jar"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(getProject().getProjectDir());

        setWorkingDir(getProject().file("build/run/"));
        setClasspath(getProject().files(bootJar));
        getMainClass().set("org.mangorage.boot.Boot");
    }

    @Override
    public List<String> getArgs() {
        return List.of("--minecraftJar", "jarHere!");
    }

}
