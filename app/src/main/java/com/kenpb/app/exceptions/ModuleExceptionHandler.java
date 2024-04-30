package com.kenpb.app.exceptions;

public class ModuleExceptionHandler extends RuntimeException {
    public ModuleExceptionHandler(String message) {
        super(message);
    }

    public static class FileSizeExceededModuleException extends ModuleExceptionHandler {
        public FileSizeExceededModuleException(String message) {
            super(message);
        }
    }



    public static class InvalidFileTypeException extends ModuleExceptionHandler {
        public InvalidFileTypeException(String message) {
            super(message);
        }
    }

    public static class InvalidModuleException extends ModuleExceptionHandler {
        public InvalidModuleException(String message) {
            super(message);
        }
    }

    public static class JarAlreadyExitException extends ModuleExceptionHandler {
        public JarAlreadyExitException(String message) {
            super(message);
        }
    }


    public static class ModuleNotFoundException extends ModuleExceptionHandler {
        public ModuleNotFoundException(String message) {
            super(message);
        }
    }

    public static class ModuleNotRunningException extends ModuleExceptionHandler {
        public ModuleNotRunningException(String message) {
            super(message);
        }
    }

    public static class ModuleInUseException extends ModuleExceptionHandler {
        public ModuleInUseException(String message) {
            super(message);
        }
    }

    public static class ModuleAlreadyRunningModuleException extends ModuleExceptionHandler {
        public ModuleAlreadyRunningModuleException(String message) {
            super(message);
        }
    }

    public static class ModuleNotLoadedException extends RuntimeException {
        public ModuleNotLoadedException(String pluginIsNotLoaded) {
            super(pluginIsNotLoaded);
        }
    }

    public static class NoPluginsFoundException extends RuntimeException {
        public NoPluginsFoundException(String noPluginsFound) {
            super(noPluginsFound);
        }
    }
}
