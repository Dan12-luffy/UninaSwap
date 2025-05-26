module com.uninaswap.uninaswap {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.jfree.jfreechart;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.google.common;
    requires annotations;
    requires java.desktop;
    requires svg.salamander;


    exports com.uninaswap.controllers;
    opens com.uninaswap.controllers to javafx.fxml;
    exports com.uninaswap.logic;
    exports com.uninaswap.model;
    opens com.uninaswap.logic to javafx.fxml;
}