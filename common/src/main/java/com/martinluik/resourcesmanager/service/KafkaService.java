package com.martinluik.resourcesmanager.service;

import com.martinluik.resourcesmanager.dto.ResourceDto;
import java.util.List;

public interface KafkaService {

  void sendResourceUpdate(ResourceDto resourceDto);

  void sendBulkExport(List<ResourceDto> resources);
}
