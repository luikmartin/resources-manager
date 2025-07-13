package com.martinluik.resourcesmanager.core.repository;

import com.martinluik.resourcesmanager.core.domain.Location;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {}
