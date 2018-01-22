package org.arquillian.container.chameleon.api;

public enum Mode {

    EMBEDDED("embedded"),
    MANAGED("managed"),
    REMOTE("remote");

    private String mode;

    Mode(String mode) {
        this.mode = mode;
    }

    public String mode() {
        return this.mode;
    }

}
