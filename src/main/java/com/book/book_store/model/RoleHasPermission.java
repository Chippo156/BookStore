package com.book.book_store.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_has_permissions")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class RoleHasPermission extends AbstractEntity<Integer> {

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permission permission;
}
