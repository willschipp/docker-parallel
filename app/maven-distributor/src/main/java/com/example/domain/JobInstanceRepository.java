package com.example.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobInstanceRepository extends JpaRepository<JobInstance, Long> {

	JobInstance findByUuid(String uuid);

}
