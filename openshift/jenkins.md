> # installation

import from catalog

    echo "192.168.56.101  jenkins-staging.apps.192.168.56.101       jenkins-staging.apps.192.168.56.101" >> /etc/hosts

sources:
https://github.com/openshift/origin/blob/master/examples/jenkins/pipeline/maven-pipeline.yaml
https://github.com/openshift/jenkins-client-plugin
https://jenkins.io/doc/pipeline/steps/openshift-pipeline/

oc policy add-role-to-user edit system:serviceaccount:ci:default -n ci
