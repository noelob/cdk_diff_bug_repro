package com.example.cdk;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.AwsIntegration;
import software.amazon.awscdk.services.apigateway.AwsIntegrationProps;
import software.amazon.awscdk.services.apigateway.IntegrationOptions;
import software.amazon.awscdk.services.apigateway.IntegrationResponse;
import software.amazon.awscdk.services.apigateway.MethodOptions;
import software.amazon.awscdk.services.apigateway.MethodResponse;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.apigateway.RestApiProps;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.PolicyStatementProps;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.RoleProps;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.s3.Bucket;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class MetricsStack extends Stack {

    public MetricsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Bucket bucket = Bucket.Builder.create(this, "data").build();

        RestApi api = new RestApi(this, "api",
                RestApiProps.builder().restApiName("Metrics").build());


        Role executionRole = new Role(this, "api-gateway-s3-assume-role", RoleProps.builder()
                .assumedBy(new ServicePrincipal("apigateway.amazonaws.com"))
                .roleName("API-Gateway-S3-Integration-Role")
                .build()
        );

        executionRole.addToPolicy(
                new PolicyStatement(PolicyStatementProps.builder()
                        .resources(List.of(
                                bucket.getBucketArn(),
                                bucket.getBucketArn() + "/*"
                        ))
                        .actions(List.of(
                                "s3:Put*"
                        ))
                        .build())
        );


        AwsIntegration s3Integration = new AwsIntegration(AwsIntegrationProps.builder()
                .service("s3")
                .integrationHttpMethod("PUT")
                .path(bucket.getBucketName() + "/{key}")
                .options(IntegrationOptions.builder()
                        .credentialsRole(executionRole)
                        .integrationResponses(List.of(
                                IntegrationResponse.builder()
                                        .statusCode("200")
                                        .responseParameters(Map.of(
                                                "method.response.header.Content-Type", "integration.response.header.Content-Type"))
                                        .build()
                        ))
                        .requestParameters(Map.of(
                                "integration.request.path.key", "method.request.path.key"))
                        .build())
                .build()
        );

        api.getRoot()
                .addResource("metrics")
                .addResource("{key}")
                .addMethod("PUT", s3Integration, MethodOptions.builder()
                        .methodResponses(List.of(
                                MethodResponse.builder()
                                        .statusCode("200")
                                        .responseParameters(Map.of(
                                                "method.response.header.Content-Type", true
                                        ))
                                        .build()

                        ))
                        .requestParameters(Map.of(
                                "method.request.path.key", true,
                                "method.request.header.Content-Type", true
                        ))
                        .build()

                );
    }
}