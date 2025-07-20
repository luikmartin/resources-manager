package com.martinluik.resourcesmanager.repository;

import com.martinluik.resourcesmanager.domain.Characteristic;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacteristicRepository extends JpaRepository<Characteristic, UUID> {

  List<Characteristic> findByResourceId(UUID resourceId);
}
