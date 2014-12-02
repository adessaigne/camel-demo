/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.adessaigne.cameldemo.basic.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

/**
 * This class is a really simple class that handles basic exercise commands from the command line.
 */
final class CommandLineReader implements Runnable {
    private final ShutdownHandler shutdownHandler;
    private final Path workingDirectory;

    /**
     * Creates a new instance.
     *
     * @param shutdownHandler Shutdown handler
     * @param workingDirectory The working directory
     */
    public CommandLineReader(ShutdownHandler shutdownHandler, Path workingDirectory) {
        this.shutdownHandler = shutdownHandler;
        this.workingDirectory = workingDirectory;
    }

    @Override
    public void run() {
        System.out.println("Welcome to the excercise.");
        System.out.println("The working directory is " + workingDirectory);
        System.out.println("Type 'exit' in order to stop the platform.");
        System.out.println();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                handleCommand(line.trim());
                System.out.print("> ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(String line) {
        switch (line) {
            case "":
                break;
            case "help":
                printHelp();
                break;
            case "exit":
            case "quit":
            case "bye":
            case "stop":
                doStop();
                break;
            default:
                System.out.println("Unknown command.");
                printHelp();
                break;
        }
    }

    private void printHelp() {
        System.out.println("The commands are:");
        System.out.println("help");
        System.out.println("exit");
    }

    public void doStop() {
        System.out.println("Stopping now !");
        shutdownHandler.shutdown();
    }
}
