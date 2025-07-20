package com.martinluik.resourcesmanager.config;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.Builder;
import org.mapstruct.MapperConfig;

@MapperConfig(
    builder = @Builder(disableBuilder = true),
    injectionStrategy = CONSTRUCTOR,
    unmappedTargetPolicy = ERROR,
    mappingInheritanceStrategy = AUTO_INHERIT_FROM_CONFIG)
public abstract class CommonMapperConfig {}
