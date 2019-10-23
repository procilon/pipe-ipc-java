package de.procilon.pipeipc.sample;

import de.procilon.pipeipc.ExecutorRegistry;

public class RegisterEcho {
    static {
	ExecutorRegistry.INSTANCE.register("echo", new EchoExecutor());
    }
}
