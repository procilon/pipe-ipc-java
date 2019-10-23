package de.procilon.pipeipc;

import static java.util.Optional.ofNullable;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import de.procilon.pipeipc.messages.Dialog;
import de.procilon.pipeipc.messages.ErrorMessages;

public enum ExecutorRegistry {
    INSTANCE;

    private final ConcurrentMap<String, IpcCommandExecutor> executors = new ConcurrentHashMap<>();

    public void register(String type, IpcCommandExecutor executor) {
	IpcCommandExecutor previous = executors.putIfAbsent(type, executor);
	if (previous != null) {
	    throw new IllegalArgumentException(
		    ErrorMessages.MULTIPLE_EXECUTORS.render(executor.getClass(), type, previous.getClass()));
	}
	System.out.println(Dialog.EXECUTOR_REGISTERED.render(type, executor.getClass().getName()));
    }

    public void deregister(String type) {
	executors.remove(type);
    }

    public Optional<IpcCommandExecutor> find(String type) {
	return ofNullable(executors.get(type));
    }
}
