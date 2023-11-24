#!/bin/bash

# This script will set up your Ubuntu machine for using Antlr and ASM.
# You need to verify you in are in the sudoers file before you can
# run this script.

echo "Update/Upgrade Packages"
echo "###############################"
sudo apt update
sudo apt upgrade -y

echo "Install Java"
echo "###############################"
sudo apt install default-jdk -y


echo "Install Apache Ant"
echo "###############################"
sudo apt install ant -y

echo "Install VSCode"
echo "##############################"
echo sudo snap install --classic code

echo "Download Antlr-4.13.1"
echo "###############################"
sudo wget http://www.antlr.org/download/antlr-4.13.1-complete.jar
echo "Download ASM 9.6"
echo "###############################"
sudo wget "https://repository.ow2.org/nexus/service/local/repositories/releases/content/org/ow2/asm/asm/9.6/asm-9.6.jar"

echo "Move jar files to correct location"
echo "###############################"
sudo mv *.jar /usr/local/lib

echo "Set Environment Variables"
echo "###############################"
echo "export JAVA_HOME=/usr/lib/jvm/default-java" >> /home/$USER/.bashrc
echo "export PATH=$PATH:$JAVA_HOME/bin" >> /home/$USER/.bashrc
echo "export CLASSPATH=$CLASSPATH:./:/usr/local/lib/antlr-4.13.1-complete.jar:/usr/local/lib/asm-9.6.jar" >> /home/$USER/.bashrc
echo "export KC=/home/$USER/Documents/KnightCodeSkeleton" >> /home/$USER/.bashrc

echo "Create Aliases"
echo "###############################"
echo "alias antlr4='java -jar /usr/local/lib/antlr-4.13.1-complete.jar'" >> /home/$USER/.bashrc
echo "alias grun='java org.antlr.v4.gui.TestRig'" >> /home/$USER/.bashrc

echo "Reload .bashrc"
echo "###############################"
echo `source /home/$USER/.bashrc`




