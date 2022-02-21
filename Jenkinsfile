buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  doUploadApidocs = true
  buildNode = 'jenkins-agent-java11'

  doApiLint = true
  doApiDoc = true
  apiTypes = 'OAS'
  apiDirectories = 'src/main/resources/swagger.api'

  doDocker = {
    buildDocker {
      publishMaster = 'yes'
      healthChk = 'yes'
      healthChkCmd = 'curl -sS --fail -o /dev/null http://localhost:8080/admin/health || exit 1'
    }
  }
}
