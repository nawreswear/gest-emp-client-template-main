module fr.univ_amu.m1info.client {
    requires com.fasterxml.jackson.datatype.jsr310;
    requires jbcrypt;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires javafx.fxml;
    exports tn.iset.m2glnt.client.viewer;
    exports tn.iset.m2glnt.client.viewer.view;
    exports tn.iset.m2glnt.client.viewer.presenter;
    exports tn.iset.m2glnt.client.service.dto;
    exports tn.iset.m2glnt.client.service.dao;
    exports tn.iset.m2glnt.client.service.dao.exceptions;
    exports tn.iset.m2glnt.client.model;
    exports tn.iset.m2glnt.client.viewer.presenter.dialog;
    exports tn.iset.m2glnt.client.viewer.controller;
    exports tn.iset.m2glnt.client.util;
    requires javafx.controls;
    requires jdk.jfr;
    requires org.jetbrains.annotations;
    requires org.apache.logging.log4j;

}