package org.brhc.dataprovider;

import org.apache.commons.lang3.StringUtils;
import org.brhc.dataprovider.aggregator.InnerAggregator;
import org.brhc.dataprovider.aggregator.h2.H2Aggregator;
import org.brhc.dataprovider.annotation.DatasourceParameter;
import org.brhc.dataprovider.annotation.ProviderName;
import org.brhc.dto.User;
import org.brhc.services.AuthenticationService;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by yfyuan on 2016/8/15.
 */
@Service
public class DataProviderManager implements ApplicationContextAware {

    private static Logger LOG = LoggerFactory.getLogger(DataProviderManager.class);

    private static Map<String, Class<? extends DataProvider>> providers = new HashMap<>();

    private static ApplicationContext applicationContext;

    static {
        Set<Class<?>> classSet = new Reflections("org.brhc").getTypesAnnotatedWith(ProviderName.class);
        for (Class c : classSet) {
            if (!c.isAssignableFrom(DataProvider.class)) {
                providers.put(((ProviderName) c.getAnnotation(ProviderName.class)).name(), c);
            } else {
                System.out.println("自定义DataProvider需要继承org.brhc.dataprovider.DataProvider");
            }
        }
    }

    public static Set<String> getProviderList() {
        return providers.keySet();
    }

    /*public static DataProvider getDataProvider(String type) throws Exception {
        return getDataProvider(type, null, null);
    }*/

    public static DataProvider getDataProvider(
            String type, Map<String, String> dataSource,
            Map<String, String> query) throws Exception {
        return getDataProvider(type, dataSource, query, false);
    }

    public static DataProvider getDataProvider(
            String type, Map<String, String> dataSource,
            Map<String, String> query,
            boolean isUseForTest) throws Exception {
        Class c = providers.get(type);

        //--------------------sql替换begin-----------
        String sql = query.get("sql");
        AuthenticationService authenticationService = applicationContext.getBean(AuthenticationService.class);
        User u = authenticationService.getCurrentUser();
        //User u = new User();
        String userid = u.getUserId();
        String userName = u.getUsername();
        String chyCode = u.getChyCode();
        String chyPcode = u.getChyPcode();
        chyCode="00020901";
        chyPcode="50091589";
        boolean admin = u.isAdmin();
        if (sql.contains("{") || sql.contains("}")) {
            if (StringUtils.equals(userid,"1") || admin) {//是超级管理员
                sql = sql.replaceAll("\\{.*\\}", "");
            }else{
                sql = sql.replace("{", "");
                sql = sql.replace("}", "");
            }
            Map<String, String> map = new HashMap();
            map.put("sql", sql);
            query = map;
        }
        if (sql.contains("?")) {//替换？为当前登录人userid
            sql = sql.replace("?", userName);
            Map<String, String> map = new HashMap();
            map.put("sql", sql);
            query = map;
        }

        String re = "\\[([^\\]]+)\\]";
        Pattern p = Pattern.compile(re);
        Matcher m = p.matcher(sql);
        while(m.find()){
            System.out.println(m.group(1));
            System.out.println(m.group(0));
            String tname = m.group(1);
            StringBuffer sb = new StringBuffer();
            if(StringUtils.isBlank(chyCode)){
                sb.append(" and ").append(tname).append(".chy_code is null");
            }else{
                if(!admin){
                    if(StringUtils.isBlank(chyPcode)){
                        sb.append(" and (").append(tname).append(".chy_code='").append(chyCode).append("'");
                        sb.append(" or ").append(tname).append(".chy_pcode='").append(chyCode).append("'");
                        sb.append(") ");
                    }else{
                        sb.append(" and ").append(tname).append(".chy_code='").append(chyCode).append("'");
                    }
                }
            }
            //替换[]为产业条件
            sql = sql.replace(m.group(0), sb.toString());
            Map<String, String> map = new HashMap();
            map.put("sql", sql);
            query = map;
        }

        //--------------------------end-------------


        ProviderName providerName = (ProviderName) c.getAnnotation(ProviderName.class);
        if (providerName.name().equals(type)) {
            DataProvider provider = (DataProvider) c.newInstance();
            provider.setQuery(query);
            provider.setDataSource(dataSource);
            provider.setUsedForTest(isUseForTest);
            if (provider instanceof Initializing) {
                ((Initializing) provider).afterPropertiesSet();
            }
            applicationContext.getAutowireCapableBeanFactory().autowireBean(provider);
            InnerAggregator innerAggregator = applicationContext.getBean(H2Aggregator.class);
            innerAggregator.setDataSource(dataSource);
            innerAggregator.setQuery(query);
            provider.setInnerAggregator(innerAggregator);
            return provider;
        }
        return null;
    }

    protected static Class<? extends DataProvider> getDataProviderClass(String type) {
        return providers.get(type);
    }

    public static List<String> getProviderFieldByType(String providerType, DatasourceParameter.Type type) throws IllegalAccessException, InstantiationException {
        Class clz = getDataProviderClass(providerType);
        Object o = clz.newInstance();
        Set<Field> fieldSet = ReflectionUtils.getAllFields(clz, ReflectionUtils.withAnnotation(DatasourceParameter.class));
        return fieldSet.stream().filter(e ->
                e.getAnnotation(DatasourceParameter.class).type().equals(type)
        ).map(e -> {
            try {
                e.setAccessible(true);
                return e.get(o).toString();
            } catch (IllegalAccessException e1) {
                LOG.error("" , e);
            }
            return null;
        }).collect(Collectors.toList());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
