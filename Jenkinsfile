buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  doUploadApidocs = true
  buildNode = 'jenkins-agent-java17'

  doDocker = {
    buildDocker {
      publishMaster = 'yes'
      // health check in org.folio.calendar.integration.health.OkapiHealthTest
    }
  }
}
