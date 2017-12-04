#!/usr/bin/env bash

yum install -y bzip2 unzip
yum install -y curl git rsync wget vim
yum install -y gcc make gcc-c++
yum install -y "kernel-devel-uname-r == $(uname -r)"

VBOX_VERSION=$(cat /home/vagrant/.vbox_version)
cd /tmp
mount -o loop /home/vagrant/VBoxGuestAdditions_$VBOX_VERSION.iso /mnt
sh /mnt/VBoxLinuxAdditions.run
umount /mnt
# rm -rf /home/vagrant/VBoxGuestAdditions_*.iso
