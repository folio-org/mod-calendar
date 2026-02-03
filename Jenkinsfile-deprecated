buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  doUploadApidocs = true
  buildNode = 'jenkins-agent-java21'

  doDocker = {
    buildDocker {
      publishMaster = 'yes'
      // health check in org.folio.calendar.integration.health.OkapiHealthTest
    }
  }
}
