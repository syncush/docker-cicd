job('NodeJS example') {
    scm {
        git('git://github.com/wardviaene/docker-demo.git') {  node -> // is hudson.plugins.git.GitSCM
            node / gitConfigName('DSL User')
            node / gitConfigEmail('jenkins-dsl@newtech.academy')
        }
    }
    triggers {
        scm('H/5 * * * *')
    }
    wrappers {
        nodejs('nodejs-v11.10.1') // this is the name of the NodeJS installation in 
                         // Manage Jenkins -> Configure Tools -> NodeJS Installations -> Name
    }
    steps {
        shell("npm install")
    }
}

job('NodeJS Docker example') {
    scm {
        git('git://github.com/syncush/docker-cicd.git') {  node -> // is hudson.plugins.git.GitSCM
            node / gitConfigName('DSL User')
            node / gitConfigEmail('jenkins-dsl@newtech.academy')
        }
    }
    triggers {
        scm('H/5 * * * *')
    }
    wrappers {
        nodejs('nodejs-v11.10.1') 
    }
    steps {
        dockerBuildAndPublish {
            repositoryName('syncush/docker-cicd') //qa / dev
            tag('${GIT_REVISION,length=9}')
            registryCredentials('docker-hub-cred')
            forcePull(false)
            forceTag(false)
            createFingerprints(false)
            skipDecorate()
            buildContext('./basics/')
        }
    }
}

pipelineJob('NodeJS Docker Pipeline') {
    def repo = 'https://github.com/syncush/docker-cicd.git'
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(repo)
                    }
                    branches('master', '**/features')
                    extensions {}
                }
                scriptPath('./basics/misc/Jenkinsfile.v2')
            }
        }
    }
    description('Pipeline')
    triggers {
        scm('H/5 * * * *')
    }
}
