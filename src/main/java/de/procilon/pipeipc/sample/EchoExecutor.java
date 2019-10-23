package de.procilon.pipeipc.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;

import de.procilon.pipeipc.IpcCommandExecutor;

/**
 * Simple executor that has no arguments and writes all of its input to its
 * output.
 * 
 * @author fichtelmannm
 *
 */
public class EchoExecutor implements IpcCommandExecutor {

    @Override
    public void execute(JsonNode arguments, Supplier<InputStream> in, Supplier<OutputStream> out) throws IOException {
	System.out.println("[EchoExecutor] received echo command");
	try (InputStream input = in.get(); OutputStream output = out.get()) {
	    input.transferTo(output);
	}
	System.out.println("[EchoExecutor] completed echo command");
    }

    @Override
    public Optional<String> type() {
	return Optional.of("echo");
    }
}
