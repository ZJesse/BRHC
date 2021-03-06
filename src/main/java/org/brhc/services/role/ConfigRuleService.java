package org.brhc.services.role;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.brhc.dao.*;
import org.brhc.pojo.DashboardRole;
import org.brhc.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yfyuan on 2016/12/22.
 */
@Repository
@Aspect
@Order(2)
public class ConfigRuleService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private RoleDao roleDao;

    @Value("${admin_user_id}")
    private String adminUserId;

    @Autowired
    private DatasetDao datasetDao;

    @Autowired
    private WidgetDao widgetDao;

    @Autowired
    private BoardDao boardDao;

    @Around("execution(* org.brhc.services.WidgetService.save(..)) || " +
            "execution(* org.brhc.services.WidgetService.update(..)) || " +
            "execution(* org.brhc.services.WidgetService.delete(..))")
    public Object widgetRule(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        } else {
            List<Long> menuIdList = menuDao.getMenuIdByUserRole(userid);
            if (menuIdList.contains(1L) && menuIdList.contains(4L)) {
                return proceedingJoinPoint.proceed();
            }
        }
        return null;
    }

    @Around("execution(* org.brhc.services.DatasetService.save(..)) || " +
            "execution(* org.brhc.services.DatasetService.update(..)) || " +
            "execution(* org.brhc.services.DatasetService.delete(..))")
    public Object datasetRule(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        } else {
            List<Long> menuIdList = menuDao.getMenuIdByUserRole(userid);
            if (menuIdList.contains(1L) && menuIdList.contains(3L)) {
                return proceedingJoinPoint.proceed();
            }
        }
        return null;
    }

    @Around("execution(* org.brhc.services.DatasourceService.save(..)) || " +
            "execution(* org.brhc.services.DatasourceService.update(..)) || " +
            "execution(* org.brhc.services.DatasourceService.delete(..))")
    public Object datasourceRule(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        } else {
            List<Long> menuIdList = menuDao.getMenuIdByUserRole(userid);
            if (menuIdList.contains(1L) && menuIdList.contains(2L)) {
                return proceedingJoinPoint.proceed();
            }
        }
        return null;
    }

    @Around("execution(* org.brhc.services.BoardService.save(..)) || " +
            "execution(* org.brhc.services.BoardService.update(..)) || " +
            "execution(* org.brhc.services.BoardService.delete(..))")
    public Object boardRule(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        } else {
            List<Long> menuIdList = menuDao.getMenuIdByUserRole(userid);
            if (menuIdList.contains(1L) && menuIdList.contains(5L)) {
                return proceedingJoinPoint.proceed();
            }
        }
        return null;
    }

    @Around("execution(* org.brhc.services.CategoryService.save(..)) || " +
            "execution(* org.brhc.services.CategoryService.update(..)) || " +
            "execution(* org.brhc.services.CategoryService.delete(..))")
    public Object categoryRule(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        } else {
            List<Long> menuIdList = menuDao.getMenuIdByUserRole(userid);
            if (menuIdList.contains(1L) && menuIdList.contains(6L)) {
                return proceedingJoinPoint.proceed();
            }
        }
        return null;
    }

    @Around("execution(* org.brhc.services.AdminSerivce.addUser(..)) || " +
            "execution(* org.brhc.services.AdminSerivce.updateUser(..)))")
    public Object userAdminRule(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        }
        return null;
    }

    @Around("execution(* org.brhc.services.AdminSerivce.addRole(..)) || " +
            "execution(* org.brhc.services.AdminSerivce.updateRole(..)) || " +
            "execution(* org.brhc.services.AdminSerivce.updateRoleRes(..))")
    public Object resAdminRule(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        } else {
            List<Long> menuIdList = menuDao.getMenuIdByUserRole(userid);
            if (menuIdList.contains(7L) && menuIdList.contains(8L)) {
                return proceedingJoinPoint.proceed();
            }
        }
        return null;
    }

    @Around("execution(* org.brhc.services.AdminSerivce.updateRole(..))")
    public Object updateRole(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        } else {
            DashboardRole role = roleDao.getRole((String) proceedingJoinPoint.getArgs()[0]);
            if (userid.equals(role.getUserId())) {
                return proceedingJoinPoint.proceed();
            }
        }
        return null;
    }

    @Around("execution(* org.brhc.services.AdminSerivce.updateUserRole(..)) ||" +
            "execution(* org.brhc.services.AdminSerivce.deleteUserRoles(..))")
    public Object updateUserRole(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed();
        } else {
            List<String> roleId = Lists.transform(roleDao.getRoleList(userid), new Function<DashboardRole, String>() {
                @Nullable
                @Override
                public String apply(@Nullable DashboardRole role) {
                    return role.getRoleId();
                }
            });
            Object[] args = proceedingJoinPoint.getArgs();
            String[] argRoleId = (String[]) args[1];
            roleId.retainAll(Arrays.asList(argRoleId));
            args[1] = roleId.toArray(new String[]{});
            return proceedingJoinPoint.proceed(args);
        }
    }

    @Around("execution(* org.brhc.services.AdminSerivce.updateRoleResUser(..))")
    public Object updateRoleResUser(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String userid = authenticationService.getCurrentUser().getUserId();
        if (userid.equals(adminUserId)) {
            return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        } else {
            Object[] args = proceedingJoinPoint.getArgs();
            JSONArray arr = JSONArray.parseArray(args[1].toString());
            List<Object> filtered = arr.stream().filter(e -> {
                JSONObject jo = (JSONObject) e;
                switch (jo.getString("resType")) {
                    case "widget":
                        return widgetDao.checkWidgetRole(userid, jo.getLong("resId"), RolePermission.PATTERN_READ) > 0;
                    case "dataset":
                        return datasetDao.checkDatasetRole(userid, jo.getLong("resId"), RolePermission.PATTERN_READ) > 0;
                    case "board":
                        return boardDao.checkBoardRole(userid, jo.getLong("resId"), RolePermission.PATTERN_READ) > 0;
                    default:
                        return false;
                }
            }).collect(Collectors.toList());
            args[1] = JSONArray.toJSON(filtered).toString();
            return proceedingJoinPoint.proceed(args);
        }
    }
}
