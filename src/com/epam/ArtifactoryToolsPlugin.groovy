#!/usr/bin/groovy
package com.epam


static def artifactoryConfig(env, repository, String atfArchivePath, String projectArchivePath, atf_version, project_name, project_version) {

//    def jobBaseName = "${env.JOB_NAME}".split('/')
//    def projectName = "${jobBaseName[0]}"
    def artifactoryATFPath = "artifactory/${repository}/atf/${atf_version}/"
    def artifactoryProjectPath = "artifactory/${repository}/${project_name}/${project_version}"


    env.uploadSpec = """{
                        "files": [{
                            "pattern": "${atfArchivePath}",
                            "target": "${artifactoryATFPath}/atf-${atf_version}.tar.gz"
                        },
                        {
                            "pattern": "${projectArchivePath}",
                            "target": "${artifactoryProjectPath}/${project_name}-${project_version}.tgz"
                        }]
                     }"""
}
