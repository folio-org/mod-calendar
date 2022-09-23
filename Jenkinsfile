buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  doUploadApidocs = true
  buildNode = 'jenkins-agent-java17'

  doApiLint = true
  doApiDoc = true
  apiTypes = 'OAS'
  apiDirectories = 'src/main/resources/api'
  apiExcludes = 'paths errors'

  doDocker = {
    buildDocker {
      publishMaster = 'yes'
      // health check in org.folio.calendar.integration.health.OkapiHealthTest
    }
  }
}
