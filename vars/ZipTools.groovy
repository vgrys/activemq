//package ctc.ad.corp.cicd

def bundle(env, sourceFolder, excludes) {

    String excludeParameters = ''
    for (exclude in excludes) {
        excludeParameters += "--exclude='${exclude}' "
    }

    def now = new Date()
    String timestamp = now.format('yyyyMMddHHmmss')
    def jobBaseName = "${env.JOB_NAME}".split('/')
    GString projectName = "${jobBaseName[0]}"
    GString archhiveFilePath = "${env.WORKSPACE}/${projectName}_${timestamp}.tgz"

    sh "tar -zcf ${archhiveFilePath} ${excludeParameters} -C ${sourceFolder} * "

    return archhiveFilePath
}