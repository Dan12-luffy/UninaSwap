package com.uninaswap.model;

public enum Faculty {
    INFORMATICA("Informatica"),
    SCIENZE("Scienze"),
    MATEMATICA("Matematica"),
    ECONOMIA("Economia"),
    ARTE("Arte"),
    MEDICINA("Medicina"),
    BIOLOGIA("Biologia"),
    FILOSOFIA("Filosofia"),
    GEOGRAFIA("Geografia"),
    PSICOLOGIA("Psicologia"),
    CHIMICA("Chimica"),
    ASTRONOMIA("Astronomia"),
    TURISMO("Turismo"),
    LINGUISTICA("Linguistica"),
    MUSICA("Musica");

    private final String facultyName;

    Faculty(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    @Override
    public String toString() {
        return facultyName;
    }
}