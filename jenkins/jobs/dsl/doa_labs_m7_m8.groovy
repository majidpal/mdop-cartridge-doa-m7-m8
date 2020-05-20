// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"

// Variables
def referenceAppgitRepo = "spring-petclinic"
def referenceAppGitUrl = "ssh://jenkins@gerrit:29418/${PROJECT_NAME}/" + referenceAppgitRepo

// Jobs
def deployToTomcat = freeStyleJob(projectFolderName + "/Module_7_Deploy_to_Tomcat")
def triggerAlarm = freeStyleJob(projectFolderName + "/Module_8_Trigger_Alarm")


deployToTomcat.with {
    description("This job builds Java Spring reference application and deploys it to a Tomcat host")
    parameters{
        credentialsParam("SSH_KEY"){
            type('org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl')
            required()
            description('Pem file used to gain access to your Tomcat host')
        }
        stringParam("TOMCAT_IP",'',"Enter the public IP address of your Tomcat host")
    }
    wrappers {
        preBuildCleanup()
        injectPasswords()
        maskPasswords()
        sshAgent("mdop-jenkins-master")
        credentialsBinding {
            file('PEM_FILE', '${SSH_KEY}')
        }
    }    
    scm {
        git {
            remote {
                url(referenceAppGitUrl)
                credentials("mdop-jenkins-master")
            }
            branch("*/master")
        }
    }
    environmentVariables {
        env('WORKSPACE_NAME', workspaceFolderName)
        env('PROJECT_NAME', projectFolderName)
    }
    label("java8")
    steps {
        maven {
            goals('clean install -DskipTests')
            mavenInstallation("MDOP Maven")
        }
        shell('''set +x

echo "Copying build war file over SSH to the Tomcat host temp folder..."
ssh -i ${PEM_FILE} -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no centos@$TOMCAT_IP "mkdir -p ~/temp"
scp -i ${PEM_FILE} -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no  ${WORKSPACE}/target/petclinic.war centos@$TOMCAT_IP:~/temp

echo "SSH onto host and unzip war file into webapps folder..."
ssh -tt -i ${PEM_FILE} -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no centos@$TOMCAT_IP "      
      sudo docker stop tomcat8
      sudo rm -rf /data/tomcat/webapps/petclinic
      sudo unzip ~/temp/petclinic.war -d /data/tomcat/webapps/petclinic >/dev/null
      sudo docker start tomcat8
"      

set -x''')
    }
}

triggerAlarm.with {
    description("This job will spike the CPU usage of the Tomcat host in order to trigger the Cloud Watch alarm in AWS.")
    parameters{
        credentialsParam("SSH_KEY"){
            type('org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl')
            required()
            description('Pem file used to gain access to your Tomcat host')
        }
        stringParam("TOMCAT_IP",'',"Enter the public IP address of your Tomcat host")
    }
    wrappers {
        preBuildCleanup()
        injectPasswords()
        maskPasswords()
        sshAgent("mdop-jenkins-master")
        credentialsBinding {
            file('PEM_FILE', '${SSH_KEY}')
        }
    } 
    environmentVariables {
        env('WORKSPACE_NAME', workspaceFolderName)
        env('PROJECT_NAME', projectFolderName)
    }
    label("java8")
    steps {
        shell('''set +x
            |echo "Spiking CPU usage for 5 minutes..."
            |ssh -i ${PEM_FILE} centos@$TOMCAT_IP -o StrictHostKeyChecking=no "fulload() { dd if=/dev/zero of=/dev/null & }; fulload; sleep 310; pkill dd;"
            |set -x'''.stripMargin())
    }
}
