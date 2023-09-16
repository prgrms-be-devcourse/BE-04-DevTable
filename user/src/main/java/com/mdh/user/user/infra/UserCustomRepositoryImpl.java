package com.mdh.user.user.infra;

import com.mdh.common.user.domain.Role;
import com.mdh.common.user.domain.User;
import com.mdh.common.user.persistence.UserCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.mdh.common.user.domain.QUser.user;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<User> findByRole(Role role) {
        return jpaQueryFactory
                .select(user)
                .from(user)
                .where(user.role.eq(role))
                .fetch();
    }
}