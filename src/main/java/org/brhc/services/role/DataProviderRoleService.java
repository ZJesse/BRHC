package org.brhc.services.role;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.brhc.dao.DatasetDao;
import org.brhc.dao.DatasourceDao;
import org.brhc.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;

/**
 * Created by yfyuan on 2016/12/23.
 */
@Repository
@Aspect
@Order(2)
public class DataProviderRoleService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DatasourceDao datasourceDao;

    @Autowired
    private DatasetDao datasetDao;

    @Value("${admin_user_id}")
    private String adminUserId;

    @Around("execution(* org.brhc.services.DataProviderService.getDimensionValues(..)) ||" +
            "execution(* org.brhc.services.DataProviderService.getColumns(..)) ||" +
            "execution(* org.brhc.services.DataProviderService.queryAggData(..)) ||" +
            "execution(* org.brhc.services.DataProviderService.viewAggDataQuery(..))")
    public Object query(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Long datasourceId = (Long) proceedingJoinPoint.getArgs()[0];
        Long datasetId = (Long) proceedingJoinPoint.getArgs()[2];
        String userid = authenticationService.getCurrentUser().getUserId();
        if (datasetId != null) {
            if (datasetDao.checkDatasetRole(userid, datasetId, RolePermission.PATTERN_READ) > 0) {
                return proceedingJoinPoint.proceed();
            }
        } else {
            if (datasourceDao.checkDatasourceRole(userid, datasourceId, RolePermission.PATTERN_READ) > 0) {
                return proceedingJoinPoint.proceed();
            }
        }
        return null;
    }

}
