> # installation

sources:
https://github.com/openshift/origin/blob/master/examples/jenkins/pipeline/maven-pipeline.yaml
https://github.com/openshift/jenkins-client-plugin
https://jenkins.io/doc/pipeline/steps/openshift-pipeline/

oc policy add-role-to-user edit system:serviceaccount:ci:default -n ci
