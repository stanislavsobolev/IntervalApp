package com.sobolev.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;

@SpringBootApplication(scanBasePackages="com.sobolev")
public class IntervalDataServicePublisher {

    public static void main(String[] args) {
        SpringApplication.run(IntervalDataServicePublisher.class, args);
    }
}
