package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HostRepository extends JpaRepository<Host, HostKey> {

	Host findByAddressAndPort(String ipAddress, String port);

}
