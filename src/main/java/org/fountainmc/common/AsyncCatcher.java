package org.fountainmc.common;

import com.google.common.base.VerifyException;

import static com.google.common.base.Preconditions.*;

public class AsyncCatcher {

    private AsyncCatcher() {
    }

    public static void checkNotAsync(String reason) {
        if (Thread.currentThread() != FountainImplementation.getMinecraftServer().getServerThread()) {
            throw new IllegalStateException(illegalAsyncOpMessage(reason));
        }
    }

    private static String illegalAsyncOpMessage(String reason) {
        return "A plugin tried to " + checkNotNull(reason, "Null reason") + " asynchronously!";
    }

    public static void verifyNotAsync() {
        if (Thread.currentThread() != FountainImplementation.getMinecraftServer().getServerThread()) {
            throw new VerifyException();
        }
    }
}