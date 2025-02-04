package com.book.book_store.service.impl;

import com.book.book_store.model.Role;
import com.book.book_store.repository.RoleRepository;
import com.book.book_store.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository  roleRepository;
    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
    }
}
