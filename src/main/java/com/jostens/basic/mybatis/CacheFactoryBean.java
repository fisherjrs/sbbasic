package com.jostens.basic.mybatis;

import org.apache.ibatis.cache.Cache;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public class CacheFactoryBean implements InitializingBean {
    protected List<Cache> cacheList;
    public CacheFactoryBean() {
    }

    public List<Cache> getCacheList() {
        return this.cacheList;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
