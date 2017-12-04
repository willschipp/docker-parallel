package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="/containers")
public interface ContainerRepository extends JpaRepository<Container, String> {

}
