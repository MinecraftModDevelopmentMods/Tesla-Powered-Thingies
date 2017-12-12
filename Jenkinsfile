#!/bin/groovy

pipeline {
    agent any

    triggers {
        upstream(upstreamProjects: '/Face of Cat/Tesla-Core-Lib/1.12,/Late Night Mod Crafters/Bush-Master-Core/1.12', threshold: hudson.model.Result.SUCCESS)
    }

    stages() {
        stage("Checkout") {
            steps {
                checkout scm
            }
        }

        stage("Clean & Setup") {
            steps {
                sh """set -x
                      chmod 755 gradlew
                      ./gradlew clean setupCIWorkspace
                   """
            }
        }

        stage("Build") {
            steps {
                sh """set -x
                      ./gradlew build
                   """

                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true, onlyIfSuccessful: true
            }
        }

        stage("Maven") {
            steps {
                if (!env.BRANCH_NAME.startsWith('PR-')) {
                    sh """set -x
                          ./gradlew uploadArchives
                       """
                }
            }
        }
    }
}