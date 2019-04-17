package com.cd.common;

import com.cd.common.domain.User;
import com.cd.common.util.Page;

public interface UserTemplateInterface {
    Page<User> queryForPage(int pageNum, int pageSize);

    User add(User user);

    User delete(Integer id);

    User update(User user);

    User find(Integer id);

    User login(User user);

    Long count();
}
