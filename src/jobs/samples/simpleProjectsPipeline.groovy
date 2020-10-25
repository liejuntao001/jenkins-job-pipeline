package samples

import groovy.json.JsonSlurper

// this document is your friend
// https://jenkinsci.github.io/job-dsl-plugin/#path/pipelineJob-definition

def content = readFileFromWorkspace('config/samples.json')
def jsonConfig = new JsonSlurper().parseText(content)
List<String> projects = jsonConfig['simple_projects'].unique()

String basePath = jsonConfig['category_name']

folder("$basePath") {
  description 'This example shows how to use DSL extensions.'
}

projects.each { project ->
  String safeProjectName = project.replaceAll('/', '-')

  folder "$basePath/$safeProjectName"

  pipelineJob("$basePath/$safeProjectName/ci-pipeline") {
    description("Continuous integration pipeline for Project <b>$project</b>.")

    logRotator {
      daysToKeep(7)
      artifactDaysToKeep(1)
      numToKeep(30)
      artifactNumToKeep(3)
    }
    definition {
      cpsScm {
        scm {
          git {
            remote {
              name('origin')
              url('${GERRIT_HOST}/a/${project}')
              credentials('gerrit_username_password')
              refspec('${GERRIT_REFSPEC}')
            }
            branch('FETCH_HEAD')
          }
        }
        scriptPath('Jenkinsfile_ci.groovy')
        lightweight(false)
      }
    }
  }

  pipelineJob("$basePath/$safeProjectName/cd-pipeline") {
    description("Continuous delivery pipeline for Project <b>$project</b>.")

    logRotator {
      daysToKeep(7)
      artifactDaysToKeep(1)
      numToKeep(30)
      artifactNumToKeep(3)
    }
    definition {
      cpsScm {
        scm {
          git {
            remote {
              name('origin')
              url('${GERRIT_HOST}/a/${project}')
              credentials('gerrit_username_password')
              refspec('${GERRIT_REFSPEC}')
            }
            branch('FETCH_HEAD')
          }
        }
        scriptPath('Jenkinsfile_cd.groovy')
        lightweight(false)
      }
    }
  }
}
