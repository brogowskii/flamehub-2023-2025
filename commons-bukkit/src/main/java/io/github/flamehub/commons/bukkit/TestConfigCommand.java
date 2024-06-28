package io.github.flamehub.commons.bukkit;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import io.github.flamehub.commons.config.FlameConfigService;
import org.bukkit.command.CommandSender;

@Command(name = "testconfig")
public class TestConfigCommand {

    private final FlameConfigService flameConfigService;

    public TestConfigCommand(FlameConfigService flameConfigService) {
        this.flameConfigService = flameConfigService;
    }

    @Execute(name = "update")
    void execute(@Context CommandSender sender) {
        try {
            this.flameConfigService.update(TestConfig.class);
            sender.sendMessage("TestConfig updated!");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Execute(name = "get")
    void get(@Context CommandSender sender) {
        TestConfig flameConfig = (TestConfig) this.flameConfigService.getConfigInstancesByClassName().get(TestConfig.class.getName());
        sender.sendMessage(flameConfig.toString());
    }


}
