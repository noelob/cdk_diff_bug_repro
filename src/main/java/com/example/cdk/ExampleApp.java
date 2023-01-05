package com.example.cdk;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class ExampleApp {

    // For more information, see https://docs.aws.amazon.com/cdk/latest/guide/environments.html
    public static final Environment US_WEST_2 = Environment.builder()
            .account("366098073292")
            .region("us-west-2")
            .build();

    public static void main(final String[] args) {
        App app = new App();

        new MetricsStack(app, "Metrics", StackProps.builder()
                .env(US_WEST_2)
                .build());

        app.synth();
    }
}

