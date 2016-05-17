#!/bin/sh

mvn clean install
ansible-playbook deploy-import.yml -f 2 --ask-sudo-pass

