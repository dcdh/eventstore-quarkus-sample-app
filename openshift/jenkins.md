> # installation

import from catalog

    echo "192.168.56.101  jenkins-staging.apps.192.168.56.101       jenkins-staging.apps.192.168.56.101" >> /etc/hosts

oc create -f jenkins-agent-maven-35-graalvm-centos7-objects.yml -n staging