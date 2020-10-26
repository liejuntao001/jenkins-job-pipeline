#!/usr/bin/env groovy

@Library("k8sagent@v0.1.1") _

build_on_node = params.BUILD_ON_NODE
if (!build_on_node) {
  echo 'BUILD_ON_NODE not defined. guess as mini'
  build_on_node = 'mini'
}

build_on_cloud = params.BUILD_ON_CLOUD
if (!build_on_cloud) {
  echo 'BUILD_ON_CLOUD not defined, guess as kubernetes'
  build_on_cloud = 'kubernetes'
}

my_node = k8sagent(name: build_on_node, cloud: build_on_cloud)

// Reference
// https://github.com/jenkinsci/job-dsl-plugin/wiki/User-Power-Moves#use-job-dsl-in-pipeline-scripts

podTemplate(my_node) {
  node(my_node.label) {
    checkout scm

    stage ('deploy') {
      def job_category = 'samples'
      if (params.JOB_CATEGORY) {
        job_category = params.JOB_CATEGORY
      }
      jobDsl targets: ["src/jobs/${job_category}/*.groovy", 'src/jobs/*.groovy'].join('\n'),
          ignoreMissingFiles: true,
          //sandbox: true
          removedJobAction: 'DISABLE',
          removedViewAction: 'DELETE',
          lookupStrategy: 'SEED_JOB'
      //additionalParameters: [message: 'Hello from pipeline', credentials: 'SECRET'],
    }
  }
}
