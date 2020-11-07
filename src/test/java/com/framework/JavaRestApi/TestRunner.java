package com.framework.JavaRestApi;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
//cuke runner
@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/"
)
public class TestRunner {
}