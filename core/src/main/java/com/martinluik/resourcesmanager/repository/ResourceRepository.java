package com.martinluik.resourcesmanager.repository;

import com.martinluik.resourcesmanager.domain.Resource;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID> {}
