#!/bin/bash -eux

echo '==> moving repos'
mv /etc/yum.repos.d/*.repo /tmp/

echo '==> adding the remote'
echo "[base]" >> /etc/yum.repos.d/remote.repo
echo "name=Centos Base" >> /etc/yum.repos.d/remote.repo
echo "baseurl=http://192.168.56.101/base/" >> /etc/yum.repos.d/remote.repo
echo "enabled=1" >> /etc/yum.repos.d/remote.repo
echo "gpgcheck=0" >> /etc/yum.repos.d/remote.repo
echo " " >> /etc/yum.repos.d/remote.repo
echo "[extras]" >> /etc/yum.repos.d/remote.repo
echo "name=Extra Centos Packages" >> /etc/yum.repos.d/remote.repo
echo "baseurl=http://192.168.56.101/extras/" >> /etc/yum.repos.d/remote.repo
echo "enabled=1" >> /etc/yum.repos.d/remote.repo
echo "gpgcheck=0" >> /etc/yum.repos.d/remote.repo
echo " " >> /etc/yum.repos.d/remote.repo
echo "[epel]" >> /etc/yum.repos.d/remote.repo
echo "name=Extra Packages" >> /etc/yum.repos.d/remote.repo
echo "baseurl=http://192.168.56.101/epel/" >> /etc/yum.repos.d/remote.repo
echo "enabled=1" >> /etc/yum.repos.d/remote.repo
echo "gpgcheck=0" >> /etc/yum.repos.d/remote.repo
echo " " >> /etc/yum.repos.d/remote.repo
echo "[MongoDB]" >> /etc/yum.repos.d/remote.repo
echo "name=Mongo DB" >> /etc/yum.repos.d/remote.repo
echo "baseurl=http://192.168.56.101/MongoDB/" >> /etc/yum.repos.d/remote.repo
echo "enabled=1" >> /etc/yum.repos.d/remote.repo
echo "gpgcheck=0" >> /etc/yum.repos.d/remote.repo
echo " " >> /etc/yum.repos.d/remote.repo
echo "[Jenkins]" >> /etc/yum.repos.d/remote.repo
echo "name=Jenkins" >> /etc/yum.repos.d/remote.repo
echo "baseurl=http://192.168.56.101/jenkins/" >> /etc/yum.repos.d/remote.repo
echo "enabled=1" >> /etc/yum.repos.d/remote.repo
echo "gpgcheck=0" >> /etc/yum.repos.d/remote.repo

sync

echo `yum clean cache`
echo `yum repolist`
echo `setenforce 0`

echo '==> finished'
