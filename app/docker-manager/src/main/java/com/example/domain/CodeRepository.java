package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="/codes")
public interface CodeRepository extends JpaRepository<Code, Long> {

}
