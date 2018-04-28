package org.brhc.services;

import org.brhc.dto.User;

/**
 * Created by yfyuan on 2016/9/29.
 */
public interface AuthenticationService {

    User getCurrentUser();
}
