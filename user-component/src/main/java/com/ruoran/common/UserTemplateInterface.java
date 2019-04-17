package com.ruoran.common;

import com.ruoran.common.domain.User;
import com.ruoran.common.util.Page;

public interface UserTemplateInterface {
    Page<User> queryForPage(int pageNum, int pageSize);

    User add(User user);

    User delete(Integer id);

    User update(User user);

    User find(Integer id);

    User login(User user);

    Long count();
}
