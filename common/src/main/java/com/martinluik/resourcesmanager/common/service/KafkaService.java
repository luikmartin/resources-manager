package com.martinluik.resourcesmanager.common.service;

import com.martinluik.resourcesmanager.common.dto.ResourceDto;
import java.util.List;

public interface KafkaService {

  void sendResourceUpdate(ResourceDto resourceDto);

  void sendBulkExport(List<ResourceDto> resources);
}
