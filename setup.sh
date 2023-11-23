#!/bin/bash

# This script will set up your Ubuntu machine for using Antlr and ASM.
# You need to verify you in are in the sudoers file before you can
# run this script.

echo "Update/Upgrade Packages"
echo "###############################"
sudo apt update
sudo apt upgrade -y

echo "Install Apache Ant"
echo "###############################"
echo sudo apt install ant

echo "Download Antlr-4.13.1"
echo "###############################"
sudo wget http://www.antlr.org/download/antlr-4.13.1-complete.jar
echo "Download ASM 9.6"
echo "###############################"
sudo wget https://repository.ow2.org/nexus/service/local/repositories/release/content/org/ow2/asm/asm/9.6/asm-6.jar

echo "Move jar files to correct location"
echo "###############################"
sudo mv *.jar /usr/local/lib

echo "Set Environment Variables"
echo "###############################"
echo "JAVA_HOME


echo "export JAVA_HOME=/



