package com.mdh.common.user.persistence;

import com.mdh.common.user.domain.Role;
import com.mdh.common.user.domain.User;

import java.util.List;

public interface UserCustomRepository {

    List<User> findByRole(Role role);
}
