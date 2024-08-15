package com.siewe_rostand.tvcam.shared.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author rostand
 * @project tv-cam
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {


    @Column(nullable = false, updatable = false, name = "created_at")
    private String createdAt;


    @Column(insertable = false, name = "updated_at")
    private String updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(insertable = false)
    private Long modifiedBy;

    @PrePersist
    protected void PrePersist() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        String formattedDate = formatter.format(now);
        if (createdAt == null) createdAt = formattedDate;
        if (this.updatedAt == null) updatedAt = formattedDate;
    }

    @PreUpdate
    protected void PreUpdate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        updatedAt = formatter.format(now);
    }
}
