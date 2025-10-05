package org.mangorage.mmlg;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mangorage.mmlg.tasks.RunGame;

public final class MMLGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getConfigurations().create("modJar", t -> {
            t.setVisible(true);
            t.setTransitive(false);
        });

        project.getConfigurations().create("gameJar", t -> {
            t.setVisible(true);
            t.setTransitive(false);
        });

        project.getConfigurations().create("bootJar", t -> {
            t.setVisible(true);
            t.setTransitive(false);
        });

        project.getTasks().register("runMCGame", RunGame.class, "minecraft");
    }
}
