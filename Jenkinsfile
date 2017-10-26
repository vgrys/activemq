#!/usr/bin/groovy

@Library('shared-library@master')
import com.epam.ArtifactoryToolsPlugin


// Add DSS shared libraries
// ---------------------------------------------------------------
// DEVELOPER NOTE: EDIT JOB SCHEDULE AS REQUIRED
// Example here runs Jenkins job every 6 hours (uncomment to use):
// properties([pipelineTriggers([cron('0 H/6 * * *')])])
// ---------------------------------------------------------------

String artifactoryRepo = 'bigdata-dss-automation'
String artifactoryUrl = 'http://192.168.56.105:8081'
String atfVersion = '0.0.1'
String projectVersion = '0.1'
String projectName = 'sample'

// Node is the dss node
node {

    // Initialization - do not change
    currentBuild.result = "SUCCESS"
    def ERROR_TYPE = ""

    echo "DEBUG CODE -----> Running ${env.JOB_NAME} on ${env.JENKINS_URL} for branch ${env.BRANCH_NAME}"

//    try {

    // --------------------------------------
    // DEVELOPER NOTE: DO NOT EDIT THIS STAGE
    // CLEAN WORKSPACE STEPS
    stage('Clean Workspace') {
        echo "********** Clean Jenkins workspace ***********"
        deleteDir()
    }

    // --------------------------------------
    // DEVELOPER NOTE: DO NOT EDIT THIS STAGE
    // CHECK OUT SCM STEPS
    stage('Check out Source') {
        echo "********** Checkout SCM and Set Bitbucket properties ***********"
        checkout scm
        // Set bitbucket user properties
        bitbucketInfo()
        // Set the git variables for the project
        bitbucketGitInfo()
        echo "DEBUG CODE -----> Git Origin: ${env.GIT_ORIGIN}, Git User: ${env.GIT_USER}, Git Project: ${env.GIT_PROJECT}, Git Branch: ${env.GIT_BRANCH}, Git Repo: ${env.GIT_REPO}, Git Feature Name (optional): ${env.GIT_FEATURE_NAME}"
    }

    stage('Check out "cd-cd-framework" repo') {
        dir('cd-cd-framework') {
            git url: 'https://github.com/vgrys/VAULT.git'
        }
    }

    // --------------------------------------
    // This stage is added to perform project build
    stage('Create project archive') {
        echo "********* Start to create project archive **********"
        GString sourceFolder = "${WORKSPACE}"
        def zip = new ZipTools()
        def bundlePath = zip.bundle(env, sourceFolder, ['.git', '.gitignore'])
        echo "created an archive $bundlePath"
        echo "********* End of create project archive **********"
    }

    // --------------------------------------
    // DEVELOPER NOTE: DO NOT EDIT THIS STAGE
    // This stage is added for Jenkins to upload artifacts to Artifactory server
    stage('Upload artifacts to Artifactory server') {
        echo "********* Start to upload artifacts to Artifactory server **********"
        GString atfArchivePath = "${WORKSPACE}/dist/*.tar.gz"
        GString projectArchivePath = "${WORKSPACE}/*tgz"
        def artifactoryServer = Artifactory.newServer url: "${artifactoryUrl}", credentialsId: 'artifactoryIDVG'
        def artifactory = new ArtifactoryConfig()
        artifactory.configure(env, artifactoryRepo, "${atfArchivePath}", "${projectArchivePath}", atfVersion, projectName, projectVersion)
        artifactoryServer.upload(env.uploadSpec)
        echo "********* End of upload artifacts to Artifactory server **********"
    }

    // --------------------------------------
    // DEVELOPER NOTE: DO NOT EDIT THIS STAGE
    // TEST DATA MANAGMENT STEPS
    stage('Test Data Management') {
        echo "********* Test Data Management started ************"
        TDMTools()
        echo "********* Test Data Management completed ************"
    }

    // --------------------------------------
    // DEVELOPER NOTE: DO NOT EDIT THIS STAGE
    // PROJECT DEPLOYMENT STAGE
    stage('Project deployment') {
        echo "********* Start project deployment **********"
        withCredentials([usernamePassword(credentialsId: 'artifactoryIDVG', usernameVariable: 'artifactory_user', passwordVariable: 'artifactory_pwd')]) {
            dir("${WORKSPACE}/cd-cd-framework/ansible") {
                sh "ansible-playbook --extra-vars 'server=prod user=artifactory_user password=artifactory_pwd artifactoryUrl=${artifactoryUrl} artifactoryRepo=${artifactoryRepo} projectVersion=${projectVersion} projectName=${projectName} workspace=${WORKSPACE}' projectDeployment.yml"
            }
        }
        echo "********* End of project deployment **********"
    }

    // --------------------------------------
    // DEVELOPER NOTE: DO NOT EDIT THIS STAGE
    // ATF DEPLOYMENT STAGE
    stage('ATF deploy') {
        echo "********* Start to deploy AFT project **********"
        withCredentials([usernamePassword(credentialsId: 'artifactoryIDVG', usernameVariable: 'artifactory_user', passwordVariable: 'artifactory_pwd')]) {
            dir("${WORKSPACE}/cd-cd-framework/ansible") {
                sh "ansible-playbook --extra-vars 'user=artifactory_user password=artifactory_pwd server=P9CPWPTDMCI01 artifactoryRepo=${artifactoryRepo} artifactoryUrl=${artifactoryUrl} atfVersion=${atfVersion} workspace=${WORKSPACE}' ATFDeployment.yml"
            }
        }
        echo "********* End of deploy AFT project **********"
    }

    stage('smoke tests') {
        String commandToRun = 'source /tmp/ATFVENV/bin/activate; echo $USER; ls -l; pwd'
        sh "ssh -o StrictHostKeyChecking=no cicdbigd@Q9LCWPTDMCI01 /bin/bash -c '\"${commandToRun}\"'"
    }


//    } catch (err) {
//        currentBuild.result = "FAILURE"
//        // Send error email
//        sendErrorEmail()
//        throw err
//    }
}