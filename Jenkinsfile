buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  doUploadApidocs = true
  buildNode = 'jenkins-agent-java11'

  doApiLint = true
  doApiDoc = true
  apiTypes = 'OAS'
  apiDirectories = 'src/main/resources/api'
  apiExcludes = 'paths parameters'

  doDocker = {
    buildDocker {
      publishMaster = 'yes'
      // health check in org.folio.calendar.integration.health.OkapiHealthTest
    }
  }
}
