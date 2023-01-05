# Welcome to your CDK Java project!

This is a blank project for CDK development with Java.

The `cdk.json` file tells the CDK Toolkit how to execute your app.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Useful commands

 * `./gradlew build` compile and run tests
 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation

Enjoy!

# Steps to reproduce issue

1. Change ACCOUNT_ID in `ExampleApp` to your AWS account ID
2. Run `cdk bootstrap` (ensure you are using Java 17)
3. Run `cdk deploy Metrics` (creates an API Gateway which proxies and S3 bucket)
4. Once deploy is complete, and without making modifications to CDK resources, run `cdk diff`. CDK indicates that 
the API Gateway needs to be deployed:
```bash
$ cdk diff

Stack Metrics
Resources
[-] AWS::ApiGateway::Deployment apiDeployment149F1294c6e53356948080ae1c422882d44c00f1 destroy
[+] AWS::ApiGateway::Deployment api/Deployment apiDeployment149F1294b8cf1628e39ad7e7e4e501de7cb32108
[~] AWS::ApiGateway::Stage api/DeploymentStage.prod apiDeploymentStageprod896C8101
 └─ [~] DeploymentId
     └─ [~] .Ref:
         ├─ [-] apiDeployment149F1294c6e53356948080ae1c422882d44c00f1
         └─ [+] apiDeployment149F1294b8cf1628e39ad7e7e4e501de7cb32108

```