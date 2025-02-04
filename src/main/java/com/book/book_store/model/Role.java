package com.book.book_store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Set;

@Entity(name = "Role")
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Role extends AbstractEntity<Integer> {
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    private String description;

    @OneToMany(mappedBy = "role")
    private Set<RoleHasPermission> roleHasPermissions;

    @OneToMany(mappedBy = "role")
    private Set<UserHasRole> userHasRoles;
}
