package com.martinluik.resourcesmanager.core.domain;

import com.martinluik.resourcesmanager.common.enums.CharacteristicType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Characteristic {

  @Id @GeneratedValue private UUID id;

  @Column(nullable = false, length = 5)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CharacteristicType type;

  @Column(nullable = false)
  private String value;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resource_id", nullable = false)
  private Resource resource;
}
