package com.book.book_store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "UserHasRole")
@Table(name = "role_has_permissions")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UserHasRole extends AbstractEntity<Integer> {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
